package com.stone.interview.foursymbols

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stone.interview.R

/**
 * TabLayout 点选 + ViewPager2 左右滑，由 [TabLayoutMediator] 双向绑定。
 * 四个 Fragment 各自独立：列表、网格、Glide、可勾选进度。
 */
class FourSymbolsActivity : AppCompatActivity() {

    private val titles = listOf("青龙", "白虎", "朱雀", "玄武")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_four_symbols)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabSymbols)
        val viewPager = findViewById<ViewPager2>(R.id.pagerSymbols)
        viewPager.adapter = FourSymbolsPagerAdapter(this)
        viewPager.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
