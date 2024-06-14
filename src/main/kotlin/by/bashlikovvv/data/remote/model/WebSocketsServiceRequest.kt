package by.bashlikovvv.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketsServiceRequest(
    @SerialName("command") val command: String,
    @SerialName("data") val data: Map<String, String>
)

interface WebSocketServiceRequestContract : java.io.Serializable {
    fun toRequest(): WebSocketsServiceRequest
}