package com.stone.kotlin.compose

import android.Manifest
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme

/**
 * Accompanist：Google 给 Compose 补能力的配套库。
 * Pager / Insets / SwipeRefresh 等已经毕业进 AndroidX，现在还常用的是 Permissions。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_ACCOMPANIST)
class ComposeAccompanistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                AccompanistScreen()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AccompanistScreen() {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Accompanist", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Compose 早期缺官方 API 时的补丁库。很多模块已迁进 Foundation / Material3，权限这一套还在用。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("rememberPermissionState", style = MaterialTheme.typography.titleMedium)
                Text(
                    when {
                        cameraPermission.status.isGranted -> "相机权限：已授予"
                        cameraPermission.status.shouldShowRationale -> "相机权限：拒绝过，需要说明用途"
                        else -> "相机权限：未授予"
                    },
                )
                Button(
                    onClick = { cameraPermission.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !cameraPermission.status.isGranted,
                ) {
                    Text(if (cameraPermission.status.isGranted) "已有权限" else "申请相机权限")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccompanistPreview() {
    KotlinTheme {
        Text("Accompanist 权限演示")
    }
}
