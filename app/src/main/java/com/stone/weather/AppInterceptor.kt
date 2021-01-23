package com.stone.weather

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AppInterceptor : Interceptor{
    companion object {
        private const val API_KEY = "3ab59769510d39f9b28e9627ae9826cd"
        private const val UNIT="metric"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url=chain.request().url
        val newUrl=url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .addQueryParameter("units", UNIT)
            .build()
        return chain.proceed(Request.Builder().url(newUrl).build())
    }
}