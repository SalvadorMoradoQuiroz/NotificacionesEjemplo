package com.salvadormorado.notificacionesejemplo

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SendNotiActivity : AppCompatActivity() {

    private var progressBar: ProgressDialog? = null

    private lateinit var textInputLayout_Title:TextInputLayout
    private lateinit var textInputLayout_Message:TextInputLayout
    private lateinit var button_SendNoti:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_noti)

        progressBar = ProgressDialog(this@SendNotiActivity)
        progressBar!!.setMessage("Enviando notificación, por favor espere...")

        textInputLayout_Title = findViewById(R.id.textInputLayout_Title)
        textInputLayout_Message = findViewById(R.id.textInputLayout_Message)
        button_SendNoti = findViewById(R.id.button_SendNoti)

        button_SendNoti.setOnClickListener{
            if(textInputLayout_Title.editText!!.text.isNotEmpty() && textInputLayout_Message.editText!!.text.isNotEmpty()){
                val json = JSONObject()
                json.put("titulo", textInputLayout_Title.editText!!.text)
                json.put("mensaje", textInputLayout_Message.editText!!.text)
                sendNotification(json.toString())
            }else{
                Snackbar.make(it, "Llena todos los campos.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun sendNotification(jsonString:String) {

        progressBar!!.show()

        GlobalScope.launch {
            Dispatchers.IO
            val url = URL("http://192.168.0.105/notificacionespushtopics/notiPushTopics.php")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            httpURLConnection.setRequestProperty("Accept", "application/json")
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = true

            val outputStreamWritter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWritter.write(jsonString)
            outputStreamWritter.flush()

            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader().use { it.readLine() }
                withContext(Dispatchers.Main) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val gsonAux = gson.toJson(JsonParser.parseString(response))
                    Log.e("response", response.toString())
                    if (response.toString().equals("{success:1}")) {
                        progressBar!!.dismiss()
                        Toast.makeText(applicationContext, "Se envió con exito la notificación.", Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar!!.dismiss()
                        Toast.makeText(applicationContext, "No se envió la notificación, intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Log.e("HTTP ERROR DE CONEXIÓN", "")
            }
        }
    }

}