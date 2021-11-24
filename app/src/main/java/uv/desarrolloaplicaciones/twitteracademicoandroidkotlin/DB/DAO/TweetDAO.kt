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

    @Query("SELECT tweet.* FROM Tweet as tweet LEFT JOIN Siguiedo as siguiendo ON tweet.idUsuario = siguiendo.idUsuario WHERE siguiendo.idSeguidor = :idSeguidor")
    suspend fun getTweetSiguiendo(idSeguidor: Int): List<Tweet>

    @Delete
    suspend fun deleteTweet(tweet: Tweet)

    @Update
    suspend fun updateTweet(tweet: Tweet)

    @Insert
    suspend fun insertTweet(tweet: Tweet)
}