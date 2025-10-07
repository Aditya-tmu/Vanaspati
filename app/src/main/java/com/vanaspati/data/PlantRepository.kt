package com.vanaspati.data

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import com.vanaspati.data.local.AppDatabase
import com.vanaspati.data.local.RecentSearch
import com.vanaspati.data.local.RecentSearchDao
import com.vanaspati.data.network.GeminiService
import com.vanaspati.data.network.RetrofitModule
import com.vanaspati.data.network.TrefleSearchResponse
import com.vanaspati.data.network.TrefleService
import kotlinx.coroutines.flow.Flow

class PlantRepository(context: Context) {
    private val geminiService: GeminiService = GeminiService()
    private val trefleService: TrefleService = RetrofitModule.trefleService

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "vanaspati.db"
    ).fallbackToDestructiveMigration().build()

    private val recentDao: RecentSearchDao = db.recentSearchDao()

    fun observeRecent(): Flow<List<RecentSearch>> = recentDao.observeRecent()

    suspend fun addRecent(query: String) {
        recentDao.insert(RecentSearch(query = query, timestamp = System.currentTimeMillis()))
        recentDao.trim()
    }

    suspend fun search(query: String, token: String): TrefleSearchResponse {
        return trefleService.search(token, query)
    }

    suspend fun getPlantDetails(prompt: String) =
        geminiService.getPlantDetails(prompt)

    suspend fun identifyPlant(image: Bitmap) =
        geminiService.identifyPlant(image)
}
