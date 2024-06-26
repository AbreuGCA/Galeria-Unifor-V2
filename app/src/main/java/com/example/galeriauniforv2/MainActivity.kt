package com.example.galeriauniforv2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val db = FirebaseFirestore.getInstance()
    private var artItemsListener: ListenerRegistration? = null
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.materialToolbar))

        val resetButton = findViewById<Button>(R.id.resetButton)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        artAdapter = ArtAdapter(this, isUserAuthenticated = isUserLoggedIn())
        recyclerView.adapter = artAdapter

        resetButton.setOnClickListener {
            loadArtworks()
        }

        setupNavigationMenu()
        setupCategoryList()
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        val bundle = intent.extras

        val aux = bundle?.getInt("autenticado")

        Log.d("MainActivity", "Valor autenticado: $aux")
        if (aux == 1) {
            navigationView.menu.findItem(R.id.nav_add_art).isVisible = true
            navigationView.menu.findItem(R.id.nav_add_category).isVisible = true
            navigationView.menu.findItem(R.id.nav_login).isVisible = false
            artAdapter.setUserAuthenticated(true) // Atualiza a visibilidade dos botões
        } else {
            navigationView.menu.findItem(R.id.nav_add_art).isVisible = false
            navigationView.menu.findItem(R.id.nav_add_category).isVisible = false
            artAdapter.setUserAuthenticated(false) // Atualiza a visibilidade dos botões
        }

        loadCategories()

        // Listen for real-time updates
        startListeningForArtItems()
    }

    override fun onPause() {
        super.onPause()
        // Remove the real-time listener
        artItemsListener?.remove()
    }

    private fun setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add_art -> {
                    startActivity(Intent(this, add_art::class.java))
                    true
                }
                R.id.nav_add_category -> {
                    startActivity(Intent(this, AddCategoryActivity::class.java))
                    true
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun startListeningForArtItems() {
        artItemsListener = db.collection("artworks")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val artItems = snapshots.map { document ->
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
            }
    }

    private fun setupCategoryList() {
        val categoryRecyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter { category -> onCategorySelected(category) }
        categoryRecyclerView.adapter = categoryAdapter

        loadCategories()
    }

    private fun loadCategories() {
        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categories = result.documents.map { document ->
                    document.getString("name") ?: ""
                }
                categoryAdapter.submitList(categories)
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting categories", exception)
            }
    }

    private fun onCategorySelected(category: String) {
        loadArtworks(category)
    }

    private fun loadArtworks(category: String? = null) {
        val collectionReference = db.collection("artworks")
        var query: Query = collectionReference

        // Se a categoria não for nula, adicionamos a cláusula de filtro
        category?.let {
            query = collectionReference.whereEqualTo("category", it)
        }

        // Executar a consulta
        query.get().addOnSuccessListener { snapshot ->
            val artItems = snapshot.documents.map { document ->
                ArtItem(
                    title = document.getString("title") ?: "",
                    artist = document.getString("artist") ?: "",
                    creationDate = document.getString("creationDate") ?: "",
                    description = document.getString("description") ?: "",
                    imageBase64 = document.getString("imageBase64") ?: ""
                )
            }
            artAdapter.submitList(artItems)
        }.addOnFailureListener { e ->
            Log.w("MainActivity", "Error loading artworks", e)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

}
