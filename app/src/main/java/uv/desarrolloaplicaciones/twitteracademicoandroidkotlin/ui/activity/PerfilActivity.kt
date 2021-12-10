package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
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

    private lateinit var tweetsAdapter: TweetAdapter
    private var tweets = mutableListOf<Tweet>()

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        val sharedPreferencesOriginal = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuarioOriginal = sharedPreferencesOriginal.getInt("id", 0)
        nameOriginal = sharedPreferencesOriginal.getString("nombre","name").toString()
        usernameOriginal = sharedPreferencesOriginal.getString("nombreUsuario","username").toString()

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

        mostrarInfoUsuario()
        llenarCampoPantallaPerfil()
        initRecyclerview()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

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


        binding.btnVolverPerfil.setOnClickListener{
            val sharedPref = getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
            sharedPref.edit().clear()
            sharedPref.edit().commit()
            finish()
        }

        recuperarTweets()
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