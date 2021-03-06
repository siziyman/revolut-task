package revolut.home.task.transaction

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import revolut.home.task.account.AccountDTO
import revolut.home.task.account.Tables.ACCOUNTS
import revolut.home.task.account.Tables.TRANSACTIONS
import revolut.home.task.common.Currency
import revolut.home.task.common.FailedOperationException
import revolut.home.task.common.NotFoundException
import revolut.home.task.common.RestrictedActionException
import java.sql.Timestamp
import javax.inject.Singleton
import kotlin.streams.toList

@Singleton
class TransactionService(private val dslContext: DSLContext) {
    private val logger: Logger = LoggerFactory.getLogger(TransactionService::class.java)
    fun submitTransaction(submitTransactionRequest: SubmitTransactionRequest): TransactionDTO {
        lateinit var transactionCreated: TransactionDTO
        if (submitTransactionRequest.sender == submitTransactionRequest.recipient) {
            throw RestrictedActionException("Cannot transfer to the same account")
        }
        try {
            dslContext.transaction { configuration ->
                transactionCreated = performSubmitTransaction(configuration, submitTransactionRequest)
            }
        } catch (e: RuntimeException) {
            when (e) {
                is FailedOperationException, is RestrictedActionException -> throw e
                else -> {
                    logger.error("submitTransaction(): $e")
                    throw FailedOperationException("Internal error on transaction submission")
                }
            }
        }
        return transactionCreated
    }

    fun getAll(): List<TransactionDTO> {
        try {
            val result = dslContext.select(
                    TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT,
                    TRANSACTIONS.SENT).from(TRANSACTIONS).fetch().stream()
            return result
                .map { TransactionDTO(it.value1(), it.value2(), it.value3(), it.value4(), it.value5().toString()) }
                .toList()
        } catch (e: DataAccessException) {
            logger.error("getAll(): ${e.message}")
            throw FailedOperationException("Internal error getting transactions list")
        }
    }

    fun getOne(id: Long): TransactionDTO {
        try {
            if (id < 0) {
                throw RestrictedActionException("Invalid ID")
            }
            val transactionRecord = dslContext.select(
                    TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT,
                    TRANSACTIONS.SENT).from(TRANSACTIONS).where(TRANSACTIONS.ID.eq(id)).fetch()
            if (transactionRecord.size == 0) {
                throw NotFoundException("Transaction with ID $id not found")
            }
            return TransactionDTO(transactionRecord[0].value1(), transactionRecord[0].value2(),
                                  transactionRecord[0].value3(), transactionRecord[0].value4(),
                                  transactionRecord[0].value5().toString())
        } catch (e: DataAccessException) {
            logger.error("getOne(): ${e.message}")
            throw FailedOperationException("Internal error getting transaction $id")
        }
    }

    fun getTransactionsByAccount(id: Long): List<TransactionDTO> {
        try {
            val result = dslContext.select(
                    TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT,
                    TRANSACTIONS.SENT).from(TRANSACTIONS).where(TRANSACTIONS.SENDER.eq(id))
                .or(TRANSACTIONS.RECIPIENT.eq(id)).fetch().stream()
            return result
                .map { TransactionDTO(it.value1(), it.value2(), it.value3(), it.value4(), it.value5().toString()) }
                .toList()
        } catch (e: DataAccessException) {
            logger.error("getTransactionsByAccount(): ${e.message}")
            throw FailedOperationException("Internal error getting transactions list")
        }
    }

    private fun performSubmitTransaction(configuration: Configuration,
                                         submitTransactionRequest: SubmitTransactionRequest): TransactionDTO {

        val (senderAccount, recipientAccount) = getSendAndReceiveAccounts(configuration, submitTransactionRequest)
        if (senderAccount.currency != recipientAccount.currency) {
            throw RestrictedActionException("Account currencies do not match")
        }
        if (senderAccount.balance < submitTransactionRequest.amount) {
            throw RestrictedActionException("Account balance is too low")
        }
        updateBalances(configuration, submitTransactionRequest)
        val insertionResult = DSL.using(configuration).insertInto(
                TRANSACTIONS, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT,
                TRANSACTIONS.SENT)
            .values(submitTransactionRequest.sender, submitTransactionRequest.recipient,
                    submitTransactionRequest.amount,
                    DSL.using(configuration).select(DSL.currentTimestamp()).fetchOne(0, Timestamp::class.java))
            .returningResult(TRANSACTIONS.ID, TRANSACTIONS.SENT).fetchOne()
        return TransactionDTO(insertionResult.value1(), submitTransactionRequest.sender,
                              submitTransactionRequest.recipient, submitTransactionRequest.amount,
                              insertionResult.value2().toString())
    }

    private fun updateBalances(configuration: Configuration, submitTransactionRequest: SubmitTransactionRequest) {
        val minusRowsUpdated =
                DSL.using(configuration).update(ACCOUNTS)
                    .set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.minus(submitTransactionRequest.amount))
                    .where(ACCOUNTS.ID.eq(submitTransactionRequest.sender)).execute()
        if (minusRowsUpdated != 1) {
            throw FailedOperationException("Transaction failed unexpectedly")
        }
        val plusRowsUpdated =
                DSL.using(configuration).update(ACCOUNTS)
                    .set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.plus(submitTransactionRequest.amount))
                    .where(ACCOUNTS.ID.eq(submitTransactionRequest.recipient)).execute()
        if (plusRowsUpdated != 1) {
            throw FailedOperationException("Transaction failed unexpectedly")
        }
    }

    private fun getSendAndReceiveAccounts(configuration: Configuration,
                                          submitTransactionRequest: SubmitTransactionRequest): Pair<AccountDTO, AccountDTO> {
        val accounts =
                DSL.using(configuration).select(ACCOUNTS.ID, ACCOUNTS.BALANCE, ACCOUNTS.CURRENCY).from(ACCOUNTS)
                    .where(ACCOUNTS.ID.eq(submitTransactionRequest.sender))
                    .or(ACCOUNTS.ID.eq(submitTransactionRequest.recipient)).forUpdate().fetch()
        if (accounts.size != 2) {
            throw RestrictedActionException("Account not found")
        }
        return when {
            (accounts[0].value1() == submitTransactionRequest.sender) ->
                AccountDTO(accounts[0].value1(), accounts[0].value2(), Currency.valueOf(accounts[0].value3())) to
                        AccountDTO(accounts[1].value1(), accounts[1].value2(), Currency.valueOf(accounts[1].value3()))
            else -> AccountDTO(accounts[1].value1(), accounts[1].value2(), Currency.valueOf(accounts[1].value3())) to
                    AccountDTO(accounts[0].value1(), accounts[0].value2(), Currency.valueOf(accounts[0].value3()))
        }
    }
}
