package com.nirmal.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nirmal.app.databinding.ActivityViewMilkEntriesBinding

class ViewMilkEntriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMilkEntriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMilkEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
// import top if needed:
// import com.google.android.material.appbar.MaterialToolbar

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Temporary placeholder — we’ll later show entries from Firestore here
        binding.textPlaceholder.text = "Milk Entries will appear here!"
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}
