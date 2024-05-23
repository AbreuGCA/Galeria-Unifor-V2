package com.example.galeriauniforv2

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class EditArtDialogFragment(private val artItem: ArtItem, private val callback: (ArtItem) -> Unit) : DialogFragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextArtist: EditText
    private lateinit var editTextCreationDate: EditText
    private lateinit var editTextDescription: EditText
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
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonCancel = view.findViewById(R.id.buttonCancel)

        editTextTitle.setText(artItem.title)
        editTextArtist.setText(artItem.artist)
        editTextCreationDate.setText(artItem.creationDate)
        editTextDescription.setText(artItem.description)

        buttonSave.setOnClickListener {
            val updatedArtItem = ArtItem(
                title = editTextTitle.text.toString(),
                artist = editTextArtist.text.toString(),
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), theme).apply {
            setTitle("Edit Art")
        }
    }
}
