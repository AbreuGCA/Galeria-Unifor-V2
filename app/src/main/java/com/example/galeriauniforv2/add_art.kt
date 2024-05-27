package com.example.galeriauniforv2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
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
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)

        buttonSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        loadCategories()

        buttonAddArt.setOnClickListener {
            val title = findViewById<EditText>(R.id.editTextTitle).text.toString()
            val artist = findViewById<EditText>(R.id.editTextArtist).text.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val creationDate = findViewById<EditText>(R.id.editTextCreationDate).text.toString()
            val description = findViewById<EditText>(R.id.editTextDescription).text.toString()

            if (title.isNotEmpty() && artist.isNotEmpty() && creationDate.isNotEmpty() && description.isNotEmpty() && imageBase64 != null) {
                val newArtItem = ArtItem(title, artist, selectedCategory, creationDate, description, imageBase64!!)

                addArtToFirestore(newArtItem)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadCategories() {
        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categories = mutableListOf<String>()
                for (document in result) {
                    val category = document.data["name"] as String
                    categories.add(category)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
                spinnerCategory.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar categorias: ${e.message}", Toast.LENGTH_SHORT).show()
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
