package com.example.hydratme.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.hydratme.R
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private lateinit var circleProgressView: CircleProgressView
    private lateinit var contadorTextView: TextView
    private lateinit var addButton: Button
    private var dailyGoal = 10
    private var currentCount = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wear_main)

        // Inicializar las vistas
        circleProgressView = findViewById(R.id.circle_progress)
        contadorTextView = findViewById(R.id.contador_agua)
        addButton = findViewById(R.id.agregar_agua)

        // Configurar el botón para incrementar el contador de vasos de agua
        addButton.setOnClickListener {
            if (currentCount < dailyGoal) {
                currentCount++
                actualizarUI()
            } else {
                mostrarAlertaMetaAlcanzada()
            }
        }

        // Actualizar la UI inicialmente
        actualizarUI()
    }

    override fun onResume() {
        super.onResume()
        // Registrar el listener de DataClient para recibir datos
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el listener de DataClient
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/configuracion_hidratacion") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    dailyGoal = dataMap.getInt("vasosDiarios", dailyGoal) // Actualiza la meta
                    currentCount = 0 // Reinicia el contador cuando cambia la meta
                    actualizarUI() // Actualiza la UI con la nueva meta
                }
            }
        }
    }

    private fun actualizarUI() {
        // Calcula el progreso como un porcentaje, asegurándose de que esté entre 0 y 1
        val progress = currentCount.toFloat() / dailyGoal.toFloat()
        contadorTextView.text = "$currentCount/$dailyGoal"

        // Asegúrate de que el progreso no exceda 1.0
        circleProgressView.setProgress(progress.coerceIn(0f, 1f))
    }


    private fun mostrarAlertaMetaAlcanzada() {
        Toast.makeText(this, "¡Has alcanzado tu meta diaria de agua!", Toast.LENGTH_SHORT).show()
        handler.postDelayed({
            // Ocultar el mensaje después de unos segundos
            Toast.makeText(this, "", Toast.LENGTH_SHORT).cancel()
        }, 2000)
    }
}

