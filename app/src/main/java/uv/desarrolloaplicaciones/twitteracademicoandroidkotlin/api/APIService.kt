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
    suspend fun getUsuario(@Header("access-token") token: String, @Path("id")idUsuario: Int): Usuario

    @POST("Login")
    suspend fun logearse(@Body usuario: RequestBody): Usuario

    @GET("Usuario")
    suspend fun getUsuarios(@Header("access-token") token: String) : List<Usuario>

    @Headers("Content-Type: application/json")
    @POST("Usuario")
    suspend fun registrarUsuario(@Body nuevoUsuario: RequestBody): Usuario

    @PUT("Usuario/{idUsuario}")
    suspend fun modificarUsuario(@Header("access-token") token: String, @Path("idUsuario") idUsuario: Int,
                                 @Body nuevaInfoUsuario: RequestBody): Usuario

    @DELETE("Usuario/{idUsuario}")
    suspend fun eliminarUsuario(@Header("access-token") token: String,
                                @Path("idUsuario") idUsuario: Int): Usuario
    
    //SEGUIDOR

    @POST("Seguidor")
    suspend fun seguirUsuario(@Header("access-token") token: String, @Body seguirUsuario: RequestBody): Seguidor

    @DELETE("Seguidor/{idUsuario}/{idSeguidor}")
    suspend fun dejarSeguir(
        @Header("access-token") token: String,
        @Path("idUsuario") idUsuarioSeguido: Int,
        @Path("idSeguidor") idUsuarioSeguidor: Int
    ): Seguidor

    //Verifica si un usuario sigue a otro
    @GET("Seguidor/{idUsuarioComparacion}/{idUsuario}")
    suspend fun verificarSeguidor(
        @Header("access-token") token: String,
        @Path("idUsuario") idUsuario: Int,
        @Path("idUsuarioComparacion") idUsuarioComparacion: Int
    ): Seguidor

    //Recupera una lista de los seguidores de un usuario definido
    @GET("Seguidores/{idUsuario}")
    suspend fun recuperarSeguidores(@Header("access-token") token: String, @Path("idUsuario") idUsuario: Int): List<Seguidor>

    //TWEETS

    @GET("Tweet")
    suspend fun recuperarTweets(@Header("access-token") token: String): List<Tweet>

    @GET("Tweet/{id}")
    suspend fun recuperarTweets(@Header("access-token") token: String, @Path("id") idUsuario: Int): List<Tweet>

    @GET("TweetPerfil/{id}")
    suspend fun recuperarTweetsPerfil(@Header("access-token") token: String, @Path("id") idUsuario: Int): List<Tweet>

    @Headers("Content-Type: application/json")
    @POST("Tweet")
    suspend fun registrarTweet(@Header("access-token") token: String, @Body nuevoTweet: RequestBody): Response<Tweet>

    @DELETE("Tweet/{idTweet}")
    suspend fun eliminarTweet(@Header("access-token") token: String, @Path("idTweet") idTweet: Int): Tweet

    @GET("Tweet/Content/{keyword}")
    suspend fun buscarTweetContenido(@Header("access-token") token: String, @Path("keyword") keyword: String): List<Tweet>

    @PUT("Tweet/{idTweet}")
    suspend fun modificarTweet(
        @Header("access-token") token: String,
        @Body tweetModificado: RequestBody,
        @Path("idTweet") idTweet: Int
    ): Tweet

    @GET("TweetByID/{idTweet}")
    suspend fun getTweet(
        @Header("access-token") token: String,
        @Path("idTweet") idTweet: Int
    ): Tweet

    //LIKES
  
    @GET("Likes/{idTweet}/{idUsuario}")
    suspend fun isLiked(@Header("access-token") token: String, @Path("idTweet") idTweet: Int, @Path("idUsuario") idUsuario: Int): Like

    @Headers("Content-Type: application/json")
    @POST("Likes")
    suspend fun darLike(@Header("access-token") token: String, @Body nuevoLike: RequestBody): Response<Like>

    @Headers("Content-Type: application/json")
    @DELETE("Likes/{idTweet}/{idUsuario}")
    suspend fun quitarLike(@Header("access-token") token: String, @Path("idTweet") idTweet: Int, @Path("idUsuario") idUsuario: Int): Like
}