package com.vedizl.accountingformaintenanceservices.data.repository

import com.vedizl.accountingformaintenanceservices.data.local.MaintenanceRecordDao
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

class MaintenanceRepository(private val recordDao: MaintenanceRecordDao) {

    fun getRecordsForCar(carId: String): Flow<List<MaintenanceRecord>> {
        return recordDao.getRecordsForCar(carId)
    }

    suspend fun addRecord(record: MaintenanceRecord) {
        recordDao.insertRecord(record)
    }

    suspend fun deleteRecord(recordId: String) {
        recordDao.deleteRecord(recordId)
    }

    suspend fun getRecordById(recordId: String): MaintenanceRecord? {
        return recordDao.getRecordById(recordId)
    }

    fun getRecordsWithParts(carId: String): Flow<List<MaintenanceRecord>> {
        return recordDao.getRecordsWithParts(carId)
    }

    suspend fun updatePartImpression(id: String, partImpression: String?) {
        recordDao.updatePartImpression(id, partImpression)
    }
}
