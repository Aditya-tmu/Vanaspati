
package com.vanaspati.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface TrefleService {
    @GET("plants/search")
    suspend fun search(
        @Query("token") token: String,
        @Query("q") query: String
    ): TrefleSearchResponse
}

data class TrefleSearchResponse(
    val data: List<TreflePlant>
)

data class TreflePlant(
    val id: Int,
    val common_name: String?,
    val scientific_name: String,
    val image_url: String?
)
