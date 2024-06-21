package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.websocket.RequestCallResponseDto
import by.bashlikovvv.data.WebSocketsServiceImpl
import by.bashlikovvv.data.remote.model.FCMNotification
import by.bashlikovvv.domain.service.FCMService
import by.bashlikovvv.util.exception.UserDisconnectedException
import by.bashlikovvv.util.respondError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.callsRouting() {
    val webSocketsService: WebSocketsServiceImpl by inject()
    val fcmService: FCMService by inject()

    requestCallByNumber(webSocketsService, fcmService)
    responseCallIp(webSocketsService, fcmService)
}

/**
 * Отправляет пользователю запрос на получение ip адреса.
 * Если пользователь активен и присутствует соединение по web сокету, то через web сокет, иначе через [FCMService]
 * */
private fun Route.requestCallByNumber(
    webSocketsService: WebSocketsServiceImpl,
    fcmService: FCMService
) {
    post("/requestCall") {
        try {
            val parameters = call.receiveParameters()
            val callerId = UUID.fromString(parameters["call_from"])
            val requestedUserId = UUID.fromString(parameters["call_to"])
            val transactionIdentifier = webSocketsService.requestUserIpByPhoneNumber(requestedUserId, callerId)

            call.respond(HttpStatusCode.OK, RequestCallResponseDto(transactionIdentifier))
        } catch (e: UserDisconnectedException) {
            fcmService.sendMessage(
                userId = e.requestedUserId,
                message = FCMNotification(
                    title = "test title",
                    body = "test body",
                    image = null
                )
            )
            call.respondError(e)
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}

/**
 * Возвращает пользователю ответ с запрошенным ip адресом.
 * Если пользователь активен и присутствует соединение по web сокету, то через web сокет, иначе через [FCMService]
 * */
private fun Route.responseCallIp(
    webSocketsService: WebSocketsServiceImpl,
    fcmService: FCMService
) {
    post("/responseCall") {
        try {
            val parameters = call.receiveParameters()
            val transactionUUID = UUID.fromString(parameters["transaction_uuid"]!!)
            val port = parameters["requested_port"]!!
            val requestedIp = "${call.request.origin.remoteAddress}:$port"
            webSocketsService.responseUserIpByTransactionUUID(transactionUUID, requestedIp)
            call.respond(HttpStatusCode.OK)
        } catch (e: UserDisconnectedException) {
            fcmService.sendMessage(
                userId = e.requestedUserId,
                message = FCMNotification(
                    title = "test title",
                    body = "test body",
                    image = null
                )
            )
            call.respondError(e)
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}