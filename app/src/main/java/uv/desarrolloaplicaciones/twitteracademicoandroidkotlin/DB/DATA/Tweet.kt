package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Usuario::class,
    parentColumns = arrayOf("idUsuario"),
    childColumns = arrayOf("idUsuario"),
    onDelete = ForeignKey.CASCADE
    )
))
data class Tweet(
    @PrimaryKey(autoGenerate = true)
    val idTweet: Int = 0,
    var cuerpo: String = "",
    val fechaHoraPublicacion: Date = Calendar.getInstance().time,
    var likes: Int = 0,
    var idUsuario: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var fotoPerfil: ByteArray? = null
)