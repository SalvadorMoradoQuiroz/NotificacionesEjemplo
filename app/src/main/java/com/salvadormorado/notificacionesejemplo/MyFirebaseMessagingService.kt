package com.salvadormorado.notificacionesejemplo
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    override fun onNewToken(s: String) {
        /*
            En este método recibimos el 'token' del dispositivo.
            Lo necesitamos si vamos a comunicarnos con el dispositivo directamente.
        */
        super.onNewToken(s)
        Log.e("NEW_TOKEN MyFirebaseMessagingService", s)
        /*
            A partir de aquí podemos hacer lo que queramos con el token como
            enviarlo al servidor para guardarlo en una B.DD.
            Nosotros no haremos nada con el token porque no nos vamos a comunicar con un sólo
            dispositivo.
         */
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var appRunningBackground: Boolean = false
        val runningAppProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(runningAppProcessInfo)
        appRunningBackground = runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND

        // En este método recibimos el mensaje
        val notificationIntent: Intent
        notificationIntent = if (appRunningBackground) {
            // Qué hacemos si la aplicación está en primer plano
            Intent(this, MainActivity::class.java)
        } else {
            // Qué hacemos si la aplicación está en background
            Intent(this, MainActivity::class.java)
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent:PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Configuramos la notificación para Android Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }
        val notificationId = Random().nextInt(60000)
        // Creamos la notificación en si
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.message) //a resource for your custom small icon
                .setContentTitle(remoteMessage.data["title"]) //the "title" value you sent in your notification
                .setContentText(remoteMessage.data["message"]) //ditto
                .setAutoCancel(true) //dismisses the notification on click
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            notificationId /* ID of notification */,
            notificationBuilder.build()
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        adminChannel.description = CHANNEL_DESCRIPTION
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "NOTIFICATION_CHANNEL"
        private const val CHANNEL_NAME = "com.salvadormorado.notificacionesejemplo"
        private const val CHANNEL_DESCRIPTION = ""
    }
}