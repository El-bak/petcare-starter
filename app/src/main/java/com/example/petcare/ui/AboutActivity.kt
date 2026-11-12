package com.example.petcare.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.petcare.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afficherPresentation()
        appliquerInsets(findViewById(R.id.root))
        findViewById<Button>(R.id.closeButton).setOnClickListener { finish() }
    }

    private fun afficherPresentation() {
        // EXERCICE M2.1 : remplacer cet écran d’attente par activity_about.
        setContentView(R.layout.activity_about)
        ViewCompat.setAccessibilityHeading(findViewById(R.id.pageTitle), true)
    }
}
