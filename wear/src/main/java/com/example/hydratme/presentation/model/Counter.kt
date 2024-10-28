package com.example.hydratme.presentation.model

class Counter {
    private var count = 0

    fun increment(): Int {
        count++
        return count
    }

    fun reset(): Int {
        count = 0
        return count
    }

    fun getCount(): Int {
        return count
    }
}