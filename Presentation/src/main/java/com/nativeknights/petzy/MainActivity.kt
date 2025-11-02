package com.nativeknights.petzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nativeknights.data.repositoryimp.datastore.DataStoreManager
import com.nativeknights.petzy.home.MainScren
import com.nativeknights.petzy.ui.theme.PetzyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val dataStoreManager = DataStoreManager(this)

        setContent {
            PetzyTheme {
//                   val navController = rememberNavController()
//                    AppNavGraph(navController, dataStoreManager)
                   val dataStoreManager = DataStoreManager(this)
                       MainScren(dataStoreManager)
                }
            }
        }
    }
