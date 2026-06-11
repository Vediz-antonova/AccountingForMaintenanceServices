package com.vedizl.accountingformaintenanceservices.ui.cars

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vedizl.accountingformaintenanceservices.data.local.AppDatabase
import com.vedizl.accountingformaintenanceservices.data.local.CarMakeEntity
import com.vedizl.accountingformaintenanceservices.data.local.CarModelEntity
import com.vedizl.accountingformaintenanceservices.data.model.Car
import com.vedizl.accountingformaintenanceservices.data.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = CarRepository(db.carDao())

    val cars: StateFlow<List<Car>> = repository.cars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carMakes: StateFlow<List<CarMakeEntity>> = db.carMakeDao().getAllMakes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carModels: StateFlow<List<CarModelEntity>> = db.carMakeDao().getAllModels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun addCar(
        brand: String,
        model: String,
        year: Int,
        licensePlate: String?,
        mileage: Int?,
    ) {
        val car = Car(
            brand = brand,
            model = model,
            year = year,
            licensePlate = licensePlate,
            mileage = mileage,
        )
        viewModelScope.launch {
            try {
                repository.addCar(car)
            } catch (e: Exception) {
                _error.value = "Ошибка при сохранении: ${e.message}"
            }
        }
    }

    fun deleteCar(carId: String) {
        viewModelScope.launch {
            try {
                db.maintenanceRecordDao().deleteRecordsForCar(carId)
                repository.deleteCar(carId)
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении: ${e.message}"
            }
        }
    }

    fun updateCarMileage(carId: String, mileage: Int) {
        viewModelScope.launch {
            try {
                repository.updateCarMileage(carId, mileage)
            } catch (e: Exception) {
                _error.value = "Ошибка при обновлении пробега: ${e.message}"
            }
        }
    }

    fun updateCarLicensePlate(carId: String, licensePlate: String?) {
        viewModelScope.launch {
            try {
                db.carDao().updateCarLicensePlate(carId, licensePlate?.ifEmpty { null })
            } catch (e: Exception) {
                _error.value = "Ошибка при обновлении госномера: ${e.message}"
            }
        }
    }
}
