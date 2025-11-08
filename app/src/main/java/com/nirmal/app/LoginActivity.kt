package com.nirmal.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ✅ If user already logged in, skip login screen
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role")
                    when (role) {
                        "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                        "farmer" -> startActivity(Intent(this, FarmerDashboardActivity::class.java))
                        "customer" -> startActivity(Intent(this, CustomerDashboardActivity::class.java))
                        else -> {
                            Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                            auth.signOut() // safety
                        }
                    }
                    finish() // Prevent back press to login
                }
        }

        val emailInput = findViewById<EditText>(R.id.inputEmail)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<TextView>(R.id.btnGoToSignup)

        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val role = doc.getString("role")
                            when (role) {
                                "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                                "farmer" -> startActivity(Intent(this, FarmerDashboardActivity::class.java))
                                "customer" -> startActivity(Intent(this, CustomerDashboardActivity::class.java))
                                else -> Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
