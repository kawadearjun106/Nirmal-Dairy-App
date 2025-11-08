package com.nirmal.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewProductsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var listLayout: LinearLayout
    private lateinit var btnGoToCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products)
// import top if needed:
// import com.google.android.material.appbar.MaterialToolbar

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listLayout = findViewById(R.id.listContainer)
        btnGoToCart = findViewById(R.id.btnGoToCart)

        loadProducts()

        btnGoToCart.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                listLayout.removeAllViews()

                for (doc in snapshot.documents) {
                    val name = doc.getString("name") ?: "Unknown"
                    val price = doc.getDouble("price") ?: 0.0

                    val itemLayout = LinearLayout(this)
                    itemLayout.orientation = LinearLayout.HORIZONTAL
                    itemLayout.setPadding(16, 16, 16, 16)

                    val textView = TextView(this)
                    textView.text = "$name - ₹$price"
                    textView.textSize = 16f
                    textView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                    val button = Button(this)
                    button.text = "Add"
                    button.setOnClickListener { addToCart(name, price) }

                    itemLayout.addView(textView)
                    itemLayout.addView(button)
                    listLayout.addView(itemLayout)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToCart(name: String, price: Double) {
        val uid = auth.currentUser?.uid ?: return
        val cartItem = hashMapOf(
            "name" to name,
            "price" to price,
            "quantity" to 1
        )

        db.collection("cart")
            .document(uid)
            .collection("items")
            .add(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "$name added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }
}
