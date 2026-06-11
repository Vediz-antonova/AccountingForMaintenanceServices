package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarMakeDao {
    @Query("SELECT * FROM car_makes ORDER BY name")
    fun getAllMakes(): Flow<List<CarMakeEntity>>

    @Query("SELECT * FROM car_models WHERE makeId = :makeId ORDER BY name")
    fun getModelsForMake(makeId: String): Flow<List<CarModelEntity>>

    @Query("SELECT * FROM car_models ORDER BY name")
    fun getAllModels(): Flow<List<CarModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMake(make: CarMakeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<CarModelEntity>)
}
