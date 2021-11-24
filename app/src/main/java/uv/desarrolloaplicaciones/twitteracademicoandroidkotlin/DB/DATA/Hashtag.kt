package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Hashtag(
    @PrimaryKey(autoGenerate = true)
    val idHashtag: Int = 0,
    val hashtag: String = ""
)