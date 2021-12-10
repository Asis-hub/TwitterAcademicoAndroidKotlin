package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityBusquedaBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityHomeBinding
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter.TweetAdapter
import java.util.*

class BusquedaActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var username: String
    private lateinit var idUsuario: Int
    private lateinit var name: String

    private lateinit var binding: ActivityBusquedaBinding
    private lateinit var tweetsAdapter: TweetAdapter
    private val tweets = mutableListOf<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id", 0)
        name = sharedPreferences.getString("nombre","name").toString()
        username = sharedPreferences.getString("nombreUsuario","username").toString()

        binding.svBuscador.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        tweetsAdapter = TweetAdapter(this, tweets,idUsuario)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewBusqueda.layoutManager = layoutManager
        binding.recyclerviewBusqueda.adapter = tweetsAdapter
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()) {
            buscarTweets(query.lowercase(Locale.getDefault()))
        }
        return true
    }

    private fun buscarTweets(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.buscarTweetContenido(query)

                runOnUiThread {
                    println(response)
                    if(response.isNotEmpty()) {
                        tweets.clear()
                        tweets.addAll(response)
                        tweetsAdapter.actualizarTweets(tweets)
                    } else {
                        mostrarMensaje("¡No hay tweets con este contenido!")
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