package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.launch

/**
 * Compose 版 Tab + 可滚动内容。
 *
 * 对照 View 体系：
 * - [PrimaryScrollableTabRow] ≈ TabLayout
 * - [HorizontalPager] ≈ ViewPager2
 * - 点 Tab 调 [androidx.compose.foundation.pager.PagerState.animateScrollToPage]
 * - 手指横滑 Pager，[PagerState.currentPage] 反过来驱动 Tab 选中
 *
 * 每个 Tab 页里再套 [LazyColumn] / [Modifier.verticalScroll]，就是 ScrollView。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_TAB)
class ComposeTabScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                TabScrollScreen()
            }
        }
    }
}

private data class TabPage(
    val title: String,
    val color: Color,
    val items: List<String>,
)

private val tabPages = listOf(
    TabPage("青龙", Color(0xFF1B5E20), listOf("角", "亢", "氐", "房", "心", "尾", "箕") + lore(7)),
    TabPage("白虎", Color(0xFF4E342E), listOf("奎", "娄", "胃", "昴", "毕", "觜", "参") + lore(7)),
    TabPage("朱雀", Color(0xFFB71C1C), listOf("井", "鬼", "柳", "星", "张", "翼", "轸") + lore(7)),
    TabPage("玄武", Color(0xFF0D47A1), listOf("斗", "牛", "女", "虚", "危", "室", "壁") + lore(7)),
)

private fun lore(n: Int): List<String> = List(n) { "说明 ${it + 1}：这一页可以上下滑，横滑切 Tab。" }

@Composable
fun TabScrollScreen() {
    val pagerState = rememberPagerState(pageCount = { tabPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Text(
            "Tab + 滚动",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            "上面点 Tab 或左右滑；每一页自己上下滚。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        PrimaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            tabPages.forEachIndexed { index, page ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(page.title) },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val data = tabPages[page]
            if (page % 2 == 0) {
                // 偶数页：LazyColumn，对照 RecyclerView
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(data.color.copy(alpha = 0.12f)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(data.items) { line ->
                        TabCard(data.color, line)
                    }
                }
            } else {
                // 奇数页：verticalScroll，对照 ScrollView
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(data.color.copy(alpha = 0.12f))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    data.items.forEach { line ->
                        TabCard(data.color, line)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabCard(color: Color, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.85f)),
    ) {
        Text(
            text,
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TabScrollPreview() {
    KotlinTheme {
        TabScrollScreen()
    }
}
