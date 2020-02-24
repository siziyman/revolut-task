package revolut.home.task.account

import revolut.home.task.common.Currency
import java.math.BigDecimal

data class AccountDTO constructor(val id: Long, val balance: BigDecimal, val currency: Currency)

data class CreateAccountRequest(val balance: BigDecimal, val currency: Currency)