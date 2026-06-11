package com.vedizl.accountingformaintenanceservices.ui.maintenance

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
import java.util.UUID

data class MaintenanceFilters(
    val category: String? = null,
    val type: String? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
)

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = MaintenanceRepository(db.maintenanceRecordDao())

    private val _filters = MutableStateFlow(MaintenanceFilters())
    val filters: StateFlow<MaintenanceFilters> = _filters

    private val _currentCarId = MutableStateFlow("")

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
    val records: StateFlow<List<MaintenanceRecord>> = _currentCarId
        .flatMapLatest { carId ->
            if (carId.isEmpty()) {
                flowOf(emptyList())
            } else {
                repository.getRecordsForCar(carId)
            }
        }
        .combine(_filters) { allRecords, filters ->
            allRecords
                .filter { filters.category == null || it.category == filters.category }
                .filter { filters.type == null || it.type == filters.type }
                .filter { filters.dateFrom == null || it.dateEpochDay >= filters.dateFrom }
                .filter { filters.dateTo == null || it.dateEpochDay <= filters.dateTo }
                .sortedByDescending { it.dateEpochDay }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadForCar(carId: String) {
        _currentCarId.value = carId
        _filters.value = MaintenanceFilters()
    }

    fun addRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            try {
                val existingCategory = db.categoryDao().getCategoryByName(record.category)
                val categoryId = existingCategory?.id ?: run {
                    val newId = UUID.randomUUID().toString()
                    db.categoryDao().insertCategory(CategoryEntity(id = newId, name = record.category))
                    newId
                }
                val existingType = db.categoryDao().getWorkTypeByNameAndCategoryId(record.type, categoryId)
                if (existingType == null) {
                    db.categoryDao().insertWorkTypes(listOf(
                        WorkTypeEntity(categoryId = categoryId, name = record.type, requiresParts = false)
                    ))
                }
                repository.addRecord(record)
            } catch (e: Exception) {
                _error.value = "Ошибка при сохранении: ${e.message}"
            }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(recordId)
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении: ${e.message}"
            }
        }
    }

    suspend fun getRecordById(recordId: String): MaintenanceRecord? {
        return repository.getRecordById(recordId)
    }

    fun updateFilters(filters: MaintenanceFilters) {
        _filters.value = filters
    }

    fun clearFilters() {
        _filters.value = MaintenanceFilters()
    }
}
