package com.nativeknights.petzy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nativeknights.domain.model.Post
import com.nativeknights.domain.model.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel(){
    // posts stream from repository
    // 🔹 Maintain your own copy of posts for instant UI updates
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts


    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error


    init {
        // Collect posts in realtime from Firestore
        viewModelScope.launch {
            postRepository.getAllPostsStream().collect { newList ->
                _posts.value = newList
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            // 🔸 1. Optimistic update: instantly flip like in local UI
            val updated = _posts.value.map { post ->
                if (post.id == postId) {
                    post.copy(
                        isLiked = !post.isLiked,
                        likes = if (post.isLiked) post.likes - 1 else post.likes + 1
                    )
                } else post
            }
            _posts.value = updated

            // Then actually update Firestore (background)
            val res = postRepository.toggleLike(postId)
            if (res.isFailure) {
                // Optional: revert like if failed
                _error.value = res.exceptionOrNull()?.localizedMessage
            }
        }
    }

    fun getCommentsForPost(postId: String) = postRepository.getCommentsFlow(postId)

    fun addCommentToPost(postId: String, text: String, username: String) {
        viewModelScope.launch {
            val res = postRepository.addComment(postId, text)
            if (res.isFailure) {
                _error.value = res.exceptionOrNull()?.localizedMessage
            }
        }
    }

}