package com.civicsidekick.app.data.api

import com.civicsidekick.app.data.model.OpenStatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStatesApi {

    @GET("people")
    suspend fun getPeople(
        @Query("jurisdiction") jurisdiction: String,
        @Query("page") page: Int = 1
    ): OpenStatesResponse
}
