package com.example.feelvibes.utils

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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