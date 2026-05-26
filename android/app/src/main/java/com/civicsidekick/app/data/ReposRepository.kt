package com.civicsidekick.app.data

import com.civicsidekick.app.data.api.ApiClients
import com.civicsidekick.app.data.model.Representative

/**
 * Repository that fetches all representatives for a given ZIP code:
 * - Governor (static lookup)
 * - 2 U.S. Senators (GovTrack) ? always shown
 * - 1 U.S. Representative for your district (GovTrack) ? shown
 * - Mayor + City Council (Google Civic API) ? requires full address
 * - Link to OpenStates for state legislators (not listed inline for states with many reps)
 * - Wikipedia photos + bios for all officials
 */
object RepsRepository {

    suspend fun findReps(zip: String): Result<List<Representative>> {
        return try {
            // Step 1: ZIP ? State
            val stateCode = lookupState(zip) ?: return Result.failure(Exception("Invalid ZIP code"))

            // Step 2: Fetch federal + governor in parallel
            val federalReps = fetchFederalReps(stateCode)
            val governor = GovernorData.getGovernor(stateCode)

            val allReps = mutableListOf<Representative>()

            // Governor first (state-level executive)
            governor?.let { gov ->
                allReps.add(
                    Representative(
                        name = gov.name,
                        party = gov.party,
                        state = stateCode,
                        district = "",
                        chamber = "Governor",
                        level = "state",
                        phone = "",
                        website = "",
                        email = "",
                        officeName = "Governor of ",
                        officeAddress = "",
                        photoUrl = "",
                        imageUrl = "",
                        bio = "",
                        title = "Governor"
                    )
                )
            }

            // Add all federal reps (this includes 2 senators + all representatives)
            allReps.addAll(federalReps)

            // Step 3: Add OpenStates link entry (instead of listing every state rep)
            allReps.add(createOpenStatesLink(stateCode))

            if (allReps.size <= 1) {
                return Result.failure(Exception("No representatives found"))
            }

            // Step 4: Enrich with Wikipedia photos and bios
            val enriched = enrichWithWikipedia(allReps)

            Result.success(enriched)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch local officials from Google Civic API using full address.
     * Call this separately when a user provides their street address.
     */
    suspend fun findLocalOfficials(streetAddress: String): Result<List<Representative>> {
        return try {
            val response = ApiClients.googleCivicApi.getRepresentatives(
                address = streetAddress
            )

            val officials = response.officials ?: emptyList()
            val offices = response.offices ?: emptyList()

            val localReps = mutableListOf<Representative>()

            // Map officials to their offices
            offices.forEach { office ->
                val level = office.levels?.firstOrNull() ?: "locality"
                val officeName = office.name ?: "Local Office"

                (office.officialIndices ?: emptyList()).forEach { index ->
                    if (index < officials.size) {
                        val official = officials[index]
                        val party = when (official.party?.lowercase()) {
                            "democratic party", "democratic" -> "Democratic"
                            "republican party", "republican" -> "Republican"
                            else -> official.party ?: "Nonpartisan"
                        }

                        val phone = official.phones?.firstOrNull() ?: ""
                        val website = official.urls?.firstOrNull() ?: ""
                        val email = official.emails?.firstOrNull() ?: ""
                        val address = official.address?.firstOrNull()
                        val addressStr = if (address != null) {
                            ", ,  "
                        } else ""

                        // Determine chamber based on office level
                        val chamber = when {
                            level == "locality" && officeName.contains("Mayor", true) -> "Mayor"
                            level == "locality" && officeName.contains("Council", true) -> "City Council"
                            level == "locality" && officeName.contains("Commission", true) -> "City Commission"
                            level == "locality" && officeName.contains("School", true) -> "School Board"
                            level == "regional" && officeName.contains("County", true) -> "County"
                            level == "regional" && officeName.contains("Sheriff", true) -> "Sheriff"
                            level == "regional" && officeName.contains("Judge", true) -> "Judge"
                            level == "administrativeArea1" -> "State Office"
                            else -> "Local Office"
                        }

                        localReps.add(
                            Representative(
                                name = official.name ?: "Unknown",
                                party = party,
                                state = response.normalizedInput?.state ?: "",
                                district = "",
                                chamber = chamber,
                                level = "local",
                                phone = phone,
                                website = website,
                                email = email,
                                officeName = officeName,
                                officeAddress = addressStr,
                                photoUrl = official.photoUrl ?: "",
                                imageUrl = official.photoUrl ?: "",
                                bio = "",
                                title = officeName,
                                localRole = chamber
                            )
                        )
                    }
                }
            }

            // Enrich local officials with Wikipedia too
            val enriched = enrichWithWikipedia(localReps)
            Result.success(enriched)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun lookupState(zip: String): String? {
        return try {
            val response = ApiClients.zippopotamApi.lookupZip(zip)
            response.places?.firstOrNull()?.stateAbbreviation
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchFederalReps(stateCode: String): List<Representative> {
        return try {
            val response = ApiClients.govTrackApi.getRoles(state = stateCode)
            response.objects.filter { it.person != null }.map { role ->
                val chamber = when (role.roleType) {
                    "senator" -> "U.S. Senate"
                    "representative" -> "U.S. House"
                    else -> "Government"
                }
                val party = when (role.party?.lowercase()) {
                    "democrat" -> "Democratic"
                    "republican" -> "Republican"
                    else -> role.party ?: "Unknown"
                }
                val fullName = role.person?.name
                    ?: " ".trim()
                val district = if (role.district != null && role.district.toIntOrNull() ?: 0 > 0) {
                    role.district
                } else if (role.roleType == "representative") {
                    "At Large"
                } else {
                    // Senators don't have districts, mark as Senior/Junior
                    ""
                }

                val fullTitle = role.titleLong ?: role.description ?: ""

                Representative(
                    name = fullName,
                    party = party,
                    state = stateCode,
                    district = district,
                    chamber = chamber,
                    level = "federal",
                    phone = role.phone ?: "",
                    website = role.website ?: "",
                    email = "",
                    officeName = fullTitle,
                    officeAddress = role.extra?.office ?: "",
                    photoUrl = "",
                    imageUrl = "",
                    bio = "",
                    title = fullTitle,
                    govtrackUrl = role.person?.link ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Creates a link-out entry for state legislators.
     * Instead of listing dozens of state reps, we link to OpenStates.
     */
    private fun createOpenStatesLink(stateCode: String): Representative {
        return Representative(
            name = "State Legislators",
            party = "",
            state = stateCode,
            district = "",
            chamber = "State Legislature",
            level = "state",
            phone = "",
            website = "https://openstates.org/",
            email = "",
            officeName = "State Senators & Assembly Members",
            officeAddress = "",
            photoUrl = "",
            imageUrl = "",
            bio = "View all state-level representatives for  on OpenStates.org",
            title = "State Legislators",
            openstatesUrl = "https://openstates.org/"
        )
    }

    private suspend fun enrichWithWikipedia(reps: List<Representative>): List<Representative> {
        return reps.mapIndexed { index, rep ->
            // Skip the OpenStates link entry (not a real person)
            if (rep.name == "State Legislators") return@mapIndexed rep

            try {
                // Build a better search query
                val searchName = when {
                    rep.chamber.contains("Senate", true) && rep.level == "federal" ->
                        "U.S. Senator "
                    rep.chamber.contains("House", true) && rep.level == "federal" ->
                        " U.S. House of Representatives"
                    rep.chamber == "Governor" ->
                        "Governor "
                    rep.name.contains(" ") -> {
                        // Try with state context
                        "  politician"
                    }
                    else -> " politician"
                }

                // Search Wikipedia
                val searchResponse = ApiClients.wikipediaApi.searchPerson(
                    search = searchName
                )
                val searchResult = searchResponse.query?.search?.firstOrNull()
                    ?: return@mapIndexed rep  // Skip if no Wikipedia page found

                val pageTitle = searchResult.title

                // Get page details (thumbnail + bio extract)
                val pageResponse = ApiClients.wikipediaApi.getPageDetails(titles = pageTitle)
                val page = pageResponse.query?.pages?.values?.firstOrNull()
                    ?: return@mapIndexed rep

                rep.copy(
                    imageUrl = page.thumbnail?.source ?: rep.photoUrl,
                    photoUrl = page.thumbnail?.source ?: rep.photoUrl,
                    bio = page.extract ?: rep.bio,
                    wikipediaTitle = pageTitle
                )
            } catch (e: Exception) {
                // Wikipedia failed silently ? return rep without enrichment
                rep
            }
        }
    }
}
