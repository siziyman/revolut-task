package revolut.home.task.transaction

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionDTO(val id: Long, val sender: Long, val recipient: Long, val amount: BigDecimal, val date:LocalDateTime)

data class SubmitTransactionRequest(val sender: Long, val recipient: Long, val amount: BigDecimal)