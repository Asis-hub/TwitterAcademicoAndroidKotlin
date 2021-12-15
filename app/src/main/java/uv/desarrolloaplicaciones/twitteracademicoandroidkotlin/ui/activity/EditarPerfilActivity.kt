package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityEditarPerfilBinding
import java.sql.Date

class EditarPerfilActivity : AppCompatActivity() {
    private var idUsuario: Int = 0
    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var infoUsuarioActual: Usuario
    private lateinit var token: String
    private var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            binding.ivSubirFoto.setImageURI(data?.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperarDatosUsuario()
        mostrarDatosUsuario()
        cargarListeners()
    }

    private fun mostrarDatosUsuario() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.getUsuario(token, idUsuario)
                runOnUiThread {
                    infoUsuarioActual = response
                    if (response != null) {
                        binding.tfNombrePerfil.setText(response.nombre)
                        binding.tfNombreUsuario.setText(response.nombreUsuario)
                        binding.tfApellidoPaternoPerfil.setText(response.apellidoPaterno)
                        binding.tfApellidoMaternoPerfil.setText(response.apellidoMaterno)
                        binding.tfEmailPerfil.setText(response.email)
                        binding.tfFecNac.setText(response.fechaNacimiento)
                    } else {
                        mostrarMensaje("No se encontro el usuario")
                    }
                }
            } catch (excepcion: Exception) {
                println("Excepcion PERFIL_MOSTRAR_DATOS")
                excepcion.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }

    private fun cargarListeners() {
        binding.ivSubirFoto.setOnClickListener {
            seleccionarImagen()
        }

        binding.tfFecNac.setOnClickListener {
            mostrarDialogoFecha()
        }

        binding.btnVolverPerfil.setOnClickListener {
            finish()
        }

        binding.btnGuardarEditarPerfil.setOnClickListener {
            editarPerfil()
        }

        binding.btnEliminar.setOnClickListener {
            confirmacionEliminarUsuario()
        }
    }

    private fun seleccionarImagen() {
        var intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmacionEliminarUsuario() {
        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)

            builder?.setTitle("¡PRECAUCIÓN!")
            builder?.setMessage("Si borra la cuenta no se podrá revertir la situación " +
                    "¿Esta seguro de realizar esto?")

            builder.apply {
                setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Clic en aceptar
                        eliminarUsuario()
                    })
                setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Clic en cancelar
                    })
            }

            // Create the AlertDialog
            builder.show()
        }
    }

    private fun eliminarUsuario() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.eliminarUsuario(token, idUsuario)
                runOnUiThread {
                    if (response.respuesta == "Y") {
                        mostrarMensaje("Usuario eliminado")
                        volverAInicio()
                    } else if (response.respuesta == "N") {
                        mostrarMensaje("No se pudo eliminar el usuario, intentelo más tarde")
                    }
                }
            } catch (excepcion: Exception) {
                println("Excepcion EDITARPERFIL_ELIMINAR:")
                excepcion.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }

    private fun volverAInicio() {
        val intent = Intent(this, MainActivity::class.java)
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        finish()
        startActivity(intent)
    }

    private fun editarPerfil() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JsonObject()
                json.addProperty("idUsuario", idUsuario)
                json.addProperty("FotoPerfil","")
                json.addProperty("Nombre", binding.tfNombrePerfil.text.toString())
                json.addProperty("ApellidoPaterno", binding.tfApellidoPaternoPerfil.text.toString())
                json.addProperty("ApellidoMaterno", binding.tfApellidoMaternoPerfil.text.toString())
                json.addProperty("FechaNacimiento", binding.tfFecNac.text.toString())
                json.addProperty("Email", binding.tfEmailPerfil.text.toString())
                json.addProperty("NombreUsuario", binding.tfNombreUsuario.text.toString())
                json.addProperty("Password", infoUsuarioActual.password)
                json.addProperty("idTipoUsuario", infoUsuarioActual.idTipoUsuario)

                val jsonString = json.toString()
                println(jsonString)
                val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.modificarUsuario(token, idUsuario, requestBody)

                runOnUiThread {
                    println(response)
                    if (response.respuesta == "A") {
                        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                        sharedPref.edit().putString(
                            "nombre",
                            binding.tfNombrePerfil.text.toString()
                        ).apply()
                        sharedPref.edit().putString(
                            "nombreUsuario",
                            binding.tfNombreUsuario.text.toString()
                        ).apply()
                        mostrarMensaje("Usuario actualizado")
                        finish()
                    } else {
                        mostrarMensaje("Usuario no actualizado, volver a intentarlo más tarde")
                    }
                }
            } catch (exception: Exception) {
                println("Excepcion PERFIL_EDITAR_PERFIL:")
                exception.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }

    private fun recuperarDatosUsuario() {
        //Recuperando datos de usuarios transferidos de la ventana de inicio de sesión
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        token = sharedPreferences.getString("token", "").toString()
    }

    private fun mostrarDialogoFecha() {
        //spinner date picker layout
        val spinnerDatePicker = layoutInflater.inflate(R.layout.spinner_date_picker_layout, null) as DatePicker

        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    binding.tfFecNac.setText("${spinnerDatePicker.dayOfMonth}/" +
                            "${spinnerDatePicker.month + 1}/" +
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
}