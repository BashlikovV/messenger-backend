package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.response.AuthResponseDto
import by.bashlikovvv.api.dto.response.RefreshTokenResponseDto
import by.bashlikovvv.data.local.ExposedUser
import by.bashlikovvv.data.local.UserService
import by.bashlikovvv.domain.service.AuthenticationService
import by.bashlikovvv.util.respond
import by.bashlikovvv.util.respondError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRouting() {
    val usersService: UserService by inject()
    val authorizationService: AuthenticationService by inject()

    registerUser(usersService, authorizationService)
    useUser(usersService, authorizationService)
    refreshToken(authorizationService)
}

private fun Route.registerUser(userService: UserService, authenticationService: AuthenticationService) {
    post("/sign-up") {
        try {
            val parameters = call.receiveParameters()
            val phone = parameters["phone"]!!
            val password = parameters["password"]!!

            val newUser = ExposedUser(password = password, phone = phone)
            val newUserId = userService.create(newUser)
            call.respond(
                HttpStatusCode.Created,
                AuthResponseDto(
                    accessToken = authenticationService.generateAccessToken(newUserId),
                    refreshToken = authenticationService.generateRefreshToken()
                )
            )
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}

private fun Route.useUser(userService: UserService, authenticationService: AuthenticationService) {
    post("/sign-in") {
        try {
            val parameters = call.receiveParameters()
            val phone = parameters["phone"]!!
            val password = parameters["password"]!!

            val requestedUserId = userService.verify(phone, password)
            call.respond(
                HttpStatusCode.OK,
                AuthResponseDto(
                    accessToken = authenticationService.generateAccessToken(requestedUserId!!),
                    refreshToken = authenticationService.generateRefreshToken()
                )
            )
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}

private fun Route.refreshToken(authenticationService: AuthenticationService) {
    post("/refresh-token") {
        try {
            val parameters = call.receiveParameters()
            val refreshToken = parameters["refresh_token"]!!

            respond(
                isCorrect = { authenticationService.checkRefreshToken(refreshToken) },
                onCorrect = {
                    call.respond(
                        HttpStatusCode.OK,
                        RefreshTokenResponseDto(authenticationService.generateRefreshToken())
                    )
                },
                onIncorrect = {
                    call.respond(HttpStatusCode.BadRequest)
                }
            )
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}