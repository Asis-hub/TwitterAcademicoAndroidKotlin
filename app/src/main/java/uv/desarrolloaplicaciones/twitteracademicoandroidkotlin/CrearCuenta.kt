package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.AppDatabase
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.UsuarioDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario
import java.util.*

class CrearCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        val txtNombre = findViewById<TextView>(R.id.et_name)
        val txtNombreUsuario = findViewById<TextView>(R.id.et_username)
        val txtContrasenia = findViewById<TextView>(R.id.et_password)

        val btnCrearCuenta = findViewById<Button>(R.id.btn_create)
        val database = AppDatabase.getDatabase(this)

        btnCrearCuenta.setOnClickListener {
            if(txtNombre.text.isNotEmpty() && txtNombre.text.isNotEmpty() && txtContrasenia.text.isNotEmpty()){
                val usuario = Usuario(0, txtNombre.text.toString(),"","", Date(2000, 2, 2) ,"", txtNombreUsuario.text.toString(), txtContrasenia.text.toString(), 1, null)
                lifecycleScope.launch {
                    database.Usuario().insertUsuario(usuario)
                }
                Toast.makeText(this,"Registro exitoso", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        val
        CrearCuenta(Usuario, String url, int requestMethod){

        }


        
    }
}