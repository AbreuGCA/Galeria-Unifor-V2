package com.example.galeriauniforv2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class EditArtDialogFragment(private val artItem: ArtItem, private val callback: (ArtItem) -> Unit) : DialogFragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextArtist: EditText
    private lateinit var editTextCreationDate: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_edit_art, container, false)

        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextArtist = view.findViewById(R.id.editTextArtist)
        editTextCreationDate = view.findViewById(R.id.editTextCreationDate)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonCancel = view.findViewById(R.id.buttonCancel)

        editTextTitle.setText(artItem.title)
        editTextArtist.setText(artItem.artist)
        editTextCreationDate.setText(artItem.creationDate)
        editTextDescription.setText(artItem.description)

        // Carregar categorias do Firestore e configurar no Spinner
        loadCategories()

        buttonSave.setOnClickListener {
            val updatedArtItem = ArtItem(
                title = editTextTitle.text.toString(),
                artist = editTextArtist.text.toString(),
                category = spinnerCategory.selectedItem.toString(),
                creationDate = editTextCreationDate.text.toString(),
                description = editTextDescription.text.toString(),
                imageBase64 = artItem.imageBase64
            )
            callback(updatedArtItem)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return view
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
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter

                val currentCategoryIndex = categories.indexOf(artItem.category)
                if (currentCategoryIndex != -1) {
                    spinnerCategory.setSelection(currentCategoryIndex)
                }
            }
            .addOnFailureListener { e ->
                // Tratar erro ao carregar categorias
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), theme).apply {
            setTitle("Edit Art")
        }
    }
}