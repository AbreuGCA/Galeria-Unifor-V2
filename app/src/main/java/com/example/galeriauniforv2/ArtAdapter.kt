package com.example.galeriauniforv2

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ArtAdapter(private val activity: FragmentActivity) : ListAdapter<ArtItem, ArtAdapter.ArtViewHolder>(ArtDiffCallback()) {

    private val artManager = ArtManager()

    class ArtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artTitle: TextView = itemView.findViewById(R.id.artTitle)
        val artArtist: TextView = itemView.findViewById(R.id.artArtist)
        val artCreationDate: TextView = itemView.findViewById(R.id.artCreationDate)
        val artImage: ImageView = itemView.findViewById(R.id.artImage)
        val artDescription: TextView = itemView.findViewById(R.id.artDescription)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_art, parent, false)
        return ArtViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        val artItem = getItem(position)
        holder.artTitle.text = artItem.title
        holder.artArtist.text = artItem.artist
        holder.artCreationDate.text = artItem.creationDate
        holder.artDescription.text = artItem.description

        // Decode the Base64 string to display the image
        val decodedBytes = Base64.decode(artItem.imageBase64, Base64.DEFAULT)
        holder.artImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size))

        // Adicionando o clique para abrir a tela de detalhes
        holder.itemView.setOnClickListener {
            val imagePath = saveImageToFile(activity, artItem.imageBase64)
            val artItem = getItem(position)

            if (imagePath != null) {
                val intent = Intent(activity, ArtDetailActivity::class.java).apply {
                    putExtra("TITLE", artItem.title)
                    putExtra("ARTIST", artItem.artist)
                    putExtra("CREATION_DATE", artItem.creationDate)
                    putExtra("DESCRIPTION", artItem.description)
                    putExtra("IMAGE_PATH", imagePath)
                }
                activity.startActivity(intent)
            } else {
                // Lidar com erro ao salvar a imagem
                Log.e("ArtAdapter", "Failed to save image")
            }
        }

        // Ajustar a visibilidade dos botões
        if (artItem.isButtonsVisible) {
            holder.editButton.visibility = View.VISIBLE
            holder.removeButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
            holder.removeButton.visibility = View.GONE
        }

        holder.editButton.setOnClickListener {
            showEditDialog(artItem)
        }

        holder.removeButton.setOnClickListener {
            showDeleteConfirmationDialog(artItem)
        }
    }


    private fun showEditDialog(artItem: ArtItem) {
        val fragmentManager = activity.supportFragmentManager
        val editDialog = EditArtDialogFragment(artItem) { updatedArtItem ->
            artManager.updateArtItem(artItem.title, updatedArtItem) { success, errorMessage ->
                if (success) {
                    val newList = currentList.toMutableList()
                    val index = newList.indexOfFirst { it.title == artItem.title }
                    if (index != -1) {
                        newList[index] = updatedArtItem
                        submitList(newList)
                    }
                } else {
                    // Handle error (optional)
                }
            }
        }
        editDialog.show(fragmentManager, "EditArtDialog")
    }

    private fun showDeleteConfirmationDialog(artItem: ArtItem) {
        AlertDialog.Builder(activity)
            .setTitle("Confirmar Exclusão")
            .setMessage("Você tem certeza que deseja excluir esta obra de arte?")
            .setPositiveButton("Sim") { dialog, _ ->
                artManager.deleteArtItem(artItem.title) { success, errorMessage ->
                    if (success) {
                        submitList(currentList.filter { it.title != artItem.title })
                    } else {
                        // Handle error (optional)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private class ArtDiffCallback : DiffUtil.ItemCallback<ArtItem>() {
        override fun areItemsTheSame(oldItem: ArtItem, newItem: ArtItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ArtItem, newItem: ArtItem): Boolean {
            return oldItem == newItem
        }
    }
}
