package com.nativeknights.domain.model.usecase

import com.nativeknights.domain.model.User
import com.nativeknights.domain.model.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
       private val repository: AuthRepository
) {
    suspend operator fun invoke(email : String , password : String) : Result<User>{
         return repository.login(email,password)
    }
}