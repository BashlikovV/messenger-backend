package by.bashlikovvv.domain.service

import java.util.UUID

interface FCMService {
    suspend fun sendNotification(userId: UUID)
}