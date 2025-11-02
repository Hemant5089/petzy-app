package com.nativeknights.petzy.viewmodel

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.domain.model.PetType
import com.nativeknights.domain.model.Post
import com.nativeknights.domain.model.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import androidx.work.*
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _petType = MutableStateFlow<PetType?>(null)
    val petType: StateFlow<PetType?> = _petType

    private val _caption = MutableStateFlow("")
    val caption: StateFlow<String> = _caption

    private val _hashtags = MutableStateFlow("")
    val hashtags: StateFlow<String> = _hashtags

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun onPetTypeSelected(type: PetType) {
        _petType.value = type
    }

    fun onCaptionChanged(text: String) {
        _caption.value = text
    }

    fun onHashtagsChanged(text: String) {
        _hashtags.value = text
    }

    fun uploadPost() {
        val imageUri = _selectedImageUri.value ?: return
        val pet = _petType.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _isUploading.value = true
            try {
                val userInfo = postRepository.getCurrentUserInfo()

                if (userInfo == null) {
                    println("❌ User not logged in")
                    _isUploading.value = false
                    return@launch
                }

                val (userId, username, profileImage) = userInfo

                val post = Post(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    username = username,
                    userProfileImage = profileImage,
                    imageUrl = "", // Will be updated after upload
                    caption = _caption.value,
                    hashtags = _hashtags.value
                        .split(" ")
                        .filter { it.startsWith("#") },
                    timestamp = System.currentTimeMillis(),
                    petType = pet
                )

                postRepository.uploadPost(post, imageUri)
                println("✅ Post uploaded successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ Upload failed: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }
}
