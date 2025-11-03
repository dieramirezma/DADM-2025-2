package com.example.myapplication

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val appToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-App-Token", appToken)
            .build()
        return chain.proceed(request)
    }
}
