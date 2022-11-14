package com.salvadormorado.notificacionesejemplo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.e("NEW TOKEN MainActivity", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })

        val imageButton_SendNotification = findViewById<ImageButton>(R.id.imageButton_SendNotification)
        val imageButton_AddData = findViewById<ImageButton>(R.id.imageButton_AddData)

        imageButton_SendNotification.setOnClickListener{
            val intent:Intent = Intent(this@MainActivity, SendNotiActivity::class.java)
            startActivity(intent)
        }

        imageButton_AddData.setOnClickListener{
            val intent:Intent = Intent(this@MainActivity, AddDataActivity::class.java)
            startActivity(intent)
        }

    }

}