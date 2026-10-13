package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * Compose 2×2 网格。对照 XML 的 GridView numColumns=2。
 * 四张都是李一桐，新浪图床，国内可加载。
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
    Photo(
        "封面",
        "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/Zt3v-fynshev4669576.jpg",
    ),
    Photo(
        "侧影",
        "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/-e4V-fynnnse0367034.jpg",
    ),
    Photo(
        "街拍",
        "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/Xku7-fykymue6229064.jpg",
    ),
    Photo(
        "微笑",
        "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/2cxa-fykywuc3752949.jpg",
    ),
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
            "2×2 网格，四张靓照。LazyVerticalGrid 对照 GridView。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(photos) { photo ->
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

@Preview(showBackground = true)
@Composable
private fun PhotoGridPreview() {
    KotlinTheme {
        PhotoGridScreen()
    }
}
