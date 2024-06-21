package by.bashlikovvv.api.controller.socket

import by.bashlikovvv.data.WebSocketsServiceImpl
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.webSocketsRouting() {
    val webSocketsService: WebSocketsServiceImpl by inject()

    connect(webSocketsService)
}

private fun Route.connect(webSocketsService: WebSocketsServiceImpl) {
    webSocket("/connect") {
        val userId = UUID.fromString(call.request.queryParameters["userId"]!!)
        try {
            webSocketsService.addUser(this, userId)
            for (frame in incoming) { }
        } finally {
            webSocketsService.removeUser(userId)
        }
    }
}