package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityEditarTweetBinding
import java.text.SimpleDateFormat
import java.util.*

class EditarTweetActivity : AppCompatActivity() {
    private var idTweet: Int = 0
    private var idUsuario: Int = 0
    private lateinit var token: String
    private lateinit var binding: ActivityEditarTweetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        binding = ActivityEditarTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperarDatosUsuario()
        mostrarInfoUsuario()
        mostrarInfoTweet()
        cargarListeners()
    }

    private fun mostrarInfoUsuario() {
        Glide.with(this)
            .load("https://ibb.co/L6jFk1W")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.default_photo)
            .into(binding.imgUserphoto)
    }

    private fun mostrarInfoTweet() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.getTweet(token, idTweet)

                runOnUiThread {
                    if (response.respuesta == "Y") {
                        binding.etCuerpotweet.setText(response.cuerpo)
                    } else if (response.respuesta == "N") {
                        mostrarMensaje("No se encontro el tweet")
                    }
                }
             } catch (excepcion: Exception) {
                println("Excepcion EDITARTWEET_MOSTRAR_INFO:")
                excepcion.printStackTrace()
             }
        }
    }

    private fun recuperarDatosUsuario() {
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPref.getInt("id", 0)
        token = sharedPref.getString("token", "").toString()
        idTweet = sharedPref.getInt("idTweet", 0)
    }

    private fun cargarListeners() {
        binding.btnEditarTweet.setOnClickListener{
            modificarTweet()
        }
        binding.toolbar.setOnClickListener {
            finish()
        }
    }

    private fun modificarTweet() {
        if(camposValidos()) {
            val json = JsonObject()

            json.addProperty("Cuerpo", binding.etCuerpotweet.text.toString())
            json.addProperty(
                "FechaHoraPublicacion",
                SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().time)
            )
            json.addProperty("idUsuario", idUsuario)

            val jsonString = json.toString()
            val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val service = ServiceBuilder.buildService(APIService::class.java)
                    val response = service.modificarTweet(token, requestBody, idTweet)

                    runOnUiThread {
                        if (response.respuesta == "A") {
                            mostrarMensaje("Tweet modificado")
                            finish()
                        } else if (response.respuesta == "N") {
                            mostrarMensaje("No se ha podido modificar el tweet")
                        }
                    }
                } catch (excepcion: Exception) {
                    println("Excepcion EDITARTWEET_MODIFICAR:")
                    excepcion.printStackTrace()
                }
            }
        }
    }

    private fun camposValidos(): Boolean {
        var valido = true

        if(binding.etCuerpotweet.text.toString().isEmpty()){
            mostrarMensaje("Es necesario contenido para el Tweet")
            valido = false
        }else{
            if(binding.etCuerpotweet.text.toString().length > 280){
                mostrarMensaje("Maximo 280 caracteres para el cuerpo del Tweet")
                valido = false
            }
        }

        return valido
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}