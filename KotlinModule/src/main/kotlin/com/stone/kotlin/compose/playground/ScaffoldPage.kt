package com.stone.kotlin.compose.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stone.kotlin.ui.theme.KotlinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldPage() {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {Text("脚手架演示")},
                navigationIcon = {
                    IconButton(onClick = {}) { Text("<——") }
                }
            )
        },

        ) { inner ->
            Column(Modifier.padding(inner)) {

        }
    }
}

@Preview
@Composable
private fun preView() {
    KotlinTheme(dynamicColor = false) {
        ScaffoldPage()
    }
}
