package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels

import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("idTweet")
    var idTweet: Int,
    @SerializedName("idUsuario")
    var idUsuario: Int,
    @SerializedName("Respuesta")
    var respuesta: String,
    @SerializedName("Cantidad")
    var cantidad: Int
)
