package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
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
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CrearTweet : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private var idUsuario = 0
    private lateinit var name: String
    private lateinit var username: String
    private lateinit var token: String

    private lateinit var binding: ActivityCrearTweetBinding

    private var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            binding.ivSeleccionada.setImageURI(data?.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        binding = ActivityCrearTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage

        //Recuperando datos de usuarios transferidos de la ventana de home
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()
        token = sharedPreferences.getString("token","").toString()

        cargarFoto()

        binding.btnTwittear.setOnClickListener{
            revisarTamañoTweet()
        }
        binding.ivAgregarImagen.setOnClickListener{
            seleccionarImagen()
        }
        binding.ivEliminarImagen.setOnClickListener{
            quitarImagen()
        }
        val close = findViewById<View>(R.id.toolbar)
        close.setOnClickListener {
            finish()
        }
    }

    private fun quitarImagen() {
        binding.ivSeleccionada.setImageDrawable(null)
    }

    private fun cargarFoto() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val img: String = service.getUsuario(token, idUsuario).fotoPerfil
                //Si el usuario tiene una foto de perfil...
                runOnUiThread {
                    if(img != "") {
                        println("toy aqui")
                        Picasso.get().load(img).into(binding.imgUserphoto)
                    }
                }
            } catch (exception: Exception) {
                println("Excepcion CREAR_TWEET_BUSCAR_FOTO_USUARIO:")
                exception.printStackTrace()
            }
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
                if(binding.ivSeleccionada.drawable != null){
                    val storageRef = storage.reference
                    val rutaImagenesPerfil = storageRef.child("Imagenes/Tweet/"+System.currentTimeMillis()+".jpeg")

                    binding.ivSeleccionada.isDrawingCacheEnabled = true
                    binding.ivSeleccionada.buildDrawingCache()
                    val bitmap = (binding.ivSeleccionada.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                    val data = baos.toByteArray()

                    var uploadTask = rutaImagenesPerfil.putBytes(data)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(this, "No fue posible subir la imagen", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        rutaImagenesPerfil.downloadUrl.addOnSuccessListener {
                            crearTweet(cuerpo, fechaPublicacion, it.toString())
                        }
                    }
                }else{
                    crearTweet(cuerpo, fechaPublicacion, "")
                }
            }
        }
    }

    private fun crearTweet(cuerpo: String, fechaPublicacion: String, picture: String) {
        val json = JsonObject()

        json.addProperty("Cuerpo", cuerpo)
        json.addProperty("FechaHoraPublicacion", fechaPublicacion)
        json.addProperty("Multimedia", picture)
        json.addProperty("idUsuario", idUsuario)

        val jsonString = json.toString()
        println(jsonString)
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)

                withContext(Dispatchers.Main) {
                    println("toy aqui 0")
                    println(token)
                    val response = service.registrarTweet(token, requestBody)
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

    private fun seleccionarImagen() {
        var intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }
}