package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.AppDatabase
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityCreateTweetBinding

class LoginActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityCreateTweetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityCreateTweetBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        try {
            val database = AppDatabase.getDatabase(this)
        }catch (sqliteException: SQLiteException){
                    Toast.makeText(this, "Fallo de conexion a la base de datos", Toast.LENGTH_SHORT).show()
                }


//        btnLogin.setOnClickListener {
//            lifecycleScope.launch {
//                try {
//                    database.Usuario().checkAccount(etUsername.text.toString(), etPassword.text.toString())
//                    val intento1 = Intent(this@LoginActivity, HomeActivity::class.java)
//                    startActivity(intento1)
//                }catch (sqliteException: SQLiteException){
//                    Toast.makeText(this@LoginActivity, "Fallo de conexion a la base de datos", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }



        val tv_sign_up = findViewById<TextView>(R.id.tv_sign_up)

        tv_sign_up.setOnClickListener {
            val intento1 = Intent(this, CrearCuenta::class.java)
            startActivity(intento1)
        }
    }
}