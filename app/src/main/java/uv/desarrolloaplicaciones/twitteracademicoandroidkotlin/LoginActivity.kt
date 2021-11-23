package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tv_sign_up = findViewById<TextView>(R.id.tv_sign_up)

        tv_sign_up.setOnClickListener {
            val intento1 = Intent(this, CrearCuenta::class.java)
            startActivity(intento1)
        }


    }
}