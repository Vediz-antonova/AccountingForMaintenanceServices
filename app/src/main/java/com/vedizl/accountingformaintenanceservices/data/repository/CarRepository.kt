package com.vedizl.accountingformaintenanceservices.data.repository

import com.vedizl.accountingformaintenanceservices.data.local.CarDao
import com.vedizl.accountingformaintenanceservices.data.model.Car
import kotlinx.coroutines.flow.Flow

class CarRepository(private val carDao: CarDao) {

    val cars: Flow<List<Car>> = carDao.getAllCars()

    suspend fun addCar(car: Car) {
        carDao.insertCar(car)
    }

    suspend fun deleteCar(carId: String) {
        carDao.deleteCar(carId)
    }

    suspend fun getCarById(carId: String): Car? {
        return carDao.getCarById(carId)
    }

    suspend fun updateCarMileage(carId: String, mileage: Int) {
        carDao.updateCarMileage(carId, mileage)
    }
}
