package com.example.petcare.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.graphics.ColorUtils
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.example.petcare.R
import com.google.android.material.color.MaterialColors

/** Indicateur de session en lecture seule : aucune interaction tactile à simuler. */
class CareProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF()
    private var nombre = 0
    private var objectif = 5

    fun setProgress(nombre: Int, objectif: Int = 5) {
        this.nombre = nombre.coerceAtLeast(0)
        this.objectif = objectif.coerceAtLeast(1)
        contentDescription = context.getString(R.string.care_progress_description, this.nombre, this.objectif)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val largeur = (240 * resources.displayMetrics.density).toInt()
        val hauteur = (140 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(resolveSize(largeur, widthMeasureSpec), resolveSize(hauteur, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // EXERCICE M2.2 : dessiner la piste, la progression et le texte avec Canvas/Paint.
        paint.color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics)
        canvas.drawText(context.getString(R.string.progress_pending_short), width / 2f, height / 2f, paint)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        // EXERCICE M2.2 : exposer le rôle ProgressBar et une RangeInfo entière.
    }
}
