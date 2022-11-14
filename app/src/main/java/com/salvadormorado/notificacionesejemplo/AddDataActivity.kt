package com.salvadormorado.notificacionesejemplo

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AddDataActivity : AppCompatActivity() {

    private var progressBar: ProgressDialog? = null

    private lateinit var button_RegisterData: Button
    private lateinit var textInputLayout_Name: TextInputLayout
    private lateinit var textInputLayout_Lastname: TextInputLayout
    private lateinit var textInputLayout_ControlNumber: TextInputLayout
    private lateinit var textInputLayout_Career: TextInputLayout
    private lateinit var textInputLayout_Email: TextInputLayout
    private lateinit var textInputLayout_Phone: TextInputLayout
    private lateinit var textInputLayout_Address: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        progressBar = ProgressDialog(this@AddDataActivity)
        progressBar!!.setMessage("Subiendo datos, por favor espere...")

        button_RegisterData = findViewById(R.id.button_RegisterData)
        textInputLayout_Name = findViewById(R.id.textInputLayout_Name)
        textInputLayout_Lastname = findViewById(R.id.textInputLayout_Lastname)
        textInputLayout_ControlNumber = findViewById(R.id.textInputLayout_ControlNumber)
        textInputLayout_Career = findViewById(R.id.textInputLayout_Career)
        textInputLayout_Email = findViewById(R.id.textInputLayout_Email)
        textInputLayout_Phone = findViewById(R.id.textInputLayout_Phone)
        textInputLayout_Address = findViewById(R.id.textInputLayout_Address)

        button_RegisterData.setOnClickListener {
            val name = textInputLayout_Name.editText!!.text
            val lastName = textInputLayout_Lastname.editText!!.text
            val controlNumber = textInputLayout_ControlNumber.editText!!.text
            val career = textInputLayout_Career.editText!!.text
            val email = textInputLayout_Email.editText!!.text
            val phone = textInputLayout_Phone.editText!!.text
            val address = textInputLayout_Address.editText!!.text

            if (name.isNotEmpty() && lastName.isNotEmpty() && controlNumber.isNotEmpty() && career.isNotEmpty() &&
                email.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()
            ) {
                val json = JSONObject()
                json.put("name", name)
                json.put("lastName", lastName)
                json.put("controlNumber", controlNumber)
                json.put("career", career)
                json.put("email", email)
                json.put("phone", phone)
                json.put("address", address)
                sendData(json.toString())
            }else{
                Snackbar.make(it, "Llena todos los campos.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun sendData(jsonString:String) {

        progressBar!!.show()

        GlobalScope.launch {
            Dispatchers.IO
            val url = URL("http://192.168.0.105/notificacionespushtopics/altaRealTime.php")

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
                        Toast.makeText(applicationContext, "Se enviaron con exito los datos.", Toast.LENGTH_SHORT).show()
                        textInputLayout_Name.editText!!.setText("")
                        textInputLayout_Lastname.editText!!.setText("")
                        textInputLayout_ControlNumber.editText!!.setText("")
                        textInputLayout_Career.editText!!.setText("")
                        textInputLayout_Email.editText!!.setText("")
                        textInputLayout_Phone.editText!!.setText("")
                        textInputLayout_Address.editText!!.setText("")
                    } else {
                        progressBar!!.dismiss()
                        Toast.makeText(applicationContext, "No se enviaron los datos, intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Log.e("HTTP ERROR DE CONEXIÓN", "")
            }
        }
    }
}