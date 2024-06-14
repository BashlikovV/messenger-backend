package by.bashlikovvv.data.remote

import by.bashlikovvv.data.local.UserService
import by.bashlikovvv.domain.service.FCMService
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FCMService(
    private val firebaseMessaging: FirebaseMessaging,
    private val userService: UserService
) : FCMService {
    override suspend fun sendNotification(userId: UUID): Unit =
        withContext(Dispatchers.IO) {
            firebaseMessaging.sendAsync(
                Message.builder()
                    .setToken(userService.getFCMToken(userId))
                    .setNotification(
                        Notification.builder()
                            .setTitle("test title")
                            .setBody("test body")
                            .build()
                    )
                    .build()
            )
        }
}