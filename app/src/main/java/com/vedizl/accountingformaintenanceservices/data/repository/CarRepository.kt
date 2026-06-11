package com.vedizl.accountingformaintenanceservices.data.repository

import com.vedizl.accountingformaintenanceservices.data.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CarRepository {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    private val _selectedCarId = MutableStateFlow<String?>(null)
    val selectedCarId: StateFlow<String?> = _selectedCarId.asStateFlow()

    fun addCar(car: Car) {
        _cars.update { current -> current + car }
    }

    fun deleteCar(carId: String) {
        _cars.update { current -> current.filter { it.id != carId } }
        if (_selectedCarId.value == carId) {
            _selectedCarId.value = null
        }
    }

    fun selectCar(carId: String) {
        _selectedCarId.value = carId
    }

    fun getCarById(carId: String): Car? {
        return _cars.value.find { it.id == carId }
    }

    fun updateCarMileage(carId: String, mileage: Int) {
        _cars.update { current ->
            current.map { if (it.id == carId) it.copy(mileage = mileage) else it }
        }
    }

    fun clearSelection() {
        _selectedCarId.value = null
    }
}
