package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.AppDatabase
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.UsuarioDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.databinding.ActivityLoginBinding
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener { validarUsuario("http://192.168.56.1:80/android_mysql/validar_usuario.php") }
    }

    private fun validarUsuario(URL: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, URL,
            Response.Listener { response ->
                try {
                    if (!response.isEmpty()) {
                        //obteniendo los registros
                        val jsonRespuesta = JSONObject(response)
                        println(jsonRespuesta.toString())

                        //val nombre_usuario = jsonRespuesta.getString("Nombre")

                        //abriendo el activiy de bienvenido
                        val intent = Intent(this, HomeActivity::class.java)
//                        intent.putExtra("usuario", nombre_usuario)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "usuario o contraseña incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.message
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this,
                    error.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val parametros: MutableMap<String, String> = HashMap()
                println(binding.etUsername.text.toString())
                println(binding.etPassword.text.toString())
                parametros["username"] = binding.etUsername.text.toString()
                parametros["password"] = binding.etPassword.text.toString()
                return parametros
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


}
