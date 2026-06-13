package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val edad: Int,
    val diagnostico: String, // "Diabetes", "Hipertensión", "Ambos"
    val fechaRegistro: Long = System.currentTimeMillis()
)

@Entity(tableName = "lecturas_glucosa")
data class LecturaGlucosa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valor: Double, // mg/dL
    val nota: String = "",
    val tipoComida: String, // "Ayunas", "Post-prandial", etc.
    val fecha: Long = System.currentTimeMillis()
)

@Entity(tableName = "lecturas_presion")
data class LecturaPresion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sistolica: Int,
    val diastolica: Int,
    val pulso: Int,
    val nota: String = "",
    val fecha: Long = System.currentTimeMillis()
)

@Entity(tableName = "medicamentos")
data class Medicamento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val dosis: String,
    val frecuencia: String, // "Cada 8 horas", "Una vez al día", etc.
    val paraEnfermedad: String, // "Diabetes" o "Hipertensión"
    val fechaInicio: Long = System.currentTimeMillis()
)
