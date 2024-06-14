package by.bashlikovvv.util

import by.bashlikovvv.domain.model.ErrorResponse
import by.bashlikovvv.util.exception.ForbiddenException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

suspend inline fun respond(
    isCorrect: () -> Boolean,
    crossinline onCorrect: suspend () -> Unit,
    crossinline onIncorrect: suspend () -> Unit
) {
    if (isCorrect.invoke()) {
        onCorrect()
    } else {
        onIncorrect()
    }
}

val Exception.httpCode: HttpStatusCode
    get() =
        when (this) {
            is IllegalArgumentException -> HttpStatusCode.BadRequest
            is NotFoundException -> HttpStatusCode.NotFound
            is ForbiddenException -> HttpStatusCode.Forbidden
            else -> throw this
        }

val Exception.httpResponse
    get() =
        ErrorResponse(
            errorCode = httpCode.value,
            errorMessage = message ?: "Error"
        )

suspend fun ApplicationCall.respondError(exception: Exception) {
    respond(exception.httpCode, exception.httpResponse)
}