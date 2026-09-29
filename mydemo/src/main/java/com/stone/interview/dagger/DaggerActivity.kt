package com.stone.interview.dagger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.AppGraph
import com.stone.interview.R
import javax.inject.Inject
import javax.inject.Named

class DaggerActivity : AppCompatActivity() {

    @Inject
    lateinit var user: User

    @Inject
    lateinit var user2: User

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var userPresenter: UserPresenter

    @Inject
    @field:Named("baseUrl")
    lateinit var baseUrl: String

    @Inject
    @field:Named("token")
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        rebuildActivityComponent()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dagger)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        render()
        findViewById<Button>(R.id.btnRebuild).setOnClickListener {
            rebuildActivityComponent()
            render()
        }
    }

    private fun rebuildActivityComponent() {
        AppGraph.userComponent
            .activityComponent()
            .create()
            .inject(this)
    }

    private fun render() {
        findViewById<TextView>(R.id.tvInfo).text = buildString {
            appendLine(userPresenter.present())
            appendLine("user hash: ${System.identityHashCode(user)}")
            appendLine("user2 hash: ${System.identityHashCode(user2)}")
            appendLine("Activity @Named baseUrl: $baseUrl")
            appendLine("Activity @Named token: $token")
            appendLine("Activity 里 api hash: ${System.identityHashCode(apiService)}")
            append("点按钮换新 Presenter（Subcomponent），user/api hash 不变（Singleton）")
        }
        Log.v("DaggerActivity", "${user.hashCode()}")
        Log.v("DaggerActivity", "${user2.hashCode()}")
    }
}
