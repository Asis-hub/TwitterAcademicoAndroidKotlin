package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet

class TweetAdapter(internal var context: Context, private var tweets: MutableList<Tweet>) : androidx.recyclerview.widget.RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlayout_tweet, parent, false)
        println(tweets)
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

        init {
            editTweet.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onItemClick(v, adapterPosition)
        }

        fun bind(tweet: Tweet) {
            //
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
            //if(tweets[position].tweetBy == context.getSharedPref("id").toString().toInt()){
            //editTweet.visibility = View.VISIBLE
            //}
        }
    }
}