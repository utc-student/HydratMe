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
    private var currentCount = 0  // Valor inicial

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

        // Obtener el valor de dailyGoal y currentCount desde Firebase
        obtenerDailyGoalDesdeFirebase()
        obtenerCurrentCountDesdeFirebase()

        // Botón para guardar la configuración de vasos diarios
        val buttonGuardar = findViewById<Button>(R.id.guardar_configuracion)
        buttonGuardar.setOnClickListener {
            dailyGoal = numberPicker.value
            guardarMetaEnFirebase(dailyGoal)  // Guardar en Firebase
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

    private fun guardarMetaEnFirebase(dailyGoal: Int) {
        // Referencia a la base de datos de Firebase
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
        // Referencia de Firebase para dailyGoal
        val database = Firebase.database
        val goalRef = database.getReference("dailyGoal")

        // Escucha los cambios en el valor de dailyGoal
        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verificar si el valor existe y actualizar dailyGoal
                snapshot.getValue(Int::class.java)?.let { value ->
                    dailyGoal = value
                    numberPicker.value = dailyGoal // Actualizar el NumberPicker
                    actualizarUI()  // Actualizar la UI con el nuevo valor
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener la meta diaria: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerCurrentCountDesdeFirebase() {
        // Referencia de Firebase para currentCount
        val database = Firebase.database
        val countRef = database.getReference("currentCount")

        // Escucha los cambios en el valor de currentCount
        countRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verificar si el valor existe y actualizar currentCount
                snapshot.getValue(Int::class.java)?.let { value ->
                    currentCount = value
                    actualizarUI()  // Actualizar la UI con el nuevo valor
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener el conteo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}