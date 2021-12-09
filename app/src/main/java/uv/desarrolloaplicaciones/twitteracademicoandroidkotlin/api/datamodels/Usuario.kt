package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("FotoPerfil")
    var fotoPerfil: ByteArray,
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
