package com.stone.interview.foursymbols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stone.interview.R

/**
 * 玄武：北方水。龟 / 蛇两种叙事，勾选七宿累计「镇北方」进度。
 */
class XuanWuFragment : Fragment() {

    private val checked = BooleanArray(FourSymbolsData.xuanWu.size)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_xuan_wu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvLore = view.findViewById<TextView>(R.id.tvLore)
        val tvProgress = view.findViewById<TextView>(R.id.tvProgress)
        val progress = view.findViewById<ProgressBar>(R.id.progressGuard)
        val box = view.findViewById<LinearLayout>(R.id.boxMansions)
        val radios = view.findViewById<RadioGroup>(R.id.radioAspect)

        fun lore(isTurtle: Boolean) {
            tvLore.text = if (isTurtle) {
                "龟象：玄武以龟为甲，主静、主藏、主北方寒水。营室、东壁管宫室文章，冬天把一年收进壳里。"
            } else {
                "蛇象：蛇为玄武之缠绕，主变、主玄冥。虚危二宿多言丧哭危难，水至柔也能决堤。"
            }
        }

        radios.setOnCheckedChangeListener { _, checkedId ->
            lore(checkedId == R.id.radioTurtle)
        }
        lore(true)

        FourSymbolsData.xuanWu.forEachIndexed { index, mansion ->
            val check = CheckBox(requireContext())
            check.text = "${mansion.name}宿 · ${mansion.animal}　${mansion.meaning}"
            check.setOnCheckedChangeListener { _, isChecked ->
                checked[index] = isChecked
                val count = checked.count { it }
                progress.progress = count * 100 / checked.size
                tvProgress.text = if (count == checked.size) {
                    "七宿齐备，玄武坐镇北方。"
                } else {
                    "已点亮 $count / ${checked.size} 宿"
                }
            }
            box.addView(check)
        }
        progress.max = 100
        progress.progress = 0
        tvProgress.text = "已点亮 0 / ${checked.size} 宿"
    }
}
