package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.launch

/**
 * Compose 入门页。
 *
 * 对照 XML 记三件事：
 * 1. @Composable 函数代替布局文件，界面是函数调用出来的。
 * 2. 状态用 remember { mutableStateOf }，改状态会触发重组（类似 notifyDataSetChanged）。
 * 3. Modifier 链式配置宽高、边距、背景，类似 LayoutParams + 一堆 xml 属性。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE)
class ComposeBasicsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                ComposeBasicsScreen()
            }
        }
    }
}

@Composable
fun ComposeBasicsScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Compose 基础",
            modifier = Modifier.fillMaxWidth()
                .background(Color.Yellow)
                .align(Alignment.CenterHorizontally)
                .padding(20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall)
        Text(
            "状态变了，相关 Composable 会重组。点按钮、改输入，看下面跟着变。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SectionCard(title = "Text / Button / TextField", color = Color.Yellow) {
            var name by remember { mutableStateOf("") }
            var count by remember { mutableIntStateOf(0) }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名字") },
                prefix = {Text("$") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = { count++ }) {
                Text(if (name.isBlank()) "点了 $count 次" else "$name 点了 $count 次")
            }
        }

        SectionCard(title = "横向跑马灯：basicMarquee") {
            Text(
                "公告：李一桐新剧开播 · 文字比容器宽才会滚 · 对照 View 的 marquee / TextView ellipsize=marquee",
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(iterations = Int.MAX_VALUE),
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "短文字不够宽，不会动。",
                modifier = Modifier.basicMarquee(),
                maxLines = 1,
            )
        }

        SectionCard(title = "自定义 Button：圆角 + 对角渐变 + 波纹") {
            var clicks by remember { mutableIntStateOf(0) }
            val shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 6.dp,
                bottomStart = 6.dp,
                bottomEnd = 24.dp,
            )
            val gradient = Brush.linearGradient(
                colors = listOf(Color(0xFFFF6B81), Color(0xFF5B6CFF), Color(0x66666666)),
                start = Offset.Zero,
                end = Offset.Infinite,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(shape)
                    .background(gradient)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = {
                            clicks++
                            scope.launch {
                                snackbarHostState.showSnackbar("已点 $clicks 次")
                            }
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (clicks == 0) "点我看波纹 / Snackbar" else "已点 $clicks 次",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        SectionCard(title = "Dialog / Popup：新建 Window") {
            var showDialog by remember { mutableStateOf(false) }
            var showPopup by remember { mutableStateOf(false) }
            Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("打开 Dialog")
            }
            Box {
                Button(onClick = { showPopup = !showPopup }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (showPopup) "关闭 Popup" else "打开 Popup")
                }
                if (showPopup) {
                    Popup(
                        alignment = Alignment.TopCenter,
                        onDismissRequest = { showPopup = false },
                        properties = PopupProperties(focusable = true),
                    ) {
                        Card(
                            modifier = Modifier.padding(top = 56.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF5B6CFF)),
                        ) {
                            Text(
                                "Popup 也是新 Window，贴着按钮",
                                modifier = Modifier.padding(16.dp),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Card {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text("这是一个新 Window", style = MaterialTheme.typography.titleMedium)
                            Text("底层等价于 WindowManager.addView，把这块 Compose 内容放进独立窗口。")
                            Button(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                                Text("关闭")
                            }
                        }
                    }
                }
            }
        }

        SectionCard(title = "Switch / Checkbox / Slider") {
            var enabled by remember { mutableStateOf(true) }
            var checked by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0.4f) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("开关", modifier = Modifier.weight(1f))
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Text(if (checked) "已勾选" else "未勾选")
            }
            Text("进度 ${(progress * 100).toInt()}%")
            Slider(
                value = progress,
                onValueChange = { progress = it },
                enabled = enabled,
            )
        }

        SectionCard(title = "AnimatedVisibility：显隐带动画") {
            var visible by remember { mutableStateOf(true) }
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "藏起来" else "显示出来")
            }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("我是带动画出现/消失的块")
                }
            }
        }

        SectionCard(title = "animate*AsState：跟状态走") {
            var big by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (big) 1.4f else 1f,
                animationSpec = tween(400),
                label = "scale",
            )
            val color by animateColorAsState(
                targetValue = if (big) Color(0xFF2F80ED) else Color(0xFFB39DDB),
                animationSpec = tween(400),
                label = "color",
            )
            Button(onClick = { big = !big }) {
                Text(if (big) "变回去" else "放大变色")
            }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(scale)
                    .background(color, CircleShape)
                    .align(Alignment.CenterHorizontally),
            )
        }

        SectionCard(title = "InfiniteTransition：一直动") {
            val infinite = rememberInfiniteTransition(label = "pulse")
            val pulse by infinite.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulse",
            )
            val offsetX by infinite.animateFloat(
                initialValue = -200f,
                targetValue = 200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "offsetX"
            )
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(pulse)
                    .graphicsLayer {
                        translationX = offsetX
                    }
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                    .align(Alignment.CenterHorizontally),
            )
        }
        Spacer(Modifier.height(12.dp))
    }
    }
}

@Composable
private fun SectionCard(
    title: String,
    color: Color = Color.White,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth().background(color).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComposeBasicsPreview() {
    KotlinTheme {
        ComposeBasicsScreen()
    }
}
