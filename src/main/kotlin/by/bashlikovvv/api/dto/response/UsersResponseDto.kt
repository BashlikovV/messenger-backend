package by.bashlikovvv.api.dto.response

import by.bashlikovvv.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UsersResponseDto(
    val users: List<UserResponseDto>
)

@Serializable
data class UserResponseDto(
    @Serializable(UUIDSerializer::class)
    @SerialName("id") val id: UUID = UUID.randomUUID(),
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("fcm-token") val token: String?
)