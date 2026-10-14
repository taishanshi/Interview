package com.stone.interview.shape

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * 自定义 View：自己 onDraw 三种几何形，自己处理拖动 / 双击旋转 / 长按自转。
 * 双指缩放放在父画布上，避免两指都要落在 112dp 的小图形里。
 * 中心点 [centerX]/[centerY] 用父 ViewGroup 坐标系，由 [ShapeCanvasView] onLayout 落地。
 */
class ShapeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    var type: ShapeType = ShapeType.CIRCLE
        set(value) {
            field = value
            rebuildPath()
            invalidate()
        }

    var centerX = 0f
    var centerY = 0f

    var onSelect: ((ShapeView) -> Unit)? = null
    var onReleased: ((ShapeView, vx: Float, vy: Float) -> Unit)? = null

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        setShadowLayer(18f, 0f, 8f, 0x55000000)
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.WHITE
    }
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.WHITE
    }
    private val path = Path()
    private val hitPath = Path()
    private var dashPhase = 0f
    private var selected = false
    private var lastX = 0f
    private var lastY = 0f
    private var pointerId = MotionEvent.INVALID_POINTER_ID
    private var downRawX = 0f
    private var downRawY = 0f
    private var lastMoveTime = 0L
    private var velX = 0f
    private var velY = 0f
    private var pinchSpan = 0f
    private var didPinch = false
    private var dashAnim: ValueAnimator? = null
    private var spinAnim: ObjectAnimator? = null

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                animate().rotationBy(360f).setDuration(420).start()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                toggleIdleSpin()
            }
        },
    )

    init {
        // shadowLayer 必须软件层，否则阴影出不来
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (112 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val colors = when (type) {
            ShapeType.CIRCLE -> intArrayOf(0xFF56CCF2.toInt(), 0xFF2F80ED.toInt())
            ShapeType.TRIANGLE -> intArrayOf(0xFFFF8A65.toInt(), 0xFFD32F2F.toInt())
            ShapeType.SQUARE -> intArrayOf(0xFFB39DDB.toInt(), 0xFF5E35B1.toInt())
        }
        fillPaint.shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            colors[0], colors[1], Shader.TileMode.CLAMP,
        )
        rebuildPath()
    }

    private fun rebuildPath() {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        val pad = 16f
        path.reset()
        when (type) {
            ShapeType.CIRCLE -> path.addOval(pad, pad, w - pad, h - pad, Path.Direction.CW)
            ShapeType.SQUARE -> path.addRoundRect(
                pad, pad, w - pad, h - pad, 18f, 18f, Path.Direction.CW,
            )
            ShapeType.TRIANGLE -> {
                path.moveTo(w / 2f, pad)
                path.lineTo(w - pad, h - pad)
                path.lineTo(pad, h - pad)
                path.close()
            }
        }
        hitPath.set(path)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, strokePaint)
        if (selected) {
            dashPaint.pathEffect = DashPathEffect(floatArrayOf(18f, 12f), dashPhase)
            canvas.drawPath(path, dashPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount < 2) {
            gestureDetector.onTouchEvent(event)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!contains(event.x, event.y)) return false
                pointerId = event.getPointerId(0)
                lastX = event.pointerRawX(0)
                lastY = event.pointerRawY(0)
                downRawX = lastX
                downRawY = lastY
                lastMoveTime = event.eventTime
                velX = 0f
                velY = 0f
                pinchSpan = 0f
                didPinch = false
                gestureDetector.setIsLongpressEnabled(true)
                parent.requestDisallowInterceptTouchEvent(true)
                bringToFront()
                onSelect?.invoke(this)
                animatePress(true)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount >= 2) {
                    abortGestures(event)
                    pinchSpan = event.fingerSpanOnScreen(this)
                    didPinch = true
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    val span = event.fingerSpanOnScreen(this)
                    if (pinchSpan < 1f) pinchSpan = span
                    applyScale(pinchFactor(pinchSpan, span))
                    pinchSpan = span
                    onSelect?.invoke(this)
                    return true
                }
                val idx = event.findPointerIndex(pointerId)
                if (idx < 0) return true
                val x = event.pointerRawX(idx)
                val y = event.pointerRawY(idx)
                val dt = max(1L, event.eventTime - lastMoveTime)
                velX = (x - lastX) * 1000f / dt
                velY = (y - lastY) * 1000f / dt
                centerX += x - lastX
                centerY += y - lastY
                lastX = x
                lastY = y
                lastMoveTime = event.eventTime
                (parent as? ViewGroup)?.requestLayout()
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pinchSpan = 0f
                val keep = if (event.actionIndex == 0) 1 else 0
                if (keep < event.pointerCount) {
                    pointerId = event.getPointerId(keep)
                    lastX = event.pointerRawX(keep)
                    lastY = event.pointerRawY(keep)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                pointerId = MotionEvent.INVALID_POINTER_ID
                pinchSpan = 0f
                gestureDetector.setIsLongpressEnabled(true)
                animatePress(false)
                val moved = hypot(event.rawX - downRawX, event.rawY - downRawY)
                if (!didPinch && moved > 12f) {
                    onReleased?.invoke(this, velX, velY)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                pointerId = MotionEvent.INVALID_POINTER_ID
                pinchSpan = 0f
                gestureDetector.setIsLongpressEnabled(true)
                animatePress(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun abortGestures(event: MotionEvent) {
        gestureDetector.setIsLongpressEnabled(false)
        val cancel = MotionEvent.obtain(event)
        cancel.action = MotionEvent.ACTION_CANCEL
        gestureDetector.onTouchEvent(cancel)
        cancel.recycle()
        spinAnim?.cancel()
        spinAnim = null
        animate().cancel()
        alpha = 1f
    }

    fun applyScale(factor: Float) {
        val next = (scaleX * factor).coerceIn(0.4f, 3.2f)
        scaleX = next
        scaleY = next
    }

    private fun contains(x: Float, y: Float): Boolean {
        val bounds = android.graphics.RectF()
        hitPath.computeBounds(bounds, true)
        val region = android.graphics.Region()
        region.setPath(
            hitPath,
            android.graphics.Region(
                bounds.left.toInt(),
                bounds.top.toInt(),
                bounds.right.toInt(),
                bounds.bottom.toInt(),
            ),
        )
        return region.contains(x.toInt(), y.toInt())
    }

    fun setSelectedShape(value: Boolean) {
        if (selected == value) return
        selected = value
        dashAnim?.cancel()
        if (value) {
            dashAnim = ValueAnimator.ofFloat(0f, 60f).apply {
                duration = 900
                repeatCount = ValueAnimator.INFINITE
                interpolator = android.view.animation.LinearInterpolator()
                addUpdateListener {
                    dashPhase = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            invalidate()
        }
    }

    fun playAppear() {
        scaleX = 0.15f
        scaleY = 0.15f
        alpha = 0f
        animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(420)
            .setInterpolator(OvershootInterpolator(2.4f))
            .start()
    }

    fun playRemove(end: () -> Unit) {
        animate()
            .scaleX(0.1f)
            .scaleY(0.1f)
            .alpha(0f)
            .rotationBy(90f)
            .setDuration(220)
            .withEndAction(end)
            .start()
    }

    fun moveToAnimated(x: Float, y: Float, duration: Long = 380) {
        val startX = centerX
        val startY = centerY
        ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = OvershootInterpolator(1.1f)
            addUpdateListener {
                val t = it.animatedFraction
                centerX = startX + (x - startX) * t
                centerY = startY + (y - startY) * t
                (parent as? ViewGroup)?.requestLayout()
            }
            start()
        }
    }

    private fun animatePress(down: Boolean) {
        animate().alpha(if (down) 0.82f else 1f).setDuration(120).start()
    }

    private fun toggleIdleSpin() {
        if (spinAnim?.isRunning == true) {
            spinAnim?.cancel()
            animate().rotation(0f).setDuration(200).start()
            return
        }
        spinAnim = ObjectAnimator.ofFloat(this, ROTATION, rotation, rotation + 360f).apply {
            duration = 2800
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        dashAnim?.cancel()
        spinAnim?.cancel()
        super.onDetachedFromWindow()
    }
}

fun clamp(v: Float, minV: Float, maxV: Float): Float = min(maxV, max(minV, v))

internal fun MotionEvent.fingerSpan(): Float = fingerSpanOnScreen(null)

internal fun MotionEvent.fingerSpanOnScreen(view: View?): Float {
    if (pointerCount < 2) return 0f
    if (Build.VERSION.SDK_INT >= 29) {
        return hypot(getRawX(0) - getRawX(1), getRawY(0) - getRawY(1)).coerceAtLeast(1f)
    }
    if (view != null) {
        val p = FloatArray(4)
        p[0] = getX(0)
        p[1] = getY(0)
        p[2] = getX(1)
        p[3] = getY(1)
        view.matrix.mapPoints(p)
        return hypot(p[0] - p[2], p[1] - p[3]).coerceAtLeast(1f)
    }
    return hypot(getX(0) - getX(1), getY(0) - getY(1)).coerceAtLeast(1f)
}

internal fun MotionEvent.pointerRawX(index: Int): Float {
    return if (Build.VERSION.SDK_INT >= 29) getRawX(index) else rawX - x + getX(index)
}

internal fun MotionEvent.pointerRawY(index: Int): Float {
    return if (Build.VERSION.SDK_INT >= 29) getRawY(index) else rawY - y + getY(index)
}

internal fun pinchFactor(previousSpan: Float, currentSpan: Float): Float {
    if (previousSpan < 1f) return 1f
    return 1f + (currentSpan / previousSpan - 1f) * 2.8f
}
