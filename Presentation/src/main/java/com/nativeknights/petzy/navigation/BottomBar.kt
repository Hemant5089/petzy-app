package com.nativeknights.petzy.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomBar(navController : NavHostController){
     Box{
          BottomAppBar(
               modifier = Modifier.fillMaxWidth(),
               content = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                         IconButton(onClick = { navController.navigate("home") }) {
                              Icon(Icons.Default.Home, contentDescription = "Home")
                         }
                         IconButton(onClick = { navController.navigate("notifications") }) {
                              Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                         }
                         Spacer(modifier = Modifier.width(68.dp)) // space for center FAB
                         IconButton(onClick = { navController.navigate("reels") }) {
                              Icon(Icons.Default.PlayArrow, contentDescription = "Reels")
                         }
                         IconButton(onClick = { navController.navigate("profile") }) {
                              Icon(Icons.Default.Person, contentDescription = "Profile")
                         }
                    }
               }
          )

          // center overlay FAB
          FloatingActionButton(
               onClick = { navController.navigate("upload") },
               modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp),
               shape = MaterialTheme.shapes.extraLarge
          ) {
               Icon(Icons.Outlined.AddCircle, contentDescription = "Upload")
          }
     }
}

//@Preview(showBackground = true)
//@Composable
//fun BottomBarPreview() {
//     val navController = rememberNavController()
//     BottomBar(navController)
//}