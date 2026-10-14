package com.stone.interview.recyclerview

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stone.interview.R

/**
 * RecyclerView 三件套：LayoutManager + Adapter + ViewHolder。
 * ListView 把排布写死成一条条；这里用 LayoutManager 换成线性或网格。
 */
class RecyclerViewActivity : AppCompatActivity() {

    private val items = MutableList(12) { i ->
        RecyclerItem("条目 ${i + 1}", "position = $i，点我看 Toast")
    }
    private lateinit var adapter: RecyclerDemoAdapter
    private lateinit var recycler: RecyclerView
    private var grid = false
    private var nextIndex = items.size + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recycler_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        recycler = findViewById(R.id.recycler)
        adapter = RecyclerDemoAdapter(items) { item, pos ->
            Toast.makeText(this, "${item.title}  @ $pos", Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter
        applyLinear()

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val pos = items.size
            items.add(RecyclerItem("新条目 $nextIndex", "notifyItemInserted($pos)"))
            nextIndex++
            adapter.notifyItemInserted(pos)
            recycler.scrollToPosition(pos)
        }
        findViewById<Button>(R.id.btnRemove).setOnClickListener {
            if (items.isEmpty()) return@setOnClickListener
            val pos = items.lastIndex
            items.removeAt(pos)
            adapter.notifyItemRemoved(pos)
        }
        findViewById<Button>(R.id.btnLayout).setOnClickListener {
            grid = !grid
            if (grid) {
                recycler.layoutManager = GridLayoutManager(this, 2)
                findViewById<Button>(R.id.btnLayout).text = "切列表"
            } else {
                applyLinear()
                findViewById<Button>(R.id.btnLayout).text = "切网格"
            }
        }
    }

    private fun applyLinear() {
        recycler.layoutManager = LinearLayoutManager(this)
        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }
    }
}
