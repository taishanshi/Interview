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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.ui.theme.KotlinTheme
import kotlinx.coroutines.launch

/**
 * 抽屉每一项走 Navigation，进入不同 Composable 页。
 * 底栏 Tab 只在「首页」里，对照 DrawerLayout + NavController。
 */
@Route(path = RouterPath.KOTLIN_COMPOSE_SCAFFOLD)
class ComposeScaffoldActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinTheme {
                ScaffoldDemoScreen()
            }
        }
    }
}

private object DrawerRoutes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

private data class DrawerDest(val route: String, val title: String, val icon: ImageVector)

private val drawerDests = listOf(
    DrawerDest(DrawerRoutes.HOME, "首页", Icons.Filled.Home),
    DrawerDest(DrawerRoutes.SETTINGS, "设置", Icons.Filled.Settings),
    DrawerDest(DrawerRoutes.ABOUT, "关于", Icons.Filled.Info),
)

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        ?: DrawerRoutes.HOME
    val title = drawerDests.firstOrNull { it.route == currentRoute }?.title ?: "Scaffold"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                Text(
                    "抽屉菜单",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                drawerDests.forEach { dest ->
                    NavigationDrawerItem(
                        icon = { Icon(dest.icon, contentDescription = dest.title) },
                        label = { Text(dest.title) },
                        selected = currentRoute == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Text("≡")
                        }
                    },
                )
            },

            floatingActionButton = {
                if (currentRoute == DrawerRoutes.HOME) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("FAB")
                            }
                        },
                    ) {
                        Text("+")
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Column(modifier = Modifier.padding()) { }
            NavHost(
                navController = navController,
                startDestination = DrawerRoutes.HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                composable(DrawerRoutes.HOME) { HomePage() }
                composable(DrawerRoutes.SETTINGS) { SettingsPage() }
                composable(DrawerRoutes.ABOUT) { AboutPage() }
            }
        }
    }
}

@Composable
private fun HomePage() {
    var tab by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("首页 · ${tabs[tab].title}", style = MaterialTheme.typography.headlineSmall)
            Text(
                "点 ≡ 打开抽屉。首页 / 设置 / 关于 是三条路由，会进入不同页面。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("当前 Tab：${tabs[tab].title}", style = MaterialTheme.typography.titleMedium)
                    Text("底栏只属于首页。进设置、关于后看不到它。")
                }
            }
        }
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
    }
}

@Composable
private fun SettingsPage() {
    var tab by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("设置页", style = MaterialTheme.typography.headlineSmall)
        Text(
            "这是抽屉「设置」navigate 过来的独立 Composable。",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("通知、缓存这类选项可以放这里。", modifier = Modifier.padding(16.dp))
        }
    }

}

@Composable
private fun AboutPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("关于页", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Drawer + NavHost：点菜单只换 content，Activity 还是这一个。",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("KotlinModule · Scaffold 演示")
                Text("对照：DrawerLayout + NavigationUI.setupWithNavController")
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
