package by.bashlikovvv.domain.service

import by.bashlikovvv.data.remote.model.FCMMessageContract
import java.util.*

interface FCMService {
    suspend fun sendMessage(userId: UUID, message: FCMMessageContract)

    suspend fun sendNotification(userId: UUID)
}