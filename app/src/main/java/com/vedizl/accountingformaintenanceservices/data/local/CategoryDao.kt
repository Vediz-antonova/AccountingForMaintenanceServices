package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM work_types WHERE categoryId = :categoryId ORDER BY name")
    fun getWorkTypesForCategory(categoryId: String): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM work_types ORDER BY name")
    fun getAllWorkTypes(): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("SELECT * FROM work_types WHERE name = :name AND categoryId = :categoryId LIMIT 1")
    suspend fun getWorkTypeByNameAndCategoryId(name: String, categoryId: String): WorkTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkTypes(types: List<WorkTypeEntity>)
}
