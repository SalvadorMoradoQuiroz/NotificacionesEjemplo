package com.salvadormorado.notificacionesejemplo

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging

class NotificationSuscriptor : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic("topic_general")
    }
}