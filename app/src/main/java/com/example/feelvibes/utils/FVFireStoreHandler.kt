package com.example.feelvibes.utils

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.feelvibes.model.CommentModel
import com.example.feelvibes.model.PostModel
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

        fun setProfileUsername(userId: String, username: String, callback: (Boolean, Exception?, String?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("usersDisplayData").whereEqualTo("username", username)
            val documentDisplayDataRef = db.collection("usersDisplayData").document(userId)
            val documentRef = db.collection("users").document(userId)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.documents?.isEmpty() == true) {
                        // Username does not exist, proceed to storing.
                        documentRef.set(mutableMapOf("username" to username), SetOptions.merge())
                            .addOnSuccessListener {
                                documentDisplayDataRef.set(mutableMapOf("username" to username), SetOptions.merge())
                                    .addOnSuccessListener {
                                        callback(true, null, null)
                                    }.addOnFailureListener {
                                        callback(false, it, null)
                                    }
                            }.addOnFailureListener {
                                callback(false, it, null)
                            }
                    } else {
                        // Username exists, ending registration
                        callback(false, null, "Username already taken.")
                    }
                } else {
                    // Handle the exception from the query
                    callback(false, task.exception, null)
                }
            }
        }

        fun getProfileFullName(userId:String, callback: (ArrayList<String>?, Exception? )->Unit) {
            val displayDataRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            displayDataRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val firstName = documentSnapshot.get("firstName")
                    val lastName = documentSnapshot.get("lastName")
                    val fullName = arrayListOf("", "")
                    if (firstName != null && firstName is String)
                        fullName[0] = firstName
                    if (lastName != null && lastName is String) {
                        fullName[1] = lastName
                    }
                    if (fullName[0].isNotEmpty() || fullName[1].isNotEmpty())
                        callback(fullName, null)
                    else
                        callback(null, null)
                } else
                    callback(null, null)
            }.addOnFailureListener {
                callback(null, it)
            }
        }

        fun setProfileFirstName(userId: String, firstName: String, callback: (Boolean, Exception?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val documentDisplayDataRef = db.collection("usersDisplayData").document(userId)
            val documentRef = db.collection("users").document(userId)
            documentRef.set(mutableMapOf("firstName" to firstName), SetOptions.merge())
                .addOnSuccessListener {
                    documentDisplayDataRef.set(mutableMapOf("firstName" to firstName), SetOptions.merge())
                        .addOnSuccessListener {
                            callback(true, null)
                        }.addOnFailureListener {
                            callback(false, it)
                        }
                }.addOnFailureListener {
                    callback(false, it)
                }
        }

        fun setProfileLastName(userId: String, lastName: String, callback: (Boolean, Exception?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val documentDisplayDataRef = db.collection("usersDisplayData").document(userId)
            val documentRef = db.collection("users").document(userId)
            documentRef.set(mutableMapOf("lastName" to lastName), SetOptions.merge())
                .addOnSuccessListener {
                    documentDisplayDataRef.set(mutableMapOf("lastName" to lastName), SetOptions.merge())
                        .addOnSuccessListener {
                            callback(true, null)
                        }.addOnFailureListener {
                            callback(false, it)
                        }
                }.addOnFailureListener {
                    callback(false, it)
                }
        }

        fun getProfileBio(userId:String, callback: (String?, Exception?)->Unit) {
            val displayDataRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            displayDataRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val bio = documentSnapshot.get("bio")
                        if (bio != null && bio is String)
                            callback(bio, null)
                        else
                            callback(null, null)
                    } else
                        callback(null, null)
                }.addOnFailureListener {
                    callback(null, it)
                }
        }

        fun setProfileBio(userId: String, bio: String, callback: (Boolean, Exception?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val documentDisplayDataRef = db.collection("usersDisplayData").document(userId)
            val documentRef = db.collection("users").document(userId)
            documentRef.set(mutableMapOf("bio" to bio), SetOptions.merge())
                .addOnSuccessListener {
                    documentDisplayDataRef.set(mutableMapOf("bio" to bio), SetOptions.merge())
                        .addOnSuccessListener {
                            callback(true, null)
                        }.addOnFailureListener {
                            callback(false, it)
                        }
                }.addOnFailureListener {
                    callback(false, it)
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

        fun queryNewsfeed(callback: ((ArrayList<PostModel>, Exception?) -> Unit)? = null) {
            val collection = FirebaseFirestore.getInstance().collection("usersPost")
            val postModels = arrayListOf<PostModel>()
            collection.get().addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    if (doc.data != null) {
                        for (post in doc.data!!) {
                            postModels.add(PostModel(doc.id, post.key))
                        }
                    }
                }
                if (callback != null) {
                    postModels.shuffle()
                    callback(postModels, null)
                }
            }.addOnFailureListener {
                if (callback != null) {
                    callback(postModels, it)
                }
            }
        }

        fun queryUsersDisplayData(filter:String? = null, callback: ((ArrayList<String>, Exception?) -> Unit)? = null) {
            val collection = FirebaseFirestore.getInstance().collection("usersDisplayData")
            val users = arrayListOf<String>()
            collection.get().addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    if (!filter.isNullOrBlank()) {
                        val displayName = doc.get("username") as? String
                        if (displayName?.contains(filter, true) == true) {
                            users.add(doc.id)
                        }
                    } else {
                        users.add(doc.id)
                    }
                }
                if (callback != null) {
                    callback(users, null)
                }
            }.addOnFailureListener {
                if (callback != null) {
                    callback(users, it)
                }
            }
        }

        /* Note: This queries only one day, which is the current day.
        private fun queryNewsfeed(callback: (ArrayList<PostModel>?) -> Unit) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatted = currentDateTime.format(formatter)
            val documentId = formatted.replace("-", "")
            val postRef = FirebaseFirestore.getInstance().collection("newsfeed").document(documentId)
            postRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val postModels = arrayListOf<PostModel>()
                    val keys = documentSnapshot.data?.keys
                    if (keys != null) {
                        for (userId in keys) {
                            val postIds = documentSnapshot[userId]
                            if (postIds is ArrayList<*>) {
                                for (id in postIds) {
                                    if (id is String) {
                                        postModels.add(PostModel(userId, id))
                                    }
                                }
                            }
                        }
                    }
                    postModels.reverse()
                    callback(postModels)
                } else {
                    callback(null)
                }
            }.addOnFailureListener {
                it.printStackTrace()
                callback(null)
            }
        }*/

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

        fun reportPost(ownerId:String, postId: String, userId:String, callback: (Boolean, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersPost").document(ownerId)
            documentRef.update(mapOf(
                "$postId.reports" to FieldValue.arrayUnion(userId)
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener {
                callback(false, it)
            }
        }

        fun followUser(userId:String, followingId:String, callback: ((Boolean, Exception?) -> Unit)? = null) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            documentRef.update(mapOf(
                "followings" to FieldValue.arrayUnion(followingId)
            )).addOnSuccessListener {
                val followingDocumentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(followingId)
                followingDocumentRef.update(mapOf(
                    "followers" to FieldValue.arrayUnion(userId)
                )).addOnSuccessListener {
                    if (callback != null) {
                        callback(true, null)
                    }
                }.addOnFailureListener {
                    if (callback != null) {
                        callback(false, it)
                    }
                }
            }.addOnFailureListener {
                if (callback != null) {
                    callback(false, it)
                }
            }
        }

        fun unfollowUser(userId:String, followingId:String, callback: ((Boolean, Exception?) -> Unit)? = null) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            documentRef.update(mapOf(
                "followings" to FieldValue.arrayRemove(followingId)
            )).addOnSuccessListener {
                val followingDocumentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(followingId)
                followingDocumentRef.update(mapOf(
                    "followers" to FieldValue.arrayRemove(userId)
                )).addOnSuccessListener {
                    if (callback != null) {
                        callback(true, null)
                    }
                }.addOnFailureListener {
                    if (callback != null) {
                        callback(false, it)
                    }
                }
            }.addOnFailureListener {
                if (callback != null) {
                    callback(false, it)
                }
            }
        }

        fun isFollowing(userId:String, followingId:String, callback: ((Boolean, Exception?) -> Unit)? = null) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            var has = false
            documentRef.get().addOnSuccessListener { snapshot ->
                val followedUsers = snapshot.get("followings") as? ArrayList<*>
                if (followedUsers != null) {
                    for (following in followedUsers) {
                        if (following is String && following.contains(followingId)) {
                            has = true
                            break
                        }
                    }
                }
                if (callback != null) {
                    callback(has, null)
                }
            }.addOnFailureListener { exception ->
                if (callback != null) {
                    callback(has, exception)
                }
            }
        }

        fun getFollowers(userId:String, callback: (ArrayList<String>, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            documentRef.get().addOnSuccessListener { snapshot ->
                val followers = ((snapshot.get("followers") as? ArrayList<*>)?.filterIsInstance<String>() ?: arrayListOf()) as ArrayList<String>
                callback(followers, null)
            }.addOnFailureListener {
                callback(arrayListOf(), it)
            }
        }

        fun getFollowings(userId:String, callback: (ArrayList<String>, Exception?) -> Unit) {
            val documentRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
            documentRef.get().addOnSuccessListener { snapshot ->
                val followings = ((snapshot.get("followings") as? ArrayList<*>)?.filterIsInstance<String>() ?: arrayListOf()) as ArrayList<String>
                callback(followings, null)
            }.addOnFailureListener {
                callback(arrayListOf(), it)
            }
        }

    }
}