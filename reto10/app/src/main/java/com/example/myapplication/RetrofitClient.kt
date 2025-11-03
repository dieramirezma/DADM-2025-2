package com.example.myapplication

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://www.datos.gov.co/resource/"
    // 🔐 Token se lee desde local.properties
    private val APP_TOKEN = BuildConfig.APP_TOKEN

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor(APP_TOKEN))
        .addInterceptor(logging)
        .build()

    val api: ZonaWifiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZonaWifiApi::class.java)
    }
}
