package com.vedizl.accountingformaintenanceservices.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "car_makes")
data class CarMakeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String
)

@Entity(tableName = "car_models")
data class CarModelEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val makeId: String,
    val name: String
)
