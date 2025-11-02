package com.nativeknights.petzy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.data.repositoryimp.datastore.DataStoreManager
import com.nativeknights.domain.model.repository.PostRepository
import com.nativeknights.domain.model.usecase.LoginUseCase
import com.nativeknights.domain.model.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
       private val loginUseCase: LoginUseCase,
       private val signupUseCase: SignupUseCase,
       private val dataStoreManager: DataStoreManager,
       private val postRepository: PostRepository
)  : ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isloading : StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _profileImage = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage



    fun login(email : String , password : String, onSuccess : () -> Unit) {
         viewModelScope.launch {
              _isLoading.value = true
              val result = loginUseCase(email,password)
             _isLoading.value = false
             result.onSuccess {
                  dataStoreManager.setLoggedIn(true)
//                 // 👇 Fetch current user info right after login
//                 val userInfo = postRepository.getCurrentUserInfo()
//                 userInfo?.let { (_, name, image) ->
//                     _displayName.value = name
//                     _profileImage.value = image
//                 }
                 onSuccess()
             }.onFailure {
                  _error.value = it.message
             }
         }
    }

    fun signup(email : String , password :String , username : String , onSuccess : () -> Unit) {
          viewModelScope.launch {
               _isLoading.value = true
               val result = signupUseCase(email,password,username)
              _isLoading.value = false
              result.onSuccess {
                   dataStoreManager.setLoggedIn(true)

//                  // 👇 Fetch current user info right after signup
//                  val userInfo = postRepository.getCurrentUserInfo()
//                  userInfo?.let { (_, name, image) ->
//                      _displayName.value = name
//                      _profileImage.value = image
//                  }

                   onSuccess()
              }.onFailure {
                  _error.value = it.message
              }
          }
    }

    fun logout(){
         viewModelScope.launch {
              dataStoreManager.setLoggedIn(false)
         }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            val userInfo = postRepository.getCurrentUserInfo()
            userInfo?.let { (_, name, image) ->
                _displayName.value = name
                _profileImage.value = image
            }
        }
    }
}