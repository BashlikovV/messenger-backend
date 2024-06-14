package by.bashlikovvv.domain.service

import java.util.UUID

interface AuthenticationService {
    fun generateAccessToken(userUUID: UUID): String

    fun generateRefreshToken(): String

    fun checkRefreshToken(refreshToken: String): Boolean
}