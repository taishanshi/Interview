package com.stone.interview.shape

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import kotlin.math.hypot
import kotlin.random.Random

/**
 * 自定义 ViewGroup：绝对定位子 [ShapeView]，格子背景、涟漪、近距连线都在 dispatchDraw。
 * 不复用 FrameLayout，onLayout 按每个图形的中心点摆放。
 */
class ShapeCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {

    var selected: ShapeView? = null
        private set
    var onSelectionChanged: ((ShapeView?) -> Unit)? = null

    private val gridPaint = Paint().apply {
        color = 0x22FFFFFF.toInt()
        strokeWidth = 1f
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x88FFFFFF.toInt()
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(12f, 10f), 0f)
    }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = 0x66FFFFFF
    }
    private val rings = mutableListOf<Ripple>()
    private val grid = 24f * resources.displayMetrics.density
    private var pinching = false
    private var pinchSpan = 0f
    private var pinchTarget: ShapeView? = null

    init {
        setWillNotDraw(false)
        clipChildren = false
        clipToPadding = false
        setBackgroundColor(0xFF1A2332.toInt())
    }

    fun addShape(type: ShapeType, at: PointF? = null) {
        val child = ShapeView(context).apply { this.type = type }
        val origin = at ?: PointF(width / 2f, height / 2f)
        child.centerX = origin.x + Random.nextInt(-80, 80)
        child.centerY = origin.y + Random.nextInt(-80, 80)
        child.onSelect = { select(it) }
        child.onReleased = { view, vx, vy -> onReleased(view, vx, vy) }
        addView(child)
        select(child)
        child.playAppear()
        spawnRipple(child.centerX, child.centerY)
    }

    fun removeSelected() {
        val target = selected ?: return
        target.playRemove {
            removeView(target)
            if (selected === target) select(null)
        }
    }

    fun scatter() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as ShapeView
            val x = paddingLeft + 80f + Random.nextFloat() * (width - 160f).coerceAtLeast(1f)
            val y = paddingTop + 80f + Random.nextFloat() * (height - 160f).coerceAtLeast(1f)
            child.moveToAnimated(x, y, 500)
        }
    }

    fun gather() {
        val cx = width / 2f
        val cy = height / 2f
        val n = childCount.coerceAtLeast(1)
        for (i in 0 until childCount) {
            val child = getChildAt(i) as ShapeView
            val angle = (Math.PI * 2 * i / n).toFloat()
            val r = 140f * resources.displayMetrics.density
            child.moveToAnimated(
                cx + kotlin.math.cos(angle) * r,
                cy + kotlin.math.sin(angle) * r,
                460,
            )
        }
    }

    private fun select(view: ShapeView?) {
        if (selected !== view) {
            selected?.setSelectedShape(false)
            selected = view
            view?.setSelectedShape(true)
            view?.bringToFront()
        }
        onSelectionChanged?.invoke(view)
    }

    private fun onReleased(view: ShapeView, vx: Float, vy: Float) {
        val speed = hypot(vx, vy)
        if (speed > 1800f) {
            fling(view, vx, vy)
        } else {
            snapAndRepel(view)
        }
    }

    private fun fling(view: ShapeView, vx: Float, vy: Float) {
        val startX = view.centerX
        val startY = view.centerY
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 520
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val ease = 1f - (1f - it.animatedFraction) * (1f - it.animatedFraction)
                view.centerX = startX + vx * 0.12f * ease
                view.centerY = startY + vy * 0.12f * ease
                clamp(view)
                requestLayout()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    snapAndRepel(view)
                }
            })
            start()
        }
    }

    private fun snapAndRepel(view: ShapeView) {
        var x = (view.centerX / grid).toInt() * grid
        var y = (view.centerY / grid).toInt() * grid
        for (i in 0 until childCount) {
            val other = getChildAt(i) as? ShapeView ?: continue
            if (other === view) continue
            val d = hypot(x - other.centerX, y - other.centerY)
            if (d < 130f && d > 1f) {
                val nx = (x - other.centerX) / d
                val ny = (y - other.centerY) / d
                x = other.centerX + nx * 150f
                y = other.centerY + ny * 150f
            }
        }
        view.moveToAnimated(
            clamp(x, paddingLeft + 70f, width - paddingRight - 70f),
            clamp(y, paddingTop + 70f, height - paddingBottom - 70f),
        )
    }

    private fun clamp(view: ShapeView) {
        view.centerX = clamp(view.centerX, paddingLeft + 70f, width - paddingRight - 70f)
        view.centerY = clamp(view.centerY, paddingTop + 70f, height - paddingBottom - 70f)
    }

    private fun spawnRipple(x: Float, y: Float) {
        val ripple = Ripple(x, y)
        rings.add(ripple)
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 650
            addUpdateListener {
                ripple.progress = it.animatedFraction
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    rings.remove(ripple)
                }
            })
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        for (i in 0 until childCount) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as ShapeView
            val hw = child.measuredWidth / 2
            val hh = child.measuredHeight / 2
            child.layout(
                (child.centerX - hw).toInt(),
                (child.centerY - hh).toInt(),
                (child.centerX + hw).toInt(),
                (child.centerY + hh).toInt(),
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawGrid(canvas)
        val sel = selected
        if (sel != null) {
            for (i in 0 until childCount) {
                val other = getChildAt(i) as ShapeView
                if (other === sel) continue
                val d = hypot(sel.centerX - other.centerX, sel.centerY - other.centerY)
                if (d < 280f) {
                    canvas.drawLine(sel.centerX, sel.centerY, other.centerX, other.centerY, linePaint)
                }
            }
        }
        super.dispatchDraw(canvas)
        rings.forEach { ring ->
            ringPaint.alpha = ((1f - ring.progress) * 140).toInt()
            canvas.drawCircle(ring.x, ring.y, 40f + ring.progress * 160f, ringPaint)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        var x = 0f
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
            x += grid
        }
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
            y += grid
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount >= 2) {
            beginPinch(ev)
            return true
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (pinching || event.pointerCount >= 2) {
            handlePinch(event)
            return true
        }
        if (event.actionMasked == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
            select(null)
            spawnRipple(event.x, event.y)
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun beginPinch(ev: MotionEvent) {
        pinching = true
        pinchSpan = ev.fingerSpan()
        pinchTarget = selected ?: nearestShape(ev)
        pinchTarget?.let { select(it) }
        parent.requestDisallowInterceptTouchEvent(true)
    }

    private fun handlePinch(ev: MotionEvent) {
        when (ev.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (ev.pointerCount >= 2) beginPinch(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ev.pointerCount < 2) return
                val span = ev.fingerSpan()
                if (pinchSpan < 1f) pinchSpan = span
                pinchTarget?.applyScale(pinchFactor(pinchSpan, span))
                onSelectionChanged?.invoke(pinchTarget)
                pinchSpan = span
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (ev.pointerCount <= 2) endPinch()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endPinch()
        }
    }

    private fun endPinch() {
        pinching = false
        pinchSpan = 0f
        pinchTarget = null
    }

    private fun nearestShape(ev: MotionEvent): ShapeView? {
        val cx = if (ev.pointerCount >= 2) (ev.getX(0) + ev.getX(1)) / 2f else ev.x
        val cy = if (ev.pointerCount >= 2) (ev.getY(0) + ev.getY(1)) / 2f else ev.y
        var best: ShapeView? = null
        var bestD = Float.MAX_VALUE
        for (i in 0 until childCount) {
            val child = getChildAt(i) as ShapeView
            val d = hypot(child.centerX - cx, child.centerY - cy)
            if (d < bestD) {
                bestD = d
                best = child
            }
        }
        return best
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (oldw == 0 && childCount == 0) {
            post {
                addShape(ShapeType.CIRCLE, PointF(w * 0.3f, h * 0.45f))
                addShape(ShapeType.TRIANGLE, PointF(w * 0.5f, h * 0.38f))
                addShape(ShapeType.SQUARE, PointF(w * 0.7f, h * 0.5f))
            }
        }
    }

    private class Ripple(val x: Float, val y: Float, var progress: Float = 0f)
}
