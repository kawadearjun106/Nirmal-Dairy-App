package com.nirmal.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var btnAddProduct: Button
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_products)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
// import top if needed:
// import com.google.android.material.appbar.MaterialToolbar

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = FirebaseFirestore.getInstance()
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductPrice = findViewById(R.id.etProductPrice)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        listContainer = findViewById(R.id.listContainer)

        btnAddProduct.setOnClickListener { addProduct() }

        loadProducts()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun addProduct() {
        val name = etProductName.text.toString().trim()
        val description = etProductDescription.text.toString().trim()
        val priceText = etProductPrice.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull() ?: 0.0

        val product = hashMapOf(
            "name" to name,
            "description" to description,
            "price" to price
        )

        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show()
                etProductName.text.clear()
                etProductDescription.text.clear()
                etProductPrice.text.clear()
                loadProducts()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                listContainer.removeAllViews()
                for (doc in snapshot.documents) {
                    val id = doc.id
                    val name = doc.getString("name") ?: "Unknown"
                    val description = doc.getString("description") ?: "No description"
                    val price = doc.getDouble("price") ?: 0.0

                    val itemLayout = LinearLayout(this)
                    itemLayout.orientation = LinearLayout.VERTICAL
                    itemLayout.setPadding(12, 12, 12, 12)

                    val title = TextView(this)
                    title.text = "🧈 $name  (₹$price)"
                    title.textSize = 17f

                    val desc = TextView(this)
                    desc.text = description
                    desc.textSize = 14f
                    desc.setPadding(0, 4, 0, 4)

                    val buttonsLayout = LinearLayout(this)
                    buttonsLayout.orientation = LinearLayout.HORIZONTAL

                    val editBtn = Button(this)
                    editBtn.text = "Edit"
                    editBtn.setOnClickListener { editProduct(id, name, description, price) }

                    val deleteBtn = Button(this)
                    deleteBtn.text = "Delete"
                    deleteBtn.setOnClickListener { deleteProduct(id, name) }

                    buttonsLayout.addView(editBtn)
                    buttonsLayout.addView(deleteBtn)

                    itemLayout.addView(title)
                    itemLayout.addView(desc)
                    itemLayout.addView(buttonsLayout)

                    listContainer.addView(itemLayout)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editProduct(id: String, oldName: String, oldDesc: String, oldPrice: Double) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etEditDescription)
        val etPrice = dialogView.findViewById<EditText>(R.id.etEditPrice)

        etName.setText(oldName)
        etDescription.setText(oldDesc)
        etPrice.setText(oldPrice.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = etName.text.toString()
                val newDesc = etDescription.text.toString()
                val newPrice = etPrice.text.toString().toDoubleOrNull() ?: oldPrice

                val updatedProduct = hashMapOf(
                    "name" to newName,
                    "description" to newDesc,
                    "price" to newPrice
                )

                db.collection("products").document(id)
                    .update(updatedProduct as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product updated!", Toast.LENGTH_SHORT).show()
                        loadProducts()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(id: String, name: String) {
        db.collection("products").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "$name deleted", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }
}
