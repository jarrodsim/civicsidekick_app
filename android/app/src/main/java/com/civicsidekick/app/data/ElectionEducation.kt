package com.civicsidekick.app.data

import com.civicsidekick.app.data.model.Representative

/**
 * Election Education module — explains what each elected office does
 * and how it affects the user's quality of life.
 */
object ElectionEducation {

    val federal = EducationLevel(
        title = "\uD83C\uDFDB\uFE0F Federal Positions",
        subtitle = "Represent you at the national level in Washington, D.C.",
        total = "542 federal offices",
        positions = listOf(
            OfficeEducation(
                title = "President & Vice President",
                level = "federal",
                howElected = "Elected every 4 years by the Electoral College, based on your state's popular vote.",
                whatTheyDo = "Sets national policy direction, signs or vetoes laws, oversees federal agencies (healthcare, education, environment, defense), appoints federal judges and Supreme Court justices, conducts foreign policy.",
                affectsYourLife = "Your healthcare costs, student loan policies, tax rates, environmental regulations, national security, and Supreme Court decisions.",
                searchable = true,
                source = "GovTrack API"
            ),
            OfficeEducation(
                title = "U.S. Senate",
                level = "federal",
                chamber = "U.S. Senate",
                howElected = "Two per state (100 total). Six-year terms, with ~1/3 of seats up every 2 years.",
                whatTheyDo = "Confirms federal judges and Supreme Court justices, approves treaties, confirms Cabinet secretaries, passes federal legislation.",
                affectsYourLife = "Confirms judges who rule on your rights, approves officials running federal agencies (FDA, EPA, Education), passes laws on healthcare, immigration, and taxes.",
                searchable = true,
                source = "GovTrack API (always in ZIP search)"
            ),
            OfficeEducation(
                title = "U.S. House of Representatives",
                level = "federal",
                chamber = "U.S. House",
                howElected = "435 voting members. Number per state based on population. All seats up every 2 years.",
                whatTheyDo = "Originates all revenue/tax bills, passes federal legislation, can impeach federal officials, represents local district interests.",
                affectsYourLife = "Determines your federal income tax rates, funds local programs (roads, schools, infrastructure), passes laws on healthcare, minimum wage, consumer protections.",
                searchable = true,
                source = "GovTrack API (always in ZIP search)"
            )
        ),
        summaryNote = "Federal officials are reliably found via GovTrack API for any valid ZIP code."
    )

    val state = EducationLevel(
        title = "\uD83C\uDFDB\uFE0F State Positions",
        subtitle = "State governments have significant power over daily life.",
        positions = listOf(
            OfficeEducation(
                title = "Governor & Lt. Governor",
                level = "state",
                chamber = "Governor",
                howElected = "Elected every 4 years (most states). Head of the state\'s executive branch.",
                whatTheyDo = "Signs or vetoes state laws, manages state budget, oversees state agencies (education, transportation, health), appoints state judges, commands National Guard.",
                affectsYourLife = "Your state income/sales taxes, quality of public schools, road conditions, state health insurance rules, emergency responses, public safety.",
                searchable = true,
                source = "Static lookup (all 50 states + DC)"
            ),
            OfficeEducation(
                title = "State Executive Officials",
                level = "state",
                chamber = "State Executive",
                howElected = "Varies by state — typically elected every 4 years.",
                whatTheyDo = mapOf(
                    "Attorney General" to "Chief legal officer — enforces state laws, consumer protections, lawsuits against corporations.",
                    "Secretary of State" to "Oversees elections, business registrations, voter registration.",
                    "State Treasurer" to "Manages state investments, unclaimed property, college savings plans.",
                    "Superintendent of Public Instruction" to "Oversees public K-12 education standards, funding, and curriculum."
                ),
                affectsYourLife = "Attorney General: protects you from scams and price gouging. Secretary of State: ensures your vote counts. Treasurer: manages state pensions. Education head: sets your kids\' learning standards.",
                searchable = "partial",
                source = "Google Civic API (requires full address)",
                limitationNote = "These officials appear inconsistently across APIs. Shown when available via Google Civic with your full address."
            ),
            OfficeEducation(
                title = "State Legislature",
                level = "state",
                chamber = "State Legislature",
                howElected = "You vote for your State Senate and State House/Assembly representatives.",
                whatTheyDo = "Pass state laws on education, healthcare, transportation, criminal justice, taxation. Approve state budget. Override governor vetoes. Confirm state officials.",
                affectsYourLife = "Sets your state income tax rate, funds local schools and roads, determines criminal laws, regulates rent/housing, sets minimum wage, defines voting districts.",
                searchable = "limited",
                source = "OpenStates API (fetches ~20 per state)",
                limitationNote = "Some states have 100+ legislators. We fetch ~20 and link to OpenStates.org for the rest."
            ),
            OfficeEducation(
                title = "State Judicial Seats",
                level = "state",
                chamber = "State Judiciary",
                howElected = "Varies by state: partisan elections, nonpartisan elections, retention elections, or appointment.",
                whatTheyDo = mapOf(
                    "Partisan Elections" to "Judges appear with party affiliation (e.g., AL, TX, PA).",
                    "Nonpartisan Elections" to "Judges without party label (e.g., MI, WA, OH).",
                    "Retention Elections" to "Voters decide if appointed judges stay (e.g., IA, KS, CO).",
                    "Appointed" to "Some states appoint judges without public vote (e.g., MA, NH)."
                ),
                affectsYourLife = "State judges rule on family law (divorce, custody), criminal trials, landlord-tenant disputes, personal injury lawsuits, and state constitutional rights.",
                searchable = false,
                source = "Not available via current APIs",
                limitationNote = "State judicial elections are not covered by any free API we use. Check Ballotpedia.org or your state\'s election website."
            )
        ),
        summaryNote = "Governors are always found. State legislators partially found (~20 per query). Executive officials and judges need a full address."
    )

