package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DailyReportActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvTotalMilk: TextView
    private lateinit var tvTotalWastage: TextView
    private lateinit var tvAvgFat: TextView
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_report)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
// import top if needed:
// import com.google.android.material.appbar.MaterialToolbar

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        tvTotalMilk = findViewById(R.id.tvTotalMilk)
        tvTotalWastage = findViewById(R.id.tvTotalWastage)
        tvAvgFat = findViewById(R.id.tvAvgFat)
        listContainer = findViewById(R.id.listContainer)

        loadTodayReport()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadTodayReport() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("milk_entries")
            .whereEqualTo("date", today)
            .get()
            .addOnSuccessListener { snapshot ->
                var totalMilk = 0.0
                var totalWastage = 0.0
                var totalFat = 0.0
                var count = 0

                listContainer.removeAllViews()

                for (doc in snapshot.documents) {
                    val name = doc.getString("farmerName") ?: "Unknown"
                    val milk = doc.getDouble("milkLitres") ?: 0.0
                    val wastage = doc.getDouble("wastageLitres") ?: 0.0
                    val fat = doc.getDouble("fatContent") ?: 0.0

                    totalMilk += milk
                    totalWastage += wastage
                    totalFat += fat
                    count++

                    val item = TextView(this)
                    item.text = "👨‍🌾 $name — Milk: $milk L | Wastage: $wastage L | Fat: $fat %"
                    item.textSize = 16f
                    item.setPadding(4, 8, 4, 8)

                    item.setOnClickListener {
                        val intent = Intent(this, BillActivity::class.java)
                        intent.putExtra("farmerName", name)
                        intent.putExtra("milkLitres", milk)
                        intent.putExtra("fat", fat)
                        intent.putExtra("wastage", wastage)
                        startActivity(intent)
                    }

                    listContainer.addView(item)
                }

                val avgFat = if (count > 0) totalFat / count else 0.0

                tvTotalMilk.text = "Total Milk: $totalMilk L"
                tvTotalWastage.text = "Wastage: $totalWastage L"
                tvAvgFat.text = "Average Fat: ${"%.2f".format(avgFat)} %"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading report", Toast.LENGTH_SHORT).show()
            }
    }
}
