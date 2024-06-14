package by.bashlikovvv.di

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.server.application.*
import io.ktor.util.*
import org.koin.ktor.plugin.Koin
import java.io.FileInputStream

typealias DigestFunction = (String) -> ByteArray

fun Application.configureKoin() {
    val serviceAccount = FileInputStream("/home/entexy/Downloads/web-sock-test-firebase-adminsdk-kweqy-a635714523.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()
    val defaultApp: FirebaseApp = FirebaseApp.initializeApp(options)

    install(Koin) {
        modules(
            dataModule
                .apply {
                    single<DigestFunction> {
                        getDigestFunction("SHA-256") { it }
                    }

                    single<FirebaseApp> { defaultApp }

                    single<FirebaseMessaging> { FirebaseMessaging.getInstance(get()) }
                }
        )
    }
}