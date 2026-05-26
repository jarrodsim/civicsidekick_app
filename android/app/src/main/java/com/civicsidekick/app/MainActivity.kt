package com.civicsidekick.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.civicsidekick.app.data.BillsRepository
import com.civicsidekick.app.data.RepsRepository
import com.civicsidekick.app.data.api.OpenStatesApiKey
import com.civicsidekick.app.data.model.Bill
import com.civicsidekick.app.data.model.Representative
import com.civicsidekick.app.ui.screens.*
import com.civicsidekick.app.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the OpenStates API key from BuildConfig
        OpenStatesApiKey.KEY = BuildConfig.OPENSTATES_API_KEY

        enableEdgeToEdge()

        setContent {
            CivicSidekickTheme {
                CivicSidekickMainScreen()
            }
        }
    }
}

// ---- Navigation Destinations ----

enum class Screen {
    LANDING, HOME, BROWSE, ELECTIONS, EVENTS, TRACKING, SETTINGS
}

data class AppUiState(
    val currentScreen: Screen = Screen.LANDING,
    val address: String = "",
    val reps: List<Representative> = emptyList(),
    val allBills: List<Bill> = emptyList(),
    val trackedBillIds: MutableSet<String> = mutableSetOf(),
    val isLoading: Boolean = false,
    val isSearchingReps: Boolean = false,
    val isAddressSearch: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicSidekickMainScreen() {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var uiState by remember { mutableStateOf(AppUiState()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val showBottomNav = uiState.currentScreen != Screen.LANDING

    // Load bills on first composition
    LaunchedEffect(Unit) {
        val billsResult = BillsRepository.fetchBills()
        if (billsResult.isSuccess) {
            uiState = uiState.copy(allBills = billsResult.getOrDefault(emptyList()))
        }
    }

    // Helper to open URLs in browser
    val openUrl: (String) -> Unit = { url ->
        if (url.isNotBlank()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    // Helper to dial a phone number
    val dialPhone: (String) -> Unit = { phone ->
        if (phone.isNotBlank()) {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.filter { it.isDigit() || it == '+' }))
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    // Helper to open wikipedia
    val openWikipedia: (String) -> Unit = { title ->
        if (title.isNotBlank()) {
            openUrl("https://en.wikipedia.org/wiki/" + title.replace(" ", "_"))
        }
    }

    Scaffold(
        topBar = {
            if (showBottomNav) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Civic Sidekick", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Your Civic Dashboard", fontSize = 11.sp, color = SurfaceWhite.copy(alpha = 0.8f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = SurfaceWhite
                    ),
                    navigationIcon = {
                        IconButton(onClick = { /* Menu */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = SurfaceWhite)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = SurfaceWhite)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 0.dp
                ) {
                    val navItems = listOf(
                        NavBarItem("Home", Icons.Default.Home),
                        NavBarItem("Browse", Icons.Default.Search),
                        NavBarItem("Elections", Icons.Default.HowToVote), // Vote icon approximation
                        NavBarItem("Events", Icons.Default.CalendarMonth),
                        NavBarItem("My Bills", Icons.Default.Description),
                        NavBarItem("Settings", Icons.Default.Settings)
                    )

                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                uiState = uiState.copy(
                                    currentScreen = when (index) {
                                        0 -> Screen.HOME
                                        1 -> Screen.BROWSE
                                        2 -> Screen.ELECTIONS
                                        3 -> Screen.EVENTS
                                        4 -> Screen.TRACKING
                                        5 -> Screen.SETTINGS
                                        else -> Screen.HOME
                                    }
                                )
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(item.label, fontSize = 10.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = Primary.copy(alpha = 0.1f),
                                unselectedIconColor = NeutralLighter,
                                unselectedTextColor = NeutralLighter
                            )
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentScreen) {
                Screen.LANDING -> {
                    LandingScreen(
                        onSearch = { zip ->
                            scope.launch {
                                uiState = uiState.copy(isLoading = true)
                                val result = RepsRepository.findReps(zip)
                                result.onSuccess { reps ->
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        address = zip,
                                        reps = reps,
                                        currentScreen = Screen.HOME
                                    )
                                    selectedTab = 0
                                }.onFailure { error ->
                                    uiState = uiState.copy(isLoading = false)
                                }
                            }
                        },
                        isLoading = uiState.isLoading
                    )
                }

                Screen.HOME -> {
                    HomeScreen(
                        address = uiState.address,
                        reps = uiState.reps,
                        recentBills = uiState.allBills.take(3),
                        trackedBillIds = uiState.trackedBillIds,
                        onNavigateBrowse = {
                            selectedTab = 1
                            uiState = uiState.copy(currentScreen = Screen.BROWSE)
                        },
                        onRepClick = { /* Show detail modal - future */ },
                        onToggleTrack = { billId ->
                            toggleTrack(billId, uiState) { newState -> uiState = newState }
                        },
                        onCallRep = { dialPhone(it) },
                        onOpenWebsite = { openUrl(it) },
                        onOpenOpenStates = { openUrl(it) },
                        onOpenWikipedia = { openWikipedia(it) }
                    )
                }

                Screen.BROWSE -> {
                    BrowseScreen(
                        state = BrowseUiState(
                            bills = uiState.allBills,
                            reps = uiState.reps,
                            trackedBillIds = uiState.trackedBillIds,
                            searchQuery = "",
                            currentPage = 1,
                            isLoadingReps = uiState.isSearchingReps,
                            isAddressSearch = uiState.isAddressSearch
                        ),
                        onSearchQueryChange = { /* Filter - future */ },
                        onPageChange = { /* Paginate - future */ },
                        onRepZipSearch = { zip ->
                            scope.launch {
                                uiState = uiState.copy(isSearchingReps = true, isAddressSearch = false)
                                val result = RepsRepository.findReps(zip)
                                result.onSuccess { reps ->
                                    uiState = uiState.copy(
                                        isSearchingReps = false,
                                        isAddressSearch = false,
                                        address = zip,
                                        reps = reps
                                    )
                                }.onFailure {
                                    uiState = uiState.copy(isSearchingReps = false, isAddressSearch = false)
                                }
                            }
                        },
                        onRepAddressSearch = { address ->
                            scope.launch {
                                uiState = uiState.copy(isSearchingReps = true, isAddressSearch = true)
                                // First get federal reps from state lookup, then local from address
                                // Extract ZIP from address
                                val zipMatch = Regex("\\d{5}(-\\d{4})?").find(address)
                                val zip = zipMatch?.value ?: ""

                                // Get federal + governor
                                var allReps = emptyList<Representative>()
                                if (zip.isNotBlank()) {
                                    val federalResult = RepsRepository.findReps(zip)
                                    federalResult.onSuccess { reps ->
                                        allReps = reps
                                    }
                                }

                                // Get local officials from Google Civic
                                val localResult = RepsRepository.findLocalOfficials(address)
                                localResult.onSuccess { localReps ->
                                    allReps = allReps.toMutableList().apply { addAll(localReps) }
                                }

                                uiState = uiState.copy(
                                    isSearchingReps = false,
                                    isAddressSearch = false,
                                    address = address,
                                    reps = allReps
                                )
                            }
                        },
                        onToggleTrack = { billId ->
                            toggleTrack(billId, uiState) { newState -> uiState = newState }
                        },
                        onBillClick = { /* Show detail - future */ },
                        onRepClick = { /* Show detail - future */ },
                        onCallRep = { dialPhone(it) },
                        onOpenWebsite = { openUrl(it) },
                        onOpenOpenStates = { openUrl(it) },
                        onOpenWikipedia = { openWikipedia(it) }
                    )
                }

                Screen.ELECTIONS -> ElectionsScreen(
                    onNavigateSettings = {
                        selectedTab = 5
                        uiState = uiState.copy(currentScreen = Screen.SETTINGS)
                    }
                )

                Screen.EVENTS -> EventsScreen()

                Screen.TRACKING -> {
                    val trackedBills = uiState.allBills.filter { uiState.trackedBillIds.contains(it.id) }
                    TrackingScreen(
                        trackedBills = trackedBills,
                        trackedBillIds = uiState.trackedBillIds,
                        onRemoveTrack = { billId ->
                            uiState.trackedBillIds.remove(billId)
                            uiState = uiState.copy()
                        },
                        onBillClick = { /* Show detail - future */ },
                        onNavigateBrowse = {
                            selectedTab = 1
                            uiState = uiState.copy(currentScreen = Screen.BROWSE)
                        }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        address = uiState.address,
                        onSaveZip = { zip ->
                            scope.launch {
                                uiState = uiState.copy(isLoading = true)
                                val result = RepsRepository.findReps(zip)
                                result.onSuccess { reps ->
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        address = zip,
                                        reps = reps
                                    )
                                }.onFailure {
                                    uiState = uiState.copy(isLoading = false)
                                }
                            }
                        },
                        onSaveFullAddress = { address ->
                            scope.launch {
                                uiState = uiState.copy(isLoading = true)
                                val zipMatch = Regex("\\d{5}(-\\d{4})?").find(address)
                                val zip = zipMatch?.value ?: ""

                                var allReps = emptyList<Representative>()
                                if (zip.isNotBlank()) {
                                    val federalResult = RepsRepository.findReps(zip)
                                    federalResult.onSuccess { reps -> allReps = reps }
                                }

                                val localResult = RepsRepository.findLocalOfficials(address)
                                localResult.onSuccess { localReps ->
                                    allReps = allReps.toMutableList().apply { addAll(localReps) }
                                }

                                uiState = uiState.copy(
                                    isLoading = false,
                                    address = address,
                                    reps = allReps
                                )
                            }
                        },
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}

// ---- Helpers ----

private data class NavBarItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private fun toggleTrack(
    billId: String,
    state: AppUiState,
    update: (AppUiState) -> Unit
) {
    val ids = state.trackedBillIds
    if (ids.contains(billId)) {
        ids.remove(billId)
    } else {
        ids.add(billId)
    }
    update(state.copy())
}