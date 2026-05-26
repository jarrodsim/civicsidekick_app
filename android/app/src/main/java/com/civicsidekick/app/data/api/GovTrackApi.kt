package com.civicsidekick.app.data.api

import com.civicsidekick.app.data.model.GovTrackBillResponse
import com.civicsidekick.app.data.model.GovTrackRoleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GovTrackApi {

    @GET("api/v2/role")
    suspend fun getRoles(
        @Query("format") format: String = "json",
        @Query("current") current: Boolean = true,
        @Query("state") state: String,
        @Query("limit") limit: Int = 60
    ): GovTrackRoleResponse

    @GET("api/v2/bill")
    suspend fun getBills(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("order_by") orderBy: String = "-current_status_date"
    ): GovTrackBillResponse
}
