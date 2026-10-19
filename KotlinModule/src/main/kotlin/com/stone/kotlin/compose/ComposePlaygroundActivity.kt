package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.compose.playground.AccompanistPage
import com.stone.kotlin.compose.playground.CanvasPage
import com.stone.kotlin.compose.playground.ComposeBasicsPage
import com.stone.kotlin.compose.playground.HandwritePage
import com.stone.kotlin.compose.playground.LaunchedEffectPage
import com.stone.kotlin.compose.playground.LiveDataPage
import com.stone.kotlin.compose.playground.NavigationPage
import com.stone.kotlin.compose.playground.PhotoGridPage
import com.stone.kotlin.compose.playground.ScaffoldPage
import com.stone.kotlin.compose.playground.ScrollConflictPage
import com.stone.kotlin.compose.playground.TabScrollPage
import com.stone.kotlin.compose.playground.WaterfallPage
import com.stone.kotlin.ui.theme.KotlinTheme

/**
 * 和 KotlinHomeActivity 同一套首页。
 * 点击卡片走 NavHost，不另开 Activity。各目标页先只显示名字。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_PLAYGROUND)
class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                PlaygroundScreen()
            }
        }
    }
}

private class PlaygroundEntry(
    val title: String,
    val subtitle: String,
    val route: String,
    val page: @Composable () -> Unit,
)

private const val HOME_ROUTE = "home"

private val playgroundEntries = listOf(
    PlaygroundEntry("Compose 基础", "控件与动画", "compose_basics") { ComposeBasicsPage() },
    PlaygroundEntry("Scaffold", "页面骨架", "scaffold") { ScaffoldPage() },
    PlaygroundEntry("瀑布流", "自定义 Layout", "waterfall") { WaterfallPage() },
    PlaygroundEntry("Compose Tab", "Pager + 滚动", "tab") { TabScrollPage() },
    PlaygroundEntry("Compose 画布", "贝塞尔与手势", "canvas") { CanvasPage() },
    PlaygroundEntry("李一桐", "轮播 + 2×2", "photo_grid") { PhotoGridPage() },
    PlaygroundEntry("Compose 手写", "空白练习页", "handwrite") { HandwritePage() },
    PlaygroundEntry("Accompanist", "权限封装", "accompanist") { AccompanistPage() },
    PlaygroundEntry("LiveData", "delay 模拟请求", "livedata") { LiveDataPage() },
    PlaygroundEntry("LaunchedEffect", "组合里的协程", "launched_effect") { LaunchedEffectPage() },
    PlaygroundEntry("滑动冲突", "内外拦截", "scroll_conflict") { ScrollConflictPage() },
)

private val cardPalette = listOf(
    Color(0xFF2F80ED),
    Color(0xFF5E35B1),
    Color(0xFF00897B),
    Color(0xFFE53935),
    Color(0xFFEF6C00),
    Color(0xFF1565C0),
    Color(0xFF6A1B9A),
    Color(0xFF2E7D32),
)

@Composable
private fun PlaygroundScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(HOME_ROUTE) {
            PlaygroundHome(onOpen = { route -> navController.navigate(route) })
        }
        playgroundEntries.forEach { entry ->
            composable(entry.route) {
                entry.page()
            }
        }
    }
}

@Composable
private fun PlaygroundHome(onOpen: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F8))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 12.dp),
    ) {
        Text(
            text = "Kotlin",
            modifier = Modifier.padding(top = 4.dp),
            color = Color(0xFF152033),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(width = 100.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF2F80ED)),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 20.dp),
            contentPadding = PaddingValues(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(playgroundEntries, key = { _, item -> item.route }) { index, entry ->
                PlaygroundCard(
                    entry = entry,
                    badgeColor = cardPalette[index % cardPalette.size],
                    onClick = { onOpen(entry.route) },
                )
            }
        }
    }
}

@Composable
private fun PlaygroundCard(
    entry: PlaygroundEntry,
    badgeColor: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE4E8EE), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(badgeColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = entry.title.first().toString(),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = entry.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            color = Color(0xFF1A2433),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (entry.subtitle.isNotBlank()) {
            Text(
                text = entry.subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                color = Color(0xFF8A94A6),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PlaygroundHomePreview() {
    KotlinTheme(dynamicColor = false) {
        PlaygroundHome(onOpen = {})
    }
}
