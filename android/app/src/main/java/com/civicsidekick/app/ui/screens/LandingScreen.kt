package com.civicsidekick.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicsidekick.app.ui.theme.*

@Composable
fun LandingScreen(
    onSearch: (String) -> Unit,
    isLoading: Boolean
) {
    var zipCode by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Civic Sidekick",
                tint = Primary,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Civic Sidekick",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Primary,
            letterSpacing = (-0.03).sp
        )

        Text(
            text = "Your companion for civic engagement",
            fontSize = 14.sp,
            color = NeutralLighter,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ZIP input
        OutlinedTextField(
            value = zipCode,
            onValueChange = {
                zipCode = it.take(10)
                error = null
            },
            label = { Text("Enter your ZIP Code") },
            placeholder = { Text("e.g. 90210") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    if (zipCode.matches(Regex("^\\d{5}(-\\d{4})?$"))) {
                        error = null
                        onSearch(zipCode)
                    } else {
                        error = "Please enter a valid 5-digit ZIP Code"
                    }
                }
            ),
            isError = error != null,
            supportingText = error?.let { { Text(it, color = Error) } },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                cursorColor = Primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (zipCode.trim().matches(Regex("^\\d{5}(-\\d{4})?$"))) {
                    error = null
                    onSearch(zipCode.trim())
                } else {
                    error = "Please enter a valid 5-digit ZIP Code"
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = SurfaceWhite,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Searching...")
            } else {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Started", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            text = "Find your representatives, track legislation, and stay informed.",
            fontSize = 13.sp,
            color = NeutralLighter,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Open vote.gov in browser */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check or Register to Vote")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
