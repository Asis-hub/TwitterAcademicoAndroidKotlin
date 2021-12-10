package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Seguidor
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario

interface APIService {
    //USUARIOS

    @GET("Usuario/{id}")
    suspend fun getUsuario(@Path("id")idUsuario: Int): Usuario

    @POST("Login")
    suspend fun logearse(@Body usuario: RequestBody): Usuario

    @GET("Usuario")
    suspend fun getUsuarios() : List<Usuario>

    @Headers("Content-Type: application/json")
    @POST("Usuario")
    suspend fun registrarUsuario(@Body nuevoUsuario: RequestBody): Response<Usuario>

    //SEGUIDOR

    @POST("Seguidor")
    suspend fun seguirUsuario(@Body seguirUsuario: RequestBody): Seguidor

    //Recupera una lista de los seguidores de un usuario definido
    @GET("Seguidor/Seguido/{idUsuario}")
    suspend fun recuperarSeguidores(@Path("idUsuario") idUsuario: Int): List<Seguidor>

    //TWEETS

    @GET("Tweet")
    suspend fun recuperarTweets(): List<Tweet>

    @GET("Tweet/{id}")
    suspend fun recuperarTweets(@Path("id") idUsuario: Int): List<Tweet>

    @Headers("Content-Type: application/json")
    @POST("Tweet")
    suspend fun registrarTweet(@Body nuevoTweet: RequestBody): Response<Tweet>
}