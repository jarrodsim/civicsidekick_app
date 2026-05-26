package com.civicsidekick.app.data

import com.civicsidekick.app.data.api.ApiClients
import com.civicsidekick.app.data.model.Bill

/**
 * Repository that fetches federal bills from GovTrack.us.
 */
object BillsRepository {

    private val topicKeywords = mapOf(
        "education" to "Education",
        "health" to "Healthcare",
        "tax" to "Tax Reform",
        "climate" to "Climate",
        "immigration" to "Immigration",
        "veteran" to "Veterans",
        "technology" to "Technology",
        "agriculture" to "Agriculture",
        "transportation" to "Transportation",
        "housing" to "Housing",
        "defense" to "Defense",
        "energy" to "Energy",
        "crime" to "Criminal Justice",
        "budget" to "Budget",
        "economy" to "Economy"
    )

    suspend fun fetchBills(): Result<List<Bill>> {
        return try {
            val response = ApiClients.govTrackApi.getBills()
            val bills = response.objects.mapNotNull { bill ->
                val title = bill.titleWithoutNumber ?: bill.title ?: return@mapNotNull null
                val titleLower = title.lowercase()

                val topic = topicKeywords.entries.firstOrNull { (keyword, _) ->
                    titleLower.contains(keyword)
                }?.value ?: "General"

                val billType = bill.billTypeLabel
                    ?: if (bill.billType?.contains("senate") == true) "S." else "H.R."

                Bill(
                    id = bill.displayNumber ?: "$billType ${bill.number ?: ""}",
                    title = title,
                    summary = if (title.length > 300) title.take(300) + "..." else title,
                    sponsor = bill.sponsor?.name ?: "Unknown",
                    status = bill.currentStatusLabel ?: "Introduced",
                    introduced = bill.introducedDate ?: "",
                    topic = topic,
                    congressDotGovUrl = bill.link ?: "",
                    originChamber = if (bill.billType?.contains("senate") == true) "Senate" else "House"
                )
            }

            Result.success(bills)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
