package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityCrearCuentaBinding
import java.io.ByteArrayOutputStream
import java.util.*

class CrearCuenta : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCuentaBinding
    private var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            binding.ivSubirFoto.setImageURI(data?.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarListener()
    }

    private fun cargarListener() {
        binding.ivSubirFoto.setOnClickListener {
            seleccionarImagen()
        }
        binding.etFecNac.setOnClickListener{
            mostrarDialogoFecha()
        }
        binding.btnCreate.setOnClickListener {
            var nombre = "nombre"
            var apellidoPaterno = "apellidoPaterno"
            var apellidoMaterno = "apellidoMaterno"
            var fechaNacimiento: String = "${Calendar.getInstance().get(Calendar.YEAR)}-" +
                    "${Calendar.getInstance().get(Calendar.MONTH)}-" +
                    "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}"
            var email = "email"
            var nombreUsuario = "nombreusuario"
            var password = "password"
            var idTipoUsuario = 1
            //var fotoPerfil: Bitmap

            if (camposTextoLlenados()) {
                nombre = binding.etName.text.toString()
                apellidoPaterno = binding.etAPaterno.text.toString()
                apellidoMaterno = binding.etAMaterno.text.toString()
                fechaNacimiento = binding.etFecNac.text.toString()
                email = binding.etEmail.text.toString()
                nombreUsuario = binding.etUsername.text.toString()
                password = binding.etPassword.text.toString()
                //fotoPerfil = binding.ivSubirFoto.drawable.toBitmap()

                registrarUsuario(
                    nombre,
                    apellidoPaterno,
                    apellidoMaterno,
                    fechaNacimiento,
                    email,
                    nombreUsuario,
                    password,
                    idTipoUsuario,
                    //fotoPerfil
                )
            } else {
                Toast.makeText(this, "Llena todos los campos de texto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoFecha() {
        //spinner date picker layout
        val spinnerDatePicker = layoutInflater.inflate(R.layout.spinner_date_picker_layout, null) as DatePicker

        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    binding.etFecNac.setText("${spinnerDatePicker.dayOfMonth} / " +
                            "${spinnerDatePicker.month + 1} / " +
                            "${spinnerDatePicker.year}")
                }
                DialogInterface.BUTTON_NEGATIVE -> {

                }
            }
        }

        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(resources.getString(R.string.fecNac))
            .setView(spinnerDatePicker)
            .setPositiveButton("Ok", dialogClickListener)
            .setNegativeButton("Cancel", dialogClickListener)
            .create()
            .show()
    }

    private fun camposTextoLlenados(): Boolean {
        var camposObligatoriosLlenados = false

        if (binding.etName.text.isNotEmpty() &&
        binding.etEmail.text.isNotEmpty() &&
        binding.etUsername.text.isNotEmpty() &&
        binding.etPassword.text.isNotEmpty()) {
            camposObligatoriosLlenados = true
        } else {
            when {
                binding.etName.text.isEmpty() -> {
                    mostrarMensaje("Falta llenar el campo de nombre")
                }
                binding.etUsername.text.isEmpty() -> {
                    mostrarMensaje("Falta llenar el campo de nombre de usuario")
                }
                binding.etPassword.text.isEmpty() -> {
                    mostrarMensaje("Falta llenar el campo de contraseña")
                }
            }
        }

        return camposObligatoriosLlenados
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
    }

    private fun registrarUsuario(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        fechaNacimiento: String,
        email: String,
        nombreUsuario: String,
        password: String,
        idTipoUsuario: Int,
        //fotoPerfil: Bitmap
    ) {

        val json = JsonObject()
        json.addProperty("idUsuario", 0)
        json.addProperty("FotoPerfil","")
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
                println("Antes de la tragedia")
                val response = service.registrarUsuario(requestBody)
                println(response)
                runOnUiThread {
                    if (response.respuesta == "Y") {
                        mostrarExito()
                    } else if (response.respuesta == "N"){
                        mostrarMensaje("Correo o nombre de usuario ya estan registrados")
                    }

                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private fun mostrarError() {
        Toast.makeText(this,"Error al crear la cuenta",Toast.LENGTH_SHORT).show()
    }

    private fun mostrarExito() {
        Toast.makeText(this,"¡Cuenta creada!",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun seleccionarImagen() {
        var intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    private fun bitmapToArray(image: Bitmap): ByteArray {
        var outStream = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.PNG,0,outStream)

        return outStream.toByteArray()
    }
}