package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme

/**
 * 自定义瀑布流：不用 LazyVerticalStaggeredGrid，自己用 [Layout] 测量、摆放。
 * 规则：固定两列，每个卡片放进当前最矮的那一列。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_WATERFALL)
class ComposeWaterfallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                WaterfallScreen()
            }
        }
    }
}

private data class WaterfallCard(
    val title: String,
    val color: Color,
    val height: Dp,
)

private val waterfallCards = listOf(
    WaterfallCard("茜红", Color(0xFFE85D4C), 140.dp),
    WaterfallCard("晴蓝", Color(0xFF4C8DFF), 200.dp),
    WaterfallCard("藤紫", Color(0xFF7E57C2), 120.dp),
    WaterfallCard("松绿", Color(0xFF2BBBAD), 180.dp),
    WaterfallCard("橙黄", Color(0xFFFFA726), 100.dp),
    WaterfallCard("玫粉", Color(0xFFEC407A), 220.dp),
    WaterfallCard("青灰", Color(0xFF546E7A), 150.dp),
    WaterfallCard("靛蓝", Color(0xFF5C6BC0), 110.dp),
    WaterfallCard("柠绿", Color(0xFF9CCC65), 190.dp),
    WaterfallCard("暖杏", Color(0xFFFF8A65), 130.dp),
)

@Composable
fun WaterfallScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("自定义瀑布流", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Layout 自己测、自己摆。每列取当前最短列放下一个卡片，所以高低错开。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        WaterfallLayout(columns = 2, horizontalGap = 8.dp, verticalGap = 8.dp) {
            waterfallCards.forEach { card ->
                Card(
                    modifier = Modifier.height(card.height),
                    colors = CardDefaults.cardColors(containerColor = card.color),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Text(card.title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterfallLayout(
    columns: Int,
    horizontalGap: Dp,
    verticalGap: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier.fillMaxWidth()) { measurables, constraints ->
        val hGap = horizontalGap.roundToPx()
        val vGap = verticalGap.roundToPx()
        val columnWidth = ((constraints.maxWidth - hGap * (columns - 1)) / columns).coerceAtLeast(0)
        val itemConstraints = constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = 0,
        )
        val placeables = measurables.map { it.measure(itemConstraints) }
        val colHeights = IntArray(columns)
        val xs = IntArray(placeables.size)
        val ys = IntArray(placeables.size)

        placeables.forEachIndexed { index, placeable ->
            val col = colHeights.indices.minBy { colHeights[it] }
            val y = if (colHeights[col] == 0) 0 else colHeights[col] + vGap
            xs[index] = col * (columnWidth + hGap)
            ys[index] = y
            colHeights[col] = y + placeable.height
        }

        val height = colHeights.max().coerceAtLeast(constraints.minHeight)
        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(xs[index], ys[index])
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterfallPreview() {
    KotlinTheme {
        WaterfallScreen()
    }
}
