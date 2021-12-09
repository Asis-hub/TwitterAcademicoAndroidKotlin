package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityCreateTweetBinding

class CreateTweetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTweetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load("https://ibb.co/L6jFk1W")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.default_photo)
            .into(binding.imageviewUserPhoto)
        binding.btnPublicarTweet.setOnClickListener{
            checkTweetLegth()
        }
    }

    private fun checkTweetLegth() {
        if(binding.etTweetText.text.toString().length == 0){
            makeToast("Es necesario contenido para el Tweet")
        }else{
            makeToast("El Tweet ha sido publicado")
        }
    }

    private fun generateTweet() {

    }


    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}