package com.civicsidekick.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClients {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(OpenStatesAuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val noAuthClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // ---- GovTrack API (public, no key needed) ----

    private val govTrackRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.govtrack.us/")
        .client(noAuthClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val govTrackApi: GovTrackApi = govTrackRetrofit.create(GovTrackApi::class.java)

    // ---- OpenStates API (uses API key via interceptor) ----

    private val openStatesRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://v3.openstates.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openStatesApi: OpenStatesApi = openStatesRetrofit.create(OpenStatesApi::class.java)

    // ---- Zippopotam API ----

    private val zippopotamRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.zippopotam.us/")
        .client(noAuthClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val zippopotamApi: ZippopotamApi = zippopotamRetrofit.create(ZippopotamApi::class.java)

    // ---- Wikipedia API ----

    private val wikipediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/")
        .client(noAuthClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikipediaApi: WikipediaApi = wikipediaRetrofit.create(WikipediaApi::class.java)

    // ---- Google Civic Information API ----

    private val googleCivicRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .client(noAuthClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleCivicApi: GoogleCivicApi = googleCivicRetrofit.create(GoogleCivicApi::class.java)
}