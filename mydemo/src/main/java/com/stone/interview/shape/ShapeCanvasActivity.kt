package com.stone.interview.shape

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R

/**
 * 自定义 ViewGroup 画布 + 自定义 View 图形。
 * 拖动改中心点、双指缩放、双击旋转、长按自转；松手网格吸附 + 互斥。
 */
class ShapeCanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shape_canvas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val canvas = findViewById<ShapeCanvasView>(R.id.canvas)
        val tvHint = findViewById<TextView>(R.id.tvHint)
        canvas.onSelectionChanged = { shape ->
            tvHint.text = if (shape == null) {
                "拖动移动 · 双指捏合缩放 · 双击旋转 · 长按自转"
            } else {
                "已选 ${shape.type.label}  ·  缩放 ${"%.2f".format(shape.scaleX)}×"
            }
        }
        findViewById<TextView>(R.id.btnCircle).setOnClickListener {
            canvas.addShape(ShapeType.CIRCLE)
        }
        findViewById<TextView>(R.id.btnTriangle).setOnClickListener {
            canvas.addShape(ShapeType.TRIANGLE)
        }
        findViewById<TextView>(R.id.btnSquare).setOnClickListener {
            canvas.addShape(ShapeType.SQUARE)
        }
        findViewById<TextView>(R.id.btnDelete).setOnClickListener {
            canvas.removeSelected()
        }
        findViewById<TextView>(R.id.btnScatter).setOnClickListener {
            canvas.scatter()
        }
        findViewById<TextView>(R.id.btnGather).setOnClickListener {
            canvas.gather()
        }
    }
}
