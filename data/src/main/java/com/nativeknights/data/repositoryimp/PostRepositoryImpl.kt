package com.nativeknights.data.repositoryimp

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nativeknights.domain.model.Comment
import com.nativeknights.domain.model.Post
import com.nativeknights.domain.model.repository.PostRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db : FirebaseFirestore,
    private val auth : FirebaseAuth
) : PostRepository{

     private val postsCollection = db.collection("posts")

    override fun getAllPostsStream(): Flow<List<Post>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
         val query = postsCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
         val listener = query.addSnapshotListener { snapshot, error ->
              if (error != null){
                       trySend(emptyList())
                       return@addSnapshotListener
                   }
                   val list =  snapshot?.documents?.mapNotNull {doc ->

                        try {
                            val p = doc.toObject(Post::class.java)
                            val likedBy = doc.get("likedBy") as? List<*>
                            val isLiked = likedBy?.contains(currentUserId) == true
                            p?.copy(id = doc.id,
                                     isLiked = isLiked)
                        }catch (e:Exception){
                             null
                        }
                   } ?: emptyList()
                  trySend(list)
              }
             awaitClose {  listener.remove() }
         }

    override suspend fun toggleLike(postId: String): Result<Boolean> {
             val uid = auth.currentUser?.uid?: return Result.failure(Exception("Not Logged In"))
             val postRef = postsCollection.document(postId)

         return try {
              val isLikedNow =  db.runTransaction { tx ->
                   val snapshot = tx.get(postRef)
                  val currenLikes = snapshot.getLong("likes") ?: 0L
                  val likedBy = snapshot.get("likedBy") as? MutableList<*> ?: mutableListOf<String>()
                  val mutableLikedBy = likedBy.map { it.toString()}.toMutableList()

                  val liked = if(mutableLikedBy.contains(uid)){
                      mutableLikedBy.remove(uid)
                      tx.update(postRef,"likes",currenLikes-1,"likedBy" , mutableLikedBy)
                      false
                  }else{
                       //add like
                      mutableLikedBy.add(uid)
                      tx.update(postRef,"likes" , currenLikes+1,"likedBy",mutableLikedBy)
                      true
                  }
                  liked
              }.await()
             Result.success(isLikedNow)

         }catch (e:Exception){
              Result.failure(e)
         }

      }

    override suspend fun addComment(postId: String, text: String): Result<Unit> {
           val uid = auth.currentUser?.uid?:return Result.failure(Exception("Not Logged In"))

          return try {
               val comment = mapOf(
                    "userId" to uid,
                    "text" to text,
                     "timestamp" to System.currentTimeMillis()
               )
              postsCollection.document(postId).collection("comments").add(comment).await()

              val postRef = postsCollection.document(postId)
              db.runTransaction { tx ->
                    val snap = tx.get(postRef)
                  val comments = (snap.getLong("comments") ?: 0L)
                  tx.update(postRef,"comments",comments+1)
              }.await()
              Log.d("FirestoreDebug", "✅ Comment count updated successfully")
              Result.success(Unit)
          }catch (e:Exception){
              Log.e("FirestoreDebug", "❌ Failed to add comment: ${e.message}")
               Result.failure(e)
          }
     }

    override fun getCommentsFlow(postId: String): Flow<List<Comment>> = callbackFlow {
        val commentsRef = postsCollection.document(postId).collection("comments")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)

        val listener = commentsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val comments = snapshot?.documents?.mapNotNull { doc ->
                try {
                    val data = doc.toObject(Comment::class.java)
                    data?.copy(id = doc.id, postId = postId)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            trySend(comments)
        }

        awaitClose { listener.remove() }
    }


    override suspend fun getCurrentUserId(): String?  = auth.currentUser?.uid

    override suspend fun getCurrentUserInfo(): Triple<String, String, String>? {
        val user = auth.currentUser ?: return null
        val doc = db.collection("users").document(user.uid).get().await()
        val username = doc.getString("username") ?: user.displayName ?: "Unknown"
        val profileImage = doc.getString("profileImage") ?: user.photoUrl?.toString() ?: ""
        return Triple(user.uid, username, profileImage)
    }

    override suspend fun uploadPost(post: Post, imageUri: Uri): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                val ref = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
                ref.putFile(imageUri).await()
                val imageUrl = ref.downloadUrl.await().toString()

                val postWithUrl = post.copy(imageUrl = imageUrl)
                db.collection("posts").document(postWithUrl.id)
                    .set(postWithUrl)
                    .await()

                println("✅ Post uploaded successfully!")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Failed to upload: ${e.message}")
            Result.failure(e)
        }
    }

}