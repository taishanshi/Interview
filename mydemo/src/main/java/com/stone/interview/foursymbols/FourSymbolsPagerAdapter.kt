package com.stone.interview.foursymbols

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FourSymbolsPagerAdapter(
    activity: FragmentActivity,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> QingLongFragment()
        1 -> BaiHuFragment()
        2 -> ZhuQueFragment()
        3 -> XuanWuFragment()
        else -> error("unknown tab $position")
    }
}
