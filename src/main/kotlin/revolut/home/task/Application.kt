package revolut.home.task

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("revolut.home.task")
                .mainClass(Application.javaClass)
                .start()
    }
}