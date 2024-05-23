package com.example.galeriauniforv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.materialToolbar))

        drawerLayout = findViewById(R.id.drawerLayout)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        artAdapter = ArtAdapter(this)
        recyclerView.adapter = artAdapter

        fetchArtItems()

        navigationView = findViewById(R.id.nav_view)
        setupNavigationMenu()
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        val bundle = getIntent().extras

        var aux = bundle?.getInt("autenticado")

        Log.d("contentValues", "valor: "+aux)
        if(aux == 1){
            navigationView.menu.findItem(R.id.nav_add_art).isVisible = true
            navigationView.menu.findItem(R.id.nav_add_category).isVisible = true
        } else{
            navigationView.menu.findItem(R.id.nav_add_art).isVisible = false
            navigationView.menu.findItem(R.id.nav_add_category).isVisible = false
        }
    }
    private fun setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add_art -> {
                    startActivity(Intent(this, add_art::class.java))
                    true
                }
                R.id.nav_add_category -> {
                    menuItem.isVisible = false
                    false
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchArtItems() {
        db.collection("artworks")
            .get()
            .addOnSuccessListener { result ->
                val artItems = result.map { document ->
                    ArtItem(
                        title = document.getString("title") ?: "",
                        artist = document.getString("artist") ?: "",
                        creationDate = document.getString("creationDate") ?: "",
                        description = document.getString("description") ?: "",
                        imageBase64 = document.getString("imageBase64") ?: ""
                    )
                }
                artAdapter.submitList(artItems)
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

}
