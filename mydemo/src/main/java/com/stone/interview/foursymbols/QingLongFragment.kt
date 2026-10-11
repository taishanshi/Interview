package com.stone.interview.foursymbols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stone.interview.R

/**
 * 青龙：东方木。RecyclerView 列出七宿，点选后底部展开释义。
 */
class QingLongFragment : Fragment() {

    private var selected = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_qing_long, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvDetail = view.findViewById<TextView>(R.id.tvDetail)
        val list = view.findViewById<RecyclerView>(R.id.rvMansions)
        val adapter = MansionAdapter(FourSymbolsData.qingLong) { index ->
            selected = index
            bindDetail(tvDetail)
        }
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter
        bindDetail(tvDetail)
    }

    private fun bindDetail(tvDetail: TextView) {
        val item = FourSymbolsData.qingLong[selected]
        tvDetail.text = buildString {
            appendLine("当前：${item.name}宿 · ${item.animal}")
            append(item.meaning)
            append("。苍龙七宿连起来是一条龙，角宿为角，箕宿为尾。")
        }
    }
}

class MansionAdapter(
    private val items: List<StarMansion>,
    private val onClick: (Int) -> Unit,
) : RecyclerView.Adapter<MansionAdapter.Holder>() {

    private var selected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_star_mansion, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.tvName.text = "${item.name}宿"
        holder.tvSub.text = "象：${item.animal}"
        holder.itemView.setBackgroundColor(
            if (position == selected) 0x332E7D32 else 0x00000000,
        )
        holder.itemView.setOnClickListener {
            val old = selected
            selected = holder.bindingAdapterPosition
            notifyItemChanged(old)
            notifyItemChanged(selected)
            onClick(selected)
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
    }
}
