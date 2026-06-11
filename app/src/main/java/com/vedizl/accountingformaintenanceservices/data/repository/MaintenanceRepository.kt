package com.vedizl.accountingformaintenanceservices.data.repository

import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MaintenanceRepository {

    private val _records = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    val records: StateFlow<List<MaintenanceRecord>> = _records.asStateFlow()

    fun addRecord(record: MaintenanceRecord) {
        _records.update { current -> current + record }
    }

    fun deleteRecord(recordId: String) {
        _records.update { current -> current.filter { it.id != recordId } }
    }

    fun getRecordsForCar(carId: String): List<MaintenanceRecord> {
        return _records.value.filter { it.carId == carId }
    }

    fun getRecordById(recordId: String): MaintenanceRecord? {
        return _records.value.find { it.id == recordId }
    }
}
