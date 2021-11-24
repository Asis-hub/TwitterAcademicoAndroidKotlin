package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Siguiendo

interface SiguiedoDAO {
    @Insert
    suspend fun insertSiguiendo(siguiendo: Siguiendo)

    @Delete
    suspend fun unfollow(siguiendo: Siguiendo)
}