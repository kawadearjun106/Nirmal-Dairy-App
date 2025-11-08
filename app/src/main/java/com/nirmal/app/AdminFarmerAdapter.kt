package com.nirmal.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

data class Farmer(val name: String, val email: String)

class AdminFarmerAdapter(
    private val context: Context,
    private val farmerList: List<Farmer>
) : RecyclerView.Adapter<AdminFarmerAdapter.FarmerViewHolder>() {

    inner class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFarmerName: TextView = view.findViewById(R.id.tvFarmerName)
        val tvFarmerEmail: TextView = view.findViewById(R.id.tvFarmerEmail)
        val cardFarmer: CardView = view.findViewById(R.id.cardFarmer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_farmer_card, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val farmer = farmerList[position]
        holder.tvFarmerName.text = "👨‍🌾 ${farmer.name}"
        holder.tvFarmerEmail.text = farmer.email

        holder.cardFarmer.setOnClickListener {
            val intent = Intent(context, AdminFarmerEntriesActivity::class.java)
            intent.putExtra("farmerName", farmer.name)
            intent.putExtra("farmerEmail", farmer.email)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = farmerList.size
}
