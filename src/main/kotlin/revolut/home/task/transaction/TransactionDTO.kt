package revolut.home.task.transaction

import java.math.BigDecimal

data class TransactionDTO(val id: Long, val sender: Long, val recipient: Long, val amount: BigDecimal, val timestamp
: String)

data class SubmitTransactionRequest(val sender: Long, val recipient: Long, val amount: BigDecimal)