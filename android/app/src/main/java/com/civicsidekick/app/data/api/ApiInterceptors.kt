package com.civicsidekick.app.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the OpenStates API key to requests to v3.openstates.org.
 */
class OpenStatesAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        // Only add API key to OpenStates requests
        if (url.contains("openstates.org")) {
            val newUrl = if (url.contains("?")) {
                "$url&apikey=${OpenStatesApiKey.KEY}"
            } else {
                "$url?apikey=${OpenStatesApiKey.KEY}"
            }
            val newRequest = request.newBuilder()
                .url(newUrl)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}

/**
 * Holds the OpenStates API key from BuildConfig.
 */
object OpenStatesApiKey {
    lateinit var KEY: String
}
