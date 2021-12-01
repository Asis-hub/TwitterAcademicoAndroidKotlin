package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Tweet

interface TweetDAO {
    @Query("SELECT * FROM Tweet")
    fun getAllTweets(): List<Tweet>

    @Query("SELECT * FROM Tweet WHERE idUsuario = :idUsuario")
    fun getAllTweetByUser(idUsuario: Int)

    @Query("SELECT T.* FROM Tweet AS T LEFT JOIN Usuario AS U ON T.idUsuario = U.idUsuario LEFT JOIN Siguiendo AS S ON U.idUsuario = S.idUsuario WHERE S.idSeguidor = :idSeguidor OR T.idUsuario = :idSeguidor")
    fun getTweetSiguiendo(idSeguidor: Int): List<Tweet>

    @Query("SELECT * FROM Tweet WHERE CONTAINS(cuerpo, :busqueda)")
    fun getTweetByBusqueda(busqueda: String): List<Tweet>

    @Delete
    fun deleteTweet(tweet: Tweet)

    @Update
    fun updateTweet(tweet: Tweet)

    @Insert
    fun insertTweet(tweet: Tweet)
}