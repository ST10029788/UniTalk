package com.scriptsquad.unitalk.Utilities


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Notices_Page.activities.Notices_Activity
import com.scriptsquad.unitalk.Marketplace.activities.Chat_Activity
import java.util.Random

class Firebase_Cloud_Messaging_Service : FirebaseMessagingService() {

    private companion object {
        private const val TAG = "MY_FCM_TAG"

        //Notification Channel ID

        private const val NOTIFICATION_CHANNEL_ID = "UNITALK_CHANNEL_TO"

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract relevant data, handling potential null cases
        val title = remoteMessage.notification?.title ?: ""
        val body = remoteMessage.notification?.body ?: ""
        val senderUid = remoteMessage.data["senderUid"] ?: ""
        val notificationType = remoteMessage.data["notificationType"] ?: ""

        Log.d(
            TAG,
            "onMessageReceived: title: $title, body: $body, senderUid: $senderUid, notificationType: $notificationType"
        )

        Log.d(TAG, "onMessageReceived: notificationType: $notificationType")

        // Choose appropriate notification display based on type
        if (notificationType == "NOTIFICATION_TYPE_NEW_MESSAGE") {
            showChatNotification(title, body, senderUid)
        } else {
            showGeneralNotification(title, body)

        }

    }

    private fun showChatNotification(
        notificationTitle: String,
        notificationDescription: String,
        senderUid: String
    ) {
        // generate random number for notification id
        val notificationId = Random().nextInt(3000)


        //init notification Manger
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // function call to setup notification channel in android 0 and above

        setupNotificationChannel(notificationManager)

        val intent = Intent(this, Chat_Activity::class.java)
        intent.putExtra("receiptUid", senderUid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // pending intent to add in Notification
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logonoti)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun showGeneralNotification(
        notificationTitle: String,
        notificationDescription: String
    ) {
        val notificationId = Random().nextInt(3000)

        // Choose appropriate activity for general notification intent
        val intent = Intent(this, Notices_Activity::class.java) // Replace with the relevant activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logonoti)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun setupNotificationChannel(notificationManager: NotificationManager) {


        // Android 8.0 all notification Must be Assigned to a Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(

                NOTIFICATION_CHANNEL_ID,
                "Chat Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.description = "Show Chats Notifications"
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)

        }

    }

}