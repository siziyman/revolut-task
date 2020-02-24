package revolut.home.task.common

import java.lang.RuntimeException

class NotFoundException(message: String) : RuntimeException(message) {
}