package com.example.data

import kotlinx.coroutines.flow.Flow

class SaludRepository(private val appDao: AppDao) {

    val paciente: Flow<Paciente?> = appDao.getPaciente()
    val allGlucosa: Flow<List<LecturaGlucosa>> = appDao.getAllGlucosa()
    val allPresion: Flow<List<LecturaPresion>> = appDao.getAllPresion()
    val allMedicamentos: Flow<List<Medicamento>> = appDao.getAllMedicamentos()

    suspend fun insertPaciente(paciente: Paciente) = appDao.insertPaciente(paciente)
    suspend fun insertGlucosa(lectura: LecturaGlucosa) = appDao.insertGlucosa(lectura)
    suspend fun deleteGlucosa(id: Int) = appDao.deleteGlucosa(id)
    suspend fun insertPresion(lectura: LecturaPresion) = appDao.insertPresion(lectura)
    suspend fun deletePresion(id: Int) = appDao.deletePresion(id)
    suspend fun insertMedicamento(medicamento: Medicamento) = appDao.insertMedicamento(medicamento)
    suspend fun deleteMedicamento(id: Int) = appDao.deleteMedicamento(id)

    suspend fun seedDatabaseIfEmpty() {
        // Here we could seed some initial data if needed
    }
}
