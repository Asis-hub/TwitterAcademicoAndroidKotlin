package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R

class TweetAdapter(internal var context: Context, private var tweets: List<Tweet>) : androidx.recyclerview.widget.RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.itemlayout_tweet, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        loadRoundedPhoto(holder.photo, tweets[position].fotoPerfil)

        //holder.tvName.text = tweets[position].name
        //holder.tvUsername.text = "@" + tweets[position].username
        //holder.tvText.text = tweets[position].text

        // if the tweet is edited.. show
        //if(tweets[position].edited){
            holder.tvEdited.visibility = View.VISIBLE
        //}

        // if the tweetBy id matches with our user id... then show the edit button
        //if(tweets[position].tweetBy == context.getSharedPref("id").toString().toInt()){
            holder.editTweet.visibility = View.VISIBLE
        //}
        // TODO: Some tweets are showing the edit tweet button even though they don't belong to that user. I don't know why.
    }

    private fun loadRoundedPhoto(photo: ImageView, fotoPerfil: ByteArray?) {
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

        internal val tvName: TextView = itemView.findViewById(R.id.tv_name)
        internal val photo: ImageView = itemView.findViewById(R.id.profile_photo)
        internal val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        internal val tvText: TextView = itemView.findViewById(R.id.tv_tweet_text)
        internal val tvEdited: TextView = itemView.findViewById(R.id.tv_tweet_edited)
        internal val editTweet: ImageView = itemView.findViewById(R.id.tweet_action_edit)

        init {
            editTweet.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onItemClick(v, adapterPosition)
        }
    }
}