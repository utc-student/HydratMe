package com.example.hydratme.presentation

class WaterTracker(private val dailyGoal: Int) {
    private var currentGlasses: Int = 0

    fun addGlass() {
        if (currentGlasses < dailyGoal) {
            currentGlasses++
        }
    }

    fun getProgress(): Int {
        return (currentGlasses * 100) / dailyGoal
    }

    fun getCurrentCount(): String {
        return "$currentGlasses/$dailyGoal"
    }
}
