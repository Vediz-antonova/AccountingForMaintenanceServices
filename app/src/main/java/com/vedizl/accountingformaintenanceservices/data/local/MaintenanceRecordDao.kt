package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceRecordDao {
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId ORDER BY dateEpochDay DESC")
    fun getRecordsForCar(carId: String): Flow<List<MaintenanceRecord>>

    @Query("SELECT * FROM maintenance_records WHERE id = :id")
    suspend fun getRecordById(id: String): MaintenanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MaintenanceRecord)

    @Query("DELETE FROM maintenance_records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("DELETE FROM maintenance_records WHERE carId = :carId")
    suspend fun deleteRecordsForCar(carId: String)

    @Query("SELECT * FROM maintenance_records WHERE carId = :carId AND partNumber IS NOT NULL ORDER BY dateEpochDay DESC")
    fun getRecordsWithParts(carId: String): Flow<List<MaintenanceRecord>>

    @Query("UPDATE maintenance_records SET partImpression = :partImpression WHERE id = :id")
    suspend fun updatePartImpression(id: String, partImpression: String?)
}
