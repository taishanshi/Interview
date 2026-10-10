package com.stone.interview.glide

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.stone.interview.R

/**
 * 经典三连：Glide.with(this).load(url).into(imageView)
 *
 * 看源码入口：
 * - [Glide.with] → RequestManagerRetriever，绑定 Activity 生命周期
 * - [com.bumptech.glide.RequestBuilder.load] → 记下 model
 * - [com.bumptech.glide.RequestBuilder.into] → 建 SingleRequest，Engine 里读缓存 / 解码 / 显示
 */
class GlideActivity : AppCompatActivity() {

    companion object {
        // 新浪图床，国内可访问（李一桐杂志封面）
        private const val IMAGE_URL =
            "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/Zt3v-fynshev4669576.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_glide)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageView = findViewById<ImageView>(R.id.ivPhoto)
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            Glide.with(this)
                .load(IMAGE_URL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView)
        }
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            Glide.with(this).clear(imageView)
        }
    }
}
