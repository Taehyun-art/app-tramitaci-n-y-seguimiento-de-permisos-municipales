package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Permiso Queries ---
    @Query("SELECT * FROM permisos ORDER BY fechaCreacion DESC")
    fun getAllPermisosFlow(): Flow<List<Permiso>>

    @Query("SELECT * FROM permisos ORDER BY fechaCreacion DESC")
    suspend fun getAllPermisosList(): List<Permiso>

    @Query("SELECT * FROM permisos WHERE codigoSeguimiento = :codigo LIMIT 1")
    suspend fun getPermisoByCodigo(codigo: String): Permiso?

    @Query("SELECT * FROM permisos WHERE codigoSeguimiento = :codigo LIMIT 1")
    fun getPermisoByCodigoFlow(codigo: String): Flow<Permiso?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermiso(permiso: Permiso)

    @Update
    suspend fun updatePermiso(permiso: Permiso)

    // --- DocumentoAdjunto Queries ---
    @Query("SELECT * FROM documentos_adjuntos WHERE codigoSeguimiento = :codigo ORDER BY fechaAdjunto DESC")
    fun getDocumentosForPermisoFlow(codigo: String): Flow<List<DocumentoAdjunto>>

    @Query("SELECT * FROM documentos_adjuntos WHERE codigoSeguimiento = :codigo ORDER BY fechaAdjunto DESC")
    suspend fun getDocumentosForPermisoList(codigo: String): List<DocumentoAdjunto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumento(documento: DocumentoAdjunto)

    // --- AuditLog Queries ---
    @Query("SELECT * FROM audit_logs WHERE codigoSeguimiento = :codigo ORDER BY timestamp DESC")
    fun getLogsForPermisoFlow(codigo: String): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)

    // --- Funcionario Queries ---
    @Query("SELECT * FROM funcionarios ORDER BY nombre ASC")
    fun getAllFuncionariosFlow(): Flow<List<Funcionario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuncionario(funcionario: Funcionario)
}
