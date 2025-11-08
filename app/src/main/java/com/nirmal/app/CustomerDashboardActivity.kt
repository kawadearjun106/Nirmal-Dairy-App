package com.nirmal.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class CustomerDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        val btnViewProducts = findViewById<Button>(R.id.btnViewProducts)
        val btnBuyNow = findViewById<Button>(R.id.btnBuyNow)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnViewProducts.setOnClickListener {
            startActivity(Intent(this, ViewProductsActivity::class.java))
        }

        btnBuyNow.setOnClickListener {
            Toast.makeText(this, "Buy Now - Coming soon", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
