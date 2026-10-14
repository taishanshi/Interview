package com.stone.interview.retrofit

/**
 * 对应 https://jsonplaceholder.typicode.com/posts 的 JSON 字段。
 * Gson 默认按字段名映射，不需要再写 @SerializedName。
 */
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
)
