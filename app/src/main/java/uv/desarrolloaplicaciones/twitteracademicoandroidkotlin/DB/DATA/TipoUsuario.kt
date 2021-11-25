package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TipoUsuario(
    @PrimaryKey(autoGenerate = true)
    val idTipoUsuario: Int = 0,
    var tipoUsuario: String = ""
)