package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.TipoUsuario

@Dao
interface TipoUsuarioDAO {
    @Query("SELECT * FROM TipoUsuario")
    fun getTipoUsuario(): List<TipoUsuario>

    @Insert
    fun insertTipoUsuario(tiposUsuario: List<TipoUsuario>)
}