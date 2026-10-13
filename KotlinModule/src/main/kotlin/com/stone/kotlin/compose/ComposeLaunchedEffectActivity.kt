package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.delay

/**
 * LaunchedEffect：在组合里启动协程。
 * 进入组合时启动，离开组合或 key 变化时取消（再按新 key 重开）。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_LAUNCHED_EFFECT)
class ComposeLaunchedEffectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                LaunchedEffectScreen()
            }
        }
    }
}

@Composable
fun LaunchedEffectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("LaunchedEffect", style = MaterialTheme.typography.headlineSmall)
        Text(
            "不是用来画 UI 的，是用来跑挂起逻辑：请求、delay、collect。生命周期跟这块 Composable 绑在一起。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TickerCard()
        DebounceCard()
        KeyRestartCard()
    }
}

@Composable
private fun TickerCard() {
    var ticks by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }

    if (running) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                ticks++
            }
        }
    }

    DemoCard(
        title = "1. 进入就跑，拿掉就取消",
        body = "LaunchedEffect(Unit) 进组合启动。关掉开关后这块离开组合，协程被取消，秒表停。",
    ) {
        Text("已走 $ticks 秒")
        Button(onClick = { running = !running }, modifier = Modifier.fillMaxWidth()) {
            Text(if (running) "停掉 Effect（取消协程）" else "再开 Effect")
        }
        if (!running) {
            Button(onClick = { ticks = 0 }, modifier = Modifier.fillMaxWidth()) {
                Text("清零")
            }
        }
    }
}

@Composable
private fun DebounceCard() {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("输入后停 500ms 才搜") }

    LaunchedEffect(query) {
        if (query.isBlank()) {
            result = "输入后停 500ms 才搜"
            return@LaunchedEffect
        }
        result = "等待中…"
        delay(500)
        result = "搜索结果：$query"
    }

    DemoCard(
        title = "2. key 变了会取消上一次",
        body = "LaunchedEffect(query)：每敲一个字，上一次 delay 被取消。停手 500ms 才出结果，就是防抖。",
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("搜索") },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(result)
    }
}

@Composable
private fun KeyRestartCard() {
    var userId by remember { mutableIntStateOf(1) }
    var log by remember { mutableStateOf("还没加载") }

    LaunchedEffect(userId) {
        log = "正在加载用户 $userId …"
        delay(800)
        log = "用户 $userId 加载完成"
    }

    DemoCard(
        title = "3. 按 key 重新加载",
        body = "userId 变了，旧请求取消、新请求开始。对照 View：key 就是你手动 job.cancel() 再开一个。",
    ) {
        Text(log)
        Button(
            onClick = { userId = if (userId == 1) 2 else 1 },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("切换用户（当前 $userId）")
        }
    }
}

@Composable
private fun DemoCard(
    title: String,
    body: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LaunchedEffectPreview() {
    KotlinTheme {
        LaunchedEffectScreen()
    }
}
