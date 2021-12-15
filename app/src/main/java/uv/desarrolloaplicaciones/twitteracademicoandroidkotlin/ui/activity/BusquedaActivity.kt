package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityBusquedaBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.UsuarioAdapter
import java.util.*

class BusquedaActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityBusquedaBinding

    private lateinit var username: String
    private var idUsuario: Int = 0
    private lateinit var name: String
    private lateinit var token: String

    private lateinit var tweetsAdapter: TweetAdapter
    private val tweets = mutableListOf<Tweet>()

    private lateinit var usuariosAdapter: UsuarioAdapter
    private val usuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperarDatosUsuario()
        mostrarInfoUsuario()

        cargarListeners()

        initRecyclerView()
    }

    private fun cargarListeners() {
        fun manageItemClick(menuItem: MenuItem): Boolean {
            var resultado = false

            when(menuItem.itemId) {
                R.id.action_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }

            return resultado
        }
        binding.bottomNavigation.menu[0].setOnMenuItemClickListener(::manageItemClick)

        binding.svBuscador.setOnQueryTextListener(this)

        binding.tweetsRefreshLayout.setOnRefreshListener {
            tweetsAdapter.actualizarTweets()
            usuariosAdapter.actualizarUsuarios()
        }

        binding.navLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            sharedPref.edit().clear()
            sharedPref.edit().commit()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarInfoUsuario() {
        binding.tvUsername.text = "@$username"
        binding.tvName.text = name
        //TODO dado que el metodo buscarFotoUsuario no funciona todavía, esto tampoco debería ser llamado
        // cargarFotoUsuario(buscarFotoUsuario(idUsuario))
        mostrarSeguidores(idUsuario)
    }

    private fun mostrarSeguidores(idUsuario: Int) {
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
                println("Excepcion HOME_MOSTRAR_SEGUIDORES:")
                excepcion.printStackTrace()
            }
        }
    }

    private fun recuperarDatosUsuario() {
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()
        token = sharedPreferences.getString("token","username").toString()
    }

    private fun initRecyclerView() {
        tweetsAdapter = TweetAdapter(this, tweets,idUsuario)

        val layoutManagerTweet = LinearLayoutManager(this)
        layoutManagerTweet.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewBusqueda.layoutManager = layoutManagerTweet
        binding.recyclerviewBusqueda.adapter = tweetsAdapter

        usuariosAdapter = UsuarioAdapter(this, usuarios)

        val layoutManagerUsuario = LinearLayoutManager(this)
        layoutManagerUsuario.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewBusquedaUsuario.layoutManager = layoutManagerUsuario
        binding.recyclerviewBusquedaUsuario.adapter = usuariosAdapter
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()) {
            buscar(query.lowercase(Locale.getDefault()))
        }
        return true
    }

    private fun buscar(query: String) {
        usuarios.clear()
        tweets.clear()
        tweetsAdapter.actualizarTweets()
        usuariosAdapter.actualizarUsuarios()
        binding.tvMsgResultado.text = ""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val responseTweet = service.buscarTweetContenido(token, query)
                val responseUsuario = service.buscarUsuarioNombre(token, query)

                runOnUiThread {
                    when {
                        responseTweet.isNotEmpty() -> {
                            tweets.clear()
                            tweets.addAll(responseTweet)
                            tweetsAdapter.actualizarTweets(tweets)
                        }
                        responseUsuario.isNotEmpty() -> {
                            usuarios.clear()
                            usuarios.addAll(responseUsuario)
                            usuariosAdapter.actualizarUsuarios(usuarios)
                        }
                        else -> {
                            //mostrarMensaje("¡No se encontraron tweets o usuarios!")
                            binding.tvMsgResultado.text = "¡No se encontraron tweets o usuarios!"
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                mostrarMensaje(resources.getString(R.string.mensajeError))
            }
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
    }
}