package com.nativeknights.domain.model.repository

import com.nativeknights.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signup(email: String, password: String, username: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun logout()
}