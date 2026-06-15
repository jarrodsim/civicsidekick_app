package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.civicsidekick.app.data.ElectionEducation
import com.civicsidekick.app.data.model.Bill
import com.civicsidekick.app.data.model.Representative
import com.civicsidekick.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    address: String,
    reps: List<Representative>,
    onRepClick: (Representative) -> Unit,
    onCallRep: (String) -> Unit,
    onOpenWebsite: (String) -> Unit,
    onOpenOpenStates: (String) -> Unit,
    onOpenWikipedia: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Greeting
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = if (reps.isNotEmpty()) "Welcome, Neighbor" else "Welcome",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = "Your elected officials, at your fingertips.",
                    fontSize = 13.sp,
                    color = NeutralLighter
                )
                if (address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .background(
                                color = Primary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(address, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Primary)
                    }
                }
            }
        }

        // Your Representatives section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Representatives",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Neutral
                )
                    Text("View all", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }

        if (reps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Enter your ZIP code to see your reps.", color = NeutralLighter)
                    }
                }
            }
        } else {
            items(reps) { rep ->
                RepCard(
                    rep = rep,
                    onClick = { onRepClick(rep) },
                    onCall = { onCallRep(rep.phone) },
                    onWebsite = { onOpenWebsite(rep.website) },
                    onOpenStates = { onOpenOpenStates(rep.openstatesUrl) },
                    onWikipedia = { onOpenWikipedia(rep.wikipediaTitle) }
                )
            }
        }

        // Recent Bills section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Bills",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Neutral
                )
                    Text("Browse all", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }

        if (
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bills loaded yet.", color = NeutralLighter)
                    }
                }
            }
        } else {
            items(
                BillMiniCard(
                    bill = bill,
                    isTracked = 
                )
            }
        }

        // Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(label = "Tracked", value = 
                StatCard(label = "Bills", value = 
                StatCard(label = "My Reps", value = reps.size.toString())
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun RepCard(
    rep: Representative,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onWebsite: () -> Unit,
    onOpenStates: () -> Unit,
    onWikipedia: () -> Unit
) {
    val partyColor = if (rep.party == "Republican") RepublicanRed else DemocratBlue
    val hasImage = rep.imageUrl.isNotBlank()
    val isStateLink = rep.name == "State Legislators"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar with Wikipedia photo
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isStateLink) Tertiary else partyColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasImage && !isStateLink) {
                        AsyncImage(
                            model = rep.imageUrl,
                            contentDescription = rep.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (isStateLink) {
                        Text(
                            "\uD83C\uDFDB",
                            fontSize = 24.sp
                        )
                    } else {
                        Text(
                            rep.name.first().uppercase(),
                            color = SurfaceWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isStateLink) {
                        Text(rep.title, fontWeight = FontWeight.SemiBold, color = Tertiary, fontSize = 15.sp)
                        Text(rep.bio, color = NeutralLighter, fontSize = 12.sp)
                    } else {
                        Text(rep.name, fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("" + rep.chamber, color = NeutralLighter, fontSize = 12.sp)
                            if (rep.party.isNotBlank() && rep.party != "Nonpartisan" && rep.party != "") {
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = partyColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        rep.party.take(4),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = partyColor
                                    )
                                }
                            }
                        }
                        val levelLabel = when (rep.level) {
                            "federal" -> "FEDERAL"
                            "state" -> "STATE"
                            "local" -> "LOCAL"
                            else -> rep.level.uppercase()
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = if (rep.level == "federal") Primary.copy(alpha = 0.08f) else if (rep.level == "local") Secondary.copy(alpha = 0.08f) else Tertiary.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    levelLabel,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (rep.level == "federal") Primary else if (rep.level == "local") Secondary else Tertiary
                                )
                            }
                            if (rep.district.isNotBlank()) {
                                Spacer(Modifier.width(4.dp))
                                Text("District " + rep.district, color = NeutralLight, fontSize = 11.sp)
                            } else if (rep.state.isNotBlank()) {
                                Spacer(Modifier.width(4.dp))
                                Text(rep.state, color = NeutralLight, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Education tooltip - What this office does
            val eduInfo = ElectionEducation.getEducationForRep(rep)
            if (eduInfo != null && !isStateLink) {
                val rolePreview = when (val wtd = eduInfo.whatTheyDo) {
                    is String -> wtd.split(".").firstOrNull() ?: ""
                    is Map<*, *> -> (wtd.values.firstOrNull() as? String)?.let { it.substring(0, minOf(it.length, 80)) } ?: ""
                    else -> ""
                }
                if (rolePreview.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = Secondary.copy(alpha = 0.06f)
                    ) {
                        Text(
                            rolePreview + if (rolePreview.length >= 80) "..." else ".",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 11.sp,
                            color = Neutral,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Bio snippet from Wikipedia
            if (rep.bio.isNotBlank() && !isStateLink) {
                Spacer(Modifier.height(6.dp))
                Text(
                    rep.bio.take(100) + if (rep.bio.length > 100) "..." else "",
                    fontSize = 12.sp,
                    color = Neutral,
                    lineHeight = 18.sp
                )
            }

            // Contact info row
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (rep.phone.isNotBlank()) {
                    OutlinedButton(
                        onClick = onCall,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("\u260E", fontSize = 11.sp)
                        Spacer(Modifier.width(3.dp))
                        Text("Call", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (rep.website.isNotBlank()) {
                    OutlinedButton(
                        onClick = onWebsite,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("\uD83C\uDF10", fontSize = 11.sp)
                        Spacer(Modifier.width(3.dp))
                        Text("Website", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (isStateLink && rep.openstatesUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = onOpenStates,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("\uD83C\uDFDB", fontSize = 11.sp)
                        Spacer(Modifier.width(3.dp))
                        Text("View All State Reps", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (rep.wikipediaTitle.isNotBlank()) {
                    OutlinedButton(
                        onClick = onWikipedia,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("W", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(3.dp))
                        Text("Wiki", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Office address (if available)
            if (rep.officeAddress.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "\u2709 " + rep.officeAddress.take(60) + if (rep.officeAddress.length > 60) "..." else "",
                        fontSize = 10.sp,
                        color = NeutralLight,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (bill.status.lowercase()) {
                            "introduced" -> NeutralLighter
                            "hearing" -> Warning
                            "passed house", "passed senate" -> Secondary
                            "enacted" -> Success
                            else -> NeutralLighter
                        }
                    )
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(bill.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralLighter)
                Text(
                    bill.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Neutral,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(bill.status, fontSize = 11.sp, color = NeutralLighter)
            }

            // Track button
            FilledTonalIconButton(
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isTracked) Secondary.copy(alpha = 0.15f) else NeutralLightest.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    if (isTracked) "\u2713" else "+",
                    color = if (isTracked) Secondary else NeutralLighter,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(label: String, value: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
            Text(label, fontSize = 11.sp, color = NeutralLighter)
        }
    }
}
