package com.nativeknights.data.repositoryimp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.nativeknights.domain.model.User
import com.nativeknights.domain.model.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
     private val auth : FirebaseAuth,
     private val db : FirebaseFirestore
) : AuthRepository{
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No UID found"))

            val snapshot = db.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java) ?: User(id = uid, email = email)

             Result.success(user)
        }    catch (e: Exception) {
             Result.failure(e)
        }
    }

    override suspend fun signup(email: String, password: String, username: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No UID found"))

            val user = User(id = uid, email = email, username = username)
            db.collection("users").document(uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
          val listener = FirebaseAuth.AuthStateListener { auth ->
               val firebaseUser = auth.currentUser
              if (firebaseUser != null) {
                  trySend(User(id = firebaseUser.uid, email = firebaseUser.email ?: ""))
              }else{
                  trySend(null)
              }
          }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
     }

    override suspend fun logout() {
       auth.signOut()
    }

}