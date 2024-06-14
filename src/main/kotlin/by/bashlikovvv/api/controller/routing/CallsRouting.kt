package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.websocket.RequestCallResponseDto
import by.bashlikovvv.data.remote.WebSocketsService
import by.bashlikovvv.domain.service.FCMService
import by.bashlikovvv.util.exception.UserDisconnectedException
import by.bashlikovvv.util.respondError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.callsRouting() {
    val webSocketsService: WebSocketsService by inject()
    val fcmService: FCMService by inject()

    requestCallByNumber(webSocketsService, fcmService)
    responseCallIp(webSocketsService, fcmService)
}

private fun Route.requestCallByNumber(
    webSocketsService: WebSocketsService,
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
            fcmService.sendNotification(e.requestedUserId)
            call.respondError(e)
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}

private fun Route.responseCallIp(
    webSocketsService: WebSocketsService,
    fcmService: FCMService
) {
    post("/responseCall") {
        try {
            val parameters = call.receiveParameters()
            val transactionUUID = UUID.fromString(parameters["transaction_uuid"]!!)
            val requestedIp = parameters["requested_ip"]!!
            webSocketsService.responseUserIpByTransactionUUID(transactionUUID, requestedIp)
            call.respond(HttpStatusCode.OK)
        } catch (e: UserDisconnectedException) {
            fcmService.sendNotification(e.requestedUserId)
            call.respondError(e)
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}