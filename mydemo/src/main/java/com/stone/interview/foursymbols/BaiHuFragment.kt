package com.stone.interview.foursymbols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stone.interview.R

/**
 * 白虎：西方金。双列网格 + 杀气滑条，滑条越高高亮越靠后的参宿杀伐。
 */
class BaiHuFragment : Fragment() {

    private var ferocity = 40

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_bai_hu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvSummary = view.findViewById<TextView>(R.id.tvSummary)
        val seek = view.findViewById<SeekBar>(R.id.seekFerocity)
        val grid = view.findViewById<RecyclerView>(R.id.rvGrid)
        val adapter = BaiHuAdapter(FourSymbolsData.baiHu)
        grid.layoutManager = GridLayoutManager(requireContext(), 2)
        grid.adapter = adapter

        fun refresh() {
            adapter.highlightFrom = (ferocity * 6 / 100)
            adapter.notifyDataSetChanged()
            val active = FourSymbolsData.baiHu.drop(adapter.highlightFrom).joinToString("、") { it.name }
            tvSummary.text = "杀气 $ferocity。参宿主杀伐，当前被点亮：${active.ifEmpty { "奎（蓄势）" }}"
        }

        seek.progress = ferocity
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ferocity = progress
                refresh()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        refresh()
    }
}

class BaiHuAdapter(
    private val items: List<StarMansion>,
) : RecyclerView.Adapter<BaiHuAdapter.Holder>() {

    var highlightFrom: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_baihu_card, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAnimal.text = item.animal
        holder.tvMeaning.text = item.meaning
        holder.itemView.alpha = if (position >= highlightFrom) 1f else 0.45f
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAnimal: TextView = view.findViewById(R.id.tvAnimal)
        val tvMeaning: TextView = view.findViewById(R.id.tvMeaning)
    }
}
