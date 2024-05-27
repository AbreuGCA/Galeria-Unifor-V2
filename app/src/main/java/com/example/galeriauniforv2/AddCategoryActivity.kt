package com.example.galeriauniforv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddCategoryActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val categoryNameTextView = findViewById<EditText>(R.id.categoryNameTextView)
        val createCategoryButton = findViewById<Button>(R.id.createCategoryButton)
        val checkBoxContainer = findViewById<LinearLayout>(R.id.checkBoxContainer)

        // Recuperar obras de arte do banco de dados
        db.collection("artworks")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val artworkTitle = document.getString("title") ?: ""
                    // Criar um checkbox para cada obra de arte
                    val checkBox = CheckBox(this)
                    checkBox.text = artworkTitle
                    checkBoxContainer.addView(checkBox)
                }
            }
            .addOnFailureListener { exception ->
                // Tratar falha ao recuperar as obras de arte
            }

        createCategoryButton.setOnClickListener {
            val categoryName = categoryNameTextView.text.toString()
            val selectedArtworks = mutableListOf<String>()

            // Percorrer todos os checkboxes para verificar quais estão marcados
            for (i in 0 until checkBoxContainer.childCount) {
                val view = checkBoxContainer.getChildAt(i)
                if (view is CheckBox && view.isChecked) {
                    selectedArtworks.add(view.text.toString())
                }
            }

            // Criar a categoria no banco de dados
            db.collection("categories")
                .add(mapOf("name" to categoryName))
                .addOnSuccessListener { categoryDocument ->
                    // Modificar a categoria de cada obra de arte selecionada
                    selectedArtworks.forEach { artworkTitle ->
                        db.collection("artworks")
                            .whereEqualTo("title", artworkTitle)
                            .get()
                            .addOnSuccessListener { artworkResult ->
                                artworkResult.documents.forEach { artworkDocument ->
                                    artworkDocument.reference.update("category", categoryName)
                                }
                            }
                    }
                    Toast.makeText(this, "Categoria adicionada com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao criar categoria: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}
