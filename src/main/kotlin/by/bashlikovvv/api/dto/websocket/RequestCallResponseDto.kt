package by.bashlikovvv.api.dto.websocket

import by.bashlikovvv.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RequestCallResponseDto(
    @Serializable(UUIDSerializer::class)
    @SerialName("transactionIdentifier")
    val transactionIdentifier: UUID
)