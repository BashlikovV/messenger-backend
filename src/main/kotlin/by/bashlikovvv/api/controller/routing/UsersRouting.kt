package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.response.UserResponseDto
import by.bashlikovvv.api.dto.response.UsersResponseDto
import by.bashlikovvv.data.local.UserService
import by.bashlikovvv.util.respondError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.usersRouting() {
    val usersService: UserService by inject()

    getUsers(usersService)
    setFCMToken(usersService)
}

private fun Route.getUsers(userService: UserService) {
    get("/users") {
        call.respond(
            UsersResponseDto(
                users = userService.read()
                    .map { UserResponseDto(it.id, it.password, it.phone, it.fcmToken) }
            )
        )
    }
}

private fun Route.setFCMToken(userService: UserService) {
    post("/user-fcm") {
        val parameters = call.receiveParameters()
        try {
            val userId: UUID = UUID.fromString(parameters["user_id"])
            val fcmToken: String = parameters["fcm_token"]!!
            userService.setFCMToken(userId, fcmToken)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respondError(e)
        }
    }
}