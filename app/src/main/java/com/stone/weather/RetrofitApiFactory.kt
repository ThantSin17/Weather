package com.stone.weather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitApiFactory {
    var retrofit: Retrofit? = null

     fun instance() : Retrofit {
        if (retrofit == null) {
            val httpClient=OkHttpClient.Builder().addInterceptor(AppInterceptor())
//            val interceptor=AppInterceptor()
//            httpClient.addInterceptor(interceptor)
            retrofit=Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient.build())
                .build()

        }
        return retrofit!!

    }
}