package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = arrayOf(ForeignKey(entity = TipoUsuario::class,
    parentColumns = arrayOf("idTipoUsuario"),
    childColumns = arrayOf("idTipoUsuario"),
    onDelete = CASCADE)))
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    var nombre: String = "",
    var apellidoPaterno: String = "",
    var apellidoMaterno: String = "",
    var fechaNacimiento: Date = Calendar.getInstance().time,
    var email: String = "",
    var nombreUsuario: String = "",
    var password: String = "",
    var idTipoUsuario: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var fotoPerfil: ByteArray? = null
)