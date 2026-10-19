package com.stone.kotlin.compose

import android.R
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlin.math.abs

/**
 * 滑动冲突：父子都想处理同一串 Move。
 *
 * 对照 View：
 * - 外部拦截：父 onInterceptTouchEvent，方向不对就 return false
 * - 内部拦截：子 requestDisallowInterceptTouchEvent(true)
 * - 同方向：不要两个竖滑套一起，NestedScroll 或合成一个列表
 *
 * Compose 里 nestedScroll 的 onPreScroll 就是父先下手；返回 Offset.Zero 表示不抢。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_SCROLL_CONFLICT)
class ComposeScrollConflictActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                ScrollConflictScreen()
            }
        }
    }
}

private val rowColors = listOf(
    Color(0xFFE85D4C),
    Color(0xFF4C8DFF),
    Color(0xFF7E57C2),
    Color(0xFF2BBBAD),
    Color(0xFFFFA726),
    Color(0xFFEC407A),
    Color(0xFF5C6BC0),
    Color(0xFF9CCC65),
)

@Composable
fun ScrollConflictScreen() {
    var resolved by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally), text = "滑动冲突", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, color = Color.Red)
        Text(
            if (resolved) {
                "解决版：同方向只留一个竖滑；横滑列表走 nestedScroll，父不抢横向。"
            } else {
                "冲突版：父在 onPreScroll 把竖向位移全吃掉，内层列表滑不动。"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = { resolved = !resolved },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (resolved) "切到冲突版" else "切到解决版")
        }

        if (resolved) {
            ResolvedContent()
        } else {
            ConflictContent()
        }
    }
}

/**
 * 父先把竖滑吃光，对照 onInterceptTouchEvent 直接 return true。
 */
private val greedyVerticalParent = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return if (abs(available.y) > abs(available.x)) Offset(0f, available.y) else Offset.Zero
    }
}

@Composable
private fun ConflictContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("外层还可以往下看", style = MaterialTheme.typography.titleMedium)
        HorizontalChips(title = "内层横滑（父没抢横向，这条还能左右滑）")
        Text("下面这块内层是竖滑，父在 onPreScroll 抢走了，基本滑不动：")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .nestedScroll(greedyVerticalParent)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
        ) {
            InnerVerticalList()
        }
        repeat(8) { i ->
            Text("外层底部 $i", modifier = Modifier.padding(vertical = 12.dp))
        }
    }
}

@Composable
private fun ResolvedContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { HorizontalChips(title = "不同方向：外竖内横，横着滑色块，竖着滑整页") }
        item { PagerWithList() }
        item {
            Text(
                "同方向：头、列表、尾都在这一个 LazyColumn 里，不再套第二层竖滑。",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        items(20) { i ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("条目 $i", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun HorizontalChips(title: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(rowColors.size) { i ->
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(72.dp)
                        .background(rowColors[i], RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${i + 1}", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PagerWithList() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "HorizontalPager + 每页 LazyColumn：横滑切页，竖滑列表（ViewPager2 套 RecyclerView）",
            style = MaterialTheme.typography.titleMedium,
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
        ) { page ->
            Card(modifier = Modifier.fillMaxSize()) {
                InnerVerticalList(prefix = "第${page + 1}页")
            }
        }
        Text(
            "第 ${pagerState.currentPage + 1} / 3 页",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InnerVerticalList(prefix: String = "内层") {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(18) { i ->
            Text(
                "$prefix · $i",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScrollConflictPreview() {
    KotlinTheme {
        ScrollConflictScreen()
    }
}
