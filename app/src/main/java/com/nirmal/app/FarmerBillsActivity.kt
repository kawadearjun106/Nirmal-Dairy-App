package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FarmerBillsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_bills)

        // Toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        listContainer = findViewById(R.id.listContainer)

        loadFarmerBills()
    }

    private fun loadFarmerBills() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch all milk entries for this farmer
        db.collection("milk_entries")
            .whereEqualTo("farmerName", currentUser.displayName)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Toast.makeText(this, "No bills found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                listContainer.removeAllViews()

                for (doc in snapshot.documents) {
                    val date = doc.getString("date") ?: ""
                    val session = doc.getString("session") ?: ""
                    val milk = doc.getDouble("milkLitres") ?: 0.0
                    val fat = doc.getDouble("fatContent") ?: 0.0
                    val wastage = doc.getDouble("wastageLitres") ?: 0.0
                    val rate = doc.getDouble("pricePerLitre") ?: 40.0
                    val total = milk * rate

                    val itemView = TextView(this).apply {
                        text = "📅 $date | $session\nMilk: $milk L | Fat: $fat% | ₹${String.format("%.2f", total)}"
                        textSize = 16f
                        setPadding(12, 18, 12, 18)
                        setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                        setOnClickListener {
                            val intent = Intent(this@FarmerBillsActivity, BillActivity::class.java)
                            intent.putExtra("farmerName", currentUser.displayName)
                            intent.putExtra("milkLitres", milk)
                            intent.putExtra("fat", fat)
                            intent.putExtra("wastage", wastage)
                            intent.putExtra("date", date)
                            intent.putExtra("session", session)
                            startActivity(intent)
                        }
                    }

                    listContainer.addView(itemView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch bills: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
