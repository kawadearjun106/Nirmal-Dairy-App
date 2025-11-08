package com.nirmal.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class BillEntry(
    val date: String,
    val session: String,
    val milkLitres: Double,
    val fat: Double,
    val wastage: Double,
    val farmerName: String
)

class FarmerBillAdapter(
    private val context: Context,
    private val bills: List<BillEntry>
) : RecyclerView.Adapter<FarmerBillAdapter.BillViewHolder>() {

    inner class BillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDateSession: TextView = view.findViewById(R.id.tvDateSession)
        val tvMilkFat: TextView = view.findViewById(R.id.tvMilkFat)
        val btnViewBill: Button = view.findViewById(R.id.btnViewBill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill_entry, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.tvDateSession.text = "📅 ${bill.date} | ${bill.session}"
        holder.tvMilkFat.text = "🥛 Milk: ${bill.milkLitres} L | Fat: ${bill.fat}%"

        holder.btnViewBill.setOnClickListener {
            val intent = Intent(context, BillActivity::class.java).apply {
                putExtra("farmerName", bill.farmerName)
                putExtra("milkLitres", bill.milkLitres)
                putExtra("fat", bill.fat)
                putExtra("wastage", bill.wastage)
                putExtra("date", bill.date)
                putExtra("session", bill.session)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bills.size
}
