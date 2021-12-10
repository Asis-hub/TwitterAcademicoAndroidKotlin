package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if(!camposVacios()) {
                logearse(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            }
        }
    }

    private fun camposVacios(): Boolean {
        var camposVacios = false

        if (binding.etUsername.text.isEmpty()) {
            camposVacios = true
            Toast.makeText(this,"Hay un campo de texto vacio",Toast.LENGTH_SHORT).show()
        } else if (binding.etPassword.text.isEmpty()) {
            camposVacios = true
            Toast.makeText(this,"Hay un campo de texto vacio",Toast.LENGTH_SHORT).show()
            //TODO cambiar el color del edittext
            // binding.etPassword.sup
        }

        return camposVacios
    }

    private fun logearse(usuario: String, password: String) {
        val json = JsonObject()
        json.addProperty("NombreUsuario", usuario)
        json.addProperty("Contraseña", password)

        val jsonString = json.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.logearse(requestBody)
                runOnUiThread {
                    if (response.respuesta == "Logeado" ) {
                        mostrarMensaje("¡Bienvenido!")
                        irAPantallaPrincipal(response)
                    } else if(response.respuesta == "Favor de verificar su informacion"){
                        mostrarMensaje("No existe un usuario con ese usuario y/o contraseña")
                    }
                }
            } catch (exception: Exception) {
                println("Excepcion:")
                mostrarMensaje(resources.getString(R.string.mensajeError))
                exception.printStackTrace()
            }
        }
    }

    private fun irAPantallaPrincipal(body: Usuario) {
        val intent = Intent(this, HomeActivity::class.java)
        
        //Comparte algunos datos del usuario para usarlos en la siguiente activity (home)
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putInt("id", body.idUsuario)
        editor.putString("nombre", body.nombre)
        editor.putString("nombreUsuario", body.nombreUsuario)
        editor.commit()

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}
