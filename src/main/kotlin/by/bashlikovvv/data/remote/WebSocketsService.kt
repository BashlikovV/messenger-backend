package by.bashlikovvv.data.remote

import by.bashlikovvv.api.dto.websocket.RequestCancelCallTransactionDto
import by.bashlikovvv.api.dto.websocket.RequestUserIpByPhoneRequestDto
import by.bashlikovvv.api.dto.websocket.ResponseUserIpDto
import by.bashlikovvv.data.remote.model.CallTransaction
import by.bashlikovvv.data.remote.model.Connection
import by.bashlikovvv.data.remote.model.WebSocketServiceRequestContract
import by.bashlikovvv.util.exception.TransactionDoesNotExistsException
import by.bashlikovvv.util.exception.UserDisconnectedException
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class WebSocketsService {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    private val callTransactions = Collections.synchronizedSet<CallTransaction?>(LinkedHashSet())

    fun addUser(session: WebSocketSession, userId: UUID) {
        connections.removeIf { it.userId == userId }
        connections += Connection(session, userId)
    }

    fun removeUser(userId: UUID) {
        connections.removeIf { it.userId == userId }
    }

    suspend fun requestUserIpByPhoneNumber(requestedUserUUID: UUID, callerId: UUID): UUID {
        val transaction = callTransactions
            .singleOrNull { it.callerId == callerId && it.calledId == requestedUserUUID }

        return if (transaction != null) {
            transaction.scheduleLivingTime()
            val connection: Connection = connections
                .firstOrNull { it.userId == requestedUserUUID } ?: throw UserDisconnectedException(requestedUserUUID)
            val request = RequestUserIpByPhoneRequestDto(transaction.transactionIdentifier)
            connection.session.outgoing.sendRequest(request)

            transaction.transactionIdentifier
        } else {
            val callTransaction = createCallTransaction(
                callerId = callerId,
                calledId = requestedUserUUID
            )
            val connection: Connection = connections
                .firstOrNull { it.userId == requestedUserUUID } ?: throw UserDisconnectedException(requestedUserUUID)
            callTransactions += callTransaction
            val request = RequestUserIpByPhoneRequestDto(callTransaction.transactionIdentifier)
            connection.session.outgoing.sendRequest(request)

            callTransaction.transactionIdentifier
        }
    }

    suspend fun responseUserIpByTransactionUUID(transactionUUID: UUID, requestedIp: String) {
        val transaction: CallTransaction = callTransactions
            .firstOrNull { it.transactionIdentifier == transactionUUID } ?: throw TransactionDoesNotExistsException()
        transaction.scheduleLivingTime()

        val connection: Connection = connections
            .firstOrNull { it.userId == transaction.callerId } ?: throw UserDisconnectedException(transaction.calledId)
        val request = ResponseUserIpDto(requestedIp)
        connection.session.outgoing.sendRequest(request)
    }

    private suspend fun cancelCallTransactionByUserUUID(userId: UUID) {
        connections
            .firstOrNull { it.userId == userId }
            ?.session
            ?.outgoing
            ?.sendRequest(RequestCancelCallTransactionDto())
    }

    private fun createCallTransaction(
        calledId: UUID, callerId: UUID
    ): CallTransaction {
        val transaction = CallTransaction(
            callerId = callerId,
            calledId = calledId,
            onRemoveAction = {
                CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->  })
                    .launch {
                        cancelCallTransactionByUserUUID(calledId)
                        cancelCallTransactionByUserUUID(callerId)
                    }
                callTransactions.remove(it)
            }
        )
        transaction.scheduleLivingTime()

        return transaction
    }

    private suspend inline fun SendChannel<Frame>.sendRequest(
        request: WebSocketServiceRequestContract
    ) = send(Frame.Text(Json.encodeToString(request.toRequest())))
}