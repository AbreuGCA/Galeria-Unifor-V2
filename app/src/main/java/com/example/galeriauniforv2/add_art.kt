package com.example.galeriauniforv2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class add_art : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var imageView: ImageView
    private var imageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art)

        db = FirebaseFirestore.getInstance()
        imageView = findViewById(R.id.imageViewArt)

        val buttonSelectImage = findViewById<Button>(R.id.buttonSelectImage)
        val buttonAddArt = findViewById<Button>(R.id.buttonAddArt)

        buttonSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        buttonAddArt.setOnClickListener {
            val title = findViewById<EditText>(R.id.editTextTitle).text.toString()
            val artist = findViewById<EditText>(R.id.editTextArtist).text.toString()
            val creationDate = findViewById<EditText>(R.id.editTextCreationDate).text.toString()
            val description = findViewById<EditText>(R.id.editTextDescription).text.toString()

            if (title.isNotEmpty() && artist.isNotEmpty() && creationDate.isNotEmpty() && description.isNotEmpty() && imageBase64 != null) {
                val newArtItem = ArtItem(title, artist, creationDate, description, imageBase64!!)

                addArtToFirestore(newArtItem)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data
            uri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
                imageBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
