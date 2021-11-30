package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario

interface UsuarioDAO {
    @Query("SELECT * FROM Usuario WHERE idUsuario = :idUsuario")
    fun getUsuario(idUsuario: Int): List<Usuario>

    @Query("SELECT * FROM USUARIO WHERE email = :email AND password = :password")
    fun checkAccount(email: String, password: String)

    @Insert
    fun insertUsuario(usuario: Usuario)

    @Delete
    fun deleteUsuario(usuario: Usuario)

    @Update
    fun updateUsuario(usuario: Usuario)
}