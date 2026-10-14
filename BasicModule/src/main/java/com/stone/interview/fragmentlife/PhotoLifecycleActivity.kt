package com.stone.interview.fragmentlife

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.stone.interview.R

/**
 * Activity 只负责容器和事务：add / hide / show / replace / remove。
 * 日志写在 Activity 上，Fragment 被 remove 后仍能看到 onDestroyView / onDetach。
 */
class PhotoLifecycleActivity : AppCompatActivity(), FragmentLifeLogHost {

    companion object {
        const val TAG = "FragLife"
        private const val FRAG_TAG = "photo_life"
    }

    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo_lifecycle)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        tvLog = findViewById(R.id.tvLog)
        appendLog("A onCreate  savedInstance=$savedInstanceState")

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            if (findFrag() != null) {
                appendLog("已经有 Fragment，先点移除")
                return@setOnClickListener
            }
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, PhotoLifecycleFragment(), FRAG_TAG)
            }
        }
        findViewById<Button>(R.id.btnHide).setOnClickListener {
            val f = findFrag() ?: return@setOnClickListener appendLog("没有 Fragment")
            supportFragmentManager.commit { hide(f) }
        }
        findViewById<Button>(R.id.btnShow).setOnClickListener {
            val f = findFrag() ?: return@setOnClickListener appendLog("没有 Fragment")
            supportFragmentManager.commit { show(f) }
        }
        findViewById<Button>(R.id.btnReplace).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, PhotoLifecycleFragment(), FRAG_TAG)
            }
        }
        findViewById<Button>(R.id.btnRemove).setOnClickListener {
            val f = findFrag() ?: return@setOnClickListener appendLog("没有 Fragment")
            supportFragmentManager.commit { remove(f) }
        }
    }

    private fun findFrag(): PhotoLifecycleFragment? =
        supportFragmentManager.findFragmentByTag(FRAG_TAG) as? PhotoLifecycleFragment

    override fun appendLog(line: String) {
        Log.d(TAG, line)
        tvLog.append(line + "\n")
    }

    override fun onStart() {
        super.onStart()
        appendLog("A onStart")
    }

    override fun onResume() {
        super.onResume()
        appendLog("A onResume")
    }

    override fun onPause() {
        appendLog("A onPause")
        super.onPause()
    }

    override fun onStop() {
        appendLog("A onStop")
        super.onStop()
    }

    override fun onDestroy() {
        appendLog("A onDestroy")
        super.onDestroy()
    }
}

interface FragmentLifeLogHost {
    fun appendLog(line: String)
}
