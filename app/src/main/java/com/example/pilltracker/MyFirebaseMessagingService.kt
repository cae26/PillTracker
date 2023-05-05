package com.example.pilltracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
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
//            generateNotification(
//                remoteMessage.notification!!.title!!,
//                remoteMessage.notification!!.body!!
//                        remoteMessage.notification!!.context!!
//            )
//        }
//
//    }
////    fun getRemoteView(title: String, message: String): RemoteViews {
////        val remoteView = RemoteViews("com.example.pilltracker",R.layout.notification)
////        remoteView.setTextViewText(R.id.title,title)
////        remoteView.setTextViewText(R.id.notificationContent,message)
////        remoteView.setImageViewResource(R.id.appLogo,R.drawable.ic_stat_ic_notification)
////        remoteView.Button(R.id.takenButton,R.layout.notification)
////        remoteView.Button(R.id.notTakenButton,R.layout.notification)
////        return remoteView
////    }
//
//    fun getRemoteView(title: String, message: String,context: Context): RemoteViews {
//        val remoteView = RemoteViews("com.example.pilltracker",R.layout.notification)
//       remoteView.setTextViewText(R.id.title,title)
//        remoteView.setTextViewText(R.id.notificationContent,message)
//        remoteView.setImageViewResource(R.id.appLogo,R.drawable.ic_stat_ic_notification)
//        // Set the click listeners for the buttons
//        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
//            action = "com.example.pilltracker.TAKEN"
//        }
//        val takenPendingIntent = PendingIntent.getBroadcast(context, 0, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        remoteView.setOnClickPendingIntent(R.id.takenButton, takenPendingIntent)
//
//        val notTakenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
//            action = "com.example.pilltracker.NOT_TAKEN"
//        }
//        val notTakenPendingIntent = PendingIntent.getBroadcast(context, 0, notTakenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        remoteView.setOnClickPendingIntent(R.id.notTakenButton, notTakenPendingIntent)
//
//        return remoteView
//    }
//
////    fun generateNotification(title: String, message: String,context: Context) {
////        // create an intent for the notification
////        val intent = Intent(this, MainActivity::class.java).apply {
////            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
////        }
////        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
////
////        // create the notification
////        var builder: NotificationCompat.Builder= NotificationCompat.Builder(applicationContext, channelId)
////            .setSmallIcon(R.drawable.ic_stat_ic_notification)
////            .setContentTitle(title)
////            .setContentText(message)
////
////            .setAutoCancel(true)
////            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
////            .setOnlyAlertOnce(true)
////            .setContentIntent(pendingIntent)
////        builder = builder.setContent(getRemoteView(title,message,context))
////
////        // Register the channel with the system
////        val notificationManager: NotificationManager =
////            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        // Create notification channel for API 26 and above
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            val importance = NotificationManager.IMPORTANCE_HIGH
////            val notificationChannel = NotificationChannel(channelId, channelName, importance)
////            notificationManager.createNotificationChannel(notificationChannel)
////            }
////        notificationManager.notify(Random().nextInt(1000),builder.build())
//
//
//    fun generateNotification(title: String, message: String, context: Context) {
//        // Create an intent for the notification
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//        // Create the custom RemoteViews for the notification
//        val remoteViews = getRemoteView(message, title, context)
//
//        // Create the notification
//        val builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//            .setCustomContentView(remoteViews) // Set the custom RemoteViews for the notification
//
//        // Register the notification channel with the system
//        val notificationManager: NotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val notificationChannel = NotificationChannel(channelId, channelName, importance)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//
//        // Show the notification
//        notificationManager.notify(Random().nextInt(1000), builder.build())
//    }
//
//
//
//
//}
//
//

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!,
                "",
                applicationContext
            )
        }
    }

    fun getRemoteView(title: String, message: String, user: String, context: Context): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.notificationContent, message)
        remoteView.setImageViewResource(R.id.appLogo, R.drawable.app_logo_playstore)

        // Set the click listeners for the buttons
        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.example.pilltracker.TAKEN"
            putExtra("title", title)
            putExtra("message", message)
            putExtra("user", user)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        remoteView.setOnClickPendingIntent(R.id.takenButton, takenPendingIntent)

        val notTakenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.example.pilltracker.NOT_TAKEN"
            putExtra("title", title)
            putExtra("message", message)
            putExtra("user", user)
        }
        val notTakenPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            notTakenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        remoteView.setOnClickPendingIntent(R.id.notTakenButton, notTakenPendingIntent)

        return remoteView
    }


    fun generateNotification(title: String, message: String, user: String, context: Context) {
        // Create an intent for the notification
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        // Create the custom RemoteViews for the notification
        val remoteViews = getRemoteView(title, message, user, context)

        // Create the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteViews) // Set the custom RemoteViews for the notification
            .setCustomBigContentView(remoteViews) // Set the custom RemoteViews for the expanded notification

        // Register the notification channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Show the notification
        notificationManager.notify(Random().nextInt(1000), builder.build())
    }


}













