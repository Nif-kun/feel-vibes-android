package com.example.feelvibes.utils

import android.net.Uri
import com.example.feelvibes.model.CommentModel
import com.example.feelvibes.model.ReplyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FVFireStoreHandler {
    companion object {

        fun checkAuth(callback: ((FirebaseUser?) -> Unit)) {
            val mAuth = FirebaseAuth.getInstance()
            if (mAuth.currentUser != null) {
                mAuth.currentUser?.reload()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(mAuth.currentUser)
                    }
                }?.addOnFailureListener { e ->
                    e.printStackTrace()
                    callback(null)
                }
            } else {
                callback(null)
            }
        }

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

        fun addPostLike(ownerId:String, postId: String, userId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update(mapOf(
                "$postId.likes" to FieldValue.arrayUnion(userId)
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener {
                callback(false, it)
            }
        }

        fun removePostLike(ownerId:String, postId: String, userId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update(mapOf(
                "$postId.likes" to FieldValue.arrayRemove(userId)
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener {
                callback(false, it)
            }
        }

        fun getComments(userId:String, postId:String, callback: ((MutableMap<*,*>?)->Unit)) {
            val postRef = FirebaseFirestore.getInstance().collection("usersPost").document(userId)

        }

        fun parcelizeComments(rawComments: HashMap<*,*>?): ArrayList<CommentModel> {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val formatted = currentDateTime.format(formatter)
            val comments = arrayListOf<CommentModel>()
            if (rawComments != null) {
                for (rawComment in rawComments) {
                    val id = rawComment.key as? String
                    val map = rawComment.value as? HashMap<*, *>
                    if (map != null) {
                        val owner = map["owner"] as? String
                        val time = map["time"] as? String ?: formatted
                        val text = map["text"] as? String
                        val likes = ((map["likes"] as? ArrayList<*>)?.filterIsInstance<String>() ?: arrayListOf()) as ArrayList<String>
                        val replies = parcelizeReplies(map["replies"] as? HashMap<*, *>)
                        if (id != null && owner != null && text != null)
                            comments.add(CommentModel(id, owner, time, text, likes, replies))
                    }
                }
            }
            return comments
        }

        fun parcelizeReplies(rawReplies: HashMap<*,*>?): ArrayList<ReplyModel> {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val formatted = currentDateTime.format(formatter)
            val replies = arrayListOf<ReplyModel>()
            if (rawReplies != null) {
                for (rawReply in rawReplies) {
                    val id = rawReply.key as? String
                    val map = rawReply.value as? HashMap<*, *>
                    if (map != null) {
                        val owner = map["owner"] as? String
                        val time = map["time"] as? String ?: formatted
                        val text = map["text"] as? String
                        val likes = ((map["likes"] as? ArrayList<*>)?.filterIsInstance<String>() ?: arrayListOf()) as ArrayList<String>
                        if (id != null && owner != null && text != null && text.isNotBlank())
                            replies.add(ReplyModel(id, owner, time, text, likes))
                    }
                }
            }
            return replies
        }

        fun addPostComment(ownerId:String, postId: String, userId:String, text:String, callback: (Boolean, Exception?) -> Unit) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val formatted = currentDateTime.format(formatter)
            val sanitizedFormatted = formatted.replace("-", "").replace(":", "").replace("/", "")
            val comment = mapOf(
                postId to mapOf(
                    "comments" to mapOf(
                        sanitizedFormatted+userId to mapOf(
                            "owner" to userId,
                            "time" to formatted,
                            "text" to text,
                            "likes" to ArrayList<String>(),
                            "replies" to mapOf<String, HashMap<String, Any>>()
                        )
                    )
                )
            )

            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.set(comment, SetOptions.merge())
                .addOnSuccessListener {
                    callback(true, null)
                }.addOnFailureListener {
                    callback(false, it)
                }
        }

        fun addPostComment(ownerId:String, postId: String, userId:String, text:String, callback: (Boolean, CommentModel?, Exception?) -> Unit) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val formatted = currentDateTime.format(formatter)
            val sanitizedFormatted = formatted.replace("-", "").replace(":", "").replace("/", "")
            val comment = mapOf(
                postId to mapOf(
                    "comments" to mapOf(
                        sanitizedFormatted+userId to mapOf(
                            "owner" to userId,
                            "time" to formatted,
                            "text" to text,
                            "likes" to ArrayList<String>(),
                            "replies" to mapOf<String, HashMap<String, Any>>()
                        )
                    )
                )
            )
            val commentModel = CommentModel(
                sanitizedFormatted+userId,
                userId,
                formatted,
                text,
                ArrayList(),
                ArrayList(),
            )

            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.set(comment, SetOptions.merge())
                .addOnSuccessListener {
                    callback(true, commentModel, null)
                }.addOnFailureListener {
                    callback(false, null, it)
                }
        }

        fun removePostComment(ownerId:String, postId: String, commentId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update("$postId.comments.$commentId", FieldValue.delete())
                .addOnSuccessListener {
                    callback(true, null)
                }.addOnFailureListener {
                    callback(false, it)
                }
        }

        fun addCommentLike(ownerId:String, postId: String, commentId:String, userId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update(mapOf(
                "$postId.comments.$commentId.likes" to FieldValue.arrayUnion(userId)
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener {
                callback(false, it)
            }
        }

        fun removeCommentLike(ownerId:String, postId: String, commentId:String, userId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update(mapOf(
                "$postId.comments.$commentId.likes" to FieldValue.arrayRemove(userId)
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener {
                callback(false, it)
            }
        }

        fun addCommentReply(ownerId:String, postId: String, commentId: String, userId:String, text:String, callback: (Boolean, ReplyModel?, Exception?) -> Unit) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val formatted = currentDateTime.format(formatter)
            val sanitizedFormatted = formatted.replace("-", "").replace(":", "").replace("/", "")
            val reply = mapOf(
                postId to mapOf(
                    "comments" to mapOf(commentId to mapOf(
                        "replies" to mapOf(sanitizedFormatted+userId to mapOf(
                            "owner" to userId,
                            "time" to formatted,
                            "text" to text,
                            "likes" to ArrayList<String>())
                        ))
                    )
                )
            )
            val replyModel = ReplyModel(
                sanitizedFormatted+userId,
                userId,
                formatted,
                text,
                ArrayList()
            )

            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.set(reply, SetOptions.merge())
                .addOnSuccessListener {
                    callback(true, replyModel, null)
                }.addOnFailureListener {
                    callback(false, null, it)
                }
        }

        fun removeCommentReply(ownerId:String, postId: String, commentId:String, replyId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update("$postId.comments.$commentId.replies.$replyId", FieldValue.delete())
                .addOnSuccessListener {
                    callback(true, null)
                }.addOnFailureListener {
                    callback(false, it)
                }
        }

    }
}