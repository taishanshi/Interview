package com.stone.interview.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stone.interview.R

data class RecyclerItem(
    val title: String,
    val subtitle: String,
)

/**
 * 对照 ListView：
 * - ViewHolder 必须写，回收复用靠它
 * - onCreateViewHolder 只在需要新盒子时调用
 * - onBindViewHolder 每次把数据填进已有盒子，不要在这里 inflate
 */
class RecyclerDemoAdapter(
    private val items: MutableList<RecyclerItem>,
    private val onItemClick: (RecyclerItem, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerDemoAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle
        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onItemClick(items[pos], pos)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
