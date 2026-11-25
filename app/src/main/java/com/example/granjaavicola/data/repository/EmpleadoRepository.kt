package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.EmpleadoDao
import com.example.granjaavicola.data.entity.Empleado
import kotlinx.coroutines.flow.Flow

class EmpleadoRepository(private val empleadoDao: EmpleadoDao) {

    val allEmpleados: Flow<List<Empleado>> = empleadoDao.getAll()

    val empleadosActivos: Flow<List<Empleado>> = empleadoDao.getActivos(true)

    fun getById(id: Long): Flow<Empleado?> = empleadoDao.getById(id)

    fun getActivos(activo: Boolean = true): Flow<List<Empleado>> =
        empleadoDao.getActivos(activo)

    fun getPorDocumento(documento: String): Flow<Empleado?> =
        empleadoDao.getPorDocumento(documento)

    suspend fun insert(empleado: Empleado): Long = empleadoDao.insert(empleado)

    suspend fun insertAll(empleados: List<Empleado>): List<Long> =
        empleadoDao.insertAll(empleados)

    suspend fun update(empleado: Empleado) = empleadoDao.update(empleado)

    suspend fun delete(empleado: Empleado) = empleadoDao.delete(empleado)
}
