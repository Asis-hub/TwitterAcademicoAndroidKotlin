package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.Api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun registrarUsuario(@Url url: String): Response<UsuarioResponse>
}