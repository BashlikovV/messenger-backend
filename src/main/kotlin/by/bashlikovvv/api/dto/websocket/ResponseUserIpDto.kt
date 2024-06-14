package by.bashlikovvv.api.dto.websocket

import by.bashlikovvv.data.remote.model.WebSocketServiceRequestContract
import by.bashlikovvv.data.remote.model.WebSocketsServiceRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUserIpDto(
    @SerialName("userIp") val userIp: String
) : WebSocketServiceRequestContract {
    override fun toRequest(): WebSocketsServiceRequest {
        return WebSocketsServiceRequest(
            command = "ResponseUserIpDto",
            data = mapOf("userIp" to userIp)
        )
    }
}