    val local = EducationLevel(
        title = "\uD83C\uDFD8\uFE0F Local Positions",
        subtitle = "Local officials have the most direct impact on your community\'s daily services.",
        positions = listOf(
            OfficeEducation(
                title = "County-Level Offices",
                level = "local",
                chamber = "County",
                whatTheyDo = mapOf(
                    "County Commissioner / Supervisor" to "Sets county budget, property tax rates, zoning laws, oversees county services.",
                    "County Executive" to "County\'s chief administrator — manages departments and budget.",
                    "County Clerk" to "Manages records, administers elections.",
                    "Sheriff" to "Chief law enforcement for unincorporated areas, runs county jails.",
                    "District Attorney (DA)" to "Prosecutes criminal cases on behalf of the state/county.",
                    "County Judge" to "Presides over county court cases.",
                    "County Treasurer / Assessor" to "Collects property taxes, assesses property values."
                ),
                affectsYourLife = "Property tax rates affect housing costs. Sheriff/DA determine law enforcement priorities. Zoning affects your neighborhood. County budget funds parks, libraries, health services.",
                searchable = "partial",
                source = "Google Civic API (requires full address)",
                limitationNote = "County offices vary by state. Google Civic covers many but not all rural counties."
            ),
            OfficeEducation(
                title = "Municipal / City Offices",
                level = "local",
                chamber = "City",
                whatTheyDo = mapOf(
                    "Mayor" to "Chief executive — proposes budget, oversees city departments, sets policy direction.",
                    "City Council Member" to "Passes ordinances, approves budget, sets property tax rates, represents ward/district.",
                    "City Clerk" to "Manages records, elections, business licenses.",
                    "City Treasurer" to "Manages city finances and investments.",
                    "City Attorney" to "Provides legal counsel, prosecutes code violations."
                ),
                affectsYourLife = "City council decides housing policies, business regulations, public safety budgets. Mayor influences city services (trash, parks, police).",
                searchable = "partial",
                source = "Google Civic API (requires full address)",
                limitationNote = "Google Civic covers larger cities well. Small towns may have limited data."
            ),
            OfficeEducation(
                title = "Special Districts",
                level = "local",
                chamber = "Special District",
                whatTheyDo = mapOf(
                    "School Board Member" to "Sets education policy, hires/fires superintendent, approves curriculum and budgets.",
                    "Water / Utility District Board" to "Oversees water quality, rates, and infrastructure.",
                    "Fire District Board" to "Manages fire protection and emergency response.",
                    "Park District Board" to "Oversees parks, recreation programs, community centers.",
                    "Public Hospital Board" to "Governs public hospital operations and healthcare access.",
                    "Library District Board" to "Oversees public library operations and funding."
                ),
                affectsYourLife = "School board decides kids\' curriculum and school funding. Water board affects water bill and quality. Fire district determines emergency response times.",
                searchable = false,
                source = "Not available via current APIs",
                limitationNote = "Special districts vary wildly. Check your county election website for these races."
            )
        ),
        summaryNote = "We find mayor, city council, and some county officials via Google Civic with your full address. School boards and special districts often require checking your county election site."
    )

