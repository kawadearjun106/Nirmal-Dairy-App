package com.nirmal.app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdminFarmerEntriesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerBills: RecyclerView
    private lateinit var tvSummary: TextView
    private val billList = mutableListOf<BillEntry>()
    private lateinit var billAdapter: FarmerBillAdapter
    private var pricePerLitre = 40.0
    private lateinit var farmerEmail: String
    private lateinit var farmerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_monthly_report) // reuse layout

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "Entries for Farmer"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        recyclerBills = findViewById(R.id.recyclerBills)
        tvSummary = findViewById(R.id.tvSummary)
        recyclerBills.layoutManager = LinearLayoutManager(this)

        farmerEmail = intent.getStringExtra("farmerEmail") ?: ""
        farmerName = intent.getStringExtra("farmerName") ?: "Farmer"

        billAdapter = FarmerBillAdapter(this, billList)
        recyclerBills.adapter = billAdapter

        db.collection("milk_prices").document("current_rate").get()
            .addOnSuccessListener { doc ->
                pricePerLitre = doc.getDouble("pricePerLitre") ?: 40.0
                loadEntries()
            }
            .addOnFailureListener { loadEntries() }
    }

    private fun loadEntries() {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        db.collection("milk_entries")
            .whereEqualTo("farmerEmail", farmerEmail)
            .get()
            .addOnSuccessListener { snapshot ->
                billList.clear()
                var totalMilk = 0.0
                var totalFat = 0.0
                var totalAmount = 0.0
                var count = 0

                for (doc in snapshot.documents) {
                    val date = doc.getString("date") ?: continue
                    if (!date.startsWith(currentMonth)) continue

                    val milk = doc.getDouble("milkLitres") ?: 0.0
                    val fat = doc.getDouble("fatContent") ?: 0.0
                    val wastage = doc.getDouble("wastageLitres") ?: 0.0
                    val session = doc.getString("session") ?: ""

                    totalMilk += milk
                    totalFat += fat
                    totalAmount += milk * pricePerLitre
                    count++

                    billList.add(BillEntry(date, session, milk, fat, wastage, farmerName))
                }

                billAdapter.notifyDataSetChanged()

                if (count > 0) {
                    val avgFat = totalFat / count
                    tvSummary.text = """
                        🧾 ${farmerName}'s Summary (${SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())})
                        🥛 Total Milk: ${String.format("%.2f", totalMilk)} L
                        🧈 Avg Fat: ${String.format("%.2f", avgFat)}%
                        💰 Total Earnings: ₹${String.format("%.2f", totalAmount)}
                    """.trimIndent()
                } else {
                    tvSummary.text = "No entries for this month."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
