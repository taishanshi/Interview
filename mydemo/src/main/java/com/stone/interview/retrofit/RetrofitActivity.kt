package com.stone.interview.retrofit

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity 里直接 enqueue：后台发请求，onResponse / onFailure 回调到主线程。
 * 不要用 Call.execute()，那是同步的，主线程会 NetworkOnMainThreadException。
 */
class RetrofitActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var inFlight: Call<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_retrofit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvResult = findViewById(R.id.tvResult)
        val api = RetrofitClient.api

        findViewById<Button>(R.id.btnGetOne).setOnClickListener {
            tvResult.text = "GET /posts/1\n请求中..."
            val call = api.getPost(1)
            track(call)
            call.enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    val post = response.body()
                    if (!response.isSuccessful || post == null) {
                        tvResult.text = "GET /posts/1\nHTTP ${response.code()}"
                        return
                    }
                    tvResult.text =
                        "GET /posts/1\nid=${post.id}\nuserId=${post.userId}\ntitle=${post.title}\nbody=${post.body}"
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    if (!call.isCanceled) {
                        tvResult.text = "GET /posts/1\n失败: ${t.message}"
                    }
                }
            })
        }

        findViewById<Button>(R.id.btnGetList).setOnClickListener {
            tvResult.text = "GET /posts?userId=1\n请求中..."
            val call = api.listPosts(1)
            track(call)
            call.enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    val list = response.body()
                    if (!response.isSuccessful || list == null) {
                        tvResult.text = "GET /posts?userId=1\nHTTP ${response.code()}"
                        return
                    }
                    tvResult.text = buildString {
                        appendLine("GET /posts?userId=1")
                        appendLine("size=${list.size}")
                        list.take(3).forEach { appendLine("- [${it.id}] ${it.title}") }
                        if (list.size > 3) append("...")
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    if (!call.isCanceled) {
                        tvResult.text = "GET /posts?userId=1\n失败: ${t.message}"
                    }
                }
            })
        }

        findViewById<Button>(R.id.btnPost).setOnClickListener {
            tvResult.text = "POST /posts\n请求中..."
            val body = Post(userId = 1, title = "retrofit demo", body = "hello")
            val call = api.createPost(body)
            track(call)
            call.enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    val post = response.body()
                    if (!response.isSuccessful || post == null) {
                        tvResult.text = "POST /posts\nHTTP ${response.code()}"
                        return
                    }
                    tvResult.text =
                        "POST /posts\n创建成功 id=${post.id}\ntitle=${post.title}\nbody=${post.body}"
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    if (!call.isCanceled) {
                        tvResult.text = "POST /posts\n失败: ${t.message}"
                    }
                }
            })
        }
    }

    private fun track(call: Call<*>) {
        inFlight?.cancel()
        inFlight = call
    }

    override fun onDestroy() {
        inFlight?.cancel()
        super.onDestroy()
    }
}
