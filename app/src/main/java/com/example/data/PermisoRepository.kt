package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PermisoRepository(private val appDao: AppDao) {

    val allPermisos: Flow<List<Permiso>> = appDao.getAllPermisosFlow()
    val allLogs: Flow<List<AuditLog>> = appDao.getAllLogsFlow()
    val allFuncionarios: Flow<List<Funcionario>> = appDao.getAllFuncionariosFlow()

    fun getPermisoByCodigoFlow(codigo: String): Flow<Permiso?> {
        return appDao.getPermisoByCodigoFlow(codigo)
    }

    suspend fun getPermisoByCodigo(codigo: String): Permiso? {
        return appDao.getPermisoByCodigo(codigo)
    }

    fun getDocumentosForPermisoFlow(codigo: String): Flow<List<DocumentoAdjunto>> {
        return appDao.getDocumentosForPermisoFlow(codigo)
    }

    suspend fun getDocumentosForPermisoList(codigo: String): List<DocumentoAdjunto> {
        return appDao.getDocumentosForPermisoList(codigo)
    }

    fun getLogsForPermisoFlow(codigo: String): Flow<List<AuditLog>> {
        return appDao.getLogsForPermisoFlow(codigo)
    }

    suspend fun insertPermiso(permiso: Permiso) {
        appDao.insertPermiso(permiso)
    }

    suspend fun updatePermiso(permiso: Permiso) {
        appDao.updatePermiso(permiso)
    }

    suspend fun insertDocumento(documento: DocumentoAdjunto) {
        appDao.insertDocumento(documento)
    }

    suspend fun insertLog(log: AuditLog) {
        appDao.insertLog(log)
    }

    suspend fun insertFuncionario(funcionario: Funcionario) {
        appDao.insertFuncionario(funcionario)
    }

    /**
     * Seeds initial database content if it is empty to ensure a realistic university environment.
     */
    suspend fun seedDatabaseIfEmpty() {
        val currentList = appDao.getAllPermisosList()
        if (currentList.isEmpty()) {
            // Seed Funcionarios
            val f1 = Funcionario(nombre = "Ing. Carlos Mendoza", cargo = "Urbano e Infraestructura")
            val f2 = Funcionario(nombre = "Lic. Lucía Ramos", cargo = "Directora de Licencias Comerciales")
            val f3 = Funcionario(nombre = "Dra. Sofía Vargas", cargo = "Coordinadora de Ventanilla Única")
            appDao.insertFuncionario(f1)
            appDao.insertFuncionario(f2)
            appDao.insertFuncionario(f3)

            // Seed Some Permisos
            val code1 = "PM-1025A"
            val p1 = Permiso(
                codigoSeguimiento = code1,
                tipoSolicitud = "Permiso de Construcción Residencial",
                solicitanteName = "Alejandro Gómez Ruiz",
                solicitanteIdentidad = "104928374",
                descripcion = "Ampliación de vivienda unifamiliar de dos plantas en Calle Las Azaleas #45.",
                estado = "Recibido",
                fechaCreacion = System.currentTimeMillis() - 86400000 * 3, // 3 days ago
                fechaActualizacion = System.currentTimeMillis() - 86400000 * 3
            )
            appDao.insertPermiso(p1)

            // Seed Documentos for P1
            appDao.insertDocumento(DocumentoAdjunto(
                codigoSeguimiento = code1,
                nombreArchivo = "Plano_Estructural_Modificado.pdf",
                tipoRequisito = "Plano de Obra",
                pesoArchivo = "4.2 MB",
                estadoDocumento = "Cargado"
            ))
            appDao.insertDocumento(DocumentoAdjunto(
                codigoSeguimiento = code1,
                nombreArchivo = "Copia_Titulo_Propiedad.pdf",
                tipoRequisito = "Título de Propiedad",
                pesoArchivo = "1.8 MB",
                estadoDocumento = "Cargado"
            ))

            // Seed Log for P1
            appDao.insertLog(AuditLog(
                codigoSeguimiento = code1,
                actor = "Cid. Alejandro Gómez Ruiz",
                accion = "Creación de Trámite",
                valorAnterior = "Ninguno",
                valorNuevo = "Recibido",
                observacion = "Envío inicial de expediente digital para ampliación de dos plantas.",
                timestamp = System.currentTimeMillis() - 86400000 * 3
            ))

            val code2 = "PM-5938B"
            val p2 = Permiso(
                codigoSeguimiento = code2,
                tipoSolicitud = "Licencia Comercial de Funcionamiento",
                solicitanteName = "María Elena Benítez",
                solicitanteIdentidad = "208756312",
                descripcion = "Apertura de cafetería de especialidad 'El Grano Diario' en Plaza Central.",
                estado = "En revisión",
                fechaCreacion = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
                fechaActualizacion = System.currentTimeMillis() - 86400000 * 2 // Updated 2 days ago
            )
            appDao.insertPermiso(p2)

            // Docs and logs for P2
            appDao.insertDocumento(DocumentoAdjunto(
                codigoSeguimiento = code2,
                nombreArchivo = "Licencia_Sanitaria_Preliminar.pdf",
                tipoRequisito = "Certificado de Salud Pública",
                pesoArchivo = "850 KB",
                estadoDocumento = "Cargado"
            ))
            appDao.insertDocumento(DocumentoAdjunto(
                codigoSeguimiento = code2,
                nombreArchivo = "DNI_Representante_Legal.pdf",
                tipoRequisito = "Identificación Oficial",
                pesoArchivo = "1.1 MB",
                estadoDocumento = "Cargado"
            ))
            appDao.insertLog(AuditLog(
                codigoSeguimiento = code2,
                actor = "Cid. María Elena Benítez",
                accion = "Creación de Trámite",
                valorAnterior = "Ninguno",
                valorNuevo = "Recibido",
                observacion = "Solicitud de apertura comercial de bar/cafetería de especialidad.",
                timestamp = System.currentTimeMillis() - 86400000 * 5
            ))
            appDao.insertLog(AuditLog(
                codigoSeguimiento = code2,
                actor = "Lic. Lucía Ramos",
                accion = "Actualización de estado",
                valorAnterior = "Recibido",
                valorNuevo = "En revisión",
                observacion = "Expediente asignado para verificación de zonificación y viabilidad comercial.",
                timestamp = System.currentTimeMillis() - 86400000 * 2
            ))

            val code3 = "PM-8341C"
            val p3 = Permiso(
                codigoSeguimiento = code3,
                tipoSolicitud = "Licencia de Letreros y Anuncios",
                solicitanteName = "Farmacias Vida Sana S.A.",
                solicitanteIdentidad = "901238475",
                descripcion = "Colocación de letrero luminoso de 4x2 metros en fachada exterior de sucursal.",
                estado = "Observado",
                fechaCreacion = System.currentTimeMillis() - 86400000 * 7,
                fechaActualizacion = System.currentTimeMillis() - 86400000 * 1
            )
            appDao.insertPermiso(p3)

            // Dco/Logs for P3
            appDao.insertDocumento(DocumentoAdjunto(
                codigoSeguimiento = code3,
                nombreArchivo = "Diseno_Infra_Letrero.png",
                tipoRequisito = "Especificación de Letreros",
                pesoArchivo = "2.5 MB",
                estadoDocumento = "Cargado"
            ))
            appDao.insertLog(AuditLog(
                codigoSeguimiento = code3,
                actor = "Ciudadano (Empresa)",
                accion = "Creación de Trámite",
                valorAnterior = "Ninguno",
                valorNuevo = "Recibido",
                observacion = "Solicitud para letrero LED luminoso.",
                timestamp = System.currentTimeMillis() - 86400000 * 7
            ))
            appDao.insertLog(AuditLog(
                codigoSeguimiento = code3,
                actor = "Ing. Carlos Mendoza",
                accion = "Actualización de estado",
                valorAnterior = "Recibido",
                valorNuevo = "Observado",
                observacion = "El tamaño propuesto de 4x2 metros excede la normativa de conservación patrimonial (máximo 2x1 en zona histórica). Por favor, adjunte un diseño de menor dimensión.",
                timestamp = System.currentTimeMillis() - 86400000 * 1
            ))
        }
    }
}
