package com.example.hydratme.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress: Float = 0f
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#555555")
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }
    private val progressPaint = Paint().apply {
        color = Color.parseColor("#00BFFF") // Color inicial del progreso
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }

    fun setProgress(newProgress: Int, dailyGoal: Int) {
        progress = (newProgress.toFloat() / dailyGoal).coerceIn(0f, 1f)
        invalidate()  // Redibuja la vista con el progreso actualizado
    }

    fun setProgressColor(color: String) {
        progressPaint.color = Color.parseColor(color)
        invalidate()  // Redibuja la vista con el nuevo color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - 45f

        // Dibuja el fondo circular
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Dibuja el progreso
        val sweepAngle = progress * 360
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f, // Empieza desde la parte superior
            sweepAngle,
            false,
            progressPaint
        )
    }
}
