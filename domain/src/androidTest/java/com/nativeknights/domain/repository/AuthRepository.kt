package com.nativeknights.domain.repository


import com.google.firebase.auth.FirebaseUser
import com.nativeknights.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email : String , password : String) : Result<FirebaseUser?>
    suspend fun signup(user : User, password: String)
    fun logout()
    fun getCurrentUser() : FirebaseUser?
    suspend fun saveLoginState(isLoggedIn:Boolean)
    val isLoggedIn : Flow<Boolean>

}