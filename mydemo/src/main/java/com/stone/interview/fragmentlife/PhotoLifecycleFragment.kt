package com.stone.interview.fragmentlife

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.stone.interview.R

/**
 * Fragment 生命周期（对照 Activity 的 A 前缀日志）：
 *
 * onAttach → onCreate → onCreateView → onViewCreated → onStart → onResume
 * 回退：onPause → onStop → onDestroyView → onDestroy → onDetach
 *
 * hide/show 一般只走 onHiddenChanged，不会拆 View。
 * remove / replace 会走 onDestroyView；replace 还会再走一套创建。
 *
 * Glide.with(this) 绑的是 Fragment，onDestroyView 时请求会被清掉。
 * 图是另一张李一桐街拍，不和 Glide 页 / 朱雀页那张封面重复。
 */
class PhotoLifecycleFragment : Fragment() {

    companion object {
        private const val IMAGE_URL =
            "https://n.sinaimg.cn/ent/4_img/upload/1f0ce517/20170915/Xku7-fykymue6229064.jpg"
    }

    private val host: FragmentLifeLogHost?
        get() = activity as? FragmentLifeLogHost

    override fun onAttach(context: Context) {
        super.onAttach(context)
        host?.appendLog("F onAttach  拿到 Activity，还没有 View")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host?.appendLog("F onCreate  可恢复状态 / 初始化非 View")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        host?.appendLog("F onCreateView  创建布局，不要在这里 load 图片以外的重活也行，但 Glide 更常见写在 onViewCreated")
        return inflater.inflate(R.layout.fragment_photo_lifecycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host?.appendLog("F onViewCreated  View 已就绪，这里 Glide.load")
        val imageView = view.findViewById<ImageView>(R.id.ivPhoto)
        Glide.with(this)
            .load(IMAGE_URL)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imageView)
    }

    override fun onStart() {
        super.onStart()
        host?.appendLog("F onStart  可见，但未必可交互")
    }

    override fun onResume() {
        super.onResume()
        host?.appendLog("F onResume  可交互")
    }

    override fun onPause() {
        host?.appendLog("F onPause  即将不可交互，可停动画")
        super.onPause()
    }

    override fun onStop() {
        host?.appendLog("F onStop  不可见")
        super.onStop()
    }

    override fun onDestroyView() {
        host?.appendLog("F onDestroyView  View 拆掉；Glide.with(Fragment) 的请求会停。成员 View 引用要丢掉")
        super.onDestroyView()
    }

    override fun onDestroy() {
        host?.appendLog("F onDestroy  Fragment 实例要没了")
        super.onDestroy()
    }

    override fun onDetach() {
        host?.appendLog("F onDetach  和 Activity 断开")
        super.onDetach()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        host?.appendLog("F onHiddenChanged hidden=$hidden  hide/show 走这里，不拆 View")
    }
}
