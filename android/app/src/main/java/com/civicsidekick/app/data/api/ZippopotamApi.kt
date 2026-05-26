package com.civicsidekick.app.data.api

import com.civicsidekick.app.data.model.ZippopotamResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ZippopotamApi {

    @GET("us/{zip}")
    suspend fun lookupZip(@Path("zip") zip: String): ZippopotamResponse
}
