package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityCrearTweetBinding
import java.text.SimpleDateFormat
import java.util.*

class CrearTweet : AppCompatActivity() {
    private var idUsuario = 0
    private lateinit var name: String
    private lateinit var username: String

    private lateinit var binding: ActivityCrearTweetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        binding = ActivityCrearTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recuperando datos de usuarios transferidos de la ventana de home
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        println("idUsuario = $idUsuario\n" +
                "name = $name\n" +
                "username = $username\n")

        Glide.with(this)
            .load("https://ibb.co/L6jFk1W")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.default_photo)
            .into(binding.imgUserphoto)
        binding.btnTwittear.setOnClickListener{
            if(binding.etCuerpotweet.text.toString().length == 0){
                makeToast("Es necesario contenido para el Tweet")
            }else{
                if(binding.etCuerpotweet.text.toString().length > 280){
                    makeToast("Maximo 280 caracteres para el cuerpo del Tweet")
                }else{
                    revisarTamañoTweet()
                }
            }
        }
        val close = findViewById<View>(R.id.toolbar)
        close.setOnClickListener {
            finish()
        }
    }

    private fun revisarTamañoTweet() {
        if(binding.etCuerpotweet.text.toString().length == 0){
            makeToast("Es necesario contenido para el Tweet")
        }else{
            if(binding.etCuerpotweet.text.toString().length > 280){
                makeToast("Maximo 280 caracteres para el cuerpo del Tweet")
            }else{
                var cuerpo: String
                var fechaPublicacion: String

                cuerpo = binding.etCuerpotweet.text.toString()
                fechaPublicacion = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().time)
                crearTweet(cuerpo, fechaPublicacion)
            }
        }
    }

    private fun crearTweet(cuerpo: String, fechaPublicacion: String) {
        val json = JsonObject()

        json.addProperty("Cuerpo", cuerpo)
        json.addProperty("FechaHoraPublicacion", fechaPublicacion)
        json.addProperty("idUsuario", idUsuario)

        val jsonString = json.toString()
        println(jsonString)
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)

                withContext(Dispatchers.Main) {
                    println("toy aqui 0")
                    val response = service.registrarTweet(requestBody)
                    println("toy aqui")
                    if (response.isSuccessful) {
                        println("toy aqui 2")
                        makeToast("Tweet creado!")
                        irPantallaHome()
                    } else {
                        println(response.message())
                        println(response.errorBody())
                        makeToast("Error al crear al Tweet")
                        irPantallaHome()
                    }

                }
                println("toy aqui 3")
            } catch (exception: Exception) {
                println("toy aqui 4")
                exception.printStackTrace()
            }
        }
    }

    private fun irPantallaHome() {
        val intent = Intent(this, HomeActivity::class.java)

        //Comparte algunos datos del usuario para usarlos en la siguiente activity (home)
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putInt("id", idUsuario)
        editor.putString("nombre", name)
        editor.putString("nombreUsuario", username)
        editor.commit()

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}