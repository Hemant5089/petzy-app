package com.nativeknights.domain.model.repository

import android.net.Uri
import com.nativeknights.domain.model.Comment
import com.nativeknights.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    fun getAllPostsStream(): Flow<List<Post>>

    suspend fun toggleLike(postId : String) : Result<Boolean>

    suspend fun addComment(postId: String , text : String) : Result<Unit>

    fun getCommentsFlow(postId: String): Flow<List<Comment>>

    suspend fun getCurrentUserId() : String?

    suspend fun getCurrentUserInfo(): Triple<String, String, String>?

    suspend fun uploadPost(post: Post, imageUri: Uri) : Result<Unit>

}