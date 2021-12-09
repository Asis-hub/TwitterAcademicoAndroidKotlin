package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        println("idUsuario = $idUsuario\n" +
                "name = $name\n" +
                "username = $username\n")

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
            val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            sharedPref.edit().clear()
            sharedPref.edit().commit()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
        }

        binding.fabCompose.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CreateTweetActivity::class.java))
        }

        // refresca la activity
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
        binding.tvUsername.text = "@$username"
        binding.tvName.text = name
        cargarFotoUsuario(buscarFotoUsuario(idUsuario))
    }

    private fun buscarFotoUsuario(idUsuario: Int): Bitmap? {
        var fotoUsuario: Bitmap? = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)

                val img: ByteArray = service.getUsuario(idUsuario).fotoPerfil
                //Si el usuario tiene una foto de perfil...
                if(img != null) {
                    fotoUsuario = BitmapFactory.decodeByteArray(img,0,img.size)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        return fotoUsuario
    }

    private fun cargarFotoUsuario(fotoUsuario: Bitmap?) {
        if (fotoUsuario != null) {
            binding.imageviewUserPhoto.setImageBitmap(fotoUsuario)
            binding.imageviewUserPhotoNav.setImageBitmap(fotoUsuario)
        }
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