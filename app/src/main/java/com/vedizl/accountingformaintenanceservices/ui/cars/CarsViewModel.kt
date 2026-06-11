package com.vedizl.accountingformaintenanceservices.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedizl.accountingformaintenanceservices.data.model.Car
import com.vedizl.accountingformaintenanceservices.data.repository.CarRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarsViewModel : ViewModel() {

    private val repository = CarRepository()

    val cars: StateFlow<List<Car>> = repository.cars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCarId: StateFlow<String?> = repository.selectedCarId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedCar: Car?
        get() = repository.selectedCarId.value?.let { repository.getCarById(it) }

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
        repository.addCar(car)
    }

    fun deleteCar(carId: String) {
        repository.deleteCar(carId)
    }

    fun selectCar(carId: String) {
        repository.selectCar(carId)
    }

    fun clearSelection() {
        repository.clearSelection()
    }
}
