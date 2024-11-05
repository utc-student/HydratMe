package com.example.hydratme

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity() {

    private lateinit var circleProgressView: CircleProgressView
    private lateinit var contadorTextView: TextView
    private lateinit var numberPicker: NumberPicker
    private lateinit var dataClient: DataClient
    private var dailyGoal = 6
    private var currentCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mobile_main)

        // Inicializar las vistas
        circleProgressView = findViewById(R.id.circle_progress)
        contadorTextView = findViewById(R.id.contador_agua_movil)
        numberPicker = findViewById(R.id.number_picker)
        dataClient = Wearable.getDataClient(this)

        // Configurar el NumberPicker para el rango de vasos diarios
        numberPicker.minValue = 1
        numberPicker.maxValue = 20
        numberPicker.value = dailyGoal

        // Actualizar la interfaz de usuario con el progreso inicial
        actualizarUI()

        // Botón para guardar la configuración de vasos diarios
        val buttonGuardar = findViewById<Button>(R.id.guardar_configuracion)
        buttonGuardar.setOnClickListener {
            dailyGoal = numberPicker.value
            enviarVasosDiariosAlWatch(dailyGoal)
            actualizarUI()
            Toast.makeText(this, "Meta de vasos diaria actualizada a $dailyGoal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarUI() {
        // Actualiza el texto del contador y el progreso del círculo
        contadorTextView.text = "$currentCount/$dailyGoal"

        // Actualizar el progreso en la vista personalizada de progreso circular
        circleProgressView.setProgress(currentCount, dailyGoal)
    }

    private fun enviarVasosDiariosAlWatch(vasosDiarios: Int) {
        val putDataReq = PutDataMapRequest.create("/configuracion_hidratacion").apply {
            dataMap.putInt("vasosDiarios", vasosDiarios)
        }.asPutDataRequest()

        dataClient.putDataItem(putDataReq)
            .addOnSuccessListener {
                Toast.makeText(this, "Configuración enviada al reloj", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar configuración", Toast.LENGTH_SHORT).show()
            }
    }
}
