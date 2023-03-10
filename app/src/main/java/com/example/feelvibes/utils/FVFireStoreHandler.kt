package com.example.feelvibes.utils

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FVFireStoreHandler {
    companion object {

        fun getProfileName(userId:String, callback: ((String?)->Unit)) {
            val displayDataRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            displayDataRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val username = documentSnapshot.getString("username")
                    if (username != null)
                        callback(username)
                    else
                        callback(null)
                } else
                    callback(null)
            }.addOnFailureListener {
                it.printStackTrace()
                callback(null)
            }
        }

        fun getProfilePicture(userId:String, callback: ((Uri?)->Unit)) {
            val storageRef = FirebaseStorage.getInstance().getReference("profilePictures")
            val imageRef = storageRef.child("${userId}.png")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri)
            }.addOnFailureListener {
                it.printStackTrace()
                callback(null)
            }
        }

        fun getPost(userId:String, postId:String, callback: ((MutableMap<*,*>?)->Unit)) {
            val postRef = FirebaseFirestore.getInstance().collection("usersPost").document(userId)
            postRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val post = documentSnapshot.get(postId)
                    if (post != null && post is MutableMap<*, *>) {
                        callback(post)
                    } else {
                        callback(null)
                    }
                } else
                    callback(null)
            }.addOnFailureListener {
                it.printStackTrace()
                callback(null)
            }
        }

        fun deletePost(userId:String, postId:String, callback: (Boolean, Exception?) -> Unit?) {
            val docRef = FirebaseFirestore.getInstance().collection("usersPost").document(userId)
            val updates = hashMapOf<String, Any>(
                postId to FieldValue.delete()
            )
            docRef.update(updates)
                .addOnSuccessListener { callback(true, null) }
                .addOnFailureListener { e ->
                    callback(false, e)
                }
        }

        fun deleteNewsfeed(userId:String, postId:String, callback: (Boolean, Exception?) -> Unit?) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatted = currentDateTime.format(formatter)
            val documentId = formatted.replace("-", "")
            val documentRef = FirebaseFirestore.getInstance().collection("newsfeed").document(documentId)
            documentRef.update(userId, FieldValue.arrayRemove(postId))
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(true, e)
                }
        }

        fun getPostTime(userId:String, postId:String, callback: ((String?)->Unit)) {
            getPost(userId, postId) { map ->
                callback(map?.get("time") as? String)
            }
        }

        fun getPostText(userId:String, postId:String, callback: ((String?)->Unit)) {
            getPost(userId, postId) { map ->
                callback(map?.get("text") as? String)
            }
        }

    }
}