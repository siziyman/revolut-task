package revolut.home.task.transaction

import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.validation.constraints.Positive

data class TransactionDTO(val id: Long, val sender: Long, val recipient: Long, val amount: BigDecimal, val date:Timestamp)

data class SubmitTransactionRequest(val sender: Long, val recipient: Long, val amount: BigDecimal)