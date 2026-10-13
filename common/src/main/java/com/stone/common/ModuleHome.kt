package com.stone.common

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.alibaba.android.arouter.launcher.ARouter

data class ModuleEntry(
    val title: String,
    val route: String? = null,
    val clazz: Class<out Activity>? = null,
    val subtitle: String = "",
)

fun Activity.bindModuleHome(
    title: String,
    items: List<ModuleEntry>,
    kicker: String = "模块",
) {
    setContentView(R.layout.activity_module_home)
    val root = findViewById<View>(R.id.main)
    val padL = root.paddingLeft
    val padT = root.paddingTop
    val padR = root.paddingRight
    val padB = root.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(
            bars.left + padL,
            bars.top + padT,
            bars.right + padR,
            bars.bottom + padB,
        )
        insets
    }
    findViewById<TextView>(R.id.tvKicker).text = kicker
    findViewById<TextView>(R.id.tvTitle).text = title
    val grid = findViewById<GridView>(R.id.gridDemos)
    grid.adapter = ModuleGridAdapter(items)
    grid.setOnItemClickListener { _, _, position, _ ->
        val item = items[position]
        when {
            item.route != null -> ARouter.getInstance().build(item.route).navigation()
            item.clazz != null -> startActivity(Intent(this, item.clazz))
        }
    }
}

private val cardPalette = intArrayOf(
    "#2F80ED".toColorInt(),
    "#5E35B1".toColorInt(),
    "#00897B".toColorInt(),
    "#E53935".toColorInt(),
    "#EF6C00".toColorInt(),
    "#1565C0".toColorInt(),
    "#6A1B9A".toColorInt(),
    "#2E7D32".toColorInt(),
)

private class ModuleGridAdapter(
    private val items: List<ModuleEntry>,
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): ModuleEntry = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_grid, parent, false)
        val item = items[position]
        view.findViewById<TextView>(R.id.tvItem).text = item.title
        val sub = view.findViewById<TextView>(R.id.tvSub)
        sub.text = item.subtitle
        sub.isVisible = item.subtitle.isNotBlank()
        val badge = view.findViewById<TextView>(R.id.tvBadge)
        badge.text = item.title.first().toString()
        badge.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(cardPalette[position % cardPalette.size])
        }
        return view
    }
}
