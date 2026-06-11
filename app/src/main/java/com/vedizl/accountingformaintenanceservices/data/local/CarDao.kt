package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vedizl.accountingformaintenanceservices.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY brand, model")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarById(id: String): Car?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    @Query("DELETE FROM cars WHERE id = :id")
    suspend fun deleteCar(id: String)

    @Query("UPDATE cars SET mileage = :mileage WHERE id = :id")
    suspend fun updateCarMileage(id: String, mileage: Int)

    @Query("UPDATE cars SET licensePlate = :licensePlate WHERE id = :id")
    suspend fun updateCarLicensePlate(id: String, licensePlate: String?)
}
