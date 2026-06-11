package com.vedizl.accountingformaintenanceservices.ui.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import com.vedizl.accountingformaintenanceservices.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MaintenanceFilters(
    val category: String? = null,
    val type: String? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
)

class MaintenanceViewModel : ViewModel() {

    private val repository = MaintenanceRepository()

    private val _filters = MutableStateFlow(MaintenanceFilters())
    val filters: StateFlow<MaintenanceFilters> = _filters

    private var currentCarId: String = ""

    val records: StateFlow<List<MaintenanceRecord>> = combine(
        repository.records,
        _filters
    ) { allRecords, filters ->
        allRecords
            .filter { it.carId == currentCarId }
            .filter { filters.category == null || it.category == filters.category }
            .filter { filters.type == null || it.type == filters.type }
            .filter { filters.dateFrom == null || it.dateEpochDay >= filters.dateFrom }
            .filter { filters.dateTo == null || it.dateEpochDay <= filters.dateTo }
            .sortedByDescending { it.dateEpochDay }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadForCar(carId: String) {
        currentCarId = carId
        updateFilters(MaintenanceFilters())
    }

    fun addRecord(record: MaintenanceRecord) {
        repository.addRecord(record)
    }

    fun deleteRecord(recordId: String) {
        repository.deleteRecord(recordId)
    }

    fun getRecordById(recordId: String): MaintenanceRecord? {
        return repository.getRecordById(recordId)
    }

    fun updateFilters(filters: MaintenanceFilters) {
        _filters.value = filters
    }

    fun clearFilters() {
        _filters.value = MaintenanceFilters()
    }
}
