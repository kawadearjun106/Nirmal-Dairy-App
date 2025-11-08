package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // ✅ Menu click listener
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    logoutAdmin()
                    true
                }
                else -> false
            }
        }

        // ✅ Card navigation setup
        findViewById<LinearLayout>(R.id.cardAddMilk).setOnClickListener {
            startActivity(Intent(this, AddMilkEntryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardViewEntries).setOnClickListener {
            startActivity(Intent(this, AdminViewEntriesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.cardDailyReport).setOnClickListener {
            startActivity(Intent(this, DailyReportActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardManageFarmers).setOnClickListener {
            startActivity(Intent(this, ManageFarmersActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardManageProducts).setOnClickListener {
            startActivity(Intent(this, ManageProductsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardManagePrices).setOnClickListener {
            startActivity(Intent(this, ManagePricesActivity::class.java))
        }
    }

    private fun logoutAdmin() {
        FirebaseAuth.getInstance().signOut()

        // Redirect to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutAdmin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
