package com.civicsidekick.app.data.api

import com.civicsidekick.app.data.model.WikipediaSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApi {

    @GET("w/api.php")
    suspend fun searchPerson(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") search: String,
        @Query("format") format: String = "json",
        @Query("origin") origin: String = "*",
        @Query("srlimit") srLimit: Int = 1
    ): WikipediaSearchResponse

    @GET("w/api.php")
    suspend fun getPageDetails(
        @Query("action") action: String = "query",
        @Query("titles") titles: String,
        @Query("prop") prop: String = "pageimages|extracts",
        @Query("format") format: String = "json",
        @Query("origin") origin: String = "*",
        @Query("pithumbsize") piThumbSize: Int = 300,
        @Query("exintro") exIntro: Boolean = true,
        @Query("explaintext") explainText: Boolean = true,
        @Query("exsentences") exSentences: Int = 3
    ): WikipediaSearchResponse
}
