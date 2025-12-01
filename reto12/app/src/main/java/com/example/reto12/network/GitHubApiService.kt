package com.example.reto12.network

import com.example.reto12.data.AccessTokenResponse
import com.example.reto12.data.GitHubUser
import retrofit2.http.*

interface GitHubApiService {

    @Headers("Accept: application/json")
    @POST("https://github.com/login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): AccessTokenResponse

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): GitHubUser
}

