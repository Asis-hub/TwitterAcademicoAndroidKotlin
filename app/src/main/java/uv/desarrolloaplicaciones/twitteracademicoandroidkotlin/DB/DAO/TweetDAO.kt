package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Tweet

interface TweetDAO {
    @Query("SELECT * FROM Tweet")
    suspend fun getAllTweets(): List<Tweet>

    @Query("SELECT * FROM Tweet WHERE idUsuario = :idUsuario")
    suspend fun getAllTweetByUser(idUsuario: Int)

    @Query("SELECT T.* FROM Tweet AS T LEFT JOIN Usuario AS U ON T.idUsuario = U.idUsuario LEFT JOIN Siguiendo AS S ON U.idUsuario = S.idUsuario WHERE S.idSeguidor = :idSeguidor OR T.idUsuario = :idSeguidor")
    suspend fun getTweetSiguiendo(idSeguidor: Int): List<Tweet>

    @Query("SELECT * FROM Tweet WHERE CONTAINS(cuerpo, :busqueda)")
    suspend fun getTweetByBusqueda(busqueda: String): List<Tweet>

    @Delete
    suspend fun deleteTweet(tweet: Tweet)

    @Update
    suspend fun updateTweet(tweet: Tweet)

    @Insert
    suspend fun insertTweet(tweet: Tweet)
}