package com.nativeknights.petzy.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nativeknights.data.repositoryimp.datastore.DataStoreManager
import com.nativeknights.petzy.navigation.AppNavGraph
import com.nativeknights.petzy.navigation.BottomBar
import com.nativeknights.petzy.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun MainScren(dataStoreManager: DataStoreManager) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    DoubleBackToExit(currentRoute)
    // Routes where BottomBar should be hidden
    val hideBottomBarRoutes = listOf(
        Routes.SPLASH,
        Routes.LOGIN,
        Routes.SIGNUP,
        Routes.UPLOAD
    )

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomBar(navController)
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(navController, dataStoreManager)
        }
    }
}
@Composable
fun DoubleBackToExit(currentRoute: String?) {
    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Only trigger this logic on the Home screen
    if (currentRoute == Routes.HOME) {
        BackHandler {
            if (backPressedOnce) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                backPressedOnce = true
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
        LaunchedEffect(Unit) {
            delay(2000)
            backPressedOnce = false
        }
    }
    }