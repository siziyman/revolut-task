package revolut.home.task.common

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Requires(classes = [NotFoundException::class, ExceptionHandler::class])
class NotFoundExceptionHandler : ExceptionHandler<NotFoundException, HttpResponse<Any>> {
    override fun handle(request: HttpRequest<Any>, exception: NotFoundException): HttpResponse<Any> {
        return HttpResponse.notFound(JsonError(exception.message))
    }
}
