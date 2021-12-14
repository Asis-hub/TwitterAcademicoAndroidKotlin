package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.content.Intent
import android.media.Image
import android.media.Session2Token
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityPerfilBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter

class PerfilActivity : AppCompatActivity() {

    private var idUsuario = 0
    private var idLogeado = 0
    private lateinit var name: String
    private lateinit var username: String
    private var esSeguidor: Boolean = false

    private var idUsuarioOriginal = 0
    private lateinit var nameOriginal: String
    private lateinit var usernameOriginal: String
    private lateinit var token: String

    private lateinit var tweetsAdapter: TweetAdapter
    private var tweets = mutableListOf<Tweet>()

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperarDatosUsuario()
        mostrarInfoUsuario()

        cargarListeners()

        initRecyclerview()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        recuperarTweets()
    }

    private fun cargarListeners() {
        fun manageItemClick(menuItem: MenuItem): Boolean {
            var resultado = false

            when (menuItem.itemId) {
                R.id.action_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.action_search -> {
                    val intent = Intent(this,BusquedaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }

            return resultado
        }
        binding.bottomNavigation.menu[0].setOnMenuItemClickListener(::manageItemClick)
        binding.bottomNavigation.menu[1].setOnMenuItemClickListener(::manageItemClick)

        binding.navLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            sharedPref.edit().clear()
            sharedPref.edit().commit()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.fabCompose.setOnClickListener {
            val intent = Intent(this, CrearTweet::class.java)

            //Comparte algunos datos del usuario para usarlos en la siguiente activity (CrearTweet)
            val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            var editor = sharedPref.edit()
            editor.putInt("id", idUsuario)
            editor.putString("nombre", name)
            editor.putString("nombreUsuario", username)
            editor.commit()

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // refresca la activity
        binding.tweetsRefreshLayout.setOnRefreshListener {
            recuperarTweets()
        }

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        binding.btnVolverPerfil.setOnClickListener{
            val sharedPref = getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
            sharedPref.edit().clear()
            sharedPref.edit().commit()
            finish()
        }

        binding.btnSeguirPerfil.setOnClickListener {
            if (esSeguidor) {
                dejarSeguirUsuario(idUsuarioOriginal, idUsuario)
            } else {
                seguirUsuario(idUsuarioOriginal,idUsuario)
            }
        }
    }

    private fun recuperarDatosUsuario() {
        val sharedPreferences = getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        val sharedPreferencesOriginal = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuarioOriginal = sharedPreferencesOriginal.getInt("id", 0)
        nameOriginal = sharedPreferencesOriginal.getString("nombre","name").toString()
        usernameOriginal = sharedPreferencesOriginal.getString("nombreUsuario","username").toString()
        token = sharedPreferencesOriginal.getString("token","").toString()
    }

    private fun seguirUsuario(usuarioSeguidor: Int, usuarioASeguir: Int) {
        val json = JsonObject()
        json.addProperty("idUsuario", usuarioASeguir)
        json.addProperty("idSeguidor", usuarioSeguidor)

        val jsonString = json.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.seguirUsuario(token, requestBody)

                CoroutineScope(Dispatchers.Main).launch {
                    if (response.respuesta == "Follow") {
                        mostrarMensaje("Usuario seguido")
                        esSeguidor = true
                    } else if (response.respuesta == "Error con el servidor") {
                        mostrarMensaje("Error con el servidor")
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }
    private fun dejarSeguirUsuario(usuarioSeguidor: Int, usuarioSeguido: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.dejarSeguir(token, usuarioSeguido, usuarioSeguidor)

                CoroutineScope(Dispatchers.Main).launch {
                    if (response.respuesta == "Unfollow") {
                        mostrarMensaje("Se ha dejado de seguir al usuario")
                        reiniciarActivity()
                    } else if (response.respuesta == "Not_Followed") {
                        mostrarMensaje("No se esta siguiendo a esta persona")
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }

    private fun reiniciarActivity() {
        val intent = Intent(this, PerfilActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun initBotonSeguir(idUsuario: Int, idUsuarioOriginal: Int) {
        if (idUsuario == idUsuarioOriginal) {
            binding.btnSeguirPerfil.visibility = View.INVISIBLE
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.verificarSeguidor(token, idUsuarioOriginal, idUsuario)

                runOnUiThread {
                    if (response.respuesta == "Y") {
                        binding.btnSeguirPerfil.text = resources.getString(R.string.dejarSeguir)
                        esSeguidor = true
                    } else if (response.respuesta == "N"){
                        binding.btnSeguirPerfil.text = resources.getString(R.string.seguirUsuario)
                        esSeguidor = false
                    }
                }
            }
        }
    }

    private fun initRecyclerview() {
        tweetsAdapter = TweetAdapter(this, tweets, idUsuarioOriginal)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewBusqueda.layoutManager = layoutManager
        binding.recyclerviewBusqueda.adapter = tweetsAdapter
    }

    private fun mostrarInfoUsuario(){
        binding.tvUsername.text = "@$usernameOriginal"
        binding.tvName.text = nameOriginal
        initBotonSeguir(idUsuario, idUsuarioOriginal)
        llenarCampoPantallaPerfil()
        mostrarSeguidores(idUsuarioOriginal)
    }

    private fun mostrarSeguidores(idUsuario: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.recuperarSeguidores(token, idUsuario)
                runOnUiThread {
                    if (response.isNotEmpty()) {
                        binding.tvFollowers.text = "Numero de seguidores: ${response.size.toString()}"
                    }
                }
            } catch (excepcion: Exception) {
                println("Excepcion PERFIL_MOSTRAR_SEGUIDORES:")
                excepcion.printStackTrace()
            }
        }
    }

    private fun llenarCampoPantallaPerfil() {
        var txtPerfil = findViewById<TextView>(R.id.txtPerfil)
        txtPerfil.text = name
    }

    private fun recuperarTweets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                binding.tweetsRefreshLayout.isRefreshing = false
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.recuperarTweetsPerfil(token, idUsuario)
                runOnUiThread {
                    if(response.isNotEmpty()) {
                        println(response)
                        tweets.clear()
                        tweets.addAll(response)
                        tweetsAdapter.actualizarTweets(tweets)
                    } else {
                        mostrarMensaje("¡No hay tweets! Sigue a alguien o haz un tweet")
                    }
                }

            } catch (excep: Exception) {
                binding.tweetsRefreshLayout.isRefreshing = false
                mostrarMensaje("Hubo un error al tratar de cargar la pagina, vuelva a intentarlo más tarde")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

}