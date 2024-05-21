package com.example.galeriauniforv2

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ArtAdapter : ListAdapter<ArtItem, ArtAdapter.ArtViewHolder>(ArtDiffCallback()) {

    class ArtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artTitle: TextView = itemView.findViewById(R.id.artTitle)
        val artArtist: TextView = itemView.findViewById(R.id.artArtist)
        val artCreationDate: TextView = itemView.findViewById(R.id.artCreationDate)
        val artImage: ImageView = itemView.findViewById(R.id.artImage)
        val artDescription: TextView = itemView.findViewById(R.id.artDescription)
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

        // Decode Base64 string to byte array
        val decodedBytes = Base64.decode(artItem.imageBase64, Base64.DEFAULT)

        // Convert byte array to Bitmap and set it to ImageView
        holder.artImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size))
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