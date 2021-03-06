package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter

class HomeActivity : AppCompatActivity() {

    private var idUsuario = 0
    private var idTipoUsuario = 0
    private lateinit var token: String
    private lateinit var name: String
    private lateinit var username: String
    private var numSeguidores: Int = 0

    private lateinit var myToggle: ActionBarDrawerToggle
    private lateinit var myDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var tweetsAdapter: TweetAdapter
    private var tweets = mutableListOf<Tweet>()

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperarDatosUsuario()
        mostrarInfoUsuario()

        cargarListeners()

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

        recuperarTweets()
    }

    private fun recuperarDatosUsuario() {
        //Recuperando datos de usuarios transferidos de la ventana de inicio de sesi??n
        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()
        token = sharedPreferences.getString("token","").toString()
        idUsuario = sharedPreferences.getInt("tipoUsuario", 0)
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
                println("Excepcion HOME_MOSTRAR_SEGUIDORES:")
                excepcion.printStackTrace()
            }
        }
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.fabCompose.setOnClickListener {
            val intent = Intent(this, CrearTweet::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // refresca la activity
        binding.tweetsRefreshLayout.setOnRefreshListener {
            val intent = Intent(this, HomeActivity::class.java)

            finish()
            startActivity(intent)
        }

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerview() {
        tweetsAdapter = TweetAdapter(this, tweets, idUsuario)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewTweets.layoutManager = layoutManager
        binding.recyclerviewTweets.adapter = tweetsAdapter
    }

    private fun mostrarInfoUsuario() {
        binding.tvUsername.text = "@$username"
        binding.tvName.text = name
        cargarFotoUsuario(idUsuario)
        mostrarSeguidores(idUsuario)
    }
    private fun cargarFotoUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val img: String = service.getUsuario(token, idUsuario).fotoPerfil
                //Si el usuario tiene una foto de perfil...
                runOnUiThread {
                    if(img != "") {
                        println("toy aqui")
                        Picasso.get().load(img).into(binding.imageviewUserPhotoNav)
                        Picasso.get().load(img).into(binding.imageviewUserPhoto)
                    }
                }
            } catch (exception: Exception) {
                println("Excepcion HOME_BUSCAR_FOTO_USUARIO:")
                exception.printStackTrace()
            }
        }
    }

    private fun recuperarTweets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                binding.tweetsRefreshLayout.isRefreshing = false
                val service = ServiceBuilder.buildService(APIService::class.java)
                var response: List<Tweet> = emptyList()
                if(idTipoUsuario == 1){
                    response = service.recuperarTweets(token, idUsuario)
                }else{
                    response = service.recuperarTweets(token)
                }
                runOnUiThread {
                    if(response.isNotEmpty()) {
                        tweets.clear()
                        tweets.addAll(response)
                        tweetsAdapter.actualizarTweets(tweets)
                    } else {
                        mostrarMensaje("??No hay tweets! Sigue a alguien o haz un tweet")
                    }
                }

            } catch (excep: Exception) {
                println("Exception HOME_RECUPERAR_TWEETS:")
                excep.printStackTrace()
                binding.tweetsRefreshLayout.isRefreshing = false
                mostrarMensaje("Hubo un error al tratar de cargar la pagina, vuelva a intentarlo m??s tarde")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}