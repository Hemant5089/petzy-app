package com.nativeknights.petzy.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nativeknights.data.repositoryimp.datastore.DataStoreManager
import com.nativeknights.petzy.auth.LoginScreen
import com.nativeknights.petzy.auth.SignupScreen
import com.nativeknights.petzy.home.HomeScreen
import com.nativeknights.petzy.home.NotificationScreen
import com.nativeknights.petzy.home.ProfileDetailScreen
import com.nativeknights.petzy.home.ProfileScreen
import com.nativeknights.petzy.home.ReelsScreen
import com.nativeknights.petzy.splash.SplashScreen
import com.nativeknights.petzy.upload.UploadScreen
import com.nativeknights.petzy.upload.YourUploadsScreen
import com.nativeknights.petzy.viewmodel.AuthViewModel
import com.nativeknights.petzy.viewmodel.UploadViewModel


object Routes{
     const val SPLASH = "splash"
     const val LOGIN = "login"
     const val SIGNUP = "signup"
     const val HOME = "home"
     const val UPLOAD = "upload"
     const val REELS = "reels"
     const val NOTIFICATIONS = "notifications"
     const val PROFILE = "profile"
     const val PROFILE_DETAILS = "profile_details"
     const val UPLOADS = "uploads"
}

@Composable
fun AppNavGraph(navController: NavHostController, dataStoreManager: DataStoreManager) {
     NavHost(
          navController = navController,
          startDestination = Routes.SPLASH
     ) {
          composable(Routes.SPLASH) {
               SplashScreen(navController, dataStoreManager)
          }
          composable(Routes.LOGIN) {
               val authViewModel: AuthViewModel = hiltViewModel()
               LoginScreen(navController, authViewModel)
          }
          composable(Routes.SIGNUP) {
               val authViewModel: AuthViewModel = hiltViewModel()
               SignupScreen(navController, authViewModel)
          }
          composable(Routes.HOME) {
               HomeScreen(navController)
          }
          composable(Routes.PROFILE_DETAILS) {
               ProfileDetailScreen(navController)
          }
          composable(Routes.UPLOADS) {
               YourUploadsScreen(navController)
          }


          composable(Routes.UPLOAD) {
               val uploadViewModel: UploadViewModel = hiltViewModel()
               UploadScreen(
                    navController,
                    viewModel = uploadViewModel,
                    onUploadSuccess = {
                         navController.popBackStack()
                    }
               )
          }
          composable(Routes.REELS) {
               ReelsScreen(navController)
          }
          composable(Routes.NOTIFICATIONS) {
               NotificationScreen(navController)
          }
          composable(Routes.PROFILE) {
               ProfileScreen(navController)
          }
     }
}