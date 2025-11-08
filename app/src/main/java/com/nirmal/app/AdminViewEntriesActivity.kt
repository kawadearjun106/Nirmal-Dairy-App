package com.nirmal.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.appbar.MaterialToolbar

class AdminViewEntriesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerFarmers: RecyclerView
    private val farmerList = mutableListOf<Farmer>()
    private lateinit var adapter: AdminFarmerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_entries)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        recyclerFarmers = findViewById(R.id.recyclerFarmers)
        recyclerFarmers.layoutManager = LinearLayoutManager(this)
        adapter = AdminFarmerAdapter(this, farmerList)
        recyclerFarmers.adapter = adapter

        loadFarmers()
    }

    private fun loadFarmers() {
        db.collection("users")
            .whereEqualTo("role", "farmer")
            .get()
            .addOnSuccessListener { snapshot ->
                farmerList.clear()
                for (doc in snapshot.documents) {
                    val name = doc.getString("name") ?: "Unnamed Farmer"
                    val email = doc.getString("email") ?: ""
                    farmerList.add(Farmer(name, email))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load farmers: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
