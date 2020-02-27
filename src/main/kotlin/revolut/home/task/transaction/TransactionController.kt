package revolut.home.task.transaction

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import javax.validation.Valid

@Controller("transaction")
class TransactionController(private val transactionService: TransactionService) {

    @Post("/")
    fun submitTransaction(@Valid @Body submitTransactionRequest: SubmitTransactionRequest): HttpResponse<TransactionDTO> {
        val submitTransaction = transactionService.submitTransaction(submitTransactionRequest)
        return HttpResponse.created(submitTransaction)
    }

    @Get("/")
    fun getAllTransactions(): List<TransactionDTO> {
        return transactionService.getAll()
    }

    @Get("/{id}")
    fun getTransactionById(@Valid @PathVariable id: Long): TransactionDTO {
        return transactionService.getOne(id)
    }
}