package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels

import com.google.gson.annotations.SerializedName

data class Seguidor(
    @SerializedName("idSeguidor")
    var idUsuario: Int,
    @SerializedName("idUsuario")
    var idSeguidor: Int,
    @SerializedName("description")
    var respuesta: String
)
