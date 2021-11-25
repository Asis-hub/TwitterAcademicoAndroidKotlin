package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario

interface UsuarioDAO {
    @Query("SELECT * FROM Usuario WHERE idUsuario = :idUsuario")
    suspend fun getUsuario(idUsuario: Int): List<Usuario>

    @Query("SELECT * FROM USUARIO WHERE email = :email AND password = :password")
    suspend fun checkAccount(email: String, password: String)

    @Insert
    suspend fun insertUsuario(usuario: Usuario)

    @Delete
    suspend fun deleteUsuario(usuario: Usuario)

    @Update
    suspend fun updateUsuario(usuario: Usuario)
}