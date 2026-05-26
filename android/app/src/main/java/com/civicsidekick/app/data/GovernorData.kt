package com.civicsidekick.app.data

import com.civicsidekick.app.data.model.Governor

object GovernorData {

    private val governors = mapOf(
        "AL" to Governor("Kay Ivey", "Republican"),
        "AK" to Governor("Mike Dunleavy", "Republican"),
        "AZ" to Governor("Katie Hobbs", "Democratic"),
        "AR" to Governor("Sarah Huckabee Sanders", "Republican"),
        "CA" to Governor("Gavin Newsom", "Democratic"),
        "CO" to Governor("Jared Polis", "Democratic"),
        "CT" to Governor("Ned Lamont", "Democratic"),
        "DE" to Governor("Matt Meyer", "Democratic"),
        "FL" to Governor("Ron DeSantis", "Republican"),
        "GA" to Governor("Brian Kemp", "Republican"),
        "HI" to Governor("Josh Green", "Democratic"),
        "ID" to Governor("Brad Little", "Republican"),
        "IL" to Governor("JB Pritzker", "Democratic"),
        "IN" to Governor("Mike Braun", "Republican"),
        "IA" to Governor("Kim Reynolds", "Republican"),
        "KS" to Governor("Laura Kelly", "Democratic"),
        "KY" to Governor("Andy Beshear", "Democratic"),
        "LA" to Governor("Jeff Landry", "Republican"),
        "ME" to Governor("Janet Mills", "Democratic"),
        "MD" to Governor("Wes Moore", "Democratic"),
        "MA" to Governor("Maura Healey", "Democratic"),
        "MI" to Governor("Gretchen Whitmer", "Democratic"),
        "MN" to Governor("Tim Walz", "Democratic"),
        "MS" to Governor("Tate Reeves", "Republican"),
        "MO" to Governor("Mike Kehoe", "Republican"),
        "MT" to Governor("Greg Gianforte", "Republican"),
        "NE" to Governor("Jim Pillen", "Republican"),
        "NV" to Governor("Joe Lombardo", "Republican"),
        "NH" to Governor("Kelly Ayotte", "Republican"),
        "NJ" to Governor("Phil Murphy", "Democratic"),
        "NM" to Governor("Michelle Lujan Grisham", "Democratic"),
        "NY" to Governor("Kathy Hochul", "Democratic"),
        "NC" to Governor("Josh Stein", "Democratic"),
        "ND" to Governor("Kelly Armstrong", "Republican"),
        "OH" to Governor("Mike DeWine", "Republican"),
        "OK" to Governor("Kevin Stitt", "Republican"),
        "OR" to Governor("Tina Kotek", "Democratic"),
        "PA" to Governor("Josh Shapiro", "Democratic"),
        "RI" to Governor("Dan McKee", "Democratic"),
        "SC" to Governor("Henry McMaster", "Republican"),
        "SD" to Governor("Larry Rhoden", "Republican"),
        "TN" to Governor("Bill Lee", "Republican"),
        "TX" to Governor("Greg Abbott", "Republican"),
        "UT" to Governor("Spencer Cox", "Republican"),
        "VT" to Governor("Phil Scott", "Republican"),
        "VA" to Governor("Glenn Youngkin", "Republican"),
        "WA" to Governor("Bob Ferguson", "Democratic"),
        "WV" to Governor("Patrick Morrisey", "Republican"),
        "WI" to Governor("Tony Evers", "Democratic"),
        "WY" to Governor("Mark Gordon", "Republican"),
        "DC" to Governor("Muriel Bowser", "Democratic")
    )

    fun getGovernor(stateCode: String): Governor? = governors[stateCode]
}
