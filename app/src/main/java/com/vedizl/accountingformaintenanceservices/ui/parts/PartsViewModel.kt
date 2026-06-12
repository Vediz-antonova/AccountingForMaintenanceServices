package com.vedizl.accountingformaintenanceservices.ui.parts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vedizl.accountingformaintenanceservices.data.local.AppDatabase
import com.vedizl.accountingformaintenanceservices.data.local.CategoryEntity
import com.vedizl.accountingformaintenanceservices.data.local.WorkTypeEntity
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import com.vedizl.accountingformaintenanceservices.data.repository.MaintenanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PartFilters(
    val category: String? = null,
    val type: String? = null,
)

class PartsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = MaintenanceRepository(db.maintenanceRecordDao())

    private val _currentCarId = MutableStateFlow("")
    private val _filters = MutableStateFlow(PartFilters())
    val partsFilters: StateFlow<PartFilters> = _filters

    val categories: StateFlow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workTypes: StateFlow<List<WorkTypeEntity>> = db.categoryDao().getAllWorkTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val parts: StateFlow<List<MaintenanceRecord>> = _currentCarId
        .flatMapLatest { carId ->
            if (carId.isEmpty()) {
                flowOf(emptyList())
            } else {
                repository.getRecordsWithParts(carId)
            }
        }
        .combine(_filters) { allRecords, filters ->
            allRecords
                .filter { filters.category == null || it.category == filters.category }
                .filter { filters.type == null || it.type == filters.type }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadForCar(carId: String) {
        _currentCarId.value = carId
        _filters.value = PartFilters()
    }

    fun updateFilters(filters: PartFilters) {
        _filters.value = filters
    }

    fun updateImpression(recordId: String, impression: String?) {
        viewModelScope.launch {
            try {
                repository.updatePartImpression(recordId, impression)
            } catch (e: Exception) {
                _error.value = "Ошибка при сохранении: ${e.message}"
            }
        }
    }
}
