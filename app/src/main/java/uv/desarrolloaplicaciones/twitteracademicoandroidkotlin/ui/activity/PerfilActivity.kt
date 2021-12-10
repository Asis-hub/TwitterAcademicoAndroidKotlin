package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityPerfilBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter

class PerfilActivity : AppCompatActivity() {

    private var idUsuario = 0
    private lateinit var name: String
    private lateinit var username: String

    private var idUsuarioOriginal = 0
    private lateinit var nameOriginal: String
    private lateinit var usernameOriginal: String

    private lateinit var myToggle: ActionBarDrawerToggle
    private lateinit var myDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var tweetsAdapter: TweetAdapter
    private var tweets = mutableListOf<Tweet>()

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

//Recuperando datos de usuarios transferidos de la ventana de inicio de sesión
        val sharedPreferences = getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        println("idUsuario = $idUsuario\n" +
                "name = $name\n" +
                "username = $username\n")

        val sharedPreferencesOriginal = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuarioOriginal = sharedPreferencesOriginal.getInt("id", 0)
        nameOriginal = sharedPreferencesOriginal.getString("nombre","name").toString()
        usernameOriginal = sharedPreferencesOriginal.getString("nombreUsuario","username").toString()

        mostrarInfoUsuario()
        llenarCampoPantallaPerfil()
        initRecyclerview()

        // refresca la activity
        binding.tweetsRefreshLayout.setOnRefreshListener {
            recuperarTweets()
        }

        val volver = findViewById<Button>(R.id.btnVolverPerfil)
        volver.setOnClickListener{
            finish()
        }
    }

    private fun initRecyclerview() {
        tweetsAdapter = TweetAdapter(this, tweets)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewBusqueda.layoutManager = layoutManager
        binding.recyclerviewBusqueda.adapter = tweetsAdapter
    }

    private fun mostrarInfoUsuario(){
        binding.tvUsername.text = "$usernameOriginal"
        binding.tvName.text = nameOriginal
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
                val response = service.recuperarTweets(idUsuario)

                runOnUiThread {
                    if(response.isNotEmpty()) {
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