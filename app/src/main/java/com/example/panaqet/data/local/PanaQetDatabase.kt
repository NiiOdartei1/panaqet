package com.example.panaqet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.panaqet.data.local.entity.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class PanaQetDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
