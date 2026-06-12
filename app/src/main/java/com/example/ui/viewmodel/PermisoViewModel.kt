package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class PermisoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PermisoRepository

    // Base flows from database
    val allPermisos: StateFlow<List<Permiso>>
    val allLogs: StateFlow<List<AuditLog>>
    val allFuncionarios: StateFlow<List<Funcionario>>

    // App state flows
    val currentRole = MutableStateFlow("CIUDADANO") // "CIUDADANO" | "FUNCIONARIO"
    val activeOfficialName = MutableStateFlow("Ing. Carlos Mendoza")
    val activeOfficialCargo = MutableStateFlow("Recursos Urbanos e Infraestructura")

    // Citizen Tracking State
    val searchCodeQuery = MutableStateFlow("")
    private val _searchResultCode = MutableStateFlow<String?>(null)
    val searchPerformed = MutableStateFlow(false)
    val searchError = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult: StateFlow<Permiso?> = _searchResultCode
        .flatMapLatest { code ->
            if (code == null) flowOf(null)
            else repository.getPermisoByCodigoFlow(code)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultDocs: StateFlow<List<DocumentoAdjunto>> = searchResult
        .flatMapLatest { permit ->
            if (permit == null) flowOf(emptyList())
            else repository.getDocumentosForPermisoFlow(permit.codigoSeguimiento)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultLogs: StateFlow<List<AuditLog>> = searchResult
        .flatMapLatest { permit ->
            if (permit == null) flowOf(emptyList())
            else repository.getLogsForPermisoFlow(permit.codigoSeguimiento)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Official Detailed State
    private val _selectedPermisoCode = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPermiso: StateFlow<Permiso?> = _selectedPermisoCode
        .flatMapLatest { code ->
            if (code == null) flowOf(null)
            else repository.getPermisoByCodigoFlow(code)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPermisoDocs: StateFlow<List<DocumentoAdjunto>> = selectedPermiso
        .flatMapLatest { permit ->
            if (permit == null) flowOf(emptyList())
            else repository.getDocumentosForPermisoFlow(permit.codigoSeguimiento)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPermisoLogs: StateFlow<List<AuditLog>> = selectedPermiso
        .flatMapLatest { permit ->
            if (permit == null) flowOf(emptyList())
            else repository.getLogsForPermisoFlow(permit.codigoSeguimiento)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated Real-time Notifications List (Newest first)
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    init {
        val appDatabase = AppDatabase.getDatabase(application)
        val appDao = appDatabase.appDao()
        repository = PermisoRepository(appDao)

        // Initialize flows
        allPermisos = repository.allPermisos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allLogs = repository.allLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allFuncionarios = repository.allFuncionarios
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Seed initial data if database is bare
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    fun selectPermiso(permiso: Permiso?) {
        _selectedPermisoCode.value = permiso?.codigoSeguimiento
    }

    fun switchRole(role: String) {
        currentRole.value = role
        addNotification(
            title = "Rol cambiado",
            message = "Ahora estás operando en modo: ${if (role == "CIUDADANO") "Ciudadano" else "Funcionario Público"}",
            type = NotificationType.INFO
        )
    }

    fun selectOfficial(funcionario: Funcionario) {
        activeOfficialName.value = funcionario.nombre
        activeOfficialCargo.value = funcionario.cargo
        addNotification(
            title = "Sesión de Funcionario",
            message = "Sesión iniciada como ${funcionario.nombre} (${funcionario.cargo})",
            type = NotificationType.INFO
        )
    }

    // --- Search Permit (Citizen & Official Lookup) ---
    fun searchPermit(code: String) {
        val uppercaseCode = code.uppercase().trim()
        searchCodeQuery.value = uppercaseCode
        searchPerformed.value = true
        searchError.value = null

        viewModelScope.launch {
            val permit = repository.getPermisoByCodigo(uppercaseCode)
            if (permit != null) {
                _searchResultCode.value = uppercaseCode
            } else {
                _searchResultCode.value = null
                searchError.value = "No se encontró ningún trámite con el código '$uppercaseCode'"
            }
        }
    }

    fun clearSearchResult() {
        _searchResultCode.value = null
        searchPerformed.value = false
        searchError.value = null
        searchCodeQuery.value = ""
    }

    // --- Create Permit (Citizen Action) ---
    fun createPermit(
        tipoSolicitud: String,
        solicitanteName: String,
        solicitanteIdentidad: String,
        descripcion: String,
        attachedFiles: List<Pair<String, String>> // list of (fileName, documentCategory)
    ) {
        viewModelScope.launch {
            // Generate Code Unique Tracking (e.g., PM-5092K)
            val generatedCode = generateTrackingCode()

            val nuevoPermiso = Permiso(
                codigoSeguimiento = generatedCode,
                tipoSolicitud = tipoSolicitud,
                solicitanteName = solicitanteName,
                solicitanteIdentidad = solicitanteIdentidad,
                descripcion = descripcion,
                estado = "Recibido"
            )

            // Save permit
            repository.insertPermiso(nuevoPermiso)

            // Save documents
            attachedFiles.forEach { (fileName, category) ->
                val size = "${Random.nextInt(1, 10)}.${Random.nextInt(0, 9)} MB"
                val doc = DocumentoAdjunto(
                    codigoSeguimiento = generatedCode,
                    nombreArchivo = fileName,
                    tipoRequisito = category,
                    pesoArchivo = size,
                    estadoDocumento = "Cargado"
                )
                repository.insertDocumento(doc)
            }

            // Create initial audit log
            val log = AuditLog(
                codigoSeguimiento = generatedCode,
                actor = "Cid. $solicitanteName (DNI: $solicitanteIdentidad)",
                accion = "Creación de Trámite",
                valorAnterior = "Ninguno",
                valorNuevo = "Recibido",
                observacion = "El ciudadano ha registrado exitosamente la solicitud mediante el Portal Digital de Permisos."
            )
            repository.insertLog(log)

            // Add real-time notification
            addNotification(
                title = "Nuevo Trámite Iniciado",
                message = "El trámite $generatedCode ($tipoSolicitud) fue registrado de forma segura. Guarda tu código de seguimiento.",
                type = NotificationType.SUCCESS
            )

            // Auto-load it to search state to show citizen their congratulations card
            _searchResultCode.value = generatedCode
            searchPerformed.value = true
            searchError.value = null
        }
    }

    // --- Update Permit Status (Official Action) ---
    fun updatePermitStatus(
        codigo: String,
        nuevoEstado: String,
        observacion: String
    ) {
        viewModelScope.launch {
            val permit = repository.getPermisoByCodigo(codigo)
            if (permit != null) {
                if (permit.estado == nuevoEstado) return@launch // No state change

                val estadoAnterior = permit.estado
                val updatedPermito = permit.copy(
                    estado = nuevoEstado,
                    fechaActualizacion = System.currentTimeMillis()
                )

                // Save status change
                repository.updatePermiso(updatedPermito)

                // Log the audit trial
                val officerName = activeOfficialName.value
                val officerCargo = activeOfficialCargo.value
                val log = AuditLog(
                    codigoSeguimiento = codigo,
                    actor = "$officerName ($officerCargo)",
                    accion = "Actualización de estado",
                    valorAnterior = estadoAnterior,
                    valorNuevo = nuevoEstado,
                    observacion = observacion.ifBlank { "Cambio de estado procesado en evaluación técnica oficial." }
                )
                repository.insertLog(log)

                // Notify in real-time
                addNotification(
                    title = "Trámite Actualizado",
                    message = "El permiso '$codigo' fue cambiado de '$estadoAnterior' a '$nuevoEstado' por $officerName.",
                    type = NotificationType.WARNING
                )

            }
        }
    }

    // --- Upload Document during tracking (Both Roles) ---
    fun attachDocumentToPermit(
        codigo: String,
        nombreArchivo: String,
        tipoRequisito: String,
        subidoPor: String // e.g. "Ciudadano", "Funcionario Juan"
    ) {
        viewModelScope.launch {
            val size = "${Random.nextInt(1, 5)}.${Random.nextInt(0, 9)} MB"
            val doc = DocumentoAdjunto(
                codigoSeguimiento = codigo,
                nombreArchivo = nombreArchivo,
                tipoRequisito = tipoRequisito,
                pesoArchivo = size,
                estadoDocumento = "Cargado"
            )
            repository.insertDocumento(doc)

            // Audit
            val auditLog = AuditLog(
                codigoSeguimiento = codigo,
                actor = subidoPor,
                accion = "Documento adjuntado",
                valorAnterior = "-",
                valorNuevo = nombreArchivo,
                observacion = "Nuevo archivo subido al expediente: $nombreArchivo ($tipoRequisito)"
            )
            repository.insertLog(log = auditLog)

            // Notify
            addNotification(
                title = "Documento Agregado",
                message = "Se adjuntó '$nombreArchivo' al expediente del trámite $codigo.",
                type = NotificationType.INFO
            )
        }
    }

    // --- Private Utilities ---
    private fun generateTrackingCode(): String {
        val letters = "ABCDEFGHJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val part1 = "PM-"
        val part2 = (1..4).map { numbers[Random.nextInt(numbers.length)] }.joinToString("")
        val part3 = letters[Random.nextInt(letters.length)]
        return "$part1$part2$part3"
    }

    private fun addNotification(title: String, message: String, type: NotificationType) {
        val notif = AppNotification(
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis()
        )
        val updated = listOf(notif) + _notifications.value
        _notifications.value = updated.take(15) // Keep last 15
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }
}

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    SUCCESS, INFO, WARNING, ERROR
}
