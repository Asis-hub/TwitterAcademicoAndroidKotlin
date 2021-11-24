package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Usuario::class,
    parentColumns = arrayOf("idUsuario"),
    childColumns = arrayOf("idUsuario"),
    onDelete = CASCADE)
))
data class Siguiedo (
    @PrimaryKey
    var idUsuario: Int = 0,
    var idSeguidor: Int = 0,
)