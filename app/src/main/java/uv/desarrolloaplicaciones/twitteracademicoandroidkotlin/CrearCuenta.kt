package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.Api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityMainBinding

class CrearCuenta : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8000/usuarios/?")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun registrarUsuario(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        fechaNacimiento: String,
        email: String,
        nombreUsuario: String,
        password: String,
        idTipoUsuario: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java)
                .registrarUsuario(
                    "Nombre=$nombre&" +
                        "ApellidoPaterno=$apellidoPaterno&" +
                        "ApellidoMaterno=$apellidoMaterno&" +
                        "FechaNacimiento=$fechaNacimiento&" +
                        "Email=$email" +
                        "NombreUsuario=$nombreUsuario&" +
                        "Password=$password&" +
                        "idTipoUsuario=$idTipoUsuario"
                )
            val response = call.body()
            if (call.isSuccessful) {
                mostrarExito()
            } else {
                mostrarError()
            }
        }
    }

    private fun mostrarError() {
        Toast.makeText(this,"Error al crear la cuenta",Toast.LENGTH_SHORT)
    }

    private fun mostrarExito() {
        Toast.makeText(this,"¡Cuenta creada!",Toast.LENGTH_SHORT)
    }
}