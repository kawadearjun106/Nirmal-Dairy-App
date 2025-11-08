package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BillActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnPrint: Button
    private lateinit var db: FirebaseFirestore
    private var farmerName = ""
    private var milkLitres = 0.0
    private var fat = 0.0
    private var wastage = 0.0
    private var session = ""
    private var date = ""
    private var pricePerLitre = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        db = FirebaseFirestore.getInstance()
        webView = findViewById(R.id.webViewBill)
        btnPrint = findViewById(R.id.btnPrintBill)

        // Get data from intent
        farmerName = intent.getStringExtra("farmerName") ?: ""
        milkLitres = intent.getDoubleExtra("milkLitres", 0.0)
        fat = intent.getDoubleExtra("fat", 0.0)
        wastage = intent.getDoubleExtra("wastage", 0.0)
        session = intent.getStringExtra("session") ?: "Morning"
        date = intent.getStringExtra("date") ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Fetch milk rate from Firestore
        db.collection("milk_prices").document("current_rate").get()
            .addOnSuccessListener { doc ->
                pricePerLitre = doc.getDouble("pricePerLitre") ?: 40.0
                generateBill()
            }
            .addOnFailureListener {
                pricePerLitre = 40.0
                generateBill()
            }

        btnPrint.setOnClickListener {
            printBill()
        }
    }

    private fun generateBill() {
        val totalAmount = milkLitres * pricePerLitre
        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial; padding: 24px; }
                    .header { text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 12px; }
                    .subheader { text-align: center; color: #555; margin-bottom: 20px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                    th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
                    th { background-color: #f3f3f3; }
                    .total { font-weight: bold; background-color: #eafbea; }
                    .footer { text-align: center; font-size: 12px; margin-top: 20px; color: #888; }
                </style>
            </head>
            <body>
                <div class="header">Nirmal Dairy</div>
                <div class="subheader">Milk Bill</div>
                <p><b>Date:</b> $formattedDate</p>
                <p><b>Farmer:</b> $farmerName</p>
                <p><b>Shift:</b> $session</p>
                
                <table>
                    <tr><th>Detail</th><th>Value</th></tr>
                    <tr><td>Milk (Litres)</td><td>$milkLitres L</td></tr>
                    <tr><td>Fat %</td><td>$fat%</td></tr>
                    <tr><td>Wastage (L)</td><td>$wastage L</td></tr>
                    <tr><td>Rate per Litre</td><td>₹$pricePerLitre</td></tr>
                    <tr class="total"><td>Total Amount</td><td>₹${String.format("%.2f", totalAmount)}</td></tr>
                </table>

                <div class="footer">Thank you for your contribution to Nirmal Dairy!</div>
            </body>
            </html>
        """.trimIndent()

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }

    private fun printBill() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        val printAdapter: PrintDocumentAdapter = webView.createPrintDocumentAdapter("Milk_Bill_$farmerName")
        val jobName = "NirmalDairyBill_${System.currentTimeMillis()}"
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}
