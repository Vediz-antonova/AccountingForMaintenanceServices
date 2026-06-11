package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String
)

@Entity(tableName = "work_types")
data class WorkTypeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val name: String,
    val requiresParts: Boolean
)
