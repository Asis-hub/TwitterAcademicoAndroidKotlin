package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels

import com.google.gson.annotations.SerializedName

data class Tweet (
    @SerializedName("idTweet")
    var idTweet: Int,
    @SerializedName("Cuerpo")
    var cuerpo: String,
    @SerializedName("FechaHoraPublicacion")
    var fechaHoraPublicacion: String,
    @SerializedName("Multimedia")
    var multimedia: ByteArray,
    @SerializedName("Likes")
    var likes: Int,
    @SerializedName("idUsuario")
    var tuiteadoPor: Int,
    @SerializedName("Nombre")
    var nombre: String,
    @SerializedName("NombreUsuario")
    var nombreUsuario: String,
    @SerializedName("FotoPerfil")
    var fotoPerfil: ByteArray
)