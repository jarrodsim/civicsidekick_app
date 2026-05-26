package com.civicsidekick.app.data.api

import com.civicsidekick.app.data.model.GoogleCivicResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Google Civic Information API.
 * Provides local officials (mayors, city councils) when given a full address.
 * Free tier: 2500 requests/day, 1 request/second.
 */
interface GoogleCivicApi {

    @GET("civicinfo/v2/representatives")
    suspend fun getRepresentatives(
        @Query("address") address: String,
        @Query("includeOffices") includeOffices: Boolean = true,
        @Query("levels") levels: String = "locality,regional,administrativeArea1",
        @Query("key") apiKey: String = "",
        @Query("fields") fields: String = "normalizedInput,offices,officials"
    ): GoogleCivicResponse
}
