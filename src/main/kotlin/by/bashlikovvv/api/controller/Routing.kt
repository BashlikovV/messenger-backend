package by.bashlikovvv.api.controller

import by.bashlikovvv.api.controller.routing.authRouting
import by.bashlikovvv.api.controller.routing.callsRouting
import by.bashlikovvv.api.controller.routing.usersRouting
import by.bashlikovvv.api.dto.response.HelloResponseDto
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api/v2/") {
            /* Just for test */
            get("hello") {
                call.respond(HelloResponseDto("Hello World!"))
            }

            authenticate("auth-jwt") { }
            authRouting()
            callsRouting()
            usersRouting()
        }
    }
}
