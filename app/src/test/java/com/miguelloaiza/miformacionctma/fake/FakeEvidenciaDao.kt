package com.miguelloaiza.miformacionctma.fake

import com.miguelloaiza.miformacionctma.data.local.EvidenciaDao
import com.miguelloaiza.miformacionctma.data.local.EvidenciaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeEvidenciaDao : EvidenciaDao {
    private val evidencias = MutableStateFlow<Map<Long, EvidenciaEntity>>(emptyMap())

    override fun observarPorActividad(actividadId: Long): Flow<EvidenciaEntity?> =
        evidencias.map { it[actividadId] }

    override suspend fun guardar(evidencia: EvidenciaEntity) {
        val current = evidencias.value.toMutableMap()
        current[evidencia.actividadId] = evidencia
        evidencias.value = current
    }

    override suspend fun eliminarPorActividad(actividadId: Long) {
        val current = evidencias.value.toMutableMap()
        current.remove(actividadId)
        evidencias.value = current
    }

    override suspend fun actualizarEstado(actividadId: Long, estado: String) {
        val current = evidencias.value.toMutableMap()
        val e = current[actividadId] ?: return
        current[actividadId] = e.copy(estado = estado)
        evidencias.value = current
    }
}
