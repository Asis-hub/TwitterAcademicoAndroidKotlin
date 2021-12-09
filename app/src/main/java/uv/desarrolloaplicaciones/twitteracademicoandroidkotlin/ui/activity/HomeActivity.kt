package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter

class HomeActivity : AppCompatActivity() {

    private var idUsuario = 0
    private lateinit var name: String
    private lateinit var username: String
    private lateinit var accessToken: String

    private lateinit var myToggle: ActionBarDrawerToggle
    private lateinit var myDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var tweetsAdapter: TweetAdapter
    private var tweets = mutableListOf<Tweet>()

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Recuperando datos de usuarios transferidos de la ventana de inicio de sesión
        idUsuario = intent.getIntExtra("id",0)
        name = intent.getStringExtra("nombre").toString()
        username = intent.getStringExtra("username").toString()
        accessToken = intent.getStringExtra("token").toString()

        mostrarInfoUsuario()
        initRecyclerview()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        navigationView = findViewById(R.id.navigation_view_home)
        myDrawerLayout = findViewById(R.id.drawerLayoutHome)
        myToggle = ActionBarDrawerToggle(this,
            myDrawerLayout,
            R.string.open,
            R.string.close)
        myDrawerLayout.addDrawerListener(myToggle)
        myToggle.syncState()

        binding.imageviewUserPhoto.setOnClickListener {
            if (myDrawerLayout.isDrawerOpen(navigationView)) {
                myDrawerLayout.closeDrawer(navigationView)
            } else {
                myDrawerLayout.openDrawer(navigationView)
            }
        }

        binding.navLogout.setOnClickListener {
            // remove saved prefs with user data and launch intro.
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
        }

        // compose fab
        binding.fabCompose.setOnClickListener {
            // start the Compose activity
            startActivity(Intent(this@HomeActivity, CreateTweetActivity::class.java))
        }

        // swipe to refresh
        binding.tweetsRefreshLayout.setOnRefreshListener {
            recuperarTweets()
        }

        recuperarTweets()
    }

    private fun initRecyclerview() {
        tweetsAdapter = TweetAdapter(this, tweets)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewTweets.layoutManager = layoutManager
        binding.recyclerviewTweets.adapter = tweetsAdapter
    }

    private fun restartActivity() {
        val intent = Intent(this@HomeActivity, HomeActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun mostrarInfoUsuario() {
        binding.tvUsername.text = username
        binding.tvName.text = name
        //TODO Debe mostrar la imagen de perfil del usuario tambien
    }

    private fun recuperarTweets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                binding.tweetsRefreshLayout.isRefreshing = false
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.recuperarTweets(idUsuario)
                /*TODO TEMPORAL
                   val response = service.recuperarTweets(5)*/

                runOnUiThread {
                    if(response.isNotEmpty()) {
                        tweets.clear()
                        tweets.addAll(response)
                        tweetsAdapter.actualizarTweets(tweets)
                        println(binding.recyclerviewTweets.adapter!!.itemCount)
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