    val coverage = CoverageSummary(
        reliable = listOf(
            "U.S. Senators — always found by state",
            "U.S. Representatives — always found by ZIP",
            "Governor — always found (50 states + DC)",
            "State Legislators — partially found (up to ~20 via OpenStates)"
        ),
        partial = listOf(
            "Mayor — found with full street address",
            "City Council — found with full street address",
            "County Officials — partially found via Google Civic",
            "State Executive Officials — some coverage",
            "School Boards — some coverage in larger districts"
        ),
        notAvailable = listOf(
            "State Judges — not covered by current APIs",
            "Special District officials (water, fire, park, library boards)",
            "Precinct-level positions",
            "Local party committee members"
        ),
        fallbackMessage = "For officials not listed, visit Ballotpedia.org or your state/county election website."
    )

    fun getEducationForRep(rep: Representative): OfficeEducation? {
        return when {
            rep.chamber == "U.S. Senate" -> federal.positions[1]
            rep.chamber == "U.S. House" -> federal.positions[2]
            rep.chamber == "Governor" || rep.title == "Governor" -> state.positions[0]
            rep.chamber in listOf("State Senate", "State House", "State Legislature") -> state.positions[2]
            rep.chamber == "State Executive" -> state.positions[1]
            rep.chamber == "State Judiciary" || rep.title.contains("Judge") || rep.title.contains("Justice") -> state.positions[3]
            rep.chamber == "Mayor" || rep.title.contains("Mayor") -> local.positions[1].copy(whatTheyDo = mapOf("Mayor" to (local.positions[1].whatTheyDo as Map<*, *>)["Mayor"].toString()))
            rep.chamber in listOf("City Council", "City Commission") -> local.positions[1].copy(whatTheyDo = mapOf("City Council Member" to (local.positions[1].whatTheyDo as Map<*, *>)["City Council Member"].toString()))
            rep.chamber == "Sheriff" -> local.positions[0].copy(whatTheyDo = mapOf("Sheriff" to (local.positions[0].whatTheyDo as Map<*, *>)["Sheriff"].toString()))
            rep.chamber == "County" || rep.title.contains("County") -> local.positions[0]
            rep.chamber == "School Board" || rep.title.contains("School") -> local.positions[2].copy(whatTheyDo = mapOf("School Board Member" to (local.positions[2].whatTheyDo as Map<*, *>)["School Board Member"].toString()))
            rep.level == "local" -> local.positions[1]
            else -> null
        }
    }

    fun getRoleDescription(rep: Representative): String {
        val edu = getEducationForRep(rep) ?: return ""
        return when (val wtd = edu.whatTheyDo) {
            is String -> wtd
            is Map<*, *> -> {
                val titleKey = rep.title.ifBlank { rep.officeName }
                val chamberKey = rep.chamber
                (wtd[titleKey] as? String) ?: (wtd[chamberKey] as? String) ?: (wtd.values.firstOrNull() as? String) ?: edu.affectsYourLife
            }
            else -> edu.affectsYourLife
        }
    }

    fun getLifeImpact(rep: Representative): String {
        return getEducationForRep(rep)?.affectsYourLife ?: ""
    }
}

data class EducationLevel(
    val title: String,
    val subtitle: String,
    val total: String = "",
    val positions: List<OfficeEducation>,
    val summaryNote: String = ""
)

data class OfficeEducation(
    val title: String,
    val level: String,
    val chamber: String = "",
    val howElected: String = "",
    val whatTheyDo: Any = "",
    val affectsYourLife: String = "",
    val districtNote: String = "",
    val nextElection: String = "",
    val searchable: Any = true,
    val source: String = "",
    val limitationNote: String = ""
)

data class CoverageSummary(
    val reliable: List<String>,
    val partial: List<String>,
    val notAvailable: List<String>,
    val fallbackMessage: String
)