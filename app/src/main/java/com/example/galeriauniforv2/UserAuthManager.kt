package com.example.galeriauniforv2

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserAuthManager{

    private val db: FirebaseFirestore = Firebase.firestore

    fun verifyUser(registration: String, password: String, callback: (Boolean, String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("registration", registration)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false, "Matricula ou senha incorretos.")
                } else {
                    callback(true, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

}
