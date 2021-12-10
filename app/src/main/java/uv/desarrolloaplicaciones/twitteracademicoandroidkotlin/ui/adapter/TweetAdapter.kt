package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.view.*
import androidx.appcompat.widget.PopupMenu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity.EditarPerfilActivity
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity.PerfilActivity

class TweetAdapter(internal var context: Context, private var tweets: MutableList<Tweet>) : androidx.recyclerview.widget.RecyclerView.Adapter<TweetAdapter.ViewHolder>() {
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
    fun actualizarTweets() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

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
        private val sharedPreference = context.getSharedPreferences("USER_DATA",Context.MODE_PRIVATE)

        fun bind(tweet: Tweet) {
            this.tweet = tweet


            cargarListeners()
            //Menu que aparece al hace clic en el icono de los tres puntos
            cargarPopupMenu()
            cargarFotoPerfil()
            cargarDatosUsuarioTweet()
            cargarMultimediaTweet()

            // Muestra el botón edit si el tweet es de la persona que esta en la aplicación
            if(tweet.tuiteadoPor == sharedPreference.getInt("id", 0)){
                editTweet.visibility = View.VISIBLE
            }
        }

        private fun cargarListeners() {
            foto.setOnClickListener{
                val intent = Intent(context,PerfilActivity::class.java)

                val sharedPref = context.getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("id",tweet.tuiteadoPor)
                editor.putString("nombre", tweet.nombre)
                editor.putString("nombreUsuario", tweet.nombreUsuario)
                editor.commit()

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(context,intent,null)
            }

            editTweet.setOnClickListener {
                //TODO Editar tweet
            }

            like.setOnClickListener {
                like.setImageResource(R.drawable.ic_twitter_like)
                like.setColorFilter(ContextCompat.getColor(context, R.color.red_like), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }

        private fun cargarPopupMenu() {
            val popupMenu = PopupMenu(context, options)
            // Si un usuario es seguidor el boton dice: Dejar de seguir
            // Si el usuario no es un seguidor el boton dice: Seguir usuario
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val service = ServiceBuilder.buildService(APIService::class.java)
                    val response = service.verificarSeguidor(
                        sharedPreference.getInt("id", 0),
                        tweet.tuiteadoPor)

                    CoroutineScope(Dispatchers.Main).launch {
                        if (response.respuesta == "Y") {
                            popupMenu.menu.add(R.string.dejarSeguir)
                        } else {
                            popupMenu.menu.add(R.string.seguirUsuario)
                        }
                    }
                }catch (exception: Exception) {
                    mostrarMensaje(context.resources.getString(R.string.mensajeError))
                }
            }

            //Revisa si el tweet fue hecho por el usuario logeado
            if(sharedPreference.getInt("id", 0) == tweet.tuiteadoPor) {
                popupMenu.menu.add(R.string.borrar_tweet)
            }
            options.setOnClickListener {
                popupMenu.setOnMenuItemClickListener(::manageItemClick)
                popupMenu.show()
            }
        }
        private fun manageItemClick(menuItem: MenuItem): Boolean {
            var resultado = false

            when(menuItem.title.toString()) {
                context.resources.getString(R.string.seguirUsuario) -> {
                    seguirUsuario(sharedPreference.getInt("id", 0), tweet.tuiteadoPor)
                }
                context.resources.getString(R.string.dejarSeguir) -> {
                    dejarSeguirUsuario(sharedPreference.getInt("id", 0), tweet.tuiteadoPor)
                }
                context.resources.getString(R.string.borrar_tweet) -> {
                    borrarTweet(tweet.idTweet)
                }
            }

            return resultado
        }
        private fun borrarTweet(idTweet: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val service = ServiceBuilder.buildService(APIService::class.java)
                val response = service.eliminarTweet(idTweet)
                CoroutineScope(Dispatchers.Main).launch {
                    if (response.respuesta == "Tweet eliminado") {
                        Toast.makeText(context,"Tweet eliminado con exito",Toast.LENGTH_SHORT).show()
                    } else if(response.respuesta == "Tweet no encontrado") {
                        Toast.makeText(context,"Tweet no encontrado", Toast.LENGTH_SHORT).show()
                    }
                    actualizarTweets()
                }
            }
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

                    CoroutineScope(Dispatchers.Main).launch {
                        if (response.respuesta == "Follow") {
                            mostrarMensaje("Usuario seguido")
                        } else if (response.respuesta == "Error con el servidor") {
                            mostrarMensaje("Error con el servidor")
                        }

                        actualizarTweets()
                    }
                } catch (exception: Exception) {

                }
            }
        }
        private fun dejarSeguirUsuario(usuarioSeguidor: Int, usuarioSeguido: Int) {
            val json = JsonObject()
            json.addProperty("idUsuario", usuarioSeguido)
            json.addProperty("idSeguidor", usuarioSeguidor)

            val jsonString = json.toString()
            val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val service = ServiceBuilder.buildService(APIService::class.java)
                    val response = service.dejarSeguir(requestBody)

                    CoroutineScope(Dispatchers.Main).launch {
                        if (response.respuesta == "Unfollow") {
                            mostrarMensaje("Se ha dejado de seguir al usuario")
                        } else if (response.respuesta == "Not_Followed") {
                            mostrarMensaje("No se esta siguiendo a esta persona")
                        }

                        actualizarTweets()
                    }
                } catch (exception: Exception) {
                    mostrarMensaje(context.resources.getString(R.string.mensajeError))
                }
            }
        }

        private fun cargarFotoPerfil() {
            if (tweet.fotoPerfil != null) {
                cargarImagen(foto, tweet.fotoPerfil)
            } else {
                foto.setImageResource(R.drawable.default_photo)
            }
        }

        private fun cargarDatosUsuarioTweet() {
            tvName.text = tweet.nombre
            tvUsername.text = "@" + tweet.nombreUsuario
            tvText.text = tweet.cuerpo
        }

        private fun cargarMultimediaTweet() {
            //Si el tweet no tiene ninguna imagen, hace el contenedor no visible
            if (tweet.multimedia != null) {
                cargarImagen(multimedia, tweet.multimedia)
            } else {
                multimedia.visibility = View.INVISIBLE
                multimedia.setImageBitmap(null)
            }
        }

        private fun cargarImagen(view: ImageView, imagenBArray: ByteArray) {
            view.setImageBitmap(BitmapFactory.decodeByteArray(imagenBArray,
                0,
                imagenBArray!!.size))
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }
}