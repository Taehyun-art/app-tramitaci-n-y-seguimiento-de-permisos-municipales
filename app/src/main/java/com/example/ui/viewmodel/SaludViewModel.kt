package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SaludViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaludRepository

    val paciente: StateFlow<Paciente?>
    val allGlucosa: StateFlow<List<LecturaGlucosa>>
    val allPresion: StateFlow<List<LecturaPresion>>
    val allMedicamentos: StateFlow<List<Medicamento>>

    init {
        val appDatabase = AppDatabase.getDatabase(application)
        val appDao = appDatabase.appDao()
        repository = SaludRepository(appDao)

        paciente = repository.paciente
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        allGlucosa = repository.allGlucosa
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allPresion = repository.allPresion
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allMedicamentos = repository.allMedicamentos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            // Seed initial patient if none exists
            if (paciente.value == null) {
                repository.insertPaciente(Paciente(nombre = "Usuario", edad = 45, diagnostico = "Diabetes e Hipertensión"))
            }
        }
    }

    fun agregarGlucosa(valor: Double, nota: String, tipoComida: String) {
        viewModelScope.launch {
            repository.insertGlucosa(LecturaGlucosa(valor = valor, nota = nota, tipoComida = tipoComida))
        }
    }

    fun eliminarGlucosa(id: Int) {
        viewModelScope.launch {
            repository.deleteGlucosa(id)
        }
    }

    fun agregarPresion(sistolica: Int, diastolica: Int, pulso: Int, nota: String) {
        viewModelScope.launch {
            repository.insertPresion(LecturaPresion(sistolica = sistolica, diastolica = diastolica, pulso = pulso, nota = nota))
        }
    }

    fun eliminarPresion(id: Int) {
        viewModelScope.launch {
            repository.deletePresion(id)
        }
    }

    fun agregarMedicamento(nombre: String, dosis: String, frecuencia: String, paraEnfermedad: String) {
        viewModelScope.launch {
            repository.insertMedicamento(Medicamento(nombre = nombre, dosis = dosis, frecuencia = frecuencia, paraEnfermedad = paraEnfermedad))
        }
    }

    fun eliminarMedicamento(id: Int) {
        viewModelScope.launch {
            repository.deleteMedicamento(id)
        }
    }
}
