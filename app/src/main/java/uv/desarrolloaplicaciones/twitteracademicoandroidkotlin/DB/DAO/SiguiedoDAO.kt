package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Siguiendo

interface SiguiedoDAO {
    @Insert
    fun insertSiguiendo(siguiendo: Siguiendo)

    @Delete
    fun unfollow(siguiendo: Siguiendo)
}