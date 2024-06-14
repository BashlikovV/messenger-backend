package by.bashlikovvv.api.dto.websocket

import by.bashlikovvv.data.remote.model.WebSocketServiceRequestContract
import by.bashlikovvv.data.remote.model.WebSocketsServiceRequest
import kotlinx.serialization.Serializable

@Serializable
class RequestCancelCallTransactionDto : WebSocketServiceRequestContract {
    override fun toRequest(): WebSocketsServiceRequest {
        return WebSocketsServiceRequest(
            command = "RequestCancelCallTransactionDto",
            data = mapOf()
        )
    }
}