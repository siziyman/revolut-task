package revolut.home.task.common

import java.lang.RuntimeException

class RestrictedActionException(message: String) : RuntimeException(message)