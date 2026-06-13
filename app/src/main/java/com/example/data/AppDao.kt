package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Paciente ---
    @Query("SELECT * FROM pacientes LIMIT 1")
    fun getPaciente(): Flow<Paciente?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: Paciente)

    // --- Glucosa ---
    @Query("SELECT * FROM lecturas_glucosa ORDER BY fecha DESC")
    fun getAllGlucosa(): Flow<List<LecturaGlucosa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlucosa(lectura: LecturaGlucosa)

    @Query("DELETE FROM lecturas_glucosa WHERE id = :id")
    suspend fun deleteGlucosa(id: Int)

    // --- Presion ---
    @Query("SELECT * FROM lecturas_presion ORDER BY fecha DESC")
    fun getAllPresion(): Flow<List<LecturaPresion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresion(lectura: LecturaPresion)

    @Query("DELETE FROM lecturas_presion WHERE id = :id")
    suspend fun deletePresion(id: Int)

    // --- Medicamentos ---
    @Query("SELECT * FROM medicamentos ORDER BY fechaInicio DESC")
    fun getAllMedicamentos(): Flow<List<Medicamento>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicamento(medicamento: Medicamento)

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun deleteMedicamento(id: Int)
}
