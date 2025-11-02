package com.nativeknights.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val petCount: Int = 0
)

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val hashtags: List<String> = emptyList(),
    val likes: Int = 0,
    val comments: Int = 0,
    val timestamp: Long = 0L,
    val isLiked: Boolean = false,
    val petType: PetType = PetType.DOG
)
data class Comment(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

enum class PetType {
    DOG, CAT, BIRD, FISH, RABBIT, OTHER
}
