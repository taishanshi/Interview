package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme

/**
 * 空白 Compose 页，给自己练手。
 * 改 [PlaygroundScreen] 即可，不必动 Activity。
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

@Composable
fun PlaygroundScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Text("在 PlaygroundScreen() 里开始写")
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaygroundPreview() {
    KotlinTheme {
        PlaygroundScreen()
    }
}
