package revolut.home.task.transaction

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
        try {

            dslContext.transaction { configuration ->
                val fetch = configuration.dsl().select(ACCOUNTS.ID, ACCOUNTS.BALANCE, ACCOUNTS.CURRENCY).from(ACCOUNTS)
                        .where(ACCOUNTS.ID.eq(submitTransactionRequest.sender)).or(ACCOUNTS.ID.eq(submitTransactionRequest.recipient)).forUpdate().fetch()
                if (fetch.size != 2) {
                    throw RestrictedActionException("Account not found")
                }
                val (senderAccount, recipientAccount) = when {
                    (fetch[0].value1() == submitTransactionRequest.sender) -> listOf(
                            AccountDTO(fetch[0].value1(), fetch[0].value2(), Currency.valueOf(fetch[0].value3())), AccountDTO(fetch[1].value1(), fetch[1].value2(), Currency.valueOf(fetch[1].value3())))
                    else -> listOf(AccountDTO(fetch[1].value1(), fetch[1].value2(), Currency.valueOf(fetch[1].value3())), AccountDTO(fetch[0].value1(), fetch[0].value2(), Currency.valueOf(fetch[0].value3())))
                }
                if (senderAccount.currency != recipientAccount.currency) {
                    throw RestrictedActionException("Account currencies do not match")
                }
                if (senderAccount.balance < submitTransactionRequest.amount) {
                    throw RestrictedActionException("Account balance is too low")
                }
                val minusExecution = dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.minus(submitTransactionRequest.amount)).where(ACCOUNTS.ID.eq(submitTransactionRequest.sender)).execute()
                if (minusExecution != 1) {
                    throw FailedOperationException("Transaction failed unexpectedly")
                }
                val plusExecution = dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.plus(submitTransactionRequest.amount)).where(ACCOUNTS.ID.eq(submitTransactionRequest.recipient)).execute()
                if (plusExecution != 1) {
                    throw FailedOperationException("Transaction failed unexpectedly")
                }
                val insertionResult = dslContext.insertInto(TRANSACTIONS, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT, TRANSACTIONS.SENT).values(submitTransactionRequest.sender, submitTransactionRequest.recipient, submitTransactionRequest.amount,
                        dslContext.select(DSL.currentTimestamp()).fetchOne(0, Timestamp::class.java)).returningResult(TRANSACTIONS.ID, TRANSACTIONS.SENT).fetchOne()
                transactionCreated = TransactionDTO(insertionResult.value1(), submitTransactionRequest.sender, submitTransactionRequest.recipient, submitTransactionRequest.amount, insertionResult.value2())
            }
        } catch (e: RuntimeException) {
            when (e) {
                is FailedOperationException, is RestrictedActionException -> throw e
                else -> {
                    logger.error("submitTransaction(): ${e.message}")
                    throw FailedOperationException("Internal error on transaction submission")
                }
            }
        }
        return transactionCreated
    }

    fun getAll(): List<TransactionDTO> {
        try {
            val result = dslContext.select(TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT, TRANSACTIONS.SENT).from(TRANSACTIONS).fetch().stream()
            return result.map { TransactionDTO(it.value1(), it.value2(), it.value3(), it.value4(), it.value5()) }.toList()
        } catch (e: DataAccessException) {
            logger.error("getAll(): ${e.message}")
            throw FailedOperationException("Internal error getting transactions list")
        }
    }

    fun getOne(id: Long): TransactionDTO {
        try {
            val result = dslContext.select(TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT, TRANSACTIONS.SENT).from(TRANSACTIONS).where(TRANSACTIONS.ID.eq(id)).fetch()
            if (result.size == 0) {
                throw NotFoundException("Account with ID $id not found")
            }
            return TransactionDTO(result[0].value1(), result[0].value2(), result[0].value3(), result[0].value4(), result[0].value5())
        } catch (e: DataAccessException) {
            logger.error("getOne(): ${e.message}")
            throw FailedOperationException("Internal error getting transaction $id")
        }
    }

    fun getTransactionsByAccount(id: Long): List<TransactionDTO> {
        try {
            val result = dslContext.select(TRANSACTIONS.ID, TRANSACTIONS.SENDER, TRANSACTIONS.RECIPIENT, TRANSACTIONS.AMOUNT, TRANSACTIONS.SENT).from(TRANSACTIONS)
                    .where(TRANSACTIONS.SENDER.eq(id)).or(TRANSACTIONS.RECIPIENT.eq(id)).fetch().stream()
            return result.map { TransactionDTO(it.value1(), it.value2(), it.value3(), it.value4(), it.value5()) }.toList()
        } catch (e: DataAccessException) {
            logger.error("getTransactionsByAccount(): ${e.message}")
            throw FailedOperationException("Internal error getting transactions list")
        }
    }
}