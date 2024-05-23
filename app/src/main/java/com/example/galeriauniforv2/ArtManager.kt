package com.example.galeriauniforv2

import com.google.firebase.firestore.FirebaseFirestore

class ArtManager {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateArtItem(oldTitle: String, newArtItem: ArtItem, callback: (Boolean, String?) -> Unit) {
        db.collection("artworks")
            .whereEqualTo("title", oldTitle)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false, "Obra não encontrada.")
                } else {
                    for (document in documents) {
                        db.collection("artworks").document(document.id)
                            .set(newArtItem)
                            .addOnSuccessListener { callback(true, null) }
                            .addOnFailureListener { e -> callback(false, e.message) }
                    }
                }
            }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun deleteArtItem(title: String, callback: (Boolean, String?) -> Unit) {
        db.collection("artworks")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false, "Obra não encontrada.")
                } else {
                    for (document in documents) {
                        db.collection("artworks").document(document.id)
                            .delete()
                            .addOnSuccessListener { callback(true, null) }
                            .addOnFailureListener { e -> callback(false, e.message) }
                    }
                }
            }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}
