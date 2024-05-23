package com.example.galeriauniforv2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.galeriauniforv2.ArtItem
import com.example.galeriauniforv2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class add_art : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art)

        db = FirebaseFirestore.getInstance()

        val buttonAddArt = findViewById<Button>(R.id.buttonAddArt)
        buttonAddArt.setOnClickListener {
            val title = findViewById<EditText>(R.id.editTextTitle).text.toString()
            val artist = findViewById<EditText>(R.id.editTextArtist).text.toString()
            val creationDate = findViewById<EditText>(R.id.editTextCreationDate).text.toString()
            val description = findViewById<EditText>(R.id.editTextDescription).text.toString()
            val image = findViewById<EditText>(R.id.editTextBase64).text.toString()

            if (title.isNotEmpty() && artist.isNotEmpty() && creationDate.isNotEmpty() && description.isNotEmpty()) {
                val newArtItem = ArtItem(title, artist, creationDate, description, image)

                addArtToFirestore(newArtItem)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addArtToFirestore(artItem: ArtItem) {
        val artworksCollection = db.collection("artworks")

        artworksCollection
            .add(artItem)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Arte adicionada com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao adicionar arte: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

