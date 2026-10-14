package com.stone.kotlin.compose

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme

/**
 * Compose Navigation ≈ 单 Activity 里的 Fragment 回退栈。
 * ARouter 管跨模块 Activity；NavHost 管同一个 Activity 里的 Composable 页面。
 *
 * - rememberNavController() 拿控制器
 * - NavHost + composable(route) 注册页
 * - navigate / popBackStack 进栈、出栈
 * - {name} + navArgument 传参
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_NAVIGATION)
class ComposeNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                NavigationDemo()
            }
        }
    }
}

private object NavRoutes {
    const val LIST = "list"
    const val DETAIL = "detail/{name}"
    const val ABOUT = "about"
    fun detail(name: String) = "detail/${Uri.encode(name)}"
}

private val people = listOf("李一桐", "杨紫", "刘诗诗")

@Composable
fun NavigationDemo() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LIST,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(NavRoutes.LIST) {
            ListScreen(
                onOpenDetail = { navController.navigate(NavRoutes.detail(it)) },
                onOpenAbout = { navController.navigate(NavRoutes.ABOUT) },
            )
        }
        composable(
            route = NavRoutes.DETAIL,
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { entry ->
            val name = Uri.decode(entry.arguments?.getString("name").orEmpty())
            DetailScreen(
                name = name,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreen(
    onOpenDetail: (String) -> Unit,
    onOpenAbout: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Navigation") }) },
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "点人名进详情（带参数）。系统返回键会 popBackStack。",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(people) { name ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(name) },
                    ) {
                        Text(name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            Button(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) {
                Text("关于（无参数）")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(name: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("路由参数 name = $name", style = MaterialTheme.typography.headlineSmall)
            Text("从 list 用 navigate(\"detail/{name}\") 传过来，对照 Intent.putExtra。")
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("返回列表")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("这是第三条路由，没有 path 参数。")
            Text("NavHost 把三个 Composable 叠成回退栈，不是三个 Activity。")
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("返回")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigationPreview() {
    KotlinTheme {
        ListScreen(onOpenDetail = {}, onOpenAbout = {})
    }
}
