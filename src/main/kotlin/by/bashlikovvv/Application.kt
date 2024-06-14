package by.bashlikovvv

import by.bashlikovvv.api.controller.configureRouting
import by.bashlikovvv.api.controller.configureSerialization
import by.bashlikovvv.api.controller.configureSockets
import by.bashlikovvv.di.configureKoin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.io.File


fun main(args: Array<String>) {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("key0") {
            password = "foobar"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "012345")

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "key0",
            keyStorePassword = { "012345".toCharArray() },
            privateKeyPassword = { "foobar".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }

    embeddedServer(Netty, environment).start(true)
}

fun Application.module() {
    configureKoin()
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .build()
            )
            validate { JWTPrincipal(it.payload) }
        }
    }
    configureSerialization()
    configureSockets()
    configureRouting()
}