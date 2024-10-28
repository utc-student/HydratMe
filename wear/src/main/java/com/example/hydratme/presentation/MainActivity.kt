/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.hydratme.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.hydratme.presentation.model.Counter
import com.example.hydratme.R


import android.widget.Button
import android.widget.TextView

class MainActivity : ComponentActivity() {

    private lateinit var countTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var resetButton: Button
    private val counter = Counter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wear_main)

        countTextView = findViewById(R.id.counter)
        incrementButton = findViewById(R.id.plus_button)
        resetButton = findViewById(R.id.reset_button)

        updateCountDisplay()

        incrementButton.setOnClickListener {
            updateCountDisplay(counter.increment())
        }

        resetButton.setOnClickListener {
            updateCountDisplay(counter.reset())
        }
    }

    private fun updateCountDisplay(newCount: Int = counter.getCount()) {
        countTextView.text = newCount.toString()
    }

}