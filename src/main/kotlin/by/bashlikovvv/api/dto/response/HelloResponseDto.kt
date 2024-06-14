package by.bashlikovvv.api.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HelloResponseDto(
    @SerialName("body") val body: String
)