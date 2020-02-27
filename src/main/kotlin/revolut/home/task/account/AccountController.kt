package revolut.home.task.account

import io.micronaut.http.annotation.*
import revolut.home.task.transaction.TransactionDTO
import revolut.home.task.transaction.TransactionService
import javax.validation.Valid

@Controller("account")
class AccountController constructor(private val accountService: AccountService,
                                    private val transactionService: TransactionService) {
    @Get("/")
    fun getAllAccounts(): List<AccountDTO> {
        return accountService.getAll()
    }

    @Get("/{id}")
    fun getAccountById(@Valid @PathVariable id: Long): AccountDTO {
        return accountService.getOne(id)
    }

    @Post("/")
    fun createAccount(@Valid @Body request: CreateAccountRequest): AccountDTO {
        return accountService.createAccount(request)
    }

    @Get("/{id}/transactions")
    fun getTransactionsByAccount(@Valid @PathVariable id: Long): List<TransactionDTO> {
        return transactionService.getTransactionsByAccount(id)
    }
}