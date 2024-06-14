package by.bashlikovvv.data.remote.model

import io.ktor.websocket.*
import java.util.*

data class Connection(
    val session: WebSocketSession,
    val userId: UUID,
)