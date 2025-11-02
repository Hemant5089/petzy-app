package com.nativeknights.petzy.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.nativeknights.domain.model.Comment
import com.nativeknights.petzy.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommentBottomSheet(
    postId: String,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    val comments by viewModel.getCommentsForPost(postId).collectAsState(initial = emptyList())
    var newComment by remember { mutableStateOf(TextFieldValue("")) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Comments List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comments) { comment ->
                        CommentItem(comment)
                    }
                }

                Divider()

                // Add Comment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF2F2F2), RoundedCornerShape(50))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (newComment.text.isEmpty()) {
                                Text("Add a comment...", color = Color.Gray)
                            }
                            inner()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            if (newComment.text.isNotBlank()) {
                                viewModel.addCommentToPost(postId, newComment.text, "Hemant")
                                newComment = TextFieldValue("")
                            }
                        }
                    ) {
                        Text("Post")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.userProfileImage.ifEmpty { null },
            contentDescription = "profile",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.username, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTime(comment.timestamp),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(comment.text)
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
