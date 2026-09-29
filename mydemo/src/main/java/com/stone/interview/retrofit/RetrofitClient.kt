package com.stone.interview.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Logger

/**
 * 经典单例：OkHttpClient + Converter + Retrofit.create(Api)。
 * baseUrl 必须以 / 结尾，接口上的 path 不要再写前导域名。
 */
object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor(object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    Logger.getAnonymousLogger().info { "wwwwwww$request" }
                    val response = chain.proceed(request)
                    Logger.getAnonymousLogger().info { "wwwwwww$response" }
                    return response
                }
            })
            .build()
    }

    val api: JsonPlaceholderApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonPlaceholderApi::class.java)
    }
}
