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
        val epaisseur = 12f * resources.displayMetrics.density   // 12 dp convertis en pixels

        // 1. Le cercle : centré dans la View, en gardant la moitié du trait à l'intérieur
        val cx = width / 2f
        val cy = height / 2f
        val rayon = minOf(width, height) / 2f - epaisseur / 2f
        bounds.set(cx - rayon, cy - rayon, cx + rayon, cy + rayon)

        // Couleurs du thème
        val couleurPrincipale = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary)
        val couleurTexte = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface)

        // 2. La piste : cercle complet, couleur principale très transparente
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = epaisseur
        paint.color = ColorUtils.setAlphaComponent(couleurPrincipale, 60)
        canvas.drawOval(bounds, paint)

        // 3. La progression : un arc qui part du haut (-90°)
        val fraction = (nombre.toFloat() / objectif).coerceIn(0f, 1f)
        paint.color = couleurPrincipale
        canvas.drawArc(bounds, -90f, 360f * fraction, false, paint)

        // 4. Le texte centré
        paint.style = Paint.Style.FILL
        paint.color = couleurTexte
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics)
        val texte = context.getString(R.string.progress_value, nombre, objectif)
        val y = cy - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(texte, cx, y, paint)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        // EXERCICE M2.2 : exposer le rôle ProgressBar et une RangeInfo entière.
    }
}
