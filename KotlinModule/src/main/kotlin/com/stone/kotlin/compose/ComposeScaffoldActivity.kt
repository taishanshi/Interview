package com.stone.kotlin.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.launch

/**
 * Scaffold ≈ CoordinatorLayout + AppBarLayout + FAB + BottomNavigation。
 * ModalNavigationDrawer 包在外面 ≈ DrawerLayout。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_SCAFFOLD)
class ComposeScaffoldActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme() {
                ScaffoldDemoScreen()
            }
        }
    }
}

private data class ScaffoldTab(val title: String, val icon: ImageVector)

private val tabs = listOf(
    ScaffoldTab("首页", Icons.Filled.Home),
    ScaffoldTab("消息", Icons.Filled.Notifications),
    ScaffoldTab("我的", Icons.Filled.Person),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldDemoScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var tab by remember { mutableIntStateOf(0) }
    var fabClicks by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                Text(
                    "抽屉菜单",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                tabs.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = tab == index,
                        onClick = {
                            tab = index
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }
        },
    ) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Scaffold") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Text("≡")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    fabClicks++
                    scope.launch {
                        snackbarHostState.showSnackbar("FAB 点了 $fabClicks 次")
                    }
                },
            ) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("当前 Tab：${tabs[tab].title}", style = MaterialTheme.typography.headlineSmall)
            Text(
                "content 用 innerPadding 避开顶栏和底栏。点 ≡ 或从左缘滑开抽屉，点 FAB 看 Snackbar。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("槽位对照", style = MaterialTheme.typography.titleMedium)
                    Text("Drawer → 左侧抽屉，包在 Scaffold 外面")
                    Text("topBar → 上面这根 TopAppBar")
                    Text("bottomBar → 下面 NavigationBar")
                    Text("floatingActionButton → 右下角 +")
                    Text("snackbarHost → FAB 点出来的条")
                    Text("content → 你正在看的这一块")
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScaffoldDemoPreview() {
    KotlinTheme {
        ScaffoldDemoScreen()
    }
}
