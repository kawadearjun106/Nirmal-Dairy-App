package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

class FarmerDashboardActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_dashboard)

        // Toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Buttons
        val btnViewBills = findViewById<Button>(R.id.btnViewBills)
        val btnViewProducts = findViewById<Button>(R.id.btnViewProducts)

        btnViewBills.setOnClickListener {
            startActivity(Intent(this, FarmerBillsActivity::class.java))
        }


        btnViewProducts.setOnClickListener {
            startActivity(Intent(this, ViewProductsActivity::class.java))
        }
        btnViewBills.setOnClickListener {
            startActivity(Intent(this, FarmerMonthlyReportActivity::class.java))
        }

    }

    // Inflate toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_farmer_dashboard, menu)
        return true
    }

    // Handle logout click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
