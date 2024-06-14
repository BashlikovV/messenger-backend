package by.bashlikovvv.api.dto.websocket

import by.bashlikovvv.data.remote.model.WebSocketServiceRequestContract
import by.bashlikovvv.data.remote.model.WebSocketsServiceRequest
import by.bashlikovvv.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RequestUserIpByPhoneRequestDto(
    @Serializable(UUIDSerializer::class)
    @SerialName("transactionIdentifier") val transactionIdentifier: UUID
) : WebSocketServiceRequestContract {
    override fun toRequest(): WebSocketsServiceRequest {
        return WebSocketsServiceRequest(
            command = "RequestUserIpByPhoneRequestDto",
            data = mapOf("transactionIdentifier" to "$transactionIdentifier")
        )
    }
}