package com.example.collegealertapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.collegealertapplication.R
import com.example.collegealertapplication.data.local.NotificationDatabase
import com.example.collegealertapplication.data.local.NotificationEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "college_alert_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "New Alert"
        val message = remoteMessage.notification?.body ?: "Check the app for more info"

        // Store in database
        val db = NotificationDatabase.getInstance(applicationContext)
        GlobalScope.launch {
            db.notificationDao().insert(
                NotificationEntity(
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Show local notification
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "College Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for college alert notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
