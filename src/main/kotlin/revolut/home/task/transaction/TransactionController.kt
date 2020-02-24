package revolut.home.task.transaction

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import javax.validation.Valid

@Controller("transaction")
class TransactionController(private val transactionService: TransactionService) {

    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun submitTransaction(@Valid @Body submitTransactionRequest: SubmitTransactionRequest): HttpResponse<TransactionDTO> {
        val submitTransaction = transactionService.submitTransaction(submitTransactionRequest)
        return HttpResponse.created(submitTransaction)
    }

    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllTransactions(): List<TransactionDTO> {
        return transactionService.getAll()
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTransactionById(@Valid @PathVariable id: Long): TransactionDTO {
        return transactionService.getOne(id)
    }


}