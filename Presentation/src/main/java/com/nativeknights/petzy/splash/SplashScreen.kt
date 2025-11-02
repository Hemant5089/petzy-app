package com.nativeknights.petzy.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nativeknights.data.repositoryimp.datastore.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    dataStoreManager: DataStoreManager
) {
    val isLoggedIn by dataStoreManager.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        delay(1500)
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("🐾 Petzy", style = MaterialTheme.typography.headlineMedium)
    }
}
