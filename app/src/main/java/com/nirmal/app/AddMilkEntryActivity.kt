package com.nirmal.app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddMilkEntryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerFarmer: Spinner
    private lateinit var spinnerSession: Spinner
    private lateinit var inputMilkLitres: EditText
    private lateinit var inputFat: EditText
    private lateinit var inputWastage: EditText
    private lateinit var inputDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnGenerateBill: Button   // ✅ Added Generate Bill button

    private val farmerList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_milk_entry)

        // Toolbar back button
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        db = FirebaseFirestore.getInstance()

        // Initialize all views
        spinnerFarmer = findViewById(R.id.spinnerFarmer)
        spinnerSession = findViewById(R.id.spinnerSession)
        inputMilkLitres = findViewById(R.id.inputMilkLitres)
        inputFat = findViewById(R.id.inputFat)
        inputWastage = findViewById(R.id.inputWastage)
        inputDate = findViewById(R.id.inputDate)
        btnSave = findViewById(R.id.btnSaveEntry)
        btnGenerateBill = findViewById(R.id.btnGenerateBill)
        btnGenerateBill.visibility = View.GONE  // hide initially

        // Session options
        val sessions = listOf("Morning", "Evening")
        val sessionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sessions)
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSession.adapter = sessionAdapter

        // Date Picker
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        inputDate.setText(dateFormat.format(calendar.time))

        inputDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                val selected = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}"
                inputDate.setText(selected)
            }, year, month, day).show()
        }

        loadFarmers()

        // Save button click
        btnSave.setOnClickListener { saveEntry() }
    }

    private fun loadFarmers() {
        db.collection("users")
            .whereEqualTo("role", "farmer")  // ✅ fetch only users whose role = "farmer"
            .get()
            .addOnSuccessListener { snapshot ->
                farmerList.clear()

                if (snapshot.isEmpty) {
                    Toast.makeText(this, "No farmers found in Firestore!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in snapshot.documents) {
                    val farmerName = doc.getString("name") ?: "Unnamed Farmer"
                    farmerList.add(farmerName)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, farmerList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFarmer.adapter = adapter

                Toast.makeText(this, "Loaded ${farmerList.size} farmers", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch farmers: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveEntry() {
        val farmerName = spinnerFarmer.selectedItem?.toString() ?: ""
        val milkLitres = inputMilkLitres.text.toString().toDoubleOrNull() ?: 0.0
        val fat = inputFat.text.toString().toDoubleOrNull() ?: 0.0
        val wastage = inputWastage.text.toString().toDoubleOrNull() ?: 0.0
        val date = inputDate.text.toString()
        val session = spinnerSession.selectedItem?.toString() ?: ""

        if (farmerName.isEmpty() || milkLitres <= 0) {
            Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Get the farmer’s email from "users" collection
        db.collection("users")
            .whereEqualTo("name", farmerName)
            .whereEqualTo("role", "farmer")
            .get()
            .addOnSuccessListener { result ->
                val farmerEmail = result.documents.firstOrNull()?.getString("email") ?: ""

                val entryData = hashMapOf(
                    "farmerName" to farmerName,
                    "farmerEmail" to farmerEmail,  // ✅ add this field
                    "milkLitres" to milkLitres,
                    "fatContent" to fat,
                    "wastageLitres" to wastage,
                    "date" to date,
                    "session" to session
                )

                db.collection("milk_entries")
                    .add(entryData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Entry Saved!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, BillActivity::class.java)
                        intent.putExtra("farmerName", farmerName)
                        intent.putExtra("milkLitres", milkLitres)
                        intent.putExtra("fat", fat)
                        intent.putExtra("wastage", wastage)
                        intent.putExtra("date", date)
                        intent.putExtra("session", session)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to find farmer email", Toast.LENGTH_SHORT).show()
            }
    }


}
