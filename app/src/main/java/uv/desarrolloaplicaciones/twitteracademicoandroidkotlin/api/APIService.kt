package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario

interface APIService {
    //USUARIOS
    @POST("Login")
    suspend fun logearse(@Body usuario: RequestBody): Usuario

    @GET("Usuario")
    suspend fun getUsuarios() : List<Usuario>

    @Headers("Content-Type: application/json")
    @POST("Usuario")
    suspend fun registrarUsuario(@Body nuevoUsuario: RequestBody): Response<Usuario>

    //TWEETS
    @GET("Tweet")
    suspend fun recuperarTweets(): List<Tweet>

    @GET("Tweet/{id}")
    suspend fun recuperarTweets(@Path("id") idUsuario: Int): List<Tweet>
}