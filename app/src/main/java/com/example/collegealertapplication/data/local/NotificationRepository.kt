package com.example.collegealertapplication.data.local

import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {
    suspend fun insert(notification: NotificationEntity) = dao.insert(notification)
    fun getAllNotifications(): Flow<List<NotificationEntity>> = dao.getAllNotifications()
}
