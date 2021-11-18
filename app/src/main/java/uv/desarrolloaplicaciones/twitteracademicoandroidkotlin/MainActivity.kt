package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.media.Image
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgLogin = findViewById<ImageView>(R.id.imgLogin)
        Glide.with(this)
            .load("https://hipertextual.com/wp-content/uploads/2012/06/twitter-bird-white-on-blue.jpg")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imgLogin)
    }
}