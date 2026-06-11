package com.vedizl.accountingformaintenanceservices.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vedizl.accountingformaintenanceservices.data.model.Car
import com.vedizl.accountingformaintenanceservices.data.model.CarMakes
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceCategories
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import java.util.UUID

@Database(
    entities = [
        Car::class,
        MaintenanceRecord::class,
        CarMakeEntity::class,
        CarModelEntity::class,
        CategoryEntity::class,
        WorkTypeEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao
    abstract fun carMakeDao(): CarMakeDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "accounting_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            seedReferenceData(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            val cursor = db.query("SELECT COUNT(*) FROM car_makes")
            cursor.moveToFirst()
            val makesCount = cursor.getInt(0)
            cursor.close()
            val catCursor = db.query("SELECT COUNT(*) FROM categories")
            catCursor.moveToFirst()
            val catCount = catCursor.getInt(0)
            catCursor.close()
            if (makesCount == 0 && catCount == 0) {
                seedReferenceData(db)
            }
        }
    }
}

private fun seedReferenceData(db: SupportSQLiteDatabase) {
    CarMakes.makes.forEach { make ->
        val makeId = UUID.randomUUID().toString()
        db.execSQL("INSERT OR REPLACE INTO car_makes (id, name) VALUES (?, ?)", arrayOf(makeId, make.name))
        make.models.forEach { model ->
            db.execSQL("INSERT OR REPLACE INTO car_models (id, makeId, name) VALUES (?, ?, ?)", arrayOf(UUID.randomUUID().toString(), makeId, model))
        }
    }

    MaintenanceCategories.categories.forEach { category ->
        val categoryId = UUID.randomUUID().toString()
        db.execSQL("INSERT OR REPLACE INTO categories (id, name) VALUES (?, ?)", arrayOf(categoryId, category.name))
        category.types.forEach { type ->
            db.execSQL("INSERT OR REPLACE INTO work_types (id, categoryId, name, requiresParts) VALUES (?, ?, ?, ?)", arrayOf(UUID.randomUUID().toString(), categoryId, type.name, if (type.requiresParts) 1 else 0))
        }
    }
}
