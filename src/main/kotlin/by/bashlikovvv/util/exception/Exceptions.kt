package by.bashlikovvv.util.exception

import java.util.UUID

open class ForbiddenException(message: String, cause: Throwable? = null) : Exception(message, cause)

class TransactionDoesNotExistsException : IllegalArgumentException("Transaction with this identifier does not exists")

class UserDisconnectedException(val requestedUserId: UUID) : IllegalArgumentException("user with this identifier does not exists")