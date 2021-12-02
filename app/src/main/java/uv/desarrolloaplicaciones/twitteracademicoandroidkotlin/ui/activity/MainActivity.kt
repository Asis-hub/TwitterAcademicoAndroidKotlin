package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.media.Image
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val tv_login = findViewById<TextView>(R.id.tv_login)



        btnCrearCuenta.setOnClickListener {
            val intento1 = Intent(this, CrearCuenta::class.java)
            startActivity(intento1)
        }

        tv_login.setOnClickListener {
            val intento1 = Intent(this, LoginActivity::class.java)
            startActivity(intento1)
        }

    }
}