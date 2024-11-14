package com.example.hydratme

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var circleProgressView: CircleProgressView
    private lateinit var contadorTextView: TextView
    private lateinit var numberPicker: NumberPicker
    private lateinit var dataClient: DataClient
    private var dailyGoal = 8
    private var currentCount = 0

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

        // Escuchar cambios en Firebase para dailyGoal y currentCount
        obtenerDailyGoalDesdeFirebase()
        obtenerCurrentCountDesdeFirebase()

        // Botón para guardar la configuración de vasos diarios
        val buttonGuardar = findViewById<Button>(R.id.guardar_configuracion)
        buttonGuardar.setOnClickListener {
            dailyGoal = numberPicker.value
            guardarMetaEnFirebase(dailyGoal)
            actualizarUI()
            Toast.makeText(this, "Meta de vasos diaria actualizada a $dailyGoal", Toast.LENGTH_SHORT).show()
        }

        // Botón para reiniciar el contador de agua
        val buttonReset = findViewById<Button>(R.id.reset_count)
        buttonReset.setOnClickListener {
            resetearContadorDeAgua()
        }
    }

    private fun actualizarUI() {
        contadorTextView.text = "$currentCount/$dailyGoal"
        circleProgressView.setProgress(currentCount, dailyGoal)
    }

    private fun guardarMetaEnFirebase(dailyGoal: Int) {
        val database = Firebase.database
        val goalRef = database.getReference("dailyGoal")

        goalRef.setValue(dailyGoal)
            .addOnSuccessListener {
                Toast.makeText(this, "Meta de vasos diaria guardada en Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la meta en Firebase", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDailyGoalDesdeFirebase() {
        val database = Firebase.database
        val goalRef = database.getReference("dailyGoal")

        goalRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Int::class.java)?.let { value ->
                    dailyGoal = value
                    numberPicker.value = dailyGoal
                    actualizarUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener la meta diaria: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerCurrentCountDesdeFirebase() {
        val database = Firebase.database
        val countRef = database.getReference("currentCount")

        countRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Int::class.java)?.let { value ->
                    currentCount = value
                    actualizarUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener el conteo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetearContadorDeAgua() {
        currentCount = 0
        actualizarUI()

        // Actualizar Firebase
        val database = Firebase.database
        val countRef = database.getReference("currentCount")

        countRef.setValue(currentCount)
            .addOnSuccessListener {
                Toast.makeText(this, "Contador reiniciado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al reiniciar el contador en Firebase", Toast.LENGTH_SHORT).show()
            }
    }
}
