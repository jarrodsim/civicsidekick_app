package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicsidekick.app.data.ElectionEducation
import com.civicsidekick.app.data.EducationLevel
import com.civicsidekick.app.data.OfficeEducation
import com.civicsidekick.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionsScreen(
    onNavigateSettings: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Know Your Elections",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            "Learn about every office you vote for \u2014 what they do and how they affect your daily life.",
            fontSize = 14.sp,
            color = NeutralLighter,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Federal
        LevelSection(
            level = ElectionEducation.federal,
            defaultExpanded = true
        )

        Spacer(Modifier.height(8.dp))

        // State
        LevelSection(level = ElectionEducation.state)

        Spacer(Modifier.height(8.dp))

        // Local
        LevelSection(level = ElectionEducation.local)

        Spacer(Modifier.height(16.dp))

        // Coverage section
        CoverageSection()

        Spacer(Modifier.height(16.dp))

        // Call to action
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Primary.copy(alpha = 0.04f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Ready to find your specific representatives?",
                    fontSize = 14.sp,
                    color = Neutral,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onNavigateSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Enter Your Address", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LevelSection(level: EducationLevel, defaultExpanded: Boolean = false) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(level.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Primary)
                    Text(level.subtitle, fontSize = 13.sp, color = NeutralLighter)
                    if (level.total.isNotBlank()) {
                        Text(level.total, fontSize = 12.sp, color = NeutralLight)
                    }
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = NeutralLighter
                )
            }
        }

        // Expanded content
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            level.positions.forEach { position ->
                PositionCard(position = position)
                Spacer(Modifier.height(6.dp))
            }

            if (level.summaryNote.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Primary.copy(alpha = 0.04f)
                ) {
                    Text(
                        level.summaryNote,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = NeutralLight,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PositionCard(position: OfficeEducation) {
    var expanded by remember { mutableStateOf(false) }

    val searchIcon = when (position.searchable) {
        true -> Icons.Default.CheckCircle
        false -> Icons.Default.Cancel
        else -> Icons.Default.Info
    }
    val searchColor = when (position.searchable) {
        true -> Secondary
        false -> NeutralLighter
        else -> Tertiary
    }
    val searchLabel = when (position.searchable) {
        true -> "Always in results"
        false -> "Not available"
        else -> "Partial coverage"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Header (clickable)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                color = NeutralLightest
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        searchIcon,
                        contentDescription = null,
                        tint = searchColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(position.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Primary)
                        Text(searchLabel, fontSize = 11.sp, color = searchColor, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = NeutralLighter,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded body
            if (expanded) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // How elected
                    if (position.howElected.isNotBlank()) {
                        Text("How elected:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Neutral)
                        Text(position.howElected, fontSize = 12.sp, color = NeutralLighter, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // What they do
                    Text("What they do:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Neutral)
                    when (val wtd = position.whatTheyDo) {
                        is String -> Text(wtd, fontSize = 12.sp, color = Neutral, lineHeight = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                        is Map<*, *> -> {
                            wtd.forEach { (role, desc) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(40.dp)
                                            .background(NeutralLightest)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(role.toString(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Primary)
                                        Text(desc.toString(), fontSize = 12.sp, color = Neutral, lineHeight = 16.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }

                    // Life impact
                    if (position.affectsYourLife.isNotBlank()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Secondary.copy(alpha = 0.06f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(60.dp)
                                        .background(Secondary)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "How this affects you:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )
                                    Text(
                                        position.affectsYourLife,
                                        fontSize = 12.sp,
                                        color = Neutral,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Additional info
                    if (position.districtNote.isNotBlank()) {
                        Text(position.districtNote, fontSize = 11.sp, color = NeutralLight, modifier = Modifier.padding(top = 4.dp))
                    }
                    if (position.nextElection.isNotBlank()) {
                        Text("Next election: " + position.nextElection, fontSize = 11.sp, color = Tertiary, modifier = Modifier.padding(top = 2.dp))
                    }
                    if (position.limitationNote.isNotBlank()) {
                        HorizontalDivider(color = NeutralLightest, modifier = Modifier.padding(vertical = 6.dp))
                        Text(position.limitationNote, fontSize = 11.sp, color = NeutralLighter, lineHeight = 16.sp)
                    }
                    if (position.source.isNotBlank()) {
                        Text("Source: " + position.source, fontSize = 10.sp, color = NeutralLighter, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CoverageSection() {
    var expanded by remember { mutableStateOf(false) }
    val cov = ElectionEducation.coverage

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("What We Can & Cannot Find", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Primary)
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = NeutralLighter
                )
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))

                // Reliable
                CoverageCategory(
                    title = "Reliably found:",
                    color = Secondary,
                    icon = Icons.Default.CheckCircle,
                    items = cov.reliable
                )
                Spacer(Modifier.height(8.dp))

                // Partial
                CoverageCategory(
                    title = "Partially found (try full address):",
                    color = Tertiary,
                    icon = Icons.Default.Info,
                    items = cov.partial
                )
                Spacer(Modifier.height(8.dp))

                // Unavailable
                CoverageCategory(
                    title = "Not available via our APIs:",
                    color = NeutralLighter,
                    icon = Icons.Default.Cancel,
                    items = cov.notAvailable
                )
                Spacer(Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Primary.copy(alpha = 0.04f)
                ) {
                    Text(
                        cov.fallbackMessage,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = NeutralLight,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CoverageCategory(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>
) {
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = color)
    Spacer(Modifier.height(4.dp))
    items.forEach { item ->
        Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(item, fontSize = 12.sp, color = Neutral, lineHeight = 16.sp)
        }
    }
}