package by.bashlikovvv.data

import by.bashlikovvv.domain.service.AuthenticationService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import java.util.*

class AuthorizationServiceImpl : AuthenticationService {
    private val secret = "access secret"

    private val refreshSecret = "refresh secret"

    override fun generateAccessToken(userUUID: UUID): String {
        return JWT.create()
            .withClaim("user_uuid", userUUID.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 5))
            .sign(Algorithm.HMAC256(secret))
    }

    override fun generateRefreshToken(): String {
        val expirationDate = Calendar.getInstance()
        expirationDate.add(Calendar.MONTH, 3)

        return JWT.create()
            .withExpiresAt(expirationDate.time)
            .sign(Algorithm.HMAC256(refreshSecret))
    }

    override fun checkRefreshToken(refreshToken: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC256(refreshSecret))
                .build()
                .verify(refreshToken)

            true
        } catch (e: TokenExpiredException) {
            false
        } catch (e: RuntimeException) {
            false
        }
    }
}