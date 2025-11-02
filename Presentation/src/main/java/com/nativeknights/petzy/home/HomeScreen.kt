package com.nativeknights.petzy.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nativeknights.domain.model.PetType
import com.nativeknights.domain.model.Post
import com.nativeknights.petzy.viewmodel.AuthViewModel
import com.nativeknights.petzy.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
     navController: androidx.navigation.NavHostController,
     viewModel: HomeViewModel = hiltViewModel(),
     authViewModel: AuthViewModel = hiltViewModel()
) {
     val posts by viewModel.posts.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
     val error by viewModel.error.collectAsState()
     val displayName by authViewModel.displayName.collectAsState()
      var selectedFilter by remember { mutableStateOf<PetType?>(null) }


     LaunchedEffect(Unit) {
          authViewModel.loadCurrentUser()
     }

     Scaffold(
          topBar = {
               TopBar(navController, if (displayName.isNotBlank()) displayName else "Petzy" , selectedPetFilter = selectedFilter , onFilterSelected =  { selectedFilter = it})
          },
     ) { padding ->
          if (error != null) {
               // show snackbar or error UI
               LaunchedEffect(error) {
                    snackbarHostState.showSnackbar(error ?: "")
               }
          }

          val filteredPosts = if (selectedFilter == null) {
               posts
          } else {
               posts.filter { it.petType == selectedFilter }
          }

          LazyColumn(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
          ) {
               items(filteredPosts) { post ->
                    PostCard(post = post, onLike = { viewModel.toggleLike(post.id) }, onComment = {
                         // navigate to comments screen - placeholder
                         navController.navigate("comments/${post.id}")
                    })
               }
          }
     }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
                   navController: androidx.navigation.NavHostController,
                   displayName: String ,
                   selectedPetFilter : PetType?,
                   onFilterSelected : (PetType?) -> Unit
 ) {

      var expanded by remember { mutableStateOf(false) }
     TopAppBar(
          title = {
              Column {
                  Text(
                      text = "Welcome back",
                      style = MaterialTheme.typography.labelMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      letterSpacing = 0.5.sp
                  )
                  Text(
                      text = displayName,
                      style = MaterialTheme.typography.headlineSmall.copy(
                          fontWeight = FontWeight.Medium
                      ),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                  )
              }
          },
          actions = {

               // Filter button
               IconButton(onClick = { expanded = true }) {
                    Icon(
                         imageVector = Icons.Default.Menu,
                         contentDescription = "Filter posts"
                    )
               }

               DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
               ) {
                    DropdownMenuItem(
                         text = { Text("All Pets") },
                         onClick = {
                              onFilterSelected(null)
                              expanded = false
                         }
                    )
                    PetType.values().forEach { type ->
                         DropdownMenuItem(
                              text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                              onClick = {
                                   onFilterSelected(type)
                                   expanded = false
                              }
                         )
                    }
               }

               // Profile image - clickable
               // Replace with real url from user data
               IconButton(onClick = { navController.navigate("profile") }) {
                    // placeholder circle
                    Box(
                         modifier = Modifier
                              .size(36.dp)
                              .clip(CircleShape)
                              .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                         Text(
                              text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                              modifier = Modifier.align(Alignment.Center)
                         )
                    }
               }
          }


     )
}

@Composable
fun PostCard(post: Post, onLike: () -> Unit, onComment: () -> Unit) {
     var expanded by remember { mutableStateOf(false) }
     var showComments by remember { mutableStateOf(false) }

     var isLiked by remember { mutableStateOf(post.isLiked) }


     //  animation for smooth like effect
     val scale by animateFloatAsState(
          targetValue = if (isLiked) 1.2f else 1f,
          label = "LikeAnimation"
     )



     Card(
          modifier = Modifier
               .fillMaxWidth()
               .padding(12.dp),
          shape = RoundedCornerShape(12.dp)
     ) {
          Column {
               // header
               Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                         model = post.userProfileImage.takeIf { it.isNotBlank() } ?: null,
                         contentDescription = "profile",
                         modifier = Modifier
                              .size(42.dp)
                              .clip(CircleShape)
                              .background(MaterialTheme.colorScheme.surfaceVariant),
                         contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                         Text(post.username, style = MaterialTheme.typography.titleSmall)
                         Text(
                              formatTimestamp(post.timestamp),
                              style = MaterialTheme.typography.bodySmall,
                              color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                    }
//                    IconButton(onClick = { /* more menu */ }) {
//                         Icon(Icons.Default.MoreVert, contentDescription = "more")
//                    }
               }

               AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "post image",
                    modifier = Modifier
                         .fillMaxWidth()
                         .clip(RoundedCornerShape(12.dp))
                         .wrapContentHeight(),
                    contentScale = ContentScale.Fit
               )


               // actions
               Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike) {
                         Icon(
                              imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                              contentDescription = "Like",
                              tint = if (isLiked) Color(0xFFE91E63) else Color.Gray,
                              modifier = Modifier.scale(scale)// dark pink when liked
                         )
                    }
                    Text("${post.likes}", modifier = Modifier.padding(end = 12.dp))
                    IconButton(onClick = { showComments = true }) {
                         Icon(Icons.Default.Email, contentDescription = "comment")
                    }
                    Text("${post.comments}")
               }

               if (showComments){
                     CommentBottomSheet(
                           postId = post.id,
                           onDismiss = {showComments = false},
                           viewModel = hiltViewModel()
                     )
               }

               // caption with see more
               Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    val caption = post.caption
                    if (caption.length > 120 && !expanded) {
                         Text(
                              text = caption.take(120) + "…",
                              maxLines = 2,
                              overflow = TextOverflow.Ellipsis
                         )
                         TextButton(onClick = { expanded = true }) {
                              Text("See more")
                         }
                    } else {
                         Text(text = caption)
                         if (caption.length > 120) {
                              TextButton(onClick = { expanded = false }) {
                                   Text("See less")
                              }
                         }
                    }
               }
          }
     }
}
private fun formatTimestamp(ts: Long): String {
     if (ts <= 0L) return ""
     val sdf = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())
     return sdf.format(Date(ts))
}
