package com.nirmal.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ManageFarmersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_farmers)

        db = FirebaseFirestore.getInstance()
        listContainer = findViewById(R.id.listContainer)

        // ✅ Toolbar Back Arrow
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        loadFarmers()
    }

    private fun loadFarmers() {
        db.collection("users")
            .whereEqualTo("role", "farmer")
            .get()
            .addOnSuccessListener { snapshot ->
                listContainer.removeAllViews()

                if (snapshot.isEmpty) {
                    val emptyText = TextView(this).apply {
                        text = "No farmers found."
                        textSize = 18f
                    }
                    listContainer.addView(emptyText)
                    return@addOnSuccessListener
                }

                for (doc in snapshot.documents) {
                    val farmerId = doc.id
                    val farmerName = doc.getString("name") ?: "Unnamed Farmer"

                    val row = layoutInflater.inflate(R.layout.item_farmer_row, listContainer, false)
                    val tvName = row.findViewById<TextView>(R.id.tvFarmerName)
                    val btnDelete = row.findViewById<MaterialButton>(R.id.btnDeleteFarmer)

                    tvName.text = farmerName

                    // ✅ Delete farmer
                    btnDelete.setOnClickListener {
                        db.collection("users").document(farmerId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Deleted $farmerName", Toast.LENGTH_SHORT).show()
                                loadFarmers() // refresh list
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                    }

                    listContainer.addView(row)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading farmers", Toast.LENGTH_SHORT).show()
            }
    }
}
