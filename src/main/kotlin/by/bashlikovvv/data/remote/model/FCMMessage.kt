package by.bashlikovvv.data.remote.model

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FCMMessage(
    @SerialName("command") private val command: String,
    @SerialName("data") private val data: Map<String, String>,
) : FCMMessageContract {
    override fun toMessage(token: String?): Message = Message.builder()
        .setToken(token)
        .putData("command", command)
        .run {
            if (command == "notification") {
                setNotification(
                    Notification.builder()
                        .setBody(data["body"])
                        .setTitle(data["title"])
                        .setImage(data["image"])
                        .build()
                )
            } else {
                putAllData(data)
            }
        }
        .build()
}

data class FCMNotification(
    private val title: String,
    private val body: String,
    private val image: String? = null
) : FCMMessageContract {
    override fun toMessage(token: String?): Message = Message.builder()
        .setToken(token)
        .putData("command", "notification")
        .setNotification(
            Notification.builder()
                .setBody(body)
                .setTitle(title)
                .setImage(image)
                .build()
        )
        .build()
}

interface FCMMessageContract {
    fun toMessage(token: String?): Message
}