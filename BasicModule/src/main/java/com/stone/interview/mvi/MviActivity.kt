package com.stone.interview.mvi

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R

class MviActivity : AppCompatActivity() {

    private val viewModel: MviViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mvi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.state.observe(this) { render(it) }

        findViewById<Button>(R.id.btnPlus).setOnClickListener {
            viewModel.dispatch(MviIntent.Plus)
        }
        findViewById<Button>(R.id.btnMinus).setOnClickListener {
            viewModel.dispatch(MviIntent.Minus)
        }
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            viewModel.dispatch(MviIntent.Reset)
        }
    }

    private fun render(state: MviState) {
        findViewById<TextView>(R.id.tvCount).text = "count = ${state.count}"
        findViewById<TextView>(R.id.tvMessage).text = state.message
    }
}
