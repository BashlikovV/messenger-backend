package by.bashlikovvv.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Стандарт для отправки сообщений через web сокеты.
 * @param command - алиас отправляемой команды
 * @param data - набор значений
 * */
@Serializable
data class WebSocketsServiceRequest(
    @SerialName("command") val command: String,
    @SerialName("data") val data: Map<String, String>
)

interface WebSocketServiceRequestContract : java.io.Serializable {
    fun toRequest(): WebSocketsServiceRequest
}