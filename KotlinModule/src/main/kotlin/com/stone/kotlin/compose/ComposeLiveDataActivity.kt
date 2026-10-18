package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * LiveData 和 Compose 互通：
 * Compose 点按钮 / 改输入 → ViewModel
 * ViewModel delay 模拟网络 → LiveData
 * Compose 用 [observeAsState] 收到新值并重组
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_LIVEDATA)
class ComposeLiveDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                LiveDataScreen()
            }
        }
    }
}

sealed class LiveUiState {
    data object Idle : LiveUiState()
    data object Loading : LiveUiState()
    data class Success(val message: String) : LiveUiState()
}

class LiveDataViewModel : ViewModel() {

    private val _uiState = MutableLiveData<LiveUiState>(LiveUiState.Idle)
    val uiState: LiveData<LiveUiState> = _uiState

    fun fetch(keyword: String) {
        viewModelScope.launch {
            _uiState.value = LiveUiState.Loading
            delay(1500)
            val query = keyword.ifBlank { "李一桐" }
            _uiState.value = LiveUiState.Success("接口返回：$query（delay 1.5s）")
        }
    }
}

@Composable
fun LiveDataScreen(viewModel: LiveDataViewModel = viewModel()) {
    val uiState by viewModel.uiState.observeAsState(LiveUiState.Idle)
    var keyword by remember { mutableStateOf("") }
    val loading = uiState is LiveUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("LiveData × Compose", style = MaterialTheme.typography.headlineSmall)
        Text(
            "输入框和按钮在 Compose 里，结果走 LiveData。observeAsState 把 LiveData 转成 State，一 post 就重组。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("关键词") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
        )
        Button(
            onClick = { viewModel.fetch(keyword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
        ) {
            Text(if (loading) "请求中…" else "模拟网络请求")
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (val state = uiState) {
                    LiveUiState.Idle -> Text("还没请求")
                    LiveUiState.Loading -> CircularProgressIndicator()
                    is LiveUiState.Success -> Text(state.message, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiveDataPreview() {
    KotlinTheme {
        Text("LiveData 演示")
    }
}
