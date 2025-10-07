package com.vanaspati.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "recent_search")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val query: String,
    val timestamp: Long
)

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_search ORDER BY timestamp DESC LIMIT 5")
    fun observeRecent(): Flow<List<RecentSearch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentSearch)

    @Query("DELETE FROM recent_search WHERE id NOT IN (SELECT id FROM recent_search ORDER BY timestamp DESC LIMIT 5)")
    suspend fun trim()
}

@Database(entities = [RecentSearch::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}
