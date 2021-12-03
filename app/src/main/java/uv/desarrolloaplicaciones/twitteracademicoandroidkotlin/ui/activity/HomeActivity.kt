package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_home)
    }
}