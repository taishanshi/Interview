package com.stone.interview.foursymbols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.stone.interview.R

/**
 * 朱雀：南方火。Glide 加载李一桐封面，七宿 Chip 切换文案。
 */
class ZhuQueFragment : Fragment() {

    companion object {
        private const val IMAGE_URL =
            "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20171109/Zt3v-fynshev4669576.jpg"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_zhu_que, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.ivPhoto)
        val tvCaption = view.findViewById<TextView>(R.id.tvCaption)
        val chips = view.findViewById<ChipGroup>(R.id.chipMansions)

        Glide.with(this)
            .load(IMAGE_URL)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imageView)

        FourSymbolsData.zhuQue.forEachIndexed { index, mansion ->
            val chip = Chip(requireContext())
            chip.id = View.generateViewId()
            chip.text = mansion.name
            chip.isCheckable = true
            chip.isChecked = index == 0
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) bindCaption(tvCaption, mansion)
            }
            chips.addView(chip)
        }
        bindCaption(tvCaption, FourSymbolsData.zhuQue.first())
    }

    private fun bindCaption(tvCaption: TextView, mansion: StarMansion) {
        tvCaption.text = buildString {
            appendLine("朱雀 · ${mansion.name}宿（${mansion.animal}）")
            appendLine(mansion.meaning)
            append("南方属火，画报里的红与眼神刚好对上朱雀这一格。图：李一桐 / Glide.with(Fragment).load().into()")
        }
    }
}
