package com.stone.kotlin.compose

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.random.Random

/**
 * Compose 自定义绘制。
 *
 * 对照自定义 View：
 * - [Canvas] { } 里的 DrawScope ≈ onDraw(android.graphics.Canvas)
 * - 手势在 [Modifier.pointerInput]，和绘制拆开（View 往往写在同一个 onTouchEvent）
 * - 改 [androidx.compose.runtime.mutableStateListOf] 会重组，Canvas 自动重画
 *
 * 这一页：三次贝塞尔 + 可拖控制点 + 沿路径跑的彗星 + 雷达扫光。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_CANVAS)
class ComposeCanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                BezierBoardScreen()
            }
        }
    }
}

@Composable
fun BezierBoardScreen() {
    val points = remember { mutableStateListOf<Offset>() }
    var selected by remember { mutableIntStateOf(-1) }
    var boardSize by remember { mutableStateOf(IntSize.Zero) }

    val infinite = rememberInfiniteTransition(label = "board")
    val traveler by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "traveler",
    )
    val radar by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
        ),
        label = "radar",
    )
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color(0xFF111820)),
    ) {
        Text(
            "自定义 Canvas",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            "拖四个点改曲线。点是控制柄，亮点沿路径跑。",
            color = Color(0xFF9AA7B8),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .onSizeChanged { size ->
                    boardSize = size
                    if (points.isEmpty() && size.width > 0) {
                        points.addAll(defaultPoints(size))
                    }
                }
                .pointerInput(Unit) {
                    val hit = 28.dp.toPx()
                    detectDragGestures(
                        onDragStart = { start ->
                            selected = points.indices.minByOrNull { i ->
                                dist(points[i], start)
                            }?.takeIf { i -> dist(points[i], start) < hit } ?: -1
                        },
                        onDrag = { change, amount ->
                            change.consume()
                            val i = selected
                            if (i in points.indices) {
                                val next = points[i] + amount
                                points[i] = Offset(
                                    next.x.coerceIn(0f, size.width.toFloat()),
                                    next.y.coerceIn(0f, size.height.toFloat()),
                                )
                            }
                        },
                    )
                },
        ) {
            if (points.size == 4) {
                BezierCanvas(
                    points = points,
                    selected = selected,
                    traveler = traveler,
                    radar = radar,
                    pulse = pulse,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = {
                    if (boardSize.width > 0) {
                        points.clear()
                        points.addAll(defaultPoints(boardSize))
                    }
                    selected = -1
                },
                modifier = Modifier.weight(1f),
            ) { Text("重置") }
            Button(
                onClick = {
                    if (boardSize.width > 0) {
                        points.clear()
                        points.addAll(randomPoints(boardSize))
                    }
                    selected = -1
                },
                modifier = Modifier.weight(1f),
            ) { Text("随机") }
        }
    }
}

@Composable
private fun BezierCanvas(
    points: List<Offset>,
    selected: Int,
    traveler: Float,
    radar: Float,
    pulse: Float,
    modifier: Modifier = Modifier,
) {
    val p0 = points[0]
    val p1 = points[1]
    val p2 = points[2]
    val p3 = points[3]
    Canvas(modifier.background(Color(0xFF1A2332))) {
        val grid = 28.dp.toPx()
        // 背景竖网格线
        var x = 0f
        while (x < size.width) {
            drawLine(Color(0x22FFFFFF), Offset(x, 0f), Offset(x, size.height), 1f)
            x += grid
        }
        // 背景横网格线
        var y = 0f
        while (y < size.height) {
            drawLine(Color(0x22FFFFFF), Offset(0f, y), Offset(size.width, y), 1f)
            y += grid
        }

        val cx = size.width / 2f
        val cy = size.height / 2f
        // 雷达扫描线：绕画板中心旋转的绿线
        rotate(radar, Offset(cx, cy)) {
            drawLine(
                Color(0x3322C55E),
                Offset(cx, cy),
                Offset(cx, 24f),
                strokeWidth = 6f,
            )
        }
        // 雷达射程圈：中心淡绿圆
        drawCircle(Color(0x2222C55E), radius = size.minDimension * 0.28f, center = Offset(cx, cy))
        val shape = Path().apply {
            // 一个朝上的小三角形
            moveTo(0f, -10f)
            lineTo(8f, 10f)
            lineTo(-8f, 10f)
            close()
        }
//        val line = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
        val line = PathEffect.stampedPathEffect(shape ,advance = 40f,      // 每 40px 盖一个章
            phase = 0f,
            style = StampedPathEffectStyle.Rotate)
        // 控制柄虚线：起点 P0 → 控制点 P1
        drawLine(Color(0x88FFFFFF), p0, p1, 3f, pathEffect = line)
        // 控制柄虚线：终点 P3 → 控制点 P2
        drawLine(Color(0x88FFFFFF), p3, p2, 3f, pathEffect = line)

        val path = Path().apply {
            moveTo(p0.x, p0.y)
            cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        }
        // 三次贝塞尔曲线：P0 出发，P1/P2 作控制点，落到 P3
        drawPath(
            path,
            Color(0xFF56CCF2),
            style = Stroke(width = 8f, cap = StrokeCap.Round),
        )

        val measure = PathMeasure()
        measure.setPath(path, false)
        val length = measure.length
        if (length > 1f) {
            // 彗星尾巴：沿曲线落后于头部的一串渐隐圆点
            for (i in 8 downTo 0) {
                val t = ((traveler - i * 0.035f + 1f) % 1f)
                val pos = measure.getPosition(length * t)
                drawCircle(
                    Color(0xFFFFCC80).copy(alpha = 1f - i / 9f),
                    radius = 10f - i * 0.7f,
                    center = pos,
                )
            }
            val head = measure.getPosition(length * traveler)
            val tan = measure.getTangent(length * traveler)
            val angle = Math.toDegrees(atan2(tan.y, tan.x).toDouble()).toFloat()
            rotate(angle, head) {
                val needle = Path().apply {
                    moveTo(head.x + 18f, head.y)
                    lineTo(head.x - 10f, head.y - 8f)
                    lineTo(head.x - 10f, head.y + 8f)
                    close()
                }
                // 彗星头：沿切线方向的小三角
                drawPath(needle, Color(0xFFFF8A65))
            }
        }

        val labels = listOf("P0", "P1", "P2", "P3")
        val colors = listOf(
            Color(0xFF56CCF2),
            Color(0xFFB39DDB),
            Color(0xFFFF8A65),
            Color(0xFF81C784),
        )
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            isAntiAlias = true
        }
        points.forEachIndexed { i, point ->
            val r = if (i == selected) 16f * pulse else 14f
            // 控制点光晕（选中时会随 pulse 放大）
            drawCircle(colors[i].copy(alpha = 0.35f), r + 10f, point)
            // 控制点实心圆：P0 起点 / P1 P2 控制点 / P3 终点
            drawCircle(colors[i], r, point)
            // 控制点文字标签 P0–P3
            drawContext.canvas.nativeCanvas.drawText(
                labels[i],
                point.x + 18f,
                point.y - 12f,
                textPaint,
            )
        }
    }
}

private fun defaultPoints(size: IntSize): List<Offset> {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    return listOf(
        Offset(w * 0.12f, h * 0.62f),
        Offset(w * 0.30f, h * 0.18f),
        Offset(w * 0.70f, h * 0.18f),
        Offset(w * 0.88f, h * 0.62f),
    )
}

private fun randomPoints(size: IntSize): List<Offset> {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    fun rand(min: Float, max: Float) = min + Random.nextFloat() * (max - min)
    return listOf(
        Offset(rand(w * 0.06f, w * 0.3f), rand(h * 0.4f, h * 0.85f)),
        Offset(rand(w * 0.15f, w * 0.5f), rand(h * 0.08f, h * 0.45f)),
        Offset(rand(w * 0.5f, w * 0.85f), rand(h * 0.08f, h * 0.45f)),
        Offset(rand(w * 0.7f, w * 0.94f), rand(h * 0.4f, h * 0.85f)),
    )
}

private fun dist(a: Offset, b: Offset): Float = hypot(a.x - b.x, a.y - b.y)

@Preview(showBackground = true)
@Composable
private fun BezierBoardPreview() {
    KotlinTheme {
        BezierBoardScreen()
    }
}
