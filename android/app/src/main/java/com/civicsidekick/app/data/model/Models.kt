package com.civicsidekick.app.data.model

import com.google.gson.annotations.SerializedName

// ---- Representative ----

data class Representative(
    val name: String,
    val party: String,
    val state: String,
    val district: String,
    val chamber: String,
    val level: String,
    val phone: String,
    val website: String,
    val email: String = "",
    val officeName: String,
    val officeAddress: String,
    val photoUrl: String,
    val imageUrl: String,
    val bio: String,
    val title: String,
    val govtrackUrl: String = "",
    val openstatesUrl: String = "",
    val wikipediaTitle: String = "",
    val localRole: String = ""
)

// ---- Bill ----

data class Bill(
    val id: String,
    val title: String,
    val summary: String,
    val sponsor: String,
    val status: String,
    val introduced: String,
    val topic: String,
    val congressDotGovUrl: String = "",
    val originChamber: String = ""
)

// ---- Room entity for tracked bills ----

data class TrackedBill(
    val billId: String
)

// ---- Governor Lookup ----

data class Governor(
    val name: String,
    val party: String
)

// ---- API Response Models (GovTrack) ----

data class GovTrackRoleResponse(
    val objects: List<GovTrackRole>
)

data class GovTrackRole(
    val person: GovTrackPerson?,
    @SerializedName("role_type") val roleType: String?,
    val party: String?,
    val district: String?,
    val phone: String?,
    val website: String?,
    @SerializedName("title_long") val titleLong: String?,
    val description: String?,
    val extra: GovTrackExtra?
)

data class GovTrackPerson(
    val firstname: String?,
    val lastname: String?,
    val name: String?,
    val link: String?
)

data class GovTrackExtra(
    val office: String?
)

data class GovTrackBillResponse(
    val objects: List<GovTrackBill>
)

data class GovTrackBill(
    @SerializedName("display_number") val displayNumber: String?,
    val title: String?,
    @SerializedName("title_without_number") val titleWithoutNumber: String?,
    val number: String?,
    @SerializedName("bill_type") val billType: String?,
    @SerializedName("bill_type_label") val billTypeLabel: String?,
    @SerializedName("current_status_label") val currentStatusLabel: String?,
    @SerializedName("introduced_date") val introducedDate: String?,
    val sponsor: GovTrackSponsor?,
    val link: String?
)

data class GovTrackSponsor(
    val name: String?
)

// ---- OpenStates API Response ----

data class OpenStatesResponse(
    val results: List<OpenStatesPerson>
)

data class OpenStatesPerson(
    val name: String,
    val party: String?,
    val image: String?,
    @SerializedName("openstates_url") val openstatesUrl: String?,
    @SerializedName("current_role") val currentRole: OpenStatesRole?
)

data class OpenStatesRole(
    val title: String?,
    @SerializedName("org_classification") val orgClassification: String?,
    val district: String?
)

// ---- Zippopotam API Response ----

data class ZippopotamResponse(
    val places: List<ZippopotamPlace>?
)

data class ZippopotamPlace(
    @SerializedName("state abbreviation") val stateAbbreviation: String?
)

// ---- Wikipedia API Response ----

data class WikipediaSearchResponse(
    val query: WikipediaQuery?
)

data class WikipediaQuery(
    val search: List<WikipediaSearchResult>?,
    val pages: Map<String, WikipediaPage>?
)

data class WikipediaSearchResult(
    val title: String
)

data class WikipediaPage(
    val thumbnail: WikipediaThumbnail?,
    val extract: String?
)

data class WikipediaThumbnail(
    val source: String?
)

// ---- Google Civic Information API ----

data class GoogleCivicResponse(
    val normalizedInput: GoogleCivicAddress? = null,
    val offices: List<GoogleCivicOffice>? = null,
    val officials: List<GoogleCivicOfficial>? = null
)

data class GoogleCivicAddress(
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null
)

data class GoogleCivicOffice(
    val name: String? = null,
    val officialIndices: List<Int>? = null,
    val levels: List<String>? = null,
    val roles: List<String>? = null,
    val divisionId: String? = null
)

data class GoogleCivicOfficial(
    val name: String? = null,
    val party: String? = null,
    val phones: List<String>? = null,
    val urls: List<String>? = null,
    val photoUrl: String? = null,
    val emails: List<String>? = null,
    val address: List<GoogleCivicOfficialAddress>? = null
)

data class GoogleCivicOfficialAddress(
    val line1: String? = null,
    val line2: String? = null,
    val line3: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null
)

