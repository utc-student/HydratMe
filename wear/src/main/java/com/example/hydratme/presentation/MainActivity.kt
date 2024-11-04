package com.example.hydratme.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.hydratme.R

class MainActivity : ComponentActivity() {

    private val waterTracker = WaterTracker(dailyGoal = 6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wear_main)

        val circleProgress = findViewById<CircleProgressView>(R.id.circle_progress)
        val waterCountText = findViewById<TextView>(R.id.contador_agua)
        val addWaterButton = findViewById<Button>(R.id.agregar_agua)

        updateUI(circleProgress, waterCountText)

        addWaterButton.setOnClickListener {
            waterTracker.addGlass()
            updateUI(circleProgress, waterCountText)
        }
    }

    private fun updateUI(circleProgress: CircleProgressView, waterCountText: TextView) {
        val progress = waterTracker.getProgress()
        circleProgress.setProgress(progress.toFloat())
        waterCountText.text = waterTracker.getCurrentCount()
    }
}