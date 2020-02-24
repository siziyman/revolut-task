package revolut.home.task.transaction

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("transaction")
class TransactionController {

    @Post("/")
    fun submitTransaction(): HttpResponse<TransactionDTO> {
        TODO("implement")
    }

    @Get("/")
    fun getAllTransactions(): List<TransactionDTO> {
        TODO("implement")
    }

    @Get("/{id}")
    fun getTransactionById(): TransactionDTO {
        TODO("implement")
    }


}