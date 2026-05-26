package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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

data class BrowseUiState(
    val bills: List<Bill> = emptyList(),
    val reps: List<Representative> = emptyList(),
    val trackedBillIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val isLoadingReps: Boolean = false,
    val isAddressSearch: Boolean = false,
    val repSearchError: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    state: BrowseUiState,
    onSearchQueryChange: (String) -> Unit,
    onPageChange: (Int) -> Unit,
    onRepZipSearch: (String) -> Unit,
    onRepAddressSearch: (String) -> Unit,
    onToggleTrack: (String) -> Unit,
    onBillClick: (Bill) -> Unit,
    onRepClick: (Representative) -> Unit,
    onCallRep: (String) -> Unit,
    onOpenWebsite: (String) -> Unit,
    onOpenOpenStates: (String) -> Unit,
    onOpenWikipedia: (String) -> Unit
) {
    val filteredBills = if (state.searchQuery.isBlank()) {
        state.bills
    } else {
        state.bills.filter { b ->
            b.title.lowercase().contains(state.searchQuery) ||
            b.id.lowercase().contains(state.searchQuery) ||
            b.topic.lowercase().contains(state.searchQuery)
        }
    }

    val totalPages = (filteredBills.size / 10) + 1
    val safePage = state.currentPage.coerceIn(1, totalPages.coerceAtLeast(1))
    val pageStart = (safePage - 1) * 10
    val pageBills = filteredBills.drop(pageStart).take(10)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search bills by title, ID, or topic...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    cursorColor = Primary
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bills", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Neutral)
                Text("" + filteredBills.size + " found", fontSize = 12.sp, color = NeutralLighter)
            }
        }

        // Bills
        items(pageBills) { bill ->
            BillCard(
                bill = bill,
                isTracked = state.trackedBillIds.contains(bill.id),
                onClick = { onBillClick(bill) },
                onToggleTrack = { onToggleTrack(bill.id) }
            )
        }

        // Pagination
        if (totalPages > 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..totalPages.coerceAtMost(5)) {
                        TextButton(
                            onClick = { onPageChange(i) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (i == safePage) SurfaceWhite else Neutral,
                                containerColor = if (i == safePage) Primary else SurfaceWhite
                            ),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(i.toString(), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // --- Rep Finder Section ---
        item {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = NeutralLightest)
            Spacer(Modifier.height(16.dp))
            Text("Find Your Representatives", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Neutral)
            Spacer(Modifier.height(12.dp))
        }

        // ZIP search
        item {
            var zipInput by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = zipInput,
                    onValueChange = { zipInput = it.take(10) },
                    placeholder = { Text("ZIP Code (e.g. 61822)...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        if (zipInput.matches(Regex("^\\d{5}(-\\d{4})?$"))) {
                            onRepZipSearch(zipInput.trim())
                        }
                    },
                    enabled = !state.isLoadingReps,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (state.isLoadingReps && !state.isAddressSearch) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = SurfaceWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Search ZIP")
                    }
                }
            }
        }

        // Full address search (for local officials)
        item {
            var addressInput by remember { mutableStateOf("") }
            Text(
                "Or enter your full address for local officials:",
                fontSize = 12.sp,
                color = NeutralLighter,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    placeholder = { Text("123 Main St, City, ST...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        if (addressInput.trim().length > 5) {
                            onRepAddressSearch(addressInput.trim())
                        }
                    },
                    enabled = !state.isLoadingReps,
                    colors = ButtonDefaults.buttonColors(containerColor = Tertiary)
                ) {
                    if (state.isLoadingReps && state.isAddressSearch) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = SurfaceWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Search Address")
                    }
                }
            }
        }

        // Rep results
        items(state.reps) { rep ->
            RepBrowseCard(
                rep = rep,
                onClick = { onRepClick(rep) },
                onCall = { onCallRep(rep.phone) },
                onWebsite = { onOpenWebsite(rep.website) },
                onOpenStates = { onOpenOpenStates(rep.openstatesUrl) },
                onWikipedia = { onOpenWikipedia(rep.wikipediaTitle) }
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun BillCard(
    bill: Bill,
    isTracked: Boolean,
    onClick: () -> Unit,
    onToggleTrack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "" + bill.id + ": " + bill.title,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                        fontSize = 15.sp
                    )
                    Text(
                        "Sponsor: " + bill.sponsor + " \u00b7 " + bill.topic + if (bill.originChamber.isNotBlank()) " \u00b7 " + bill.originChamber else "",
                        color = NeutralLighter,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        bill.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(bill.summary, fontSize = 13.sp, color = Neutral, lineHeight = 20.sp)

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onToggleTrack,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        if (isTracked) "Tracked" else "Track",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Details", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun RepBrowseCard(
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Text("\uD83C\uDFDB", fontSize = 24.sp)
                    } else {
                        Text(
                            rep.name.first().uppercase(),
                            color = SurfaceWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isStateLink) {
                        Text(rep.title, fontWeight = FontWeight.SemiBold, color = Tertiary, fontSize = 15.sp)
                        Text(rep.bio, color = NeutralLighter, fontSize = 12.sp)
                    } else {
                        Text(rep.name, fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 15.sp)
                        Text("" + rep.chamber + " - " + rep.party, color = NeutralLighter, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = if (rep.level == "federal") Primary.copy(alpha = 0.08f) else if (rep.level == "local") Secondary.copy(alpha = 0.08f) else Tertiary.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    rep.level.uppercase(),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (rep.level == "federal") Primary else if (rep.level == "local") Secondary else Tertiary
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "" + rep.state + if (rep.district.isNotBlank()) " - District " + rep.district else "",
                                color = NeutralLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Education tooltip
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

            if (rep.bio.isNotBlank() && !isStateLink) {
                Spacer(Modifier.height(6.dp))
                Text(rep.bio.take(120) + if (rep.bio.length > 120) "..." else "", fontSize = 12.sp, color = Neutral, lineHeight = 18.sp)
            }

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

            if (rep.officeAddress.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
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