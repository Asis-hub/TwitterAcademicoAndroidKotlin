package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.Api

import com.google.gson.annotations.SerializedName
import java.util.*

data class UsuarioResponse(
    @SerializedName("Nombre")
    var nombre: String,
    @SerializedName("ApellidoPaterno")
    var apellidoPaterno: String,
    @SerializedName("ApellidoMaterno")
    var apellidoMaterno: String,
    @SerializedName("FechaNacimiento")
    var fechaNacimiento: String,
    @SerializedName("Email")
    var email: String,
    @SerializedName("NombreUsuario")
    var nombreUsuario: String,
    @SerializedName("Password")
    var password: String,
    @SerializedName("idTipoUsuario")
    var idTipoUsuario: Int
)
