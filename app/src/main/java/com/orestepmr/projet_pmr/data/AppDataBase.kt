package com.orestepmr.projet_pmr.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.orestepmr.projet_pmr.models.GameEntity
import com.orestepmr.projet_pmr.models.PlayersEntity

@Database(entities = [GameEntity::class, PlayersEntity::class], version = 1, exportSchema =  true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): gameDao

}