package by.bashlikovvv.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errorCode: Int,
    val errorMessage: String
)