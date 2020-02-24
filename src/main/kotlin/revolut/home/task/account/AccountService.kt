package revolut.home.task.account

import org.jooq.DSLContext
import org.jooq.TransactionalRunnable
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jooq.impl.DSL.currentTimestamp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import revolut.home.task.account.Tables.ACCOUNTS
import revolut.home.task.common.Currency
import revolut.home.task.common.FailedOperationException
import revolut.home.task.common.NotFoundException
import java.sql.Timestamp
import javax.inject.Singleton
import kotlin.streams.toList

@Singleton
class AccountService constructor(private val dslContext: DSLContext) {
    private val logger: Logger = LoggerFactory.getLogger(AccountService::class.java)
    fun createAccount(request: CreateAccountRequest): AccountDTO {
        lateinit var accCreated: AccountDTO
        try {
            dslContext.transaction(TransactionalRunnable { configuration ->
                val insertedAccount = configuration.dsl().insertInto(ACCOUNTS, ACCOUNTS.BALANCE, ACCOUNTS.CREATED, ACCOUNTS.CURRENCY)
                        .values(request.balance, DSL.select(currentTimestamp()).fetchOne(0, Timestamp::class.java), request.currency.toString())
                        .returningResult(ACCOUNTS.ID, ACCOUNTS.BALANCE, ACCOUNTS.CURRENCY).fetchOne()
                accCreated = AccountDTO(insertedAccount.component1(), insertedAccount.component2(), Currency.valueOf(insertedAccount.component3()))

            })
        } catch (e: RuntimeException) {
            // It's up for debate whether we should (or not) expose internal errors
            // assuming that API consumers are external entities/customers, we probably should not, therefore, rethrow w/o details
            logger.error("createAccount(): ${e.message}")
            throw FailedOperationException("Internal error creating account")
        }
        return accCreated
    }

    fun getAll(): List<AccountDTO> {
        // Decided to stay away from AOP as a replacement for try/catch boilerplate & error logging for simplicity "here and now"
        try {
            val result = dslContext.select(ACCOUNTS.ID, ACCOUNTS.BALANCE, ACCOUNTS.CURRENCY).from(ACCOUNTS).fetch().stream()
            return result.map { AccountDTO(it.value1(), it.value2(), Currency.valueOf(it.value3())) }.toList()
        } catch (e: DataAccessException) {
            logger.error("getAll(): ${e.message}")
            throw FailedOperationException("Internal error getting accounts list")
        }
    }

    fun getOne(id: Long): AccountDTO {
        try {
            val result = dslContext.select(ACCOUNTS.ID, ACCOUNTS.BALANCE, ACCOUNTS.CURRENCY).from(ACCOUNTS).where(ACCOUNTS.ID.eq(id)).fetch()
            if (result.size == 0) {
                throw NotFoundException("Account with ID $id not found")
            }
            return AccountDTO(result[0].value1(), result[0].value2(), Currency.valueOf(result[0].value3()))
        } catch (e: DataAccessException) {
            logger.error("getOne(): ${e.message}")
            throw FailedOperationException("Internal error getting account $id")
        }
    }
}