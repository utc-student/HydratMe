package com.example.hydratme.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.hydratme.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    private lateinit var circleProgressView: CircleProgressView
    private lateinit var contadorTextView: TextView
    private lateinit var addButton: Button
    private var dailyGoal = 8
    private var currentCount = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wear_main)

        // Inicializar las vistas
        circleProgressView = findViewById(R.id.circle_progress)
        contadorTextView = findViewById(R.id.contador_agua)
        addButton = findViewById(R.id.agregar_agua)

        // Escuchar cambios en Firebase para dailyGoal y currentCount
        obtenerDatosDesdeFirebase()

        // Configurar el botón para incrementar el contador de vasos de agua
        addButton.setOnClickListener {
            if (currentCount < dailyGoal) {
                currentCount++
                guardarCurrentCountEnFirebase(currentCount)  // Actualizar en Firebase
                actualizarUI()
            } else {
                mostrarAlertaMetaAlcanzada()
            }
        }

        // Actualizar la UI inicialmente
        actualizarUI()
    }

    private fun obtenerDatosDesdeFirebase() {
        val database = FirebaseDatabase.getInstance()
        val goalRef = database.getReference("dailyGoal")
        val countRef = database.getReference("currentCount")

        // Listener para obtener dailyGoal
        goalRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Int::class.java)?.let { updatedDailyGoal ->
                    dailyGoal = updatedDailyGoal
                    actualizarUI() // Actualiza la UI con el nuevo dailyGoal
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener dailyGoal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Listener para obtener currentCount
        countRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Int::class.java)?.let { updatedCurrentCount ->
                    currentCount = updatedCurrentCount
                    actualizarUI() // Actualiza la UI con el nuevo currentCount
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener currentCount: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarCurrentCountEnFirebase(count: Int) {
        val database = FirebaseDatabase.getInstance()
        val countRef = database.getReference("currentCount")

        countRef.setValue(count)
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar el contador", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarUI() {
        contadorTextView.text = "$currentCount/$dailyGoal"

        // Cambia el color del círculo al alcanzar la meta
        if (currentCount >= dailyGoal) {
            circleProgressView.setProgressColor("#00FF00")  // Color verde al alcanzar la meta
        } else {
            circleProgressView.setProgressColor("#00BFFF")  // Color azul si no ha alcanzado la meta
        }

        circleProgressView.setProgress(currentCount, dailyGoal)
    }

    private fun mostrarAlertaMetaAlcanzada() {
        Toast.makeText(this, "¡Has alcanzado tu meta diaria de agua!", Toast.LENGTH_SHORT).show()
        handler.postDelayed({
            Toast.makeText(this, "", Toast.LENGTH_SHORT).cancel()
        }, 2000)
    }
}
