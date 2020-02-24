package revolut.home.task.account

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import revolut.home.task.transaction.TransactionDTO
import javax.validation.Valid

@Controller("account")
class AccountController constructor(private val accountService: AccountService) {
    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllAccounts(): List<AccountDTO> {
        return accountService.getAll()
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAccountById(@Valid @PathVariable id: Long): AccountDTO {
        return accountService.getOne(id)
    }

    @Post("/")
    fun createAccount(@Valid @Body request: CreateAccountRequest): HttpResponse<AccountDTO> {
        val createAccountResult = accountService.createAccount(request)
        return HttpResponse.created(createAccountResult)
    }

    @Get("/{id}/transactions")
    fun getTransactionsByAccount(@Valid @PathVariable id: Long): List<TransactionDTO> {
        TODO("implement")
    }
}