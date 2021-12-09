package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.logearse(usuario, password)
                runOnUiThread {
                    if (response != null) {
                        if (response.isNotEmpty()) {
                            mostrarMensaje("¡Bienvenido!")
                            irAPantallaPrincipal(response[0])
                        } else {
                            mostrarMensaje("No existe un usuario con ese usuario y/o contraseña")
                        }
                    } else {
                        mostrarMensaje("Error al ingresar")
                    }
                }
            } catch (exception: Exception) {
                println("Excepcion:")
                mostrarMensaje("Hubo un problema de conexión, intenta más tarde")
                exception.printStackTrace()
            }
        }
    }

    private fun irAPantallaPrincipal(body: Usuario) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("id",body!!.idUsuario)
        intent.putExtra("nombre",body!!.nombre)
        intent.putExtra("nombreUsuario", body!!.nombreUsuario)
        startActivity(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}
