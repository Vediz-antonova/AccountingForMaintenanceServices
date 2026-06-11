package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY CASE WHEN name = 'Другое' THEN 1 ELSE 0 END, name")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM work_types WHERE categoryId = :categoryId ORDER BY CASE WHEN name = 'Другое' THEN 1 ELSE 0 END, name")
    fun getWorkTypesForCategory(categoryId: String): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM work_types ORDER BY CASE WHEN name = 'Другое' THEN 1 ELSE 0 END, name")
    fun getAllWorkTypes(): Flow<List<WorkTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkTypes(types: List<WorkTypeEntity>)
}
