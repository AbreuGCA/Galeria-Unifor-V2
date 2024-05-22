package com.example.galeriauniforv2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.materialToolbar))

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        artAdapter = ArtAdapter()
        recyclerView.adapter = artAdapter

        fetchArtItems()
    }

    private fun fetchArtItems() {
        db.collection("artworks")
            .get()
            .addOnSuccessListener { result ->
                val artList = mutableListOf<ArtItem>()
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val artist = document.getString("artist") ?: ""
                    val creationDate = document.getString("creationDate") ?: ""
                    val description = document.getString("description") ?: ""
                    val imageBase64 = document.getString("imageBase64") ?: ""
                    val artItem = ArtItem(title, artist, creationDate, description, imageBase64)
                    artList.add(artItem)
                }
                artAdapter.submitList(artList)
            }
            .addOnFailureListener { exception ->
            }
    }
}