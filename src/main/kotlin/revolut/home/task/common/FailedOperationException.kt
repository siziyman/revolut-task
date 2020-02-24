package revolut.home.task.common

import java.lang.RuntimeException

class FailedOperationException(message: String) : RuntimeException(message)