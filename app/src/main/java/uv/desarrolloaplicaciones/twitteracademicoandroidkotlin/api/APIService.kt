package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Like
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
    suspend fun registrarUsuario(@Body nuevoUsuario: RequestBody): Usuario



    //SEGUIDOR

    @POST("Seguidor")
    suspend fun seguirUsuario(@Body seguirUsuario: RequestBody): Seguidor

    @DELETE("Seguidor/{idUsuario}/{idSeguidor}")
    suspend fun dejarSeguir(
        @Path("idUsuario") idUsuarioSeguido: Int,
        @Path("idSeguidor") idUsuarioSeguidor: Int
    ): Seguidor

    //Recupera una lista de los seguidores de un usuario definido
    @GET("Seguidor/{idUsuarioComparacion}/{idUsuario}")
    suspend fun verificarSeguidor(
        @Path("idUsuario") idUsuario: Int,
        @Path("idUsuarioComparacion") idUsuarioComparacion: Int
    ): Seguidor

    @GET("Seguidores/{idUsuario}")
    suspend fun recuperarSeguidores(@Path("idUsuario") idUsuario: Int): List<Seguidor>

    //TWEETS

    @GET("Tweet")
    suspend fun recuperarTweets(): List<Tweet>

    @GET("Tweet/{id}")
    suspend fun recuperarTweets(@Path("id") idUsuario: Int): List<Tweet>

    @Headers("Content-Type: application/json")
    @POST("Tweet")
    suspend fun registrarTweet(@Body nuevoTweet: RequestBody): Response<Tweet>

    @DELETE("Tweet/{idTweet}")
    suspend fun eliminarTweet(@Path("idTweet") idTweet: Int): Tweet

    @GET("Tweet/Content/{keyword}")
    suspend fun buscarTweetContenido(@Path("keyword") keyword: String): List<Tweet>

    //LIKES
  
    @GET("Likes/{idTweet}/{idUsuario}")
    suspend fun isLiked(@Path("idTweet") idTweet: Int, @Path("idUsuario") idUsuario: Int): Like

    @Headers("Content-Type: application/json")
    @POST("Likes")
    suspend fun darLike(@Body nuevoLike: RequestBody): Response<Like>

    @Headers("Content-Type: application/json")
    @DELETE("Likes/{idTweet}/{idUsuario}")
    suspend fun quitarLike(@Path("idTweet") idTweet: Int, @Path("idUsuario") idUsuario: Int): Like
}