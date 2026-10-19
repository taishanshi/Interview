package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.delay

/**
 * 李一桐页：上方 HorizontalPager 自动轮播 10 张，下方仍是 2×2 网格。
 * 轮播对照 ViewPager；LaunchedEffect(settledPage) + delay 就是定时切页。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_PHOTOS)
class ComposePhotoGridActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                PhotoGridScreen()
            }
        }
    }
}

private data class Photo(
    val title: String,
    val url: String,
)

private val photos = listOf(
    Photo("封面", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/Zt3v-fynshev4669576.jpg"),
    Photo("侧影", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/-e4V-fynnnse0367034.jpg"),
    Photo("杂志", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/ZnOx-fynship1503465.jpg"),
    Photo("冷峻", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/C20p-fynnnse0367049.jpg"),
    Photo("街拍", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/Xku7-fykymue6229064.jpg"),
    Photo("红外套", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/jgn6-fykymwm9086869.jpg"),
    Photo("微笑", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/2cxa-fykywuc3752949.jpg"),
    Photo("长腿", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/W7Ln-fykymwm9086873.jpg"),
    Photo("街拍2", "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/TJpe-fykywuc3752972.jpg"),
    Photo("天鹅", "https://n.sinaimg.cn/default/4_img/uplaod/3933d981/20171024/nyK9-fymzzpw0603425.jpg"),
)

@Composable
fun PhotoGridScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Text(
            "李一桐",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            "上边轮播 10 张，3 秒切一张；下边仍是 2×2。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        PhotoCarousel(photos)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(photos.take(4)) { photo ->
                Box {
                    AsyncImage(
                        model = photo.url,
                        contentDescription = "李一桐 ${photo.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Text(
                        photo.title,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoCarousel(photos: List<Photo>) {
    val pagerState = rememberPagerState(pageCount = { photos.size })
    LaunchedEffect(pagerState.settledPage) {
        delay(3000)
        pagerState.animateScrollToPage((pagerState.settledPage + 1) % photos.size)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(22.dp)),
        ) { page ->
            val photo = photos[page]
            Box {
                AsyncImage(
                    model = photo.url,
                    contentDescription = "李一桐 ${photo.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    "${photo.title}  ${page + 1}/${photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(photos.size) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 8.dp else 6.dp)
                        .background(
                            if (i == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.4f),
                            CircleShape,
                        ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoGridPreview() {
    KotlinTheme {
        PhotoGridScreen()
    }
}
