package com.example.feelvibes.utils

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireBaseStorageHandler {

    var fileStoredListener: (()-> Unit)? = null

    // Deprecate
    fun putFile(activity: Activity, ref: StorageReference, userId: String, uri: Uri) {
        ref.putFile(uri)
            .addOnSuccessListener {
                // Upload succeeded, get download URL
                ref.downloadUrl.addOnSuccessListener { uri ->
                    // Update FireStore document with download URL
                    val userRef = FirebaseFirestore.getInstance().collection("usersDisplayData").document(userId)
                    userRef.update("profilePictureUrl", uri.toString())
                        .addOnSuccessListener {
                            fileStoredListener?.invoke()
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            Toast.makeText(activity, "Profile picture failed to save", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(activity, "Profile picture failed to save", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {

        fun putFile(ref: StorageReference, uri: Uri, callback: (Boolean)-> Unit) {
            ref.putFile(uri)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }

        fun getDownloadUrl(ref: StorageReference, callback: (Uri?) -> Unit) {
            ref.downloadUrl
                .addOnSuccessListener { uri ->
                    callback(uri)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun deleteFolder(storage: FirebaseStorage, folderPath: String, onComplete: (Boolean, Exception?) -> Unit) {
            val folderRef = storage.reference.child(folderPath)
            folderRef.listAll()
                .addOnSuccessListener { listResult ->
                    val itemsToDelete = listResult.items + listResult.prefixes
                    var deletedItems = 0
                    itemsToDelete.forEach { item ->
                        item.delete().addOnSuccessListener {
                            deletedItems++
                            if (deletedItems == itemsToDelete.size) {
                                folderRef.delete()
                                    .addOnSuccessListener {
                                        onComplete(true, null)
                                    }
                                    .addOnFailureListener { exception ->
                                        onComplete(false, exception)
                                    }
                            }
                        }.addOnFailureListener { exception ->
                            onComplete(false, exception)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    onComplete(false, exception)
                }
        }

    }

}