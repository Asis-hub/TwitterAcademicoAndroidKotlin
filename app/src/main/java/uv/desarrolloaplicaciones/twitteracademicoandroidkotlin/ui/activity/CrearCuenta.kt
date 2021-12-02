package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityCrearCuentaBinding
import java.util.*

class CrearCuenta : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCuentaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etFecNac.setOnClickListener{
            mostrarDialogoFecha()
        }
        binding.btnCreate.setOnClickListener {
            var nombre: String = "nombre"
            var apellidoPaterno: String = "apellidoPaterno"
            var apellidoMaterno: String = "apellidoMaterno"
            var fechaNacimiento: String = "${Calendar.getInstance().get(Calendar.YEAR)}-" +
                                        "${Calendar.getInstance().get(Calendar.MONTH)}-" +
                                        "${Calendar.getInstance().get(Calendar.DAY_OF_WEEK)}"
            var email: String = "email"
            var nombreUsuario: String = "nombreusuario"
            var password: String = "password"
            var idTipoUsuario: Int = 1

            if (verificarCamposTextoVacios()) {
                nombre = binding.etName.text.toString()
                apellidoPaterno = binding.etAPaterno.text.toString()
                apellidoMaterno = binding.etAMaterno.text.toString()
                fechaNacimiento = binding.etFecNac.text.toString()
                email = binding.etEmail.text.toString()
                nombreUsuario = binding.etUsername.text.toString()
                password = binding.etPassword.text.toString()

                registrarUsuario(
                    nombre,
                    apellidoPaterno,
                    apellidoMaterno,
                    fechaNacimiento,
                    email,
                    nombreUsuario,
                    password,
                    idTipoUsuario
                )
            } else {
                Toast.makeText(this, "Llena todos los campos de texto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoFecha() {
        val newFragment = DatePickerFragment.newInstance(
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = "$day / ${month + 1} / $year"
                binding.etFecNac.setText(selectedDate)
            })
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun verificarCamposTextoVacios(): Boolean {
        return binding.etName.text.isNotEmpty() &&
            binding.etAPaterno.text.isNotEmpty() &&
            binding.etAMaterno.text.isNotEmpty() &&
            binding.etFecNac.text.isNotEmpty() &&
            binding.etEmail.text.isNotEmpty() &&
            binding.etUsername.text.isNotEmpty() &&
            binding.etPassword.text.isNotEmpty()
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

        val json = JsonObject()
        json.addProperty("idUsuario", 0)
        json.addProperty("Nombre", nombre)
        json.addProperty("ApellidoPaterno", apellidoPaterno)
        json.addProperty("ApellidoMaterno", apellidoMaterno)
        json.addProperty("FechaNacimiento", fechaNacimiento)
        json.addProperty("Email", email)
        json.addProperty("NombreUsuario", nombreUsuario)
        json.addProperty("Password", password)
        json.addProperty("idTipoUsuario", idTipoUsuario)

        val jsonString = json.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)

                withContext(Dispatchers.Main) {
                    val response = service.registrarUsuario(requestBody)

                    if (response.isSuccessful) {
                        mostrarExito()
                    } else {
                        println(response.message())
                        println(response.errorBody())
                        mostrarError()
                    }

                }
            } catch (exception: Exception) {
                println(exception.stackTrace)
            }
        }
    }

    private fun mostrarError() {
        Toast.makeText(this,"Error al crear la cuenta",Toast.LENGTH_SHORT).show()
    }

    private fun mostrarExito() {
        Toast.makeText(this,"¡Cuenta creada!",Toast.LENGTH_SHORT).show()
    }
}