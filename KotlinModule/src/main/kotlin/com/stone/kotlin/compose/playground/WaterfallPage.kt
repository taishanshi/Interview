package com.stone.kotlin.compose.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stone.kotlin.ui.theme.KotlinTheme

@Composable
fun WaterfallPage() {
    Column(Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "瀑布流演示",
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            color = Color.Cyan,
            style = MaterialTheme.typography.headlineSmall)

        waterfallScreen(2, 10.dp, 10.dp) {
            waterfallCards.forEach {card ->
                Card(modifier = Modifier
                        .height(card.height),
                    colors = CardDefaults.cardColors(containerColor = card.color),) {
                    Box(Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Text(text = card.title,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp),
                            color = Color.White
                        )
                    }
                }


            }
        }
    }
}

@Composable
private fun waterfallScreen(
    columnSize: Int,
    horizontalGap: Dp,
    verticalGap: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ()-> Unit
) {
    Layout(content = content, modifier = modifier.fillMaxWidth()) {  measurables, constraints ->
        val hGap = horizontalGap.roundToPx()
        val vGap = verticalGap.roundToPx()
        val columnWidth = (constraints.maxWidth - hGap*(columnSize - 1)) / columnSize.coerceAtLeast(0)
        val itemConstraints = constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = 0,
        )
        val placeables = measurables.map { it.measure(itemConstraints) }

        val colHeights = IntArray(columnSize)
        val xs = IntArray(placeables.size)
        val ys = IntArray(placeables.size)

        placeables.forEachIndexed { index, placeable ->
            val col = colHeights.indices.minBy { colHeights[it] }
            val y = if(colHeights[col] == 0) 0 else colHeights[col] + vGap
            xs[index] = (columnWidth + hGap) * col
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

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PlaygroundHomePreview() {
    KotlinTheme(dynamicColor = false) {
        WaterfallPage()
    }
}
