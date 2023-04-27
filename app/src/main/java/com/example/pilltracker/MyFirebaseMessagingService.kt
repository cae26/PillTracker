package com.example.pilltracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

val channelId = "my_channel_id"
val channelName = "com.example.pilltracker"

//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if (remoteMessage.getNotification() != null) {
//                generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
//            }
//        }
//
//    fun getRemoteView(title: String, message: String): RemoteViews {
//        val remoteView = RemoteViews("com.example.pilltracker",R.layout.notification)
//        remoteView.setTextViewText(R.id.title,title)
//        remoteView.setTextViewText(R.id.notificationContent,message)
//        remoteView.setImageViewResource(R.id.appLogo,R.drawable.ic_stat_ic_notification)
//        return remoteView
//    }
//    fun generateNotification(title: String, message: String) {
//        // create an intent for the notification
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//        // create the notification
//        var builder: NotificationCompat.Builder= NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//        builder = builder.setContent(getRemoteView(title,message))
//
//        // Register the channel with the system
//        val notificationManager: NotificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        // Create notification channel for API 26 and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val notificationChannel = NotificationChannel(channelId, channelName, importance)
//            notificationManager.createNotificationChannel(notificationChannel)
//            }
//        notificationManager.notify(Random().nextInt(1000),builder.build())
//
//
//
//
//
//    }
//
//    }



class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getNotification() != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    fun generateNotification(title: String, message: String) {
        // create an intent for the notification
        val intent = Intent(this, LogFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // create the intent for the "Taken" button click
        val takenIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "taken"
        }
        val takenPendingIntent =
            PendingIntent.getBroadcast(this, 0, takenIntent, PendingIntent.FLAG_ONE_SHOT)

        // create the intent for the "Not Taken" button click
        val notTakenIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "not_taken"
        }
        val notTakenPendingIntent =
            PendingIntent.getBroadcast(this, 0, notTakenIntent, PendingIntent.FLAG_ONE_SHOT)

        // create the notification
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                )
                .addAction(R.drawable.ic_check_circle_outline, "Taken", takenPendingIntent)
                .addAction(R.drawable.ic_cancel_outline, "Not Taken", notTakenPendingIntent)

        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create notification channel for API 26 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(Random().nextInt(1000), builder.build())
    }
}








