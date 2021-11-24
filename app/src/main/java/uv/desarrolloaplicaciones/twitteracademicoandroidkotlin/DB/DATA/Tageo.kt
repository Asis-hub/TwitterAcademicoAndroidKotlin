package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Tweet::class,
    parentColumns = arrayOf("idTweet"),
    childColumns = arrayOf("idTweet"),
    onDelete = CASCADE),
    ForeignKey(entity = Hashtag::class,
        parentColumns = arrayOf("idHashtag"),
        childColumns = arrayOf("idHashtag"),
        onDelete = CASCADE)
))
data class Tageo(
    var idTweet: Int,
    var Hashtag: Int,
)