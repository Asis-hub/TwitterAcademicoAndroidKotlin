package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.*
import androidx.appcompat.widget.PopupMenu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.APIService
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.ServiceBuilder
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity.PerfilActivity

class TweetAdapter(internal var context: Context, private var tweets: MutableList<Tweet>) : androidx.recyclerview.widget.RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlayout_tweet, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tweets[position]
        holder.bind(item)
    }

    fun actualizarTweets(nuevosTweets: MutableList<Tweet>) {
        tweets = nuevosTweets
        notifyDataSetChanged()
    }

    private fun cargarImagen(photo: ImageView, fotoPerfil: ByteArray?) {
        photo.setImageBitmap(BitmapFactory.decodeByteArray(fotoPerfil, 0, fotoPerfil!!.size))
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.clickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val foto: ImageView = itemView.findViewById(R.id.profile_photo)
        private val tvUsername = itemView.findViewById<TextView>(R.id.tv_username)
        private val tvText = itemView.findViewById<TextView>(R.id.tv_tweet_text)
        private val multimedia = itemView.findViewById<ImageView>(R.id.imageview_media)
        private val tvEdited = itemView.findViewById<TextView>(R.id.tv_tweet_edited)
        private val editTweet = itemView.findViewById<ImageView>(R.id.tweet_action_edit)
        private val like = itemView.findViewById<ImageView>(R.id.tweet_action_like)
        private val options = itemView.findViewById<ImageView>(R.id.iv_moreOptions)
        private lateinit var tweet: Tweet

        init {
            editTweet.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onItemClick(v, adapterPosition)
        }

        fun bind(tweet: Tweet) {
            this.tweet = tweet
            val sharedPreference = context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE)

            foto.setOnClickListener{
                val intent = Intent(context,PerfilActivity::class.java)

                val sharedPref = context.getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
                sharedPref.edit().putInt("id",tweet.tuiteadoPor)
                sharedPref.edit().putString("nombre", tweet.nombre)
                sharedPref.edit().putString("nombreUsuario", tweet.nombreUsuario)
                sharedPref.edit().commit()

                startActivity(context,intent,null)
            }
            options.setOnClickListener {view ->
                val popupMenu = PopupMenu(context, view)
                //TODO Si un usuario es seguidor el boton dice: Dejar de seguir
                // Si el usuario no es un seguidor el boton dice: Seguir usuario
                // if(usuarioEsSeguidor(sharedPreference.getInt("id", 0),tweet.tuiteadoPor))
                popupMenu.menu.add(R.string.seguirUsuario)

                popupMenu.setOnMenuItemClickListener(::manageItemClick)
                popupMenu.show()
            }


            if (tweet.fotoPerfil != null) {
                cargarImagen(foto, tweet.fotoPerfil)
            } else {
                foto.setImageResource(R.drawable.default_photo)
            }
            tvName.text = tweet.nombre
            tvUsername.text = "@" + tweet.nombreUsuario
            tvText.text = tweet.cuerpo
            //Si el tweet no tiene ninguna imagen, hace el contenedor no visible
            if (tweet.multimedia != null) {
                cargarImagen(multimedia, tweet.multimedia)
            } else {
                multimedia.visibility = View.INVISIBLE
                multimedia.setImageBitmap(null)
            }

            // Muestra el botón edit si el tweeet es de la persona que esta en la aplicación
            if(tweet.tuiteadoPor == sharedPreference.getInt("id", 0)){
                editTweet.visibility = View.VISIBLE
            }
        }
        //TODO Funcion en progreso
        private fun usuarioEsSeguidor(usuario: Int, seguidor: Int): Boolean {
            var usuarioEsSeguidor = false
            CoroutineScope(Dispatchers.IO).launch {

            }
            return usuarioEsSeguidor
        }

        private fun manageItemClick(menuItem: MenuItem): Boolean {
            val sharedPreference = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

            var resultado = false
            when(menuItem.itemId) {
                R.id.seguirUsuario -> {
                    seguirUsuario(
                        sharedPreference.getInt("id", 0), tweet.tuiteadoPor)
                }
            }

            return resultado
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
                    val response = service.seguirUsuario(requestBody)

                    withContext(Dispatchers.Main) {
                        if (response.respuesta == "Follow") {
                            mostrarMensaje("Usuario seguido")
                        } else if (response.respuesta == "Error con el servidor") {
                            mostrarMensaje("Error con el servidor")
                        }
                    }
                } catch (exception: Exception) {

                }
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }
}