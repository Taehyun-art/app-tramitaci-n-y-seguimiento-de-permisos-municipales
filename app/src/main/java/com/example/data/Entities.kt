package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permisos")
data class Permiso(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigoSeguimiento: String, // e.g. "PM-123456"
    val tipoSolicitud: String,     // e.g. "Permiso de Edificación", "Licencia de Funcionamiento", "Uso de Suelo", "Instalación de Anuncios"
    val solicitanteName: String,
    val solicitanteIdentidad: String, // e.g. DNI
    val descripcion: String,
    val estado: String,            // "Recibido", "En revisión", "Observado", "Aprobado", "Rechazado"
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis()
)

@Entity(tableName = "documentos_adjuntos")
data class DocumentoAdjunto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigoSeguimiento: String, // links to Permiso
    val nombreArchivo: String,
    val tipoRequisito: String,
    val fechaAdjunto: Long = System.currentTimeMillis(),
    val pesoArchivo: String,       // e.g. "1.5 MB"
    val estadoDocumento: String    // "Cargado", "Pendiente"
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigoSeguimiento: String,
    val actor: String,             // e.g. "Oficial Juan Pérez" or "Ciudadano [Solicitante]"
    val accion: String,            // "Creación de trámite", "Actualización de estado", "Documento adjuntado"
    val valorAnterior: String,
    val valorNuevo: String,
    val observacion: String,       // Justificación de la decisión
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "funcionarios")
data class Funcionario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val cargo: String
)
