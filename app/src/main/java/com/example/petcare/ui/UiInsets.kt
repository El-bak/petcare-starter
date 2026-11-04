package com.example.petcare.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Infrastructure commune fournie, indépendante des exercices. */
fun appliquerInsets(view: View) {
    val gauche = view.paddingLeft; val haut = view.paddingTop
    val droite = view.paddingRight; val bas = view.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(gauche + bars.left, haut + bars.top, droite + bars.right, bas + bars.bottom)
        insets
    }
    ViewCompat.requestApplyInsets(view)
}
