package com.nirmal.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ManagePricesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvCurrentRates: TextView
    private lateinit var etPricePerLitre: EditText
    private lateinit var btnSaveRates: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_prices)

        // Toolbar back arrow
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = FirebaseFirestore.getInstance()
        tvCurrentRates = findViewById(R.id.tvCurrentRates)
        etPricePerLitre = findViewById(R.id.etPricePerLitre)
        btnSaveRates = findViewById(R.id.btnSaveRates)

        loadCurrentRates()

        btnSaveRates.setOnClickListener {
            savePrices()
        }
    }

    private fun loadCurrentRates() {
        db.collection("milk_prices").document("current")
            .get()
            .addOnSuccessListener { doc ->
                val litrePrice = doc.getDouble("pricePerLitre") ?: 0.0
                tvCurrentRates.text = "Current Milk Rate: ₹$litrePrice per litre"
            }
            .addOnFailureListener {
                tvCurrentRates.text = "Failed to load current rates."
            }
    }

    private fun savePrices() {
        val litrePrice = etPricePerLitre.text.toString().toDoubleOrNull()
        if (litrePrice == null || litrePrice <= 0) {
            Toast.makeText(this, "Please enter a valid price per litre.", Toast.LENGTH_SHORT).show()
            return
        }

        val priceData = hashMapOf(
            "pricePerLitre" to litrePrice
        )

        db.collection("milk_prices").document("current")
            .set(priceData)
            .addOnSuccessListener {
                Toast.makeText(this, "Prices updated successfully!", Toast.LENGTH_SHORT).show()
                tvCurrentRates.text = "Current Milk Rate: ₹$litrePrice per litre"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update prices.", Toast.LENGTH_SHORT).show()
            }
    }
}
