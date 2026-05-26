package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicsidekick.app.ui.theme.*

@Composable
fun SettingsScreen(
    address: String,
    onSaveZip: (String) -> Unit,
    onSaveFullAddress: (String) -> Unit,
    isLoading: Boolean
) {
    var zipInput by remember { mutableStateOf("") }
    var fullAddressInput by remember { mutableStateOf("") }
    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(false) }
    var educationEnabled by remember { mutableStateOf(true) }
    var healthcareEnabled by remember { mutableStateOf(true) }
    var climateEnabled by remember { mutableStateOf(true) }
    var taxEnabled by remember { mutableStateOf(false) }
    var immigrationEnabled by remember { mutableStateOf(false) }
    var veteransEnabled by remember { mutableStateOf(false) }
    var showLocalSearchFields by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Location section
        item {
            Text(
                "LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralLighter,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // ZIP code
                    Text("ZIP Code (for federal & state reps)", fontSize = 12.sp, color = NeutralLighter)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = zipInput,
                            onValueChange = { zipInput = it.take(10) },
                            placeholder = { Text("e.g. 61822") },
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
                                    onSaveZip(zipInput.trim())
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = SurfaceWhite,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Find Reps")
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Full address for local officials
                    TextButton(
                        onClick = { showLocalSearchFields = !showLocalSearchFields }
                    ) {
                        Text(
                            if (showLocalSearchFields) "Hide local official search" else "Search for local officials (mayor, city council...)",
                            fontSize = 12.sp,
                            color = Tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (showLocalSearchFields) {
                        Spacer(Modifier.height(4.dp))
                        Text("Full Street Address (for mayor, city council, etc.)", fontSize = 12.sp, color = NeutralLighter)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = fullAddressInput,
                                onValueChange = { fullAddressInput = it },
                                placeholder = { Text("123 Main St, City, ST ZIP...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Tertiary,
                                    cursorColor = Tertiary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Button(
                                onClick = {
                                    if (fullAddressInput.trim().length > 5) {
                                        onSaveFullAddress(fullAddressInput.trim())
                                    }
                                },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = Tertiary)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = SurfaceWhite,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Find Local")
                                }
                            }
                        }
                    }

                    if (address.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(address, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Primary)
                        }
                    }
                }
            }
        }

        // Notifications section
        item {
            Text(
                "NOTIFICATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralLighter,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp, 0.dp)) {
                    SettingsToggle(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = NeutralLight) },
                        label = "Push Notifications",
                        description = "Get alerts on bill updates",
                        checked = pushEnabled,
                        onToggle = { pushEnabled = !pushEnabled }
                    )
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(
                        icon = { Icon(Icons.Default.Mail, contentDescription = null, tint = NeutralLight) },
                        label = "Email Digest",
                        description = "Weekly summary of tracked bills",
                        checked = emailEnabled,
                        onToggle = { emailEnabled = !emailEnabled }
                    )
                }
            }
        }

        // Topics section
        item {
            Text(
                "TOPICS OF INTEREST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralLighter,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp, 0.dp)) {
                    SettingsToggle(label = "Education", checked = educationEnabled, onToggle = { educationEnabled = !educationEnabled })
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(label = "Healthcare", checked = healthcareEnabled, onToggle = { healthcareEnabled = !healthcareEnabled })
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(label = "Climate", checked = climateEnabled, onToggle = { climateEnabled = !climateEnabled })
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(label = "Tax Reform", checked = taxEnabled, onToggle = { taxEnabled = !taxEnabled })
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(label = "Immigration", checked = immigrationEnabled, onToggle = { immigrationEnabled = !immigrationEnabled })
                    HorizontalDivider(color = NeutralLightest)
                    SettingsToggle(label = "Veterans", checked = veteransEnabled, onToggle = { veteransEnabled = !veteransEnabled })
                }
            }
        }

        // Privacy notice
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.04f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Your privacy matters. Your ZIP code and address are only used to find your representatives via public APIs. No data is stored on our servers.",
                        fontSize = 12.sp,
                        color = NeutralLight,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Footer
        item {
            Text(
                "Civic Sidekick v1.0 \u00b7 Data from GovTrack.us, OpenStates, Google Civic & Wikipedia",
                fontSize = 12.sp,
                color = NeutralLighter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SettingsToggle(
    icon: @Composable (() -> Unit)? = null,
    label: String,
    description: String? = null,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Neutral)
                if (description != null) {
                    Text(description, fontSize = 11.sp, color = NeutralLighter)
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Secondary,
                checkedThumbColor = SurfaceWhite
            )
        )
    }
}