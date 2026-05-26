package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicsidekick.app.ui.theme.*

data class CivicEvent(
    val month: String,
    val day: String,
    val title: String,
    val description: String,
    val time: String
)

private val sampleEvents = listOf(
    CivicEvent("MAR", "15", "House Budget Committee Hearing", "FY2026 Budget Resolution markup", "10:00 AM EST"),
    CivicEvent("MAR", "22", "Senate Judiciary Committee", "Nomination hearing for Circuit Court", "2:00 PM EST"),
    CivicEvent("APR", "5", "Education & Labor Subcommittee", "Hearing on higher education reform", "9:30 AM EST"),
    CivicEvent("APR", "12", "House Floor Vote", "HR-142: Climate Resilience Act", "TBD")
)

@Composable
fun EventsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Calendar sync card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Tertiary
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "\uD83D\uDCC5 Stay Informed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Upcoming committee hearings, floor votes, and civic events.",
                        fontSize = 13.sp,
                        color = SurfaceWhite.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Secondary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Calendar sync ready", fontSize = 12.sp, color = SurfaceWhite.copy(alpha = 0.7f))
                    }
                }
            }
        }

        // Events
        items(sampleEvents) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Primary
                    ) {
                        Column(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                event.month,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SurfaceWhite,
                                lineHeight = 12.sp
                            )
                            Text(
                                event.day,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.title, fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 14.sp)
                        Text(event.description, color = NeutralLighter, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = NeutralLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(event.time, color = NeutralLight, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { /* Add to calendar */ },
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Text("Add to Calendar", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Sync button
        item {
            OutlinedButton(
                onClick = { /* Sync with Google Calendar */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sync with Google Calendar", fontWeight = FontWeight.SemiBold)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
