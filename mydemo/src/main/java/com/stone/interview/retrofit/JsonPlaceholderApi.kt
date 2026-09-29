package com.stone.interview.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit 用动态代理把接口方法转成 HTTP 请求。
 * 返回 Call，用 enqueue 异步、execute 同步（不能在主线程 execute）。
 */
interface JsonPlaceholderApi {

    @GET("posts/{id}")
    fun getPost(@Path("id") id: Int): Call<Post>

    @GET("posts")
    fun listPosts(@Query("userId") userId: Int): Call<List<Post>>

    @POST("posts")
    fun createPost(@Body post: Post): Call<Post>
}
