package com.nirmal.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var listLayout: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listLayout = findViewById(R.id.cartContainer)
        tvTotal = findViewById(R.id.tvTotalAmount)
        btnCheckout = findViewById(R.id.btnCheckout)

        loadCartItems()

        btnCheckout.setOnClickListener {
            checkout()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadCartItems() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("cart").document(uid).collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                listLayout.removeAllViews()
                totalAmount = 0.0

                for (doc in snapshot.documents) {
                    val name = doc.getString("name") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val qty = (doc.getLong("quantity") ?: 1).toInt()
                    val itemTotal = price * qty
                    totalAmount += itemTotal

                    val itemText = TextView(this)
                    itemText.text = "$name x$qty  —  ₹${String.format("%.2f", itemTotal)}"
                    itemText.textSize = 16f
                    listLayout.addView(itemText)
                }

                tvTotal.text = "Total: ₹${String.format("%.2f", totalAmount)}"
            }
    }

    private fun checkout() {
        val uid = auth.currentUser?.uid ?: return
        val orderData = hashMapOf(
            "userId" to uid,
            "totalAmount" to totalAmount,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                db.collection("cart").document(uid).collection("items")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            doc.reference.delete()
                        }
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Checkout failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
