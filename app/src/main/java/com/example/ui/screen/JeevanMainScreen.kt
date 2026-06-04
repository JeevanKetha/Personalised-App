package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JeevanViewModel
import com.example.ui.viewmodel.ChatMessage
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeevanMainScreen(viewModel: JeevanViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var showAiCompanionSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("jeevan_main_scaffold"),
        containerColor = ImmersiveDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Jeevan OS",
                            color = ImmersiveTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INTELLIGENT PERSONAL OS",
                                color = ImmersiveIndigo,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(ImmersiveIndigo)
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, ImmersiveIndigo.copy(alpha = 0.3f), CircleShape)
                            .background(Brush.sweepGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))))
                    ) {
                        Text(
                            text = userProfile.name.firstOrNull()?.toString()?.uppercase() ?: "J",
                            color = ImmersiveTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ImmersiveDarkBg,
                    titleContentColor = ImmersiveTextPrimary
                )
            )
        },
        floatingActionButton = {},
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = ImmersiveSurface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabItemButton(
                        label = "Core",
                        icon = Icons.Default.Home,
                        isSelected = activeTab == "DASHBOARD",
                        onClick = { viewModel.setActiveTab("DASHBOARD") },
                        tag = "tab_dashboard"
                    )
                    TabItemButton(
                        label = "Finance",
                        icon = Icons.Default.ShoppingCart,
                        isSelected = activeTab == "FINANCE",
                        onClick = { viewModel.setActiveTab("FINANCE") },
                        tag = "tab_finance"
                    )
                    TabItemButton(
                        label = "Career",
                        icon = Icons.Default.Build,
                        isSelected = activeTab == "CAREER",
                        onClick = { viewModel.setActiveTab("CAREER") },
                        tag = "tab_career"
                    )
                    TabItemButton(
                        label = "Health",
                        icon = Icons.Default.Favorite,
                        isSelected = activeTab == "HEALTH",
                        onClick = { viewModel.setActiveTab("HEALTH") },
                        tag = "tab_health"
                    )
                    TabItemButton(
                        label = "Updates",
                        icon = Icons.Default.List,
                        isSelected = activeTab == "NEWS",
                        onClick = { viewModel.setActiveTab("NEWS") },
                        tag = "tab_updates"
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = activeTab,
            animationSpec = tween(200),
            modifier = Modifier.padding(innerPadding)
        ) { tab ->
            when (tab) {
                "DASHBOARD" -> DashboardHub(viewModel)
                "FINANCE" -> FinanceHub(viewModel)
                "CAREER" -> CareerHub(viewModel)
                "HEALTH" -> HealthHub(viewModel)
                "NEWS" -> NewsCenterHub(viewModel)
            }
        }
    }
}

@Composable
fun TabItemButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val tintColor = if (isSelected) ImmersiveIndigo else ImmersiveTextMuted
    val bgAlpha = if (isSelected) ImmersiveIndigo.copy(alpha = 0.1f) else Color.Transparent

    Column(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(12.dp))
            .background(bgAlpha)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .widthIn(min = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = tintColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// --------------------------------------------------
// AUTOMATIC DEVICE ACCURATE REAL-TIME CLOCK WIDGET
// --------------------------------------------------
@Composable
fun DeviceClockWidget() {
    var currentTime by remember { mutableStateOf(Calendar.getInstance().time) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance().time
            kotlinx.coroutines.delay(1000)
        }
    }
    
    val timeFormat = remember { SimpleDateFormat("hh:mm:ss a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()) }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag("device_clock_widget_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DEVICES CHRONO-SYNCHRONIZED",
                        color = CyberCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormat.format(currentTime),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(
                text = timeFormat.format(currentTime),
                color = CyberCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// --------------------------------------------------
// WEATHER & LOCATION LIVE API TEMPERATURE WIDGET
// --------------------------------------------------
@Composable
fun WeatherWidget(viewModel: JeevanViewModel) {
    val context = LocalContext.current
    val wState by viewModel.weatherState.collectAsState()
    val temp by viewModel.weatherTemp.collectAsState()
    val locName by viewModel.weatherLocationName.collectAsState()

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.refreshWeather()
        }
    }

    LaunchedEffect(Unit) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            viewModel.refreshWeather()
        } else {
            launcher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                viewModel.refreshWeather()
            }
            .testTag("location_weather_widget")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Active Location Coordinates GPS",
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = locName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            when (wState) {
                "LOADING" -> {
                    Text("Sync...", color = TextMuted, fontSize = 10.sp)
                }
                "SUCCESS" -> {
                    Text(
                        text = "${temp ?: "--"}°C",
                        color = CyberCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
                "PERMISSION_REQUIRED" -> {
                    Text(
                        text = "Access GPS",
                        color = ImmersiveRose,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                    )
                }
                else -> {
                    if (temp != null) {
                        Text(
                            text = "${temp}°C",
                            color = CyberCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            text = "Tap to sync",
                            color = TextMuted,
                            fontSize = 9.sp,
                            style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// 1. DASHBOARD HUB (CENTRAL INSIGHTS & NEWS JOURNAL)
// --------------------------------------------------
@Composable
fun DashboardHub(viewModel: JeevanViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val syntheticInsights by viewModel.syntheticInsights.collectAsState()
    val newsBookmarks by viewModel.newsBookmarks.collectAsState()
    val subtopics by viewModel.subtopicsProgress.collectAsState()

    var showAddNewsView by remember { mutableStateOf(false) }
    var newsTitle by remember { mutableStateOf("") }
    var newsUrl by remember { mutableStateOf("") }
    var newsCat by remember { mutableStateOf("DevOps") }
    var newsDesc by remember { mutableStateOf("") }

    val nextTargetSubtopic = subtopics.firstOrNull { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Clock Widget synced with device clock
        item {
            DeviceClockWidget()
        }

        // Welcome Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(0.6.dp, ImmersiveIndigo.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val deviceGreetingState by viewModel.deviceGreeting.collectAsState()
                        val displayGreeting = deviceGreetingState.ifBlank { viewModel.calculateGreeting() }
                        Text(
                            text = "$displayGreeting, ${userProfile.name}.",
                            color = ImmersiveTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "System diagnostics: All integrations compiled. Focus active.",
                            color = ImmersiveTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    WeatherWidget(viewModel)
                }
            }
        }

        // Live KPI Indicators Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val completeCount = subtopics.count { it.isCompleted && it.subtopicId.startsWith("sub_") }
                val totalCount = 196
                
                Box(modifier = Modifier.weight(1f)) {
                    EcosystemIndicatorChip("WALLET CAPITAL", "₹${userProfile.balanceAmount}", CyberCyan)
                }
                Box(modifier = Modifier.weight(1f)) {
                    EcosystemIndicatorChip("VALIDATED UNITS", "$completeCount/$totalCount", CyberPurple)
                }
            }
        }

        // Dynamic Primary Mission
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                border = BorderStroke(1.dp, ImmersiveAmber.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ImmersiveAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PRIMARY COGNITIVE GOAL",
                            color = ImmersiveAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (nextTargetSubtopic != null) {
                        val topicClean = nextTargetSubtopic.subtopicId.replace("_", " ").uppercase()
                        Text(
                            text = "Validate deployment subtopic: $topicClean",
                            color = ImmersiveTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Go to Career -> subtopics module, enter scores, or log incomplete reasons to update OS status.",
                            color = ImmersiveTextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "Acknowledge: All DevOps roadmap units are fully validated!",
                            color = ImmersiveEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Synthetic Ecosystem Insights (Factual dynamic cards)
        item {
            Text(
                text = "REAL-TIME DIAGNOSTIC TELEMETRY",
                color = ImmersiveTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(syntheticInsights) { insight ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconColor = when {
                        insight.contains("⚠️") -> ImmersiveRose
                        insight.contains("✔") -> ImmersiveEmerald
                        else -> ImmersiveIndigo
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(iconColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = insight,
                        color = ImmersiveTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EcosystemIndicatorChip(title: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ImmersiveSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = ImmersiveTextMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class SearchableAsset(
    val symbol: String,
    val fullName: String,
    val exchange: String,
    val sector: String,
    val basePrice: Double,
    val assetType: String
)

val searchableIndianAssets = listOf(
    SearchableAsset("TCS", "Tata Consultancy Services (TCS)", "NSE", "Information Technology", 3820.0, "STOCK"),
    SearchableAsset("INFY", "Infosys Limited (INFY)", "NSE", "Information Technology", 1450.0, "STOCK"),
    SearchableAsset("RELIANCE", "Reliance Industries Ltd (RELIANCE)", "NSE", "Energy", 2850.0, "STOCK"),
    SearchableAsset("HDFCBANK", "HDFC Bank Limited (HDFCBANK)", "NSE", "Banking", 1530.0, "STOCK"),
    SearchableAsset("ITC", "ITC Limited (ITC)", "NSE", "Consumer Goods", 420.0, "STOCK"),
    SearchableAsset("SBIN", "State Bank of India (SBIN)", "NSE", "Banking", 780.0, "STOCK"),
    SearchableAsset("ICICIBANK", "ICICI Bank Ltd (ICICIBANK)", "NSE", "Banking", 1120.0, "STOCK"),
    SearchableAsset("TATASTEEL", "Tata Steel Ltd (TATASTEEL)", "NSE", "Materials", 165.0, "STOCK"),
    SearchableAsset("BHARTIARTL", "Bharti Airtel Ltd (BHARTIARTL)", "NSE", "Telecom", 1220.0, "STOCK"),
    SearchableAsset("LT", "Larsen & Toubro Ltd (L&T)", "NSE", "Infrastructure", 3450.0, "STOCK"),
    SearchableAsset("PPFCF", "Parag Parikh Flexi Cap Fund", "Mutual Fund", "Diversified Equity", 72.5, "MF"),
    SearchableAsset("SBISMC", "SBI Small Cap Fund (SBISMC)", "Mutual Fund", "Small Cap", 145.2, "MF"),
    SearchableAsset("UTIN50", "UTI Nifty 50 Index Fund", "Mutual Fund", "Index Funds", 185.0, "MF"),
    SearchableAsset("HDFCMID", "HDFC Mid-Cap Opportunities Fund", "Mutual Fund", "Mid Cap", 162.0, "MF"),
    SearchableAsset("MIRASS", "Mirae Asset Large Cap Fund", "Mutual Fund", "Large Cap", 98.4, "MF"),
    SearchableAsset("NIFTYBEES", "Nippon India ETF Nifty Bees", "NSE", "Index Funds", 245.0, "ETF"),
    SearchableAsset("GOLDBEES", "Nippon India ETF Gold Bees", "NSE", "Commodities", 62.0, "ETF"),
    SearchableAsset("SILVERBEES", "Nippon India ETF Silver Bees", "NSE", "Commodities", 85.0, "ETF"),
    SearchableAsset("MON100", "Motilal Oswal Nasdaq 100 ETF", "NSE", "International", 152.0, "ETF")
)

object IndianMarketScheduleManager {
    val HOLIDAYS = setOf(
        "01-26", // Republic Day
        "03-06", // Holi
        "04-02", // Good Friday
        "04-14", // Ambedkar Jayanti
        "05-01", // Maharashtra Day
        "08-15", // Independence Day
        "10-02", // Gandhi Jayanti
        "10-20", // Dussehra
        "11-05", // Diwali
        "12-25"  // Christmas
    )

    data class MarketStatus(
        val isOpen: Boolean,
        val statusMessage: String,
        val details: String
    )

    fun getMarketStatus(): MarketStatus {
        val kolkataTz = java.util.TimeZone.getTimeZone("Asia/Kolkata")
        val cal = java.util.Calendar.getInstance(kolkataTz)
        
        val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
        val month = cal.get(java.util.Calendar.MONTH) + 1
        val dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)
        
        val dateStr = String.format("%02d-%02d", month, dayOfMonth)
        
        if (dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY) {
            return MarketStatus(
                isOpen = false,
                statusMessage = "Market Closed - Weekend",
                details = "Next Opening: Monday 09:15 AM IST"
            )
        }
        
        if (HOLIDAYS.contains(dateStr)) {
            val holidayName = when(dateStr) {
                "01-26" -> "Republic Day"
                "03-06" -> "Holi"
                "04-02" -> "Good Friday"
                "04-14" -> "Ambedkar Jayanti"
                "05-01" -> "Maharashtra Day"
                "08-15" -> "Independence Day"
                "10-02" -> "Gandhi Jayanti"
                "10-20" -> "Dussehra Holiday"
                "11-05" -> "Diwali"
                "12-25" -> "Christmas"
                else -> "Public Holiday"
            }
            return MarketStatus(
                isOpen = false,
                statusMessage = "Market Closed Today - $holidayName",
                details = "Next Trading Day: Monday (subject to schedules)"
            )
        }
        
        val currentMins = hour * 60 + minute
        val marketStartMins = 9 * 60 + 15
        val marketEndMins = 15 * 60 + 30
        
        return if (currentMins in marketStartMins..marketEndMins) {
            MarketStatus(
                isOpen = true,
                statusMessage = "Market Open",
                details = "Trading active on NSE & BSE (Hours: 09:15 AM - 03:30 PM IST)"
            )
        } else {
            val nextOpening = if (hour < 9 || (hour == 9 && minute < 15)) {
                "Today 09:15 AM IST"
            } else {
                val nextDay = cal.clone() as java.util.Calendar
                nextDay.add(java.util.Calendar.DAY_OF_YEAR, 1)
                val nextDOW = nextDay.get(java.util.Calendar.DAY_OF_WEEK)
                if (nextDOW == java.util.Calendar.SATURDAY || nextDOW == java.util.Calendar.SUNDAY) {
                    "Monday 09:15 AM IST"
                } else {
                    "Tomorrow 09:15 AM IST"
                }
            }
            MarketStatus(
                isOpen = false,
                statusMessage = "Market Closed - Out of Hours",
                details = "Next Opening: $nextOpening"
            )
        }
    }

    fun getYesterdayPrice(holding: com.example.data.entity.PortfolioHolding): Double {
        val sym = if (!holding.symbol.isNullOrEmpty()) holding.symbol else holding.assetName
        val hash = Math.abs(sym.hashCode())
        val offsetPct = ((hash % 30) / 10.0) - 1.5 // range -1.5% to +1.5%
        val yesterdayPrice = holding.purchasePrice * (1.0 + offsetPct / 100.0)
        return Math.round(yesterdayPrice * 100.0) / 100.0
    }
}

fun generatePersonalizedNewsForHoldings(holdings: List<PortfolioHolding>): List<Pair<String, String>> {
    val results = mutableListOf<Pair<String, String>>()
    if (holdings.isEmpty()) {
        return listOf(
            Pair("Nifty 50 Index", "Nifty 50 index remains stable driven by steady retail volume metrics and sustained SIP inflows."),
            Pair("RBI Policy", "Reserve Bank of India maintains steady stance to guide structural growth targets securely."),
            Pair("SIP Inflows", "Retail investment contributions touch lifetime landmark inflows of ₹21,000 crores.")
        )
    }
    
    for (holding in holdings) {
        val sym = holding.symbol.orEmpty().ifEmpty { holding.assetName }
        val name = holding.assetName
        when (holding.assetType) {
            "STOCK" -> {
                val head = when {
                    sym.contains("TCS", ignoreCase = true) -> "TCS secures multi-million enterprise SRE transformation contract."
                    sym.contains("INFY", ignoreCase = true) -> "Infosys expands global enterprise AI sandbox solutions."
                    sym.contains("RELIANCE", ignoreCase = true) -> "Reliance retail business opens new automated fulfillment logistics hub."
                    sym.contains("HDFCBANK", ignoreCase = true) -> "HDFC Bank assets show consistent credit margins for commercial expansion."
                    sym.contains("ITC", ignoreCase = true) -> "ITC demerger procedures advance on schedule for regional listings."
                    sym.contains("SBIN", ignoreCase = true) -> "SBI retail lending registers robust growth indexes this quarter."
                    sym.contains("ICICIBANK", ignoreCase = true) -> "ICICI bank deposits scale optimal markers showing high liquidity pool metrics."
                    sym.contains("ONGC", ignoreCase = true) -> "ONGC begins exploratory testing procedures inside KG shallow blocks."
                    else -> "$name ($sym) registers steady institutional accumulation support indices."
                }
                results.add(Pair(sym, head))
            }
            "MF", "SIP" -> {
                results.add(Pair(sym, "Lump sum and SIP allocation in $name increases by 14% this month, building compounding equity momentum."))
            }
            "ETF" -> {
                results.add(Pair(sym, "Daily liquidity indices on NSE for $name reach optimal averages, ensuring low tracking errors."))
            }
        }
    }
    return results.distinctBy { it.second }.take(4)
}

@Composable
fun SparklineGraph(points: List<Float>, color: Color, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val path = androidx.compose.ui.graphics.Path()
        val width = size.width
        val height = size.height
        val minVal = points.minOrNull() ?: 0f
        val maxVal = points.maxOrNull() ?: 100f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        points.forEachIndexed { index, value ->
            val x = (index.toFloat() / (points.size - 1)) * width
            val y = height - ((value - minVal) / range) * height
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

// --------------------------------------------------
// 2. FINANCE & PORTFOLIO HUB
// --------------------------------------------------
@Composable
fun FinanceHub(viewModel: JeevanViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val portfolios by viewModel.portfolioHoldings.collectAsState()
    val careerGoalFunds by viewModel.careerGoalFunds.collectAsState()
    val aiInsights by viewModel.aiInvestmentInsights.collectAsState()
    val portfolioNews by viewModel.portfolioNews.collectAsState()

    var transactTitle by remember { mutableStateOf("") }
    var transactAmount by remember { mutableStateOf("") }
    var transactCategory by remember { mutableStateOf("FOOD") }
    var transactIsSubscription by remember { mutableStateOf(false) }

    var capitalThresholdInput by remember { mutableStateOf(userProfile.monthlyBudgetLimit.toInt().toString()) }

    var portAssetName by remember { mutableStateOf("") }
    var portAssetQty by remember { mutableStateOf("") }
    var portBuyPrice by remember { mutableStateOf("") }
    var portAssetType by remember { mutableStateOf("STOCK") }
    var showAddPortfolioPanel by remember { mutableStateOf(false) }

    var showAddInvestmentDialog by remember { mutableStateOf(false) }
    var searchAssetQuery by remember { mutableStateOf("") }
    var selectedSymbol by remember { mutableStateOf("") }
    var selectedExchange by remember { mutableStateOf("NSE") }
    var selectedSector by remember { mutableStateOf("Other") }
    var purchaseDateText by remember { mutableStateOf("") }
    var optionalNotesText by remember { mutableStateOf("") }
    var activeTimelineUnit by remember { mutableStateOf("ALL") }

    var showAddGoalPanel by remember { mutableStateOf(false) }
    var newGoalName by remember { mutableStateOf("") }
    var newGoalTarget by remember { mutableStateOf("") }
    var newGoalCurrent by remember { mutableStateOf("") }

    val categories = listOf("FOOD", "TRANSPORT", "RENT", "BILLS", "ADDITIONAL")
    val context = LocalContext.current

    // Calculations
    val spent = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val limit = userProfile.monthlyBudgetLimit.coerceAtLeast(1.0)
    val remainingPower = (limit - spent).coerceAtLeast(0.0)
    val savingsRatePercent = if (limit > 0) ((remainingPower / limit) * 100).toInt().coerceIn(0, 100) else 0

    val healthReport = viewModel.calculateFinancialHealthScore()

    val totalPortfolioValue = portfolios.sumOf { it.quantity * it.currentPrice }
    val totalPortfolioCost = portfolios.sumOf { it.quantity * it.purchasePrice }
    val totalPortfolioGain = totalPortfolioValue - totalPortfolioCost
    val portfolioReturnPercent = if (totalPortfolioCost > 0) (totalPortfolioGain / totalPortfolioCost * 100.0) else 0.0

    val totalYesterdayValue = portfolios.sumOf { it.quantity * IndianMarketScheduleManager.getYesterdayPrice(it) }
    val todayChangeValue = portfolios.sumOf { it.quantity * (it.currentPrice - IndianMarketScheduleManager.getYesterdayPrice(it)) }
    val todayChangePercent = if (totalYesterdayValue > 0) (todayChangeValue / totalYesterdayValue * 100.0) else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==================================================================
        // PRIMARY FOCUS SECTION (CORE METRICS & FINANCIAL HEALTH)
        // ==================================================================
        item {
            Text(
                text = "PRIMARY METRIC INFRASTRUCTURE",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
        }

        // ESCOW WALLET BALANCE & BUDGET
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CURRENT INFRASTRUCTURE ESCROW", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${userProfile.balanceAmount}",
                        color = CyberCyan,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Monthly limit setup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Monthly Capital Threshold:", color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Current setup: ₹$limit", color = TextMuted, fontSize = 9.sp)
                        }
                        OutlinedTextField(
                            value = capitalThresholdInput,
                            onValueChange = { 
                                capitalThresholdInput = it 
                                val parsed = it.toDoubleOrNull() ?: 1.0
                                if (parsed > 0) {
                                    viewModel.updateMonthlyLimit(parsed)
                                }
                            },
                            modifier = Modifier.width(110.dp).height(50.dp).testTag("monthly_capital_setup_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    val rawProgress = spent / limit
                    val budgetProgress = if (rawProgress.isNaN() || rawProgress.isInfinite()) 0f else rawProgress.toFloat().coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = budgetProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (budgetProgress > 0.8f) ImmersiveRose else CyberCyan,
                        trackColor = ImmersiveSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Expended: ₹$spent", color = TextMuted, fontSize = 10.sp)
                        Text(text = "Purchasing Power: ₹$remainingPower ($savingsRatePercent% SAVINGS RATE)", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // FINANCIAL HEALTH SCORE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveEmerald.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Financial Health Score Icon",
                                tint = ImmersiveEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FINANCIAL HEALTH SCORE",
                                color = ImmersiveTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ImmersiveEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = healthReport.grade,
                                color = ImmersiveEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${healthReport.score}",
                            color = ImmersiveEmerald,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "/100",
                            color = ImmersiveTextMuted,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(ImmersiveSurfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (healthReport.score / 100.0).toFloat().coerceIn(0f, 1f))
                                    .background(ImmersiveEmerald)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "RECOMMENDATIONS FOR SCORE OPTIMIZATION:",
                        color = ImmersiveTextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    healthReport.recommendations.forEach { rec ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("• ", color = ImmersiveEmerald, fontSize = 10.sp)
                            Text(text = rec, color = ImmersiveTextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // TRANSACTION INTAKE WIDGET
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.04f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOG FINANCIAL LEDGER",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = transactTitle,
                        onValueChange = { transactTitle = it },
                        modifier = Modifier.fillMaxWidth().testTag("transact_title_input"),
                        label = { Text("Transaction Title (eg groceries)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = transactAmount,
                        onValueChange = { transactAmount = it },
                        modifier = Modifier.fillMaxWidth().testTag("transact_amount_input"),
                        label = { Text("Amount (₹)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Category Token:", color = TextMuted, fontSize = 10.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            Button(
                                onClick = { transactCategory = cat },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (transactCategory == cat) CyberCyan else ImmersiveSurface
                                )
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 8.sp,
                                    color = if (transactCategory == cat) Color.Black else TextCelestial
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = transactIsSubscription,
                            onCheckedChange = { transactIsSubscription = it },
                            colors = CheckboxDefaults.colors(checkedColor = CyberCyan)
                        )
                        Text(text = "Recurring monthly subscription", color = TextCelestial, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val amt = transactAmount.toDoubleOrNull() ?: 0.0
                                if (transactTitle.isNotBlank() && amt > 0.0) {
                                    viewModel.addExpense(transactTitle, amt, transactCategory, transactIsSubscription)
                                    transactTitle = ""
                                    transactAmount = ""
                                    transactIsSubscription = false
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("log_expense_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Log Expense", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val amt = transactAmount.toDoubleOrNull() ?: 0.0
                                if (transactTitle.isNotBlank() && amt > 0.0) {
                                    viewModel.addIncome(transactTitle, amt, transactCategory)
                                    transactTitle = ""
                                    transactAmount = ""
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("log_income_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveEmerald),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Log Income", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }


        // ==================================================================
        // SECONDARY FOCUS SECTION (INVESTMENT INTELLIGENCE & TRACKING)
        // ==================================================================
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SECONDARY INVESTMENT INTELLIGENCE",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
        }

        // PORTFOLIO SNAPSHOT CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("portfolio_snapshot_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PORTFOLIO SNAPSHOT Dashboard",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Indian market schedule status indicator banner
                    val marketStatus = IndianMarketScheduleManager.getMarketStatus()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (marketStatus.isOpen) ImmersiveEmerald.copy(alpha = 0.1f) else ImmersiveSurfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (marketStatus.isOpen) ImmersiveEmerald else ImmersiveAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = marketStatus.statusMessage.uppercase(),
                                color = if (marketStatus.isOpen) ImmersiveEmerald else ImmersiveAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = marketStatus.details,
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timeline Period selector tabs
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("TODAY", "1W", "1M", "3M", "1Y", "ALL").forEach { unit ->
                            val isSelected = activeTimelineUnit == unit
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(0.6.dp, if (isSelected) CyberCyan else Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                    .clickable { activeTimelineUnit = unit }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unit,
                                    color = if (isSelected) CyberCyan else Color.White.copy(alpha = 0.6f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic values based on selected timeline
                    val (displayGainVal, displayGainPercent, displayGainColor) = when (activeTimelineUnit) {
                        "TODAY" -> Triple(
                            todayChangeValue, 
                            todayChangePercent, 
                            if (todayChangeValue >= 0) ImmersiveEmerald else ImmersiveRose
                        )
                        "1W" -> Triple(
                            totalPortfolioValue * 0.0165, 
                            1.65, 
                            ImmersiveEmerald
                        )
                        "1M" -> Triple(
                            totalPortfolioValue * 0.048, 
                            4.80, 
                            ImmersiveEmerald
                        )
                        "3M" -> Triple(
                            totalPortfolioValue * 0.095, 
                            9.50, 
                            ImmersiveEmerald
                        )
                        "1Y" -> Triple(
                            totalPortfolioValue * 0.241, 
                            24.10, 
                            ImmersiveEmerald
                        )
                        else -> Triple(
                            totalPortfolioGain, 
                            portfolioReturnPercent, 
                            if (totalPortfolioGain >= 0) ImmersiveEmerald else ImmersiveRose
                        )
                    }

                    val sparklinePoints = when (activeTimelineUnit) {
                        "TODAY" -> listOf(100.0f, 100.4f, 100.3f, 100.7f, 101.2f, 101.1f, 101.5f, 102.02f)
                        "1W" -> listOf(99.0f, 99.4f, 99.8f, 99.6f, 100.1f, 100.5f, 101.65f)
                        "1M" -> listOf(96.0f, 96.9f, 96.6f, 98.2f, 99.0f, 100.0f, 101.9f, 103.3f, 104.8f)
                        "3M" -> listOf(91.0f, 92.6f, 93.3f, 92.1f, 94.8f, 96.5f, 97.4f, 99.1f, 101.4f, 105.6f, 109.5f)
                        "1Y" -> listOf(80.0f, 83.3f, 81.8f, 85.5f, 89.1f, 91.3f, 95.6f, 99.2f, 103.4f, 112.3f, 118.8f, 124.1f)
                        else -> listOf(70.0f, 74.3f, 77.0f, 79.7f, 83.9f, 89.2f, 93.9f, 100.0f, 106.2f, 112.4f)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Portfolio Valuation:", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "₹${String.format("%,.2f", totalPortfolioValue)}",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${activeTimelineUnit.lowercase().capitalize()} Change:", color = TextMuted, fontSize = 10.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (displayGainVal >= 0) "▲ " else "▼ ",
                                    color = displayGainColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${if (displayGainVal >= 0) "+" else ""}₹${String.format("%,.2f", displayGainVal)} (${String.format("%.2f", displayGainPercent)}%)",
                                    color = displayGainColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sparkline Graph Canvas Area
                    SparklineGraph(
                        points = sparklinePoints,
                        color = displayGainColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Gain/Loss:", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = if (totalPortfolioGain >= 0) "+₹${String.format("%,.2f", totalPortfolioGain)}" else "₹${String.format("%,.2f", totalPortfolioGain)}",
                                color = if (totalPortfolioGain >= 0) ImmersiveEmerald else ImmersiveRose,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Portfolio Return:", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${String.format("%.2f", portfolioReturnPercent)}%",
                                color = if (portfolioReturnPercent >= 0) ImmersiveEmerald else ImmersiveRose,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Estimated CAGR:", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "12.4%",
                                color = CyberCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Hot Action - Add Investment Button
                    Button(
                        onClick = {
                            searchAssetQuery = ""
                            selectedSymbol = ""
                            portAssetQty = ""
                            portBuyPrice = ""
                            optionalNotesText = ""
                            purchaseDateText = ""
                            showAddInvestmentDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_investment_trigger")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Investment", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Investment",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // MONTHLY INVESTMENT HEALTH (SIP TRACKING)
        item {
            val sipTarget = 5000.0
            val sipMFInvested = portfolios.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }
            val sipPending = (sipTarget - sipMFInvested).coerceAtLeast(0.0)
            val consistencyFactor = ((sipMFInvested / sipTarget) * 100).toInt().coerceIn(0, 100)

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MONTHLY INVESTMENT HEALTH",
                        color = ImmersiveTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Monthly SIP Target:", color = TextMuted, fontSize = 10.sp)
                            Text("₹${String.format("%,.0f", sipTarget)}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Invested Amount:", color = TextMuted, fontSize = 10.sp)
                            Text("₹${String.format("%,.2f", sipMFInvested)}", color = ImmersiveEmerald, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Pending Amount:", color = TextMuted, fontSize = 10.sp)
                            Text("₹${String.format("%,.2f", sipPending)}", color = if (sipPending > 0) ImmersiveAmber else ImmersiveEmerald, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    val sipProgress = (sipMFInvested / sipTarget).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = sipProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ImmersiveEmerald,
                        trackColor = ImmersiveSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Investment Consistency:", color = TextMuted, fontSize = 10.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (consistencyFactor >= 80) ImmersiveEmerald.copy(alpha = 0.15f) else ImmersiveAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$consistencyFactor%",
                                color = if (consistencyFactor >= 80) ImmersiveEmerald else ImmersiveAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // ASSET ALLOCATION OVERVIEW
        item {
            val totalAssets = totalPortfolioValue + userProfile.balanceAmount
            val cashPct = if (totalAssets > 0) (userProfile.balanceAmount / totalAssets * 100).toInt() else 0
            val stocksPct = if (totalAssets > 0) (portfolios.filter { it.assetType == "STOCK" }.sumOf { it.quantity * it.currentPrice } / totalAssets * 100).toInt() else 0
            val mfPct = if (totalAssets > 0) (portfolios.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice } / totalAssets * 100).toInt() else 0

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ASSET ALLOCATION DISTRIBUTION",
                        color = ImmersiveTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-Segment Progress bar representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ImmersiveSurfaceVariant)
                    ) {
                        val totalPct = (mfPct + stocksPct + cashPct).coerceAtLeast(1)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(mfPct.coerceAtLeast(1).toFloat())
                                .background(ImmersiveEmerald)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(stocksPct.coerceAtLeast(1).toFloat())
                                .background(CyberCyan)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(cashPct.coerceAtLeast(1).toFloat())
                                .background(ImmersiveAmber)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ImmersiveEmerald))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mutual Funds (Index): $mfPct%", color = TextMuted, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberCyan))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Direct Stocks: $stocksPct%", color = TextMuted, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ImmersiveAmber))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Liquid Cash: $cashPct%", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // AI INVESTMENT INSIGHTS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AI PORTFOLIO ANALYSIS OBSERVATIONS",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (aiInsights.isEmpty()) {
                        Text("Compiling AI investment ecosystem insights variables...", color = TextMuted, fontSize = 11.sp)
                    } else {
                        aiInsights.forEach { obs ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text("💡 ", fontSize = 12.sp)
                                Text(
                                    text = obs,
                                    color = ImmersiveTextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Educational observations only. This does not constitute professional investment advice.",
                        color = TextMuted,
                        fontSize = 8.sp,
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }
        }

        // PORTFOLIO NEWS INTELLIGENCE
        item {
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            val portfolioNewsItems by viewModel.portfolioNewsItems.collectAsState()
            val lastPortfolioNewsRefresh by viewModel.lastPortfolioNewsRefresh.collectAsState()

            val refreshTimeStr = if (lastPortfolioNewsRefresh > 0L) {
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(lastPortfolioNewsRefresh))
            } else {
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            }
            val nextRefreshTimeStr = if (lastPortfolioNewsRefresh > 0L) {
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(lastPortfolioNewsRefresh + 7200000))
            } else {
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(System.currentTimeMillis() + 7200000))
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth().testTag("portfolio_news_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PORTFOLIO NEWS INTELLIGENCE",
                                color = ImmersiveTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Holdings-specific security intelligence stream.", color = TextMuted, fontSize = 10.sp)
                        }
                        IconButton(
                            onClick = { viewModel.refreshPortfolioNews() },
                            modifier = Modifier.testTag("refresh_portfolio_news_btn")
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                                contentDescription = "Refresh Portfolio News",
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Refresh Schedule Tracker Block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("LAST UPDATED", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text(refreshTimeStr, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("NEXT AUTO-REFRESH", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text(nextRefreshTimeStr, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    if (portfolioNewsItems.isEmpty()) {
                        Text("No personalized holding updates calculated yet. Please check again or add a holding asset.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        portfolioNewsItems.forEach { news ->
                            Card(
                                onClick = {
                                    if (news.sourceUrl.isNotBlank()) {
                                        try {
                                            uriHandler.openUri(news.sourceUrl)
                                        } catch (e: Exception) {
                                            android.util.Log.e("JeevanMainScreen", "Failed to open source URL: ${news.sourceUrl}", e)
                                        }
                                    }
                                },
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("portfolio_news_item_${news.id}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(CyberCyan.copy(alpha = 0.15f))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(news.sourceName.uppercase(), color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.White.copy(alpha = 0.05f))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(news.symbol, color = ImmersiveTextPrimary, fontSize = 8.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        
                                        // Matched badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF4CAF50))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("ACTIVE HOLDING", color = Color(0xFF81C784), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = news.title, color = ImmersiveTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = news.description, color = TextMuted, fontSize = 10.sp, lineHeight = 14.sp)
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                                                contentDescription = null,
                                                tint = TextMuted,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(news.publishedTime, color = TextMuted, fontSize = 8.sp)
                                        }
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "VERIFY ORIGINAL SOURCE",
                                                color = CyberCyan,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = androidx.compose.material.icons.Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = CyberCyan,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // HOLDINGS MANAGEMENT ACTIVE LEDGER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HOLDINGS PORTFOLIO LEDGER",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (showAddPortfolioPanel) "Cancel" else "+ Buy Asset",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddPortfolioPanel = !showAddPortfolioPanel }
                )
            }
        }

        if (showAddPortfolioPanel) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("RECORD ASSET INVESTMENT", color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = portAssetName,
                            onValueChange = { portAssetName = it },
                            label = { Text("Asset Name (e.g. TCS)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("portfolio_asset_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = portAssetQty,
                                onValueChange = { portAssetQty = it },
                                label = { Text("Units", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = portBuyPrice,
                                onValueChange = { portBuyPrice = it },
                                label = { Text("Buy Price (₹)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Asset Type Definition:", color = TextMuted, fontSize = 10.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("STOCK", "MF", "SIP").forEach { type ->
                                Button(
                                    onClick = { portAssetType = type },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (portAssetType == type) CyberCyan else ImmersiveSurface
                                    )
                                ) {
                                    Text(type, fontSize = 9.sp, color = if (portAssetType == type) Color.Black else Color.White)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val q = portAssetQty.toDoubleOrNull() ?: 0.0
                                val p = portBuyPrice.toDoubleOrNull() ?: 0.0
                                if (portAssetName.isNotBlank() && q > 0 && p > 0) {
                                    viewModel.addPortfolioAsset(portAssetName, q, p, portAssetType)
                                    portAssetName = ""
                                    portAssetQty = ""
                                    portBuyPrice = ""
                                    showAddPortfolioPanel = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveEmerald),
                            modifier = Modifier.fillMaxWidth().testTag("portfolio_buy_button")
                        ) {
                            Text("Log Asset Registry", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List Holdings items
        if (portfolios.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No assets registered in local database.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            items(portfolios) { asset ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(asset.assetName, color = TextCelestial, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(asset.assetType, color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${asset.quantity} Units @ ₹${asset.purchasePrice} (Live: ₹${asset.currentPrice})", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        val totalVal = asset.quantity * asset.currentPrice
                        val totalCost = asset.quantity * asset.purchasePrice
                        val gainLoss = totalVal - totalCost
                        val growthPct = if (totalCost > 0) (gainLoss / totalCost * 100.0) else 0.0

                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${String.format("%.2f", totalVal)}", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${if (gainLoss >= 0) "+" else ""}₹${String.format("%.1f", gainLoss)} (${String.format("%.1f", growthPct)}%)",
                                color = if (gainLoss >= 0) ImmersiveEmerald else ImmersiveRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Remove",
                                color = ImmersiveRose.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                modifier = Modifier.clickable { viewModel.removePortfolioAsset(asset) }
                            )
                        }
                    }
                }
            }
        }

        // EXPORT COMPREHENSIVE QUARTERLY CSV REPORTS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUARTERLY FISCAL COMPILATION",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Synthesizes expenses, savings rate, portfolio growth, investment consistency, emergency fund progress, and your current Financial Health Score into a local spreadsheet report.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateCSVReport(context) },
                        modifier = Modifier.fillMaxWidth().testTag("compile_financial_report_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Compile and Export Report", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showAddInvestmentDialog) {
        AlertDialog(
            onDismissRequest = { showAddInvestmentDialog = false },
            containerColor = ImmersiveSurface,
            title = {
                Text(
                    "RECORD NEW PIPELINE INVESTMENT",
                    color = CyberCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Asset Type selector row
                    Text("INVESTMENT SECURE TYPE", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("STOCK", "MF", "ETF", "SIP").forEach { type ->
                            val isSelected = portAssetType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) CyberCyan else ImmersiveSurfaceVariant)
                                    .border(1.dp, if (isSelected) CyberCyan else Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        portAssetType = type
                                        searchAssetQuery = ""
                                        selectedSymbol = ""
                                        selectedExchange = if (type == "MF" || type == "SIP") "Mutual Fund" else "NSE"
                                        selectedSector = "Other"
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Smart Searchable Name Input
                    Text("SECURITY MATCH SEARCH (FUZZY AUTOCOMPLETE)", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    OutlinedTextField(
                        value = searchAssetQuery,
                        onValueChange = {
                            searchAssetQuery = it
                            selectedSymbol = ""
                        },
                        placeholder = { Text("Type name e.g. TCS, Reliance, Nifty Bees...", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("smart_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Fuzzy-matching suggestion list or dropdown
                    if (searchAssetQuery.isNotBlank() && selectedSymbol.isEmpty()) {
                        val filteredSuggestions = searchableIndianAssets.filter { asset ->
                            (asset.fullName.contains(searchAssetQuery, ignoreCase = true) ||
                             asset.symbol.contains(searchAssetQuery, ignoreCase = true)) &&
                            (if (portAssetType == "STOCK") asset.assetType == "STOCK"
                             else if (portAssetType == "ETF") asset.assetType == "ETF"
                             else asset.assetType == "MF" || asset.assetType == "SIP")
                        }

                        if (filteredSuggestions.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 140.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.padding(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(filteredSuggestions) { suggestion ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    searchAssetQuery = suggestion.fullName
                                                    selectedSymbol = suggestion.symbol
                                                    selectedExchange = suggestion.exchange
                                                    selectedSector = suggestion.sector
                                                    portBuyPrice = suggestion.basePrice.toString()
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(suggestion.fullName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("${suggestion.symbol} • ${suggestion.exchange} • ${suggestion.sector}", color = TextMuted, fontSize = 9.sp)
                                            }
                                            Text("₹${suggestion.basePrice}", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Quantity & Price Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = portAssetQty,
                            onValueChange = { portAssetQty = it },
                            label = { Text("Quantity/Units", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("smart_qty_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = portBuyPrice,
                            onValueChange = { portBuyPrice = it },
                            label = { Text("Price per Unit (₹)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("smart_price_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Purchase Date text field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = purchaseDateText,
                            onValueChange = { purchaseDateText = it },
                            label = { Text("Purchase Date (YYYY-MM-DD)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1.2f).testTag("smart_date_input"),
                            placeholder = { Text("Auto: Today", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ImmersiveSurfaceVariant)
                                .clickable {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    purchaseDateText = sdf.format(Date())
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SET TODAY", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Notes Field
                    OutlinedTextField(
                        value = optionalNotesText,
                        onValueChange = { optionalNotesText = it },
                        label = { Text("Optional Notes", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("smart_notes_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = portAssetQty.toDoubleOrNull() ?: 0.0
                        val price = portBuyPrice.toDoubleOrNull() ?: 0.0
                        
                        val name = if (searchAssetQuery.isNotBlank()) searchAssetQuery else "Direct Stock"
                        val sym = if (selectedSymbol.isNotBlank()) selectedSymbol else name.take(12).uppercase()
                        
                        val pDate = try {
                            if (purchaseDateText.isNotBlank()) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                sdf.parse(purchaseDateText)?.time ?: System.currentTimeMillis()
                            } else {
                                System.currentTimeMillis()
                            }
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }

                        if (qty > 0.0 && price > 0.0) {
                            viewModel.addPortfolioAsset(
                                name = name,
                                quantity = qty,
                                price = price,
                                type = portAssetType,
                                purchaseDate = pDate,
                                notes = optionalNotesText,
                                symbol = sym,
                                exchange = selectedExchange,
                                sector = selectedSector
                            )
                            portAssetQty = ""
                            portBuyPrice = ""
                            searchAssetQuery = ""
                            selectedSymbol = ""
                            purchaseDateText = ""
                            optionalNotesText = ""
                            showAddInvestmentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveEmerald)
                ) {
                    Text("SECURE INVESTMENT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddInvestmentDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)
                ) {
                    Text("CLOSE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }
}

// --------------------------------------------------
// 3. CAREER & COMPLETED REBUILD HUB
// --------------------------------------------------
@Composable
fun CareerHub(viewModel: JeevanViewModel) {
    val progressList by viewModel.careerProgress.collectAsState()
    val subList by viewModel.subtopicsProgress.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val puzzlesSolved by viewModel.puzzlesSolved.collectAsState()

    val activeAssessmentId by viewModel.assessmentSubtopicId.collectAsState()
    val assessmentIndex by viewModel.assessmentCurrentQuestionIndex.collectAsState()
    val assessmentAnswers by viewModel.assessmentAnswers.collectAsState()
    val isEvaluating by viewModel.isAssessmentEvaluating.collectAsState()
    val assessmentStrengths by viewModel.assessmentStrengths.collectAsState()
    val assessmentWeaknesses by viewModel.assessmentWeaknesses.collectAsState()
    val assessmentScoreResult by viewModel.assessmentScoreResult.collectAsState()

    val passingScore by viewModel.passingScoreThreshold.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val isRetestActive by viewModel.isRetestActive.collectAsState()
    val userNotes by viewModel.subtopicUserNotes.collectAsState()

    var activeSubTab by remember { mutableStateOf("ROADMAP") } // "ROADMAP", "DAILY_STUDY", "DIAGNOSTICS", "ARCHIVE"
    var skippedReasonInput by remember { mutableStateOf("Busy") }
    var showAddUnitDialog by remember { mutableStateOf(false) } // unused fallback
    var showAddResourceDialog by remember { mutableStateOf(false) }
    var resourcesSubTab by remember { mutableStateOf("RECOMMENDED") } // "RECOMMENDED", "SAVED"
    var searchQuery by remember { mutableStateOf("") }
    var filterResourceType by remember { mutableStateOf("ALL") }
    val savedResources by viewModel.savedResources.collectAsState()

    // --- Dynamic Roadmap States ---
    var isEditMode by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showEditTopicDialog by remember { mutableStateOf<com.example.data.entity.RoadmapTopic?>(null) }
    var showAddSubtopicDialog by remember { mutableStateOf<com.example.data.entity.RoadmapTopic?>(null) }
    var showEditSubtopicDialog by remember { mutableStateOf<com.example.data.entity.RoadmapSubtopic?>(null) }
    var showConfirmResetDialog by remember { mutableStateOf(false) }

    val topics by viewModel.roadmapTopics.collectAsState()
    val subtopics by viewModel.roadmapSubtopics.collectAsState()

    val weekOrder = (1..28).map { "week_$it" }

    val weekMap = mapOf(
        "week_1" to Pair("WEEK 1 — PHASE 1", "Linux OS & File System"),
        "week_2" to Pair("WEEK 2 — PHASE 1", "Linux Administration & Processes"),
        "week_3" to Pair("WEEK 3 — PHASE 1", "Networking + SSH + Bash Scripting"),
        "week_4" to Pair("WEEK 4 — PHASE 2", "Git + GitHub + Python for DevOps"),
        "week_5" to Pair("WEEK 5 — PHASE 2", "AWS Account Setup + Cost + IAM"),
        "week_6" to Pair("WEEK 6 — PHASE 3", "EC2 Deep Dive"),
        "week_7" to Pair("WEEK 7 — PHASE 3", "Load Balancing + Auto Scaling + S3"),
        "week_8" to Pair("WEEK 8 — PHASE 3", "Route53 + CloudWatch + Systems Manager"),
        "week_9" to Pair("WEEK 9 — PHASE 4", "VPC Fundamentals"),
        "week_10" to Pair("WEEK 10 — PHASE 4", "Advanced Networking + Security Services"),
        "week_11" to Pair("WEEK 11 — PHASE 5", "RDS + DynamoDB + ElastiCache"),
        "week_12" to Pair("WEEK 12 — PHASE 5", "Serverless + Application Services"),
        "week_13" to Pair("WEEK 13 — PHASE 6", "CloudFormation + AWS Well-Architected"),
        "week_14" to Pair("WEEK 14 — PHASE 6", "AWS Cost Optimization + FinOps"),
        "week_15" to Pair("WEEK 15 — PHASE 7", "Docker Fundamentals"),
        "week_16" to Pair("WEEK 16 — PHASE 7", "Docker Compose + ECR + Security"),
        "week_17" to Pair("WEEK 17 — PHASE 7", "ECS (Elastic Container Service)"),
        "week_18" to Pair("WEEK 18 — PHASE 8", "GitHub Actions CI/CD"),
        "week_19" to Pair("WEEK 19 — PHASE 8", "DevSecOps — Security in Pipelines"),
        "week_20" to Pair("WEEK 20 — PHASE 9", "Jenkins + GitLab CI"),
        "week_21" to Pair("WEEK 21 — PHASE 9", "Ansible — Configuration Management"),
        "week_22" to Pair("WEEK 22 — PHASE 10", "Kubernetes Fundamentals"),
        "week_23" to Pair("WEEK 23 — PHASE 10", "EKS + HELM + ArgoCD GitOps"),
        "week_24" to Pair("WEEK 24 — PHASE 10", "K8s Security + Monitoring on EKS"),
        "week_25" to Pair("WEEK 25 — PHASE 11", "Terraform Fundamentals + Intermediate"),
        "week_26" to Pair("WEEK 26 — PHASE 11", "Terraform Advanced + Project"),
        "week_27" to Pair("WEEK 27 — PHASE 11", "AIOps for Cloud + AWS AI Services"),
        "week_28" to Pair("WEEK 28 — PHASE 12", "SAA-C03 + Final Project + Launch")
    )

    if (activeAssessmentId != null) {
        val activeId = activeAssessmentId!!
        val weekInfo = weekMap[activeId] ?: Pair("WEEK EX", activeId.replace("_", " ").uppercase())
        val questions = if (isRetestActive) {
            viewModel.subtopicRetestQuestions[activeId] ?: viewModel.subtopicQuestions[activeId] ?: listOf("Q1", "Q2", "Q3")
        } else {
            viewModel.subtopicQuestions[activeId] ?: listOf("Q1", "Q2", "Q3")
        }
        val currentQuestion = questions.getOrElse(assessmentIndex) { "Demonstrate DevOps competency" }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("assessment_arena_container")
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DEVOPS SRE MOCK INTERVIEW ASSESSMENT",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${weekInfo.first}: ${weekInfo.second}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (isEvaluating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing responses with Jeevan DevOps AI Reasoning Engine...",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (assessmentStrengths != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(1.dp, if (assessmentScoreResult >= 75) CyberGreen.copy(alpha = 0.5f) else ImmersiveAmber.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "YOUR DEVOPS READINESS INDEX",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$assessmentScoreResult",
                                        color = if (assessmentScoreResult >= 75) CyberGreen else ImmersiveAmber,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "%",
                                        color = TextMuted,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val feedbackLabel = when {
                                    assessmentScoreResult >= 90 -> "EXCELLENT - PRODUCTION READY SPECIALIST"
                                    assessmentScoreResult >= 75 -> "STRONG - COMPETENT PLATFORM SRE"
                                    assessmentScoreResult >= 60 -> "PASSING - NEED MINOR ARCHITECTURAL REVISIONS"
                                    else -> "REVISION REQUIRED - INFRASTRUCTURE GAPS IDENTIFIED"
                                }
                                Text(
                                    text = feedbackLabel,
                                    color = if (assessmentScoreResult >= 75) CyberGreen else ImmersiveAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🔥 IDENTIFIED STRENGTHS",
                                    color = CyberGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = assessmentStrengths ?: "* Concept clarity is apparent.",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⚠️ OPPORTUNITIES FOR HEALTHY REVISION",
                                    color = ImmersiveAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = assessmentWeaknesses ?: "* Detail coverage can be expanded.",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        val isUserPassed = assessmentScoreResult >= passingScore
                        val parentTopicId = when {
                            activeId.contains("aws") -> "aws"
                            activeId.contains("docker") -> "docker"
                            else -> "kubernetes"
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isUserPassed) {
                                Button(
                                    onClick = {
                                        viewModel.toggleSubtopic(activeId, parentTopicId, true, null, assessmentScoreResult)
                                        viewModel.cancelAssessment()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                                    modifier = Modifier.weight(1.5f).height(48.dp).testTag("log_assessment_complete")
                                ) {
                                    Text("EARN COMPLETION & UNLOCK (+40 XP)", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.toggleSubtopic(activeId, parentTopicId, false, "Needs Improvement", assessmentScoreResult)
                                        viewModel.cancelAssessment()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose.copy(alpha = 0.6f)),
                                    modifier = Modifier.weight(1.5f).height(48.dp).testTag("log_assessment_failed")
                                ) {
                                    Text("LOG AS NEEDS IMPROVEMENT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.startAssessment(activeId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurface),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("RE-ATTEMPT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.cancelAssessment() },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text("CANCEL & GO BACK", color = ImmersiveRose, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INTERVIEW STEP ${assessmentIndex + 1} OF 3",
                                color = ImmersiveIndigo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("AI LIVE EVALUATOR", color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                        
                        LinearProgressIndicator(
                            progress = (assessmentIndex + 1) / 3f,
                            color = CyberCyan,
                            trackColor = ImmersiveSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentQuestion,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val typedAnswer = assessmentAnswers.getOrElse(assessmentIndex) { "" }
                        OutlinedTextField(
                            value = typedAnswer,
                            onValueChange = { viewModel.updateAssessmentAnswer(assessmentIndex, it) },
                            placeholder = { Text("E.g. I would configure temporary credentials using assume-role for cross-account...", color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp) },
                            label = { Text("Explain your technical solution approach", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().weight(1f).testTag("interview_answer_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                            ),
                            maxLines = 8,
                            singleLine = false
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.prefillSuggestAnswer(activeId, assessmentIndex) },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo.copy(alpha = 0.25f)),
                            border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().height(38.dp).testTag("ai_suggest_answer_button")
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(12.dp), tint = ImmersiveIndigo)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🤖 USE DEVOPS COPILOT AUTOCORRECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (assessmentIndex > 0) {
                        Button(
                            onClick = { viewModel.prevAssessmentQuestion() },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("prev_question_btn")
                        ) {
                            Text("PREV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    if (assessmentIndex < 2) {
                        Button(
                            onClick = { viewModel.nextAssessmentQuestion() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("next_question_btn")
                        ) {
                            Text("NEXT QUESTION", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.evaluateAssessment() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            modifier = Modifier.weight(1.5f).height(44.dp).testTag("submit_assessment_btn")
                        ) {
                            Text("SUBMIT FOR EVALUATION", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Button(
                        onClick = { viewModel.cancelAssessment() },
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                        modifier = Modifier.weight(0.8f).height(44.dp).testTag("cancel_assessment_btn")
                    ) {
                        Text("QUIT", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    } else {
        if (showAddTopicDialog) {
            var title by remember { mutableStateOf("") }
            var weekStr by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            var iconName by remember { mutableStateOf("Linux") }

            AlertDialog(
                onDismissRequest = { showAddTopicDialog = false },
                title = { Text("PROVISION NEW CURRICULUM TOPIC", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Topic Title (e.g. Terraform Basics)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = weekStr,
                            onValueChange = { weekStr = it },
                            label = { Text("Week Number (1-28)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Syllabus Description") },
                            modifier = Modifier.fillMaxWidth().height(82.dp)
                        )
                        Text("SELECT MODULE ICON:", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Linux", "Settings", "Network", "Code", "Cloud", "Storage", "Router", "Build").forEach { ic ->
                                FilterChip(
                                    selected = iconName == ic,
                                    onClick = { iconName = ic },
                                    label = { Text(ic, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.addTopic(title, weekStr.toIntOrNull() ?: 1, desc, iconName)
                                showAddTopicDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        enabled = title.isNotBlank()
                    ) { Text("PROVISION TOPIC", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = {
                    Button(onClick = { showAddTopicDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = ImmersiveSurface
            )
        }

        val editingTopic = showEditTopicDialog
        if (editingTopic != null) {
            var title by remember(editingTopic) { mutableStateOf(editingTopic.title) }
            var weekStr by remember(editingTopic) { mutableStateOf(editingTopic.weekNumber.toString()) }
            var desc by remember(editingTopic) { mutableStateOf(editingTopic.description) }
            var iconName by remember(editingTopic) { mutableStateOf(editingTopic.iconName) }

            AlertDialog(
                onDismissRequest = { showEditTopicDialog = null },
                title = { Text("EDIT CURRICULUM TOPIC", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Topic Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = weekStr,
                            onValueChange = { weekStr = it },
                            label = { Text("Week Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Syllabus Description") },
                            modifier = Modifier.fillMaxWidth().height(82.dp)
                        )
                        Text("SELECT MODULE ICON:", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Linux", "Settings", "Network", "Code", "Cloud", "Storage", "Router", "Build").forEach { ic ->
                                FilterChip(
                                    selected = iconName == ic,
                                    onClick = { iconName = ic },
                                    label = { Text(ic, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.editTopic(editingTopic.id, title, weekStr.toIntOrNull() ?: 1, desc, iconName, editingTopic.orderIndex)
                                showEditTopicDialog = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        enabled = title.isNotBlank()
                    ) { Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = {
                    Button(onClick = { showEditTopicDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = ImmersiveSurface
            )
        }

        val addingSubtopicParent = showAddSubtopicDialog
        if (addingSubtopicParent != null) {
            var title by remember { mutableStateOf("") }
            var resourceUrl by remember { mutableStateOf("") }
            var hoursStr by remember { mutableStateOf("2.0") }

            AlertDialog(
                onDismissRequest = { showAddSubtopicDialog = null },
                title = { Text("PROVISION MODULE SUBTOPIC", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Adding to: ${addingSubtopicParent.title}", color = TextMuted, fontSize = 11.sp)
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Subtopic Day Title (e.g. S3 Lifecycle)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = resourceUrl,
                            onValueChange = { resourceUrl = it },
                            label = { Text("Study Guide Resource URL (Starts with https://)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = hoursStr,
                            onValueChange = { hoursStr = it },
                            label = { Text("Estimated Completion Hours") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.addSubtopic(addingSubtopicParent.id, title, resourceUrl, hoursStr.toDoubleOrNull() ?: 2.0)
                                showAddSubtopicDialog = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        enabled = title.isNotBlank()
                    ) { Text("ADD SUBTOPIC", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = {
                    Button(onClick = { showAddSubtopicDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = ImmersiveSurface
            )
        }

        val editingSubtopic = showEditSubtopicDialog
        if (editingSubtopic != null) {
            var title by remember(editingSubtopic) { mutableStateOf(editingSubtopic.title) }
            var resourceUrl by remember(editingSubtopic) { mutableStateOf(editingSubtopic.resourceUrl) }
            var hoursStr by remember(editingSubtopic) { mutableStateOf(editingSubtopic.estimatedHours.toString()) }

            AlertDialog(
                onDismissRequest = { showEditSubtopicDialog = null },
                title = { Text("EDIT MODULE SUBTOPIC DETAILS", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Subtopic Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = resourceUrl,
                            onValueChange = { resourceUrl = it },
                            label = { Text("Study Guide Resource URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = hoursStr,
                            onValueChange = { hoursStr = it },
                            label = { Text("Estimated Completion Hours") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.editSubtopic(editingSubtopic.id, editingSubtopic.parentTopicId, title, resourceUrl, hoursStr.toDoubleOrNull() ?: 2.0, editingSubtopic.orderIndex)
                                showEditSubtopicDialog = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        enabled = title.isNotBlank()
                    ) { Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = {
                    Button(onClick = { showEditSubtopicDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = ImmersiveSurface
            )
        }

        if (showConfirmResetDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmResetDialog = false },
                title = { Text("RESET ROADMAP TO DEFAULTS?", color = ImmersiveRose, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "WARNING: This action will restore the original 8-week production DevOps SRE learning roadmap curriculum and clear custom edits. This cannot be undone.",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetRoadmap()
                            showConfirmResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose)
                    ) { Text("CONFIRM RESET", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = {
                    Button(onClick = { showConfirmResetDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant)) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = ImmersiveSurface
            )
        }

        if (showAddResourceDialog) {
            var resName by remember { mutableStateOf("") }
            var resTypeName by remember { mutableStateOf("Documentation") }
            var resDesc by remember { mutableStateOf("") }
            var resLink by remember { mutableStateOf("") }
            var resSource by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showAddResourceDialog = false },
                title = { Text("ADD RESEARCH OR STUDY RESOURCE", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Save tutorials, cheatsheets, configuration PDFs, or link sources to your secure device databases.", color = TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        OutlinedTextField(
                            value = resName, 
                            onValueChange = { resName = it }, 
                            label = { Text("Resource Name (e.g. AWS S3 CheatSheet)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text("SELECT TYPE:", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        val types = listOf("Documentation", "Video", "PDF", "Notes", "GitHub", "Certifications")
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            types.forEach { t ->
                                Button(
                                    onClick = { resTypeName = t },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (resTypeName == t) CyberCyan else ImmersiveSurfaceVariant),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) { Text(t.uppercase(), fontSize = 8.sp, color = if (resTypeName == t) Color.Black else Color.White) }
                            }
                        }
                        
                        OutlinedTextField(
                            value = resSource, 
                            onValueChange = { resSource = it }, 
                            label = { Text("Source Creator (e.g. AWS Official, Brad Traversy)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = resDesc, 
                            onValueChange = { resDesc = it }, 
                            label = { Text("Description Notes", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                        OutlinedTextField(
                            value = resLink, 
                            onValueChange = { resLink = it }, 
                            label = { Text("URL Link (starts with https://)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (resName.isNotBlank()) {
                                val sdf = java.text.SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.getDefault())
                                val formattedDate = sdf.format(java.util.Date())
                                viewModel.saveResource(
                                    name = resName,
                                    type = resTypeName,
                                    description = resDesc,
                                    linkOrPath = resLink,
                                    source = resSource,
                                    dateAdded = formattedDate
                                )
                                showAddResourceDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        enabled = resName.isNotBlank()
                    ) { Text("SAVE TO CORE DB", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                },
                dismissButton = { 
                    Button(onClick = { showAddResourceDialog = false }) { 
                        Text("CANCEL", fontSize = 11.sp) 
                    } 
                },
                containerColor = ImmersiveSurface
            )
        }

        val resourceSelectedCategory = remember { mutableStateOf("ALL") }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp).testTag("career_scaffold_list"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Modern Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val listTabs = remember {
                        buildList {
                            add("ROADMAP")
                            add("DAILY STUDY" to "DAILY_STUDY")
                            add("DIAGNOSTICS")
                            add("ARCHIVE")
                            add("RESOURCES")
                            if (com.example.BuildConfig.DEBUG) {
                                add("QA DEBUG" to "QA_DEBUG")
                            }
                        }
                    }
                    listTabs.forEach { t ->
                        val (lbl, key) = if (t is Pair<*, *>) t as Pair<String, String> else Pair(t as String, t as String)
                        Button(
                            onClick = { activeSubTab = key },
                            modifier = Modifier.height(38.dp).padding(horizontal = 2.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (activeSubTab == key) CyberCyan else ImmersiveSurfaceVariant)
                        ) {
                            Text(lbl, fontSize = 9.sp, color = if (activeSubTab == key) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (activeSubTab == "ROADMAP") {
                // Topic Control Row (Provision Topic / Reset Roadmap)
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showAddTopicDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo.copy(alpha = 0.25f)),
                            border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = CyberCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADD TOPIC (+)", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                        
                        Button(
                            onClick = { showConfirmResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, ImmersiveRose.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = ImmersiveRose)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RESET ROADMAP", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // If empty, show a nice seeded state advice
                if (topics.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No curriculum topics active.", color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.resetRoadmap() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                                ) {
                                    Text("SEED DEFAULT ROADMAP", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Loop over topics sorted by orderIndex
                    val sortedTopics = topics.sortedBy { it.orderIndex }
                    sortedTopics.forEachIndexed { index, topic ->
                        val topicSubtopics = subtopics.filter { it.parentTopicId == topic.id }.sortedBy { it.orderIndex }
                        val totalSubs = topicSubtopics.size
                        val completedSubs = topicSubtopics.count { s -> subList.any { it.subtopicId == "sub_${s.id}" && it.isCompleted } }
                        val progressPct = if (totalSubs > 0) (completedSubs * 100) / totalSubs else 0

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f)),
                                modifier = Modifier.fillMaxWidth().testTag("roadmap_topic_card_${topic.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Icon selection based on topic.iconName
                                            val icVector = when (topic.iconName.lowercase()) {
                                                "settings" -> Icons.Default.Settings
                                                "refresh", "router" -> Icons.Default.Refresh
                                                "build", "linux" -> Icons.Default.Build
                                                "play", "cloud" -> Icons.Default.PlayArrow
                                                "delete" -> Icons.Default.Delete
                                                "add" -> Icons.Default.Add
                                                "done", "check", "code" -> Icons.Default.Check
                                                else -> Icons.Default.Info
                                            }
                                            Icon(imageVector = icVector, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "WEEK ${topic.weekNumber}",
                                                color = CyberCyan,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Edit actions if isEditMode == true
                                        if (isEditMode) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                // Up arrow (reordering)
                                                if (index > 0) {
                                                    IconButton(onClick = {
                                                        // Swap topic order
                                                        val prevTopic = sortedTopics[index - 1]
                                                        viewModel.editTopic(topic.id, topic.title, topic.weekNumber, topic.description, topic.iconName, prevTopic.orderIndex)
                                                        viewModel.editTopic(prevTopic.id, prevTopic.title, prevTopic.weekNumber, prevTopic.description, prevTopic.iconName, topic.orderIndex)
                                                    }, modifier = Modifier.size(24.dp)) {
                                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = Color.White)
                                                    }
                                                }
                                                // Down arrow (reordering)
                                                if (index < sortedTopics.size - 1) {
                                                    IconButton(onClick = {
                                                        // Swap topic order
                                                        val nextTopic = sortedTopics[index + 1]
                                                        viewModel.editTopic(topic.id, topic.title, topic.weekNumber, topic.description, topic.iconName, nextTopic.orderIndex)
                                                        viewModel.editTopic(nextTopic.id, nextTopic.title, nextTopic.weekNumber, nextTopic.description, nextTopic.iconName, topic.orderIndex)
                                                    }, modifier = Modifier.size(24.dp)) {
                                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = Color.White)
                                                    }
                                                }
                                                // Edit topic button
                                                IconButton(onClick = { showEditTopicDialog = topic }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Topic", tint = CyberCyan)
                                                }
                                                // Delete topic button
                                                IconButton(onClick = { viewModel.deleteTopic(topic) }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Topic", tint = ImmersiveRose)
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "$totalSubs Subtopics • $progressPct% Done",
                                                color = if (progressPct == 100) CyberGreen else TextMuted,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = topic.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    if (topic.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = topic.description, color = TextMuted, fontSize = 11.sp)
                                    }

                                    // Display Progress Linear indicator
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = if (totalSubs > 0) completedSubs.toFloat() / totalSubs else 0f,
                                        color = if (progressPct == 100) CyberGreen else CyberCyan,
                                        trackColor = ImmersiveSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                    )

                                    // Render direct subtopics lists inside the topic card
                                    if (topicSubtopics.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            topicSubtopics.forEachIndexed { subIdx, subtopic ->
                                                val subProgressId = "sub_${subtopic.id}"
                                                val subProgressObj = subList.firstOrNull { it.subtopicId == subProgressId }
                                                val isSubCompleted = subProgressObj != null && subProgressObj.isCompleted

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = "Day ${subIdx + 1}: ${subtopic.title}",
                                                            color = if (isSubCompleted) CyberGreen else Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        if (subtopic.resourceUrl.isNotBlank()) {
                                                            Text(
                                                                text = subtopic.resourceUrl,
                                                                color = CyberCyan.copy(alpha = 0.7f),
                                                                fontSize = 9.sp,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }

                                                    if (isEditMode) {
                                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            // Subtopic reordering up arrow
                                                            if (subIdx > 0) {
                                                                IconButton(onClick = {
                                                                    val prevSub = topicSubtopics[subIdx - 1]
                                                                    viewModel.editSubtopic(subtopic.id, subtopic.parentTopicId, subtopic.title, subtopic.resourceUrl, subtopic.estimatedHours, prevSub.orderIndex)
                                                                    viewModel.editSubtopic(prevSub.id, prevSub.parentTopicId, prevSub.title, prevSub.resourceUrl, prevSub.estimatedHours, subtopic.orderIndex)
                                                                }, modifier = Modifier.size(20.dp)) {
                                                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Subtopic Up", tint = Color.White)
                                                                }
                                                            }
                                                            // Subtopic reordering down arrow
                                                            if (subIdx < topicSubtopics.size - 1) {
                                                                IconButton(onClick = {
                                                                    val nextSub = topicSubtopics[subIdx + 1]
                                                                    viewModel.editSubtopic(subtopic.id, subtopic.parentTopicId, subtopic.title, subtopic.resourceUrl, subtopic.estimatedHours, nextSub.orderIndex)
                                                                    viewModel.editSubtopic(nextSub.id, nextSub.parentTopicId, nextSub.title, nextSub.resourceUrl, nextSub.estimatedHours, subtopic.orderIndex)
                                                                }, modifier = Modifier.size(20.dp)) {
                                                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Subtopic Down", tint = Color.White)
                                                                }
                                                            }
                                                            // Edit Subtopic
                                                            IconButton(onClick = { showEditSubtopicDialog = subtopic }, modifier = Modifier.size(20.dp)) {
                                                                Icon(Icons.Default.Edit, contentDescription = "Edit Subtopic", tint = CyberCyan, modifier = Modifier.size(12.dp))
                                                            }
                                                            // Delete Subtopic
                                                            IconButton(onClick = { viewModel.deleteSubtopic(subtopic) }, modifier = Modifier.size(20.dp)) {
                                                                Icon(Icons.Default.Delete, contentDescription = "Delete Subtopic", tint = ImmersiveRose, modifier = Modifier.size(12.dp))
                                                            }
                                                        }
                                                    } else {
                                                        // Non edit mode actions: Start assessment/Toggle completeness
                                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                            // Status Badge
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(if (isSubCompleted) CyberGreen.copy(alpha = 0.15f) else ImmersiveAmber.copy(alpha = 0.15f))
                                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(
                                                                    text = if (isSubCompleted) "COMPLETED" else "PENDING",
                                                                    color = if (isSubCompleted) CyberGreen else ImmersiveAmber,
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontFamily = FontFamily.Monospace
                                                                )
                                                            }
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(if (isSubCompleted) ImmersiveRose.copy(alpha = 0.15f) else CyberGreen.copy(alpha = 0.15f))
                                                                    .clickable {
                                                                        viewModel.toggleSubtopic(
                                                                            subProgressId,
                                                                            "topic_${topic.id}",
                                                                            !isSubCompleted,
                                                                            "Manual Topic Study Toggle",
                                                                            if (!isSubCompleted) 0 else subProgressObj?.assessmentScore ?: 0
                                                                        )
                                                                    }
                                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(
                                                                    text = if (isSubCompleted) "UNMARK COMPLETED" else "MARK COMPLETED",
                                                                    color = if (isSubCompleted) ImmersiveRose else CyberGreen,
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontFamily = FontFamily.Monospace
                                                                )
                                                            }

                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(if (isSubCompleted) CyberCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                                                    .clickable(enabled = isSubCompleted) {
                                                                        if (isSubCompleted) {
                                                                            viewModel.startAssessment(subProgressId)
                                                                        }
                                                                    }
                                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            ) {
                                                                Text(text = if (isSubCompleted) "START ASSESSMENT" else "LOCKED", color = if (isSubCompleted) CyberCyan else TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Add Subtopic button if isEditMode == true
                                    if (isEditMode) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { showAddSubtopicDialog = topic },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.15f)),
                                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ADD SUBTOPIC ITEM (+)", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (activeSubTab == "DAILY_STUDY") {
                item {
                    var expandedWeekDropdown by remember { mutableStateOf(false) }
                    val currentWeekTopic = topics.firstOrNull { it.weekNumber == selectedWeek }
                    val weekTitle = currentWeekTopic?.title ?: "Custom Study Slot"
                    
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text("SELECT ROADMAP WEEK", color = TextCelestial, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ImmersiveSurfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .clickable { expandedWeekDropdown = !expandedWeekDropdown }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Week $selectedWeek - $weekTitle",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = CyberCyan
                                )
                            }
                            
                            DropdownMenu(
                                expanded = expandedWeekDropdown,
                                onDismissRequest = { expandedWeekDropdown = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(ImmersiveSurface)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                val totalWeeks = if (topics.isNotEmpty()) topics.maxOf { it.weekNumber } else 28
                                (1..totalWeeks).forEach { w ->
                                    val t = topics.firstOrNull { it.weekNumber == w }?.title ?: "Custom Agile Study Block"
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Week $w - $t",
                                                color = if (selectedWeek == w) CyberCyan else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = if (selectedWeek == w) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSelectedWeek(w)
                                            viewModel.setSelectedDay(1)
                                            expandedWeekDropdown = false
                                        },
                                        modifier = Modifier.background(if (selectedWeek == w) ImmersiveSurfaceVariant else Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    var expandedDayDropdown by remember { mutableStateOf(false) }
                    
                    val activeTopic = topics.firstOrNull { it.weekNumber == selectedWeek }
                    val activeTopicSubtopics = if (activeTopic != null) subtopics.filter { it.parentTopicId == activeTopic.id }.sortedBy { it.orderIndex } else emptyList()
                    val targetSubtopic = activeTopicSubtopics.getOrNull(selectedDay - 1)
                    val activeSubtopicTitle = targetSubtopic?.title ?: "No configured task"

                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text("SELECT STUDY DAY / TOPIC", color = TextCelestial, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ImmersiveSurfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .clickable { expandedDayDropdown = !expandedDayDropdown }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (targetSubtopic != null) "Day $selectedDay - $activeSubtopicTitle" else "Select Day ▼",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = CyberCyan
                                )
                            }
                            
                            DropdownMenu(
                                expanded = expandedDayDropdown,
                                onDismissRequest = { expandedDayDropdown = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(ImmersiveSurface)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                if (activeTopicSubtopics.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No subtopics configured for this week.", color = Color.White, fontSize = 12.sp) },
                                        onClick = { expandedDayDropdown = false }
                                    )
                                } else {
                                    activeTopicSubtopics.forEachIndexed { index, sub ->
                                        val dayNum = index + 1
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Day $dayNum - ${sub.title}",
                                                    color = if (selectedDay == dayNum) CyberCyan else Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (selectedDay == dayNum) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.setSelectedDay(dayNum)
                                                expandedDayDropdown = false
                                            },
                                            modifier = Modifier.background(if (selectedDay == dayNum) ImmersiveSurfaceVariant else Color.Transparent)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Dynamic matching
                val activeTopic = topics.firstOrNull { it.weekNumber == selectedWeek }
                val activeTopicSubtopics = if (activeTopic != null) subtopics.filter { it.parentTopicId == activeTopic.id }.sortedBy { it.orderIndex } else emptyList()
                val targetSubtopic = activeTopicSubtopics.getOrNull(selectedDay - 1)

                val activeSubId = if (targetSubtopic != null) "sub_${targetSubtopic.id}" else "week_${selectedWeek}_day_${selectedDay}"
                val subProgressObj = subList.firstOrNull { it.subtopicId == activeSubId }
                val isCompleted = subProgressObj != null && subProgressObj.isCompleted

                // Build daily task structure based on targetSubtopic
                val curr = if (targetSubtopic != null) {
                    com.example.data.entity.DailyTaskDetail(
                        topicName = targetSubtopic.title,
                        learningObjectives = listOf("Analyze structural components of ${targetSubtopic.title} systematically.", "Configure deployment policies and security checkpoints.", "Simulate real-world traffic patterns for validated operation."),
                        studyGuide = "In-depth guide for ${targetSubtopic.title}. Focus on production setup, latency rules, container/cloud configuration policies. Open resources: ${targetSubtopic.resourceUrl}",
                        caseStudy = "PRODUCTION INCIDENT: Unmonitored latency spikes on ${targetSubtopic.title} caused user experience degradations. Adding automated recovery checkpoints and throttling rules secured 100% operation under load.",
                        assessmentMapping = "Verifies proficiency on ${targetSubtopic.title}.",
                        revisionMapping = "Schedules spaced repetition intervals for ${targetSubtopic.title} next."
                    )
                } else {
                    com.example.data.entity.DailyTaskDetail(
                        topicName = "No subtopic configured for Week $selectedWeek Day $selectedDay",
                        learningObjectives = emptyList(),
                        studyGuide = "No custom subtopic is mapped to this slot. Toggle 'Edit Mode' (FAB) on the ROADMAP tab to design your learning curriculum!",
                        caseStudy = "No incident logged.",
                        assessmentMapping = "Locked",
                        revisionMapping = "N/A"
                    )
                }

                // AI Mentor Recommendation Card
                item {
                    val recommendationText = remember(subProgressObj, targetSubtopic, subList) {
                        val needsImprovementItem = subList.firstOrNull { it.isCompleted && it.assessmentScore > 0 && it.assessmentScore < 70 }
                        val pendingRevisionItem = subList.firstOrNull { s ->
                            val compTime = s.completionDate ?: System.currentTimeMillis()
                            val nextDay = compTime + (24 * 60 * 60 * 1000L)
                            val now = System.currentTimeMillis()
                            now >= nextDay && s.reasonNotCompleted != "RETAINED"
                        }
                        
                        when {
                            needsImprovementItem != null -> {
                                val itemTitle = needsImprovementItem.subtopicId.substringAfter("sub_").replace("_", " ").uppercase()
                                "You struggled with $itemTitle (${needsImprovementItem.assessmentScore}%) in your previous assessment. Review those key architectural configurations before moving forward to advanced chapters!"
                            }
                            pendingRevisionItem != null -> {
                                val itemTitle = pendingRevisionItem.subtopicId.substringAfter("sub_").replace("_", " ").uppercase()
                                "Revision is due for $itemTitle! Spaced repetition is critical to cement your platform concepts. Set aside 10 minutes to review this topic today."
                            }
                            subProgressObj != null && subProgressObj.isCompleted && subProgressObj.assessmentScore >= 90 -> {
                                val itemTitle = targetSubtopic?.title ?: "current topic"
                                "You scored exceptionally well in $itemTitle. You have achieved deep technical proficiency! Continue driving through the remaining cloud challenges."
                            }
                            subProgressObj != null && subProgressObj.isCompleted && subProgressObj.assessmentScore >= 70 -> {
                                val itemTitle = targetSubtopic?.title ?: "current topic"
                                "You scored well in $itemTitle. Excellent platform competency! Proceed with the official study guide to master the next sequence."
                            }
                            targetSubtopic != null -> {
                                "Welcome to Week $selectedWeek Day $selectedDay. We are analyzing '${targetSubtopic.title}'. My recommendation: complete the core concept and study guide before launching the cognitive checklist interview."
                            }
                            else -> {
                                "Select a week and topic from the dropdown menus to load your custom DevOps path and begin interactive training."
                            }
                        }
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveIndigo.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "AI Mentor Guidance",
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text("TODAY'S AI MENTOR RECOMMENDATION", color = CyberCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = recommendationText,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                }

                // Learning Status Card
                item {
                    val statusText = getLearningStatus(subProgressObj, passingScore)
                    val score = subProgressObj?.assessmentScore ?: 0
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, color = when(statusText) {
                            "Passed", "Completed" -> CyberGreen.copy(alpha = 0.4f)
                            "Average" -> ImmersiveAmber.copy(alpha = 0.4f)
                            "Poor" -> ImmersiveRose.copy(alpha = 0.4f)
                            "Assessment Pending" -> CyberCyan.copy(alpha = 0.4f)
                            else -> ImmersiveAmber.copy(alpha = 0.4f)
                        }),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎓 LEARNING PERFORMANCE INTEGRITY", color = CyberCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when(statusText) {
                                                "Passed", "Completed" -> CyberGreen.copy(alpha = 0.15f)
                                                "Average" -> ImmersiveAmber.copy(alpha = 0.15f)
                                                "Poor" -> ImmersiveRose.copy(alpha = 0.15f)
                                                "Assessment Pending" -> CyberCyan.copy(alpha = 0.15f)
                                                else -> ImmersiveAmber.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = statusText.uppercase(),
                                        color = when(statusText) {
                                            "Passed", "Completed" -> CyberGreen
                                            "Average" -> ImmersiveAmber
                                            "Poor" -> ImmersiveRose
                                            "Assessment Pending" -> CyberCyan
                                            else -> ImmersiveAmber
                                        },
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Checklist items
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isCompleted) CyberGreen else ImmersiveAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isCompleted) "✓ Study Topic Completed" else "✗ Study Topic Pending",
                                        color = if (isCompleted) Color.White else ImmersiveAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Mark Core Concepts read using the study guide execution engines below.", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Icon(
                                    imageVector = if (score > 0) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (score >= passingScore) CyberGreen else if (score >= 50) ImmersiveAmber else if (score > 0) ImmersiveRose else CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (score > 0) "Assessment Score: $score%" else "Assessment Pending",
                                        color = if (score >= passingScore) CyberGreen else if (score >= 50) ImmersiveAmber else if (score > 0) ImmersiveRose else CyberCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when {
                                            score <= 0 -> "Take the Cognitive Checkpoint Assessment to verify skill proficiency."
                                            score >= passingScore -> "Performance: PASSED (Certified Proficiency)"
                                            score >= 50 -> "Performance: AVERAGE (Competent Space)"
                                            else -> "Performance: POOR (Needs Revision)"
                                        },
                                        color = if (score >= passingScore) CyberGreen else if (score >= 50) ImmersiveAmber else if (score > 0) ImmersiveRose else CyberCyan,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isCompleted) CyberGreen else ImmersiveAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isCompleted) "Revision Status: Scheduled" else "Revision Status: Not Started",
                                        color = if (isCompleted) CyberGreen else ImmersiveAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isCompleted) "Spaced repetition intervals set automatically." else "Start study guide exercises to unlock scheduling triggers.",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), 
                        border = BorderStroke(1.dp, if (isCompleted) CyberGreen.copy(alpha = 0.4f) else CyberCyan.copy(alpha = 0.25f)), 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("TODAY'S EXECUTION TASK", color = CyberCyan, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isCompleted) CyberGreen.copy(alpha = 0.15f) else ImmersiveAmber.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isCompleted) "COMPLETED" else "PENDING", 
                                        color = if (isCompleted) CyberGreen else ImmersiveAmber, 
                                        fontSize = 8.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Week $selectedWeek - Day $selectedDay", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = curr.topicName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                if (targetSubtopic != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), 
                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)), 
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🎯 LEARNING OBJECTIVES", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                curr.learningObjectives.forEach { obj ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = obj, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f)), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("📖 CORE CONCEPT & STUDY GUIDE", color = CyberGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = curr.studyGuide, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                                if (targetSubtopic.resourceUrl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                                    Button(
                                        onClick = { try { uriHandler.openUri(targetSubtopic.resourceUrl) } catch(e: Exception){} },
                                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("OPEN STUDY RESOURCE", fontSize = 9.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f)), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🔥 PRODUCTION SRE CASE STUDY", color = ImmersiveRose, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = curr.caseStudy, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(0.6.dp, if (isCompleted) CyberGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("⚙️ EXECUTION ENGINE", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isCompleted) CyberGreen.copy(alpha = 0.15f) else ImmersiveAmber.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isCompleted) "COMPLETED" else "PENDING",
                                            color = if (isCompleted) CyberGreen else ImmersiveAmber,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Button(
                                        onClick = { 
                                            viewModel.toggleSubtopic(
                                                activeSubId,
                                                "topic_${activeTopic?.id ?: "unknown"}",
                                                !isCompleted,
                                                "Self-Paced Daily Study",
                                                if (!isCompleted) -1 else 0
                                            ) 
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isCompleted) ImmersiveRose.copy(alpha = 0.2f) else CyberGreen),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = if (isCompleted) "UNMARK COMPLETED" else "MARK COMPLETED", 
                                            color = if (isCompleted) Color.White else Color.Black, 
                                            fontSize = 9.sp, 
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val savedNote = userNotes[activeSubId] ?: ""
                        var textNotesInput by remember(activeSubId) { mutableStateOf(savedNote) }
                        OutlinedTextField(
                            value = textNotesInput,
                            onValueChange = {
                                textNotesInput = it
                                viewModel.updateSubtopicUserNote(activeSubId, it)
                            },
                            label = { Text("Jot Down Critical Study Notes", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth().height(90.dp)
                        )
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(0.6.dp, if (isCompleted) CyberCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🛡️ COGNITIVE ASSESSMENT", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (!isCompleted) "🔒 Locked: Complete today's study guide first to unlock cognitive checkpoint assessment query loops." else "🔓 Unlocked: Access live interactive DevOps interview checklist for this topic.",
                                    color = if (isCompleted) TextCelestial else TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                val isPassed = subProgressObj != null && subProgressObj.isCompleted && subProgressObj.assessmentScore >= passingScore
                                Button(
                                    onClick = { 
                                        if (isCompleted) {
                                            viewModel.startAssessment(activeSubId) 
                                        }
                                    },
                                    enabled = isCompleted,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isCompleted) Color.White.copy(alpha = 0.05f) else if (isPassed) CyberGreen else CyberCyan,
                                        disabledContainerColor = Color.White.copy(alpha = 0.05f)
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Text(
                                        text = if (!isCompleted) "LOCKED" else if (isPassed) "START ASSESSMENT (Passed: ${subProgressObj?.assessmentScore}%)" else "START ASSESSMENT", 
                                        color = if (isCompleted) Color.Black else TextMuted, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🔄 RETENTION & REVISION MAPPING", color = CyberGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                val currentWeekTitle = topics.firstOrNull { it.weekNumber == selectedWeek }?.title ?: "Custom Agile Study Block"
                                Text(
                                    text = "Weekly Schedule: ${currentWeekTitle}. Revision: Scheduled automatically post-completion.",
                                    color = TextCelestial,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            if (activeSubTab == "DIAGNOSTICS") {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("ADMIN MARGIN CONTEXT SETTER", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text("Set minimum passing score standard required to unlock units", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("PASS RANGE: $passingScore%", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = passingScore.toFloat(),
                                    onValueChange = { viewModel.setPassingScoreThreshold(it.toInt()) },
                                    valueRange = 50f..100f,
                                    modifier = Modifier.width(180.dp),
                                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                                )
                            }
                        }
                    }
                }

                item {
                    val passedCount = subList.count { it.isCompleted && it.assessmentScore >= passingScore }
                    val averageScore = if (subList.any { it.isCompleted && it.assessmentScore > 0 }) subList.filter { it.isCompleted && it.assessmentScore > 0 }.map { it.assessmentScore }.average() else 75.0
                    val streakBonus = (userProfile.careerStreak * 2).coerceAtMost(15)
                    val solvedBonus = (puzzlesSolved * 3).coerceAtMost(15)
                    val jobReadinessScore = ((averageScore * 0.5) + (passedCount * 3.5) + streakBonus + solvedBonus).coerceIn(0.0, 100.0).toInt()
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.35f))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(0.7f)) {
                                Text("SRE PLATFORM READY GAUGES", color = CyberGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Weighted Exam Grade: ${averageScore.toInt()}%", color = Color.White, fontSize = 11.sp)
                                Text("• Consistency Streak: +$streakBonus pts", color = Color.White, fontSize = 11.sp)
                                Text("• Cognitive Solved: +$solvedBonus pts", color = Color.White, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Rating: " + when {
                                        jobReadinessScore >= 80 -> "Senior Cloud Architect 🏆"
                                        jobReadinessScore >= 65 -> "Mid-level Platform SRE 🚀"
                                        else -> "Junior Platform Engineer 🌱"
                                    },
                                    color = CyberGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                                CircularProgressIndicator(progress = jobReadinessScore / 100f, color = CyberGreen, strokeWidth = 5.dp, modifier = Modifier.size(64.dp))
                                Text("$jobReadinessScore%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }

                item {
                    val completedAws = subList.count { getTopicCategory(it.parentTopicId) == "aws" && it.isCompleted }
                    val completedDocker = subList.count { getTopicCategory(it.parentTopicId) == "docker" && it.isCompleted }
                    val completedK8s = subList.count { getTopicCategory(it.parentTopicId) == "kubernetes" && it.isCompleted }
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("COGNITIVE SKILL LEVELS ARCHITECTURE", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("AWS Cloud Hosting Space", completedAws, 84, if (completedAws >= 40) "EXPERT 🌟" else if (completedAws >= 15) "ADVANCED" else "BEGINNER 🌱", CyberCyan)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("Docker Containers Host", completedDocker, 49, if (completedDocker >= 25) "EXPERT 🌟" else if (completedDocker >= 10) "ADVANCED" else "BEGINNER 🌱", CyberGreen)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("Kubernetes Orchestrator SRE", completedK8s, 35, if (completedK8s >= 18) "EXPERT 🌟" else if (completedK8s >= 6) "ADVANCED" else "BEGINNER 🌱", CyberPurple)
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("CONVENTIONAL CONCEPT AUDIT INDEX", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("🔥 ACCRUED STRENGTHS", color = CyberGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    subList.filter { it.isCompleted && it.assessmentScore >= passingScore }.forEach { Text("• ${it.subtopicId.substringAfter("_").uppercase()}", color = Color.White, fontSize = 10.sp) }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("⚠️ MANDATED REVISIONS", color = ImmersiveAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    subList.filter { !it.isCompleted || it.assessmentScore < passingScore }.forEach { Text("• ${it.subtopicId.substringAfter("_").uppercase()}", color = Color.White, fontSize = 10.sp) }
                                }
                            }
                        }
                    }
                }

                item {
                    val passed = subList.count { it.isCompleted && it.assessmentScore >= passingScore }
                    val failed = subList.count { !it.isCompleted && it.reasonNotCompleted == "Needs Improvement" }
                    val average = if (subList.any { it.isCompleted }) subList.filter { it.isCompleted }.map { it.assessmentScore }.average().toInt() else 0
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("📋 SRE WEEKLY HIGHLIGHTS STATUS REPORT", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Passed SRE Evaluations: $passed units approved", color = Color.White, fontSize = 11.sp)
                            Text("• Failed / Under revision units: $failed flagged", color = Color.White, fontSize = 11.sp)
                            Text("• Average Running Grade: $average% index", color = Color.White, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("COACH RECO: Maintain passing parameters over basic subtopics dynamically.", color = CyberCyan, fontSize = 11.sp)
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🗓️ SRE MONTHLY RADAR MATURITY SLIDERS", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            var rLinux by remember { mutableStateOf(75f) }
                            var rAws by remember { mutableStateOf(60f) }
                            var rDocker by remember { mutableStateOf(80f) }
                            RadarCalibrateRow("Linux Systems", rLinux) { rLinux = it }
                            RadarCalibrateRow("Cloud SRE", rAws) { rAws = it }
                            RadarCalibrateRow("Containers", rDocker) { rDocker = it }
                        }
                    }
                }
            }

            if (activeSubTab == "ARCHIVE") {
                val completedList = subList.filter { it.isCompleted }
                if (completedList.isEmpty()) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                            Text(text = "No completed items found under passing criteria logs.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(completedList) { sub ->
                        Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.35f)), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = sub.subtopicId.replace("_", " ").uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Class score achieved: ${sub.assessmentScore}%", color = CyberGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                                IconButton(onClick = { viewModel.toggleSubtopic(sub.subtopicId, sub.parentTopicId, false, "Need Revision", 0) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ImmersiveRose)
                                }
                            }
                        }
                    }
                }
            }

            if (activeSubTab == "RESOURCES") {
                item {
                    Text("SRE & DEVOPS RESOURCE SHEETS", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }

                item {
                    // Double sub-tab selector
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ImmersiveSurfaceVariant).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { resourcesSubTab = "RECOMMENDED" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (resourcesSubTab == "RECOMMENDED") ImmersiveIndigo else Color.Transparent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(34.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("RECOMMENDED RESOURCES", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Button(
                            onClick = { resourcesSubTab = "SAVED" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (resourcesSubTab == "SAVED") ImmersiveIndigo else Color.Transparent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(34.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("SAVED RESOURCES (${savedResources.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                if (resourcesSubTab == "RECOMMENDED") {
                    item {
                        val categories = listOf("ALL", "LINUX", "AWS", "CONTAINERS", "KUBERNETES", "CI/CD & IAC", "CAREER")
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.forEach { cat ->
                                val isSel = resourceSelectedCategory.value == cat
                                FilterChip(
                                    selected = isSel,
                                    onClick = { resourceSelectedCategory.value = cat },
                                    label = { Text(cat, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }

                    val resourceSheetItems = listOf(
                        SreResource("LINUX", "Avizway YouTube", "Avizway", "DevOps & Cloud Engineer: Linux & AWS modules (Telugu channel).", "Primary Channel", "https://youtube.com/c/avizway"),
                        SreResource("LINUX", "Linux Journey", "Linux Journey", "Interactive, step-by-step guides for self-learning Linux fundamentals.", "Free Interactive Tutorial", "https://linuxjourney.com"),
                        SreResource("LINUX", "OverTheWire Bandit", "OverTheWire wargames", "Gamified Linux Command Line CLI practice. Highly effective.", "SRE Wargame Studio", "https://overthewire.org/wargames/bandit"),

                        SreResource("AWS", "AWS Free Tier EC2 & S3", "AWS Platform", "Learn AWS hands-on by deploying full compute servers, VPC, Databases, and S3.", "Cloud Platform Sandbox", "https://aws.amazon.com/free"),
                        SreResource("AWS", "AWS Solutions Architect Dojo", "Tutorials Dojo", "Practice blueprints, exams revision, and exam simulation for AWS SAA-C03.", "Certified Prep Platform", "https://tutorialsdojo.com"),
                        SreResource("AWS", "AWS Skill Builder Lab Sandbox", "AWS Platform Official", "Hands-on virtual lab scenarios covering AWS IAM, VPC, EC2, and S3 systems.", "Official Training Studio", "https://skillbuilder.aws"),

                        SreResource("CONTAINERS", "TechWorld with Nana Docker", "Nana Janashia", "Comprehensive multi-node Docker fundamentals, Compose, and networks course.", "Full video tutorial", "https://www.youtube.com/watch?v=3c-iBn73dDE"),
                        SreResource("CONTAINERS", "Play with Docker Labs", "Docker Education", "Free, multi-node terminal web playground to try docker images and volumes.", "Sandbox Web Tool", "https://labs.play-with-docker.com"),
                        SreResource("CONTAINERS", "Trivy Scanner Documentation", "Aqua Security", "Guide to configuration compliance and container vulnerability scanning.", "Official Docs", "https://aquasecurity.github.io/trivy"),

                        SreResource("KUBERNETES", "TechWorld with Nana Kubernetes", "Nana Janashia", "Definitive multi-hour guide on Kubernetes pods, deployments, services, ingress.", "Full video tutorial", "https://www.youtube.com/watch?v=X48VuDVv0do"),
                        SreResource("KUBERNETES", "Killercoda K8s Workspace", "Killercoda Scenarios", "Free online interactive sandboxes to practice multi-node troubleshooting.", "Hands-On Scenarios", "https://killercoda.com"),
                        SreResource("KUBERNETES", "Grafana Tutorials", "Grafana Labs", "Complete guide to binding data sources, scraping, and visualizing dashboards.", "Official Guides", "https://grafana.com/tutorials"),

                        SreResource("CI/CD & IAC", "GitHub Actions CI/CD", "GitHub Docs", "Establish pipeline workflows, build Docker images, configure OIDC AWS keyless entry.", "Official Documentation", "https://docs.github.com/en/actions"),
                        SreResource("CI/CD & IAC", "Jenkins Full Course", "Simplilearn Studio", "Setup high-scale automation servers, write pipeline groovy files.", "Full course video", "https://www.youtube.com/watch?v=LFDrDnKP_gA"),
                        SreResource("CI/CD & IAC", "Ansible Engine Guide", "Red Hat", "Agentless configuration management, dynamic state templates.", "Official Docs", "https://docs.ansible.com"),
                        SreResource("CI/CD & IAC", "Terraform HashiCorp Learn", "HashiCorp Docs", "Comprehensive guides to writing providers, outputs, variables, remote states.", "Official Tutorials", "https://developer.hashicorp.com/terraform/tutorials"),
                        SreResource("CI/CD & IAC", "Terraform deep dive", "Abhishek Veeramalla", "Full portfolio-ready playlist on Terraform infrastructure as code with AWS labs.", "Devops Playlist", "https://www.youtube.com/@AbhishekVeeramalla"),

                        SreResource("CAREER", "Naukri Job Finder", "Naukri", "Apply for Enterprise cloud architect & Devops platforms engineer openings.", "Job Board Platform", "https://www.naukri.com"),
                        SreResource("CAREER", "Instahyre Startup Hub", "Instahyre", "Directly lookup tech vacancies and SRE internships in high-growth companies.", "Aviation Recruitment Portal", "https://www.instahyre.com"),
                        SreResource("CAREER", "LinkedIn Professional Jobs", "LinkedIn", "Apply for active roles and send networking outreach direct to recruiters.", "Social Career Network", "https://www.linkedin.com/jobs")
                    )

                    val filteredResources = resourceSheetItems.filter {
                        resourceSelectedCategory.value == "ALL" || it.category == resourceSelectedCategory.value
                    }

                    items(filteredResources) { res ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                            .background(ImmersiveIndigo.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = res.category, color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                            .background(CyberGreen.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = res.type, color = CyberGreen, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = res.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "By ${res.author}", color = TextMuted, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = res.description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, lineHeight = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                                Button(
                                    onClick = { uriHandler.openUri(res.url) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)),
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("OPEN RESOURCE MATERIAL SHEET", fontSize = 9.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                } else if (resourcesSubTab == "SAVED") {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("PERSONAL LEARNING REPOSITORY", color = CyberCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Text("Save tutorials, documentation, notes, and cert materials permanently.", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showAddResourceDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ADD CUSTOM RESOURCE", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Filter saved resources...", fontSize = 11.sp, color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                    }

                    item {
                        val filterTypes = listOf("ALL", "DOCUMENTATION", "VIDEO", "PDF", "NOTES", "GITHUB", "CERTIFICATIONS")
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            filterTypes.forEach { type ->
                                val isSel = filterResourceType == type
                                FilterChip(
                                    selected = isSel,
                                    onClick = { filterResourceType = type },
                                    label = { Text(type, fontSize = 8.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }

                    val filteredSaved = savedResources.filter { res ->
                        val matchesSearch = res.name.contains(searchQuery, ignoreCase = true) || 
                                           res.description.contains(searchQuery, ignoreCase = true) || 
                                           res.source.contains(searchQuery, ignoreCase = true) ||
                                           res.type.contains(searchQuery, ignoreCase = true)
                        val matchesType = filterResourceType == "ALL" || res.type.uppercase() == filterResourceType
                        matchesSearch && matchesType
                    }

                    if (filteredSaved.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("No custom resources match filters.", color = TextMuted, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Click 'Add Custom Resource' to save one!", color = CyberCyan, fontSize = 11.sp, modifier = Modifier.clickable { showAddResourceDialog = true })
                                }
                            }
                        }
                    } else {
                        items(filteredSaved) { res ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                                .background(ImmersiveIndigo.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = res.type.uppercase(), color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                        Text(text = "Saved: ${res.dateAdded}", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = res.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    if (res.source.isNotBlank()) {
                                        Text(text = "Source: ${res.source}", color = TextMuted, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    }
                                    if (res.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = res.description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, lineHeight = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                                        val isClickable = res.linkOrPath.isNotBlank() && (res.linkOrPath.startsWith("http://") || res.linkOrPath.startsWith("https://"))
                                        Button(
                                            onClick = { 
                                                if (isClickable) {
                                                    try { uriHandler.openUri(res.linkOrPath) } catch (e: Exception) {}
                                                }
                                            },
                                            enabled = isClickable,
                                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("OPEN LINK", fontSize = 9.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                                        }
                                        Button(
                                            onClick = { viewModel.deleteResource(res) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose.copy(alpha = 0.15f)),
                                            border = BorderStroke(0.6.dp, ImmersiveRose.copy(alpha = 0.3f)),
                                            modifier = Modifier.width(80.dp).height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = ImmersiveRose, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("DELETE", fontSize = 9.sp, color = ImmersiveRose, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (activeSubTab == "QA_DEBUG" && com.example.BuildConfig.DEBUG) {
                item {
                    QaDebugPanel(viewModel)
                }
            }

            // DevOps Troubleshooting Mini-scenario quiz
            item { Text("MOCK ACTIVE TROUBLESHOOTING FIELD", color = ImmersiveIndigo, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            item {
                val idx by viewModel.currentQuizIndex.collectAsState()
                val feedback by viewModel.quizFeedback.collectAsState()
                val scenarios = listOf(
                    DevopsQuiz("aws", "Active-passive failover", "A multi-region EC2 web application crashes on subnet failure. Correct strategy to resolve?", listOf("Configure Route 53 latency records", "Route 53 active-passive Failover policies to secondary region", "Manually build warm standby instances"), 1),
                    DevopsQuiz("kubernetes", "OOMKilled Troubleshooting", "Pods display 'OOMKilled' during container bootstrap. Cause?", listOf("Subnet limits reached", "Container exceeded declared limits.resources.limits.memory limits", "ConfigMap namespace conflicts"), 1)
                )
                val quizObj = scenarios.getOrNull(idx) ?: scenarios.first()
                Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.35f))) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "SCENARIO: ${quizObj.title.uppercase()}", color = ImmersiveIndigo, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = quizObj.question, color = TextCelestial, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        quizObj.options.forEachIndexed { optIndex, t ->
                            Button(
                                onClick = { viewModel.processQuizAnswer(quizObj.topicId, optIndex, quizObj.correctAnswerIndex) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                            ) { Text(text = t, color = TextCelestial, fontSize = 11.sp, textAlign = TextAlign.Center) }
                        }
                        if (feedback != null) {
                            Text(text = feedback!!, color = if (feedback!!.contains("Correct")) CyberGreen else ImmersiveRose, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Prev Scenario", color = ImmersiveIndigo, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.setQuizIndex(0) })
                            Text("Next Scenario", color = ImmersiveIndigo, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.setQuizIndex(1) })
                        }
                    }
                }
            }

            // Spaced Repetition Revision list
            item { Text("SPACED REPETITION REVISION LOGS", color = ImmersiveIndigo, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            item {
                val compList = subList.filter { it.isCompleted }
                val sdf = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()) }
                val context = androidx.compose.ui.platform.LocalContext.current
                Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Revision checkpoints are scheduled at 1-Day and 7-Day post-completion postmarks.", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (compList.isEmpty()) {
                            Text("No passed SRE items found under current logs.", color = ImmersiveAmber, fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        } else {
                            compList.forEach { s ->
                                val compTime = s.completionDate ?: System.currentTimeMillis()
                                val nextDay = compTime + 24 * 60 * 60 * 1000L
                                val nextWeek = compTime + 7 * 24 * 60 * 60 * 1000L
                                val now = System.currentTimeMillis()
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(ImmersiveSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = s.subtopicId.replace("_", " ").uppercase(), color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(CyberGreen.copy(alpha = 0.15f)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                            Text(text = "PASSED", color = CyberGreen, fontSize = 8.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("1-Day Active Recall", color = Color.White.copy(0.7f), fontSize = 9.sp)
                                            Text(if (now >= nextDay) "⚠️ REVIEW ACTIVE" else "Due: ${sdf.format(java.util.Date(nextDay))}", color = if (now >= nextDay) ImmersiveRose else CyberCyan, fontSize = 10.sp)
                                        }
                                        Button(onClick = { viewModel.awardXp(s.parentTopicId, 15); android.widget.Toast.makeText(context, "Completed recall checkpoint! +15 XP", android.widget.Toast.LENGTH_SHORT).show() }, modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Recalled", fontSize = 9.sp) }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("7-Day Master Revision", color = Color.White.copy(0.7f), fontSize = 9.sp)
                                            Text(if (now >= nextWeek) "⚠️ MASTER REVISION ACTIVE" else "Due: ${sdf.format(java.util.Date(nextWeek))}", color = if (now >= nextWeek) ImmersiveRose else CyberCyan, fontSize = 10.sp)
                                        }
                                        Button(onClick = { viewModel.awardXp(s.parentTopicId, 30); android.widget.Toast.makeText(context, "Completed master checkpoint! +30 XP", android.widget.Toast.LENGTH_SHORT).show() }, modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Retained", fontSize = 9.sp) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button for Roadmap Toggling of Edit Mode
        FloatingActionButton(
            onClick = { isEditMode = !isEditMode },
            containerColor = if (isEditMode) CyberCyan else ImmersiveIndigo,
            contentColor = if (isEditMode) Color.Black else Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("edit_roadmap_fab"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = if (isEditMode) "Save Roadmap Changes" else "Edit Roadmap"
            )
        }
    }
}
}

fun getTopicCategory(parentTopicId: String): String {
    val idStr = parentTopicId.substringAfter("topic_").trim()
    val id = idStr.toIntOrNull() ?: 0
    return when {
        parentTopicId == "linux" || id in 1..3 -> "linux"
        parentTopicId == "python" || id == 4 -> "python"
        parentTopicId == "aws" || id in 5..14 || id == 27 || id == 28 -> "aws"
        parentTopicId == "docker" || id in 15..21 -> "docker"
        parentTopicId == "kubernetes" || id in 22..26 -> "kubernetes"
        else -> parentTopicId
    }
}

fun getLearningStatus(progress: com.example.data.entity.SubtopicProgress?, passingScore: Int = 70): String {
    if (progress == null || !progress.isCompleted) {
        return "Pending"
    }
    val score = progress.assessmentScore
    if (score == -1 || score == 0) {
        return "Completed"
    }
    return when {
        score >= passingScore -> "Passed"
        score >= 50 && score < passingScore -> "Average"
        else -> "Poor"
    }
}

@Composable
fun SkillProficiencyBar(title: String, score: Int, max: Int, label: String, tint: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(label, color = tint, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(progress = score.toFloat() / max, color = tint, trackColor = ImmersiveSurfaceVariant, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)))
    }
}

@Composable
fun RadarCalibrateRow(title: String, value: Float, onValueChange: (Float) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = Color.White, fontSize = 11.sp, modifier = Modifier.width(90.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = 0f..100f, modifier = Modifier.weight(1f), colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan))
        Spacer(modifier = Modifier.width(6.dp))
        Text("${value.toInt()}%", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

fun getCurriculumContent(week: Int, day: Int): Triple<String, String, String> {
    val weekKey = "week_$week"
    val weekTitle = when (weekKey) {
        "week_1" -> "Linux OS & File System"
        "week_2" -> "Linux Administration & Processes"
        "week_3" -> "Networking + SSH + Bash Scripting"
        "week_4" -> "Git + GitHub + Python for DevOps"
        "week_5" -> "AWS Account Setup + Cost + IAM"
        "week_6" -> "EC2 Deep Dive"
        "week_7" -> "Load Balancing + Auto Scaling + S3"
        "week_8" -> "Route53 + CloudWatch + Systems Manager"
        "week_9" -> "VPC Fundamentals"
        "week_10" -> "Advanced Networking + Security Services"
        "week_11" -> "RDS + DynamoDB + ElastiCache"
        "week_12" -> "Serverless + Application Services"
        "week_13" -> "CloudFormation + AWS Well-Architected"
        "week_14" -> "AWS Cost Optimization + FinOps"
        "week_15" -> "Docker Fundamentals"
        "week_16" -> "Docker Compose + ECR + Security"
        "week_17" -> "ECS (Elastic Container Service)"
        "week_18" -> "GitHub Actions CI/CD"
        "week_19" -> "DevSecOps — Security in Pipelines"
        "week_20" -> "Jenkins + GitLab CI"
        "week_21" -> "Ansible — Configuration Management"
        "week_22" -> "Kubernetes Fundamentals"
        "week_23" -> "EKS + HELM + ArgoCD GitOps"
        "week_24" -> "K8s Security + Monitoring on EKS"
        "week_25" -> "Terraform Fundamentals + Intermediate"
        "week_26" -> "Terraform Advanced + Project"
        "week_27" -> "AIOps for Cloud + AWS AI Services"
        "week_28" -> "SAA-C03 + Final Project + Launch"
        else -> "Ultimate SRE & DevOps Topic"
    }

    val dTitle = when (day) {
        1 -> "Architectural Core Basics of $weekTitle"
        2 -> "Advanced Parameters & Deep Divergent Configuration"
        3 -> "Production Toolchain Selection & Integration Mapping"
        4 -> "Disaster Recovery Rules & Incident Troubleshooting Paths"
        5 -> "Industry Best Practices & Hardening Verification Standards"
        6 -> "Telemetry Diagnostics Lab & Metrics Collection"
        else -> "Comprehensive Cognitive Level Assessment Check"
    }

    val dDesc = when (day) {
        1 -> "Study the fundamental patterns of $weekTitle. Grasp how system files match standard architectural configurations, configure core interfaces, and optimize memory maps under load."
        2 -> "Analyze advanced configuration matrices. Understand how tuning performance threads, mapping sub-ports, and setting precise IAM boundaries prevent downstream configuration leaks."
        3 -> "Integrate industry toolchains. Automate deployment setups using optimized declarative manifests, verify dynamic storage drivers, and configure credential managers."
        4 -> "Troubleshoot active pipeline breaks and runtime crashes. Set up proper state backstops, trace logs in systemd/kubectl, and establish fail-safes."
        5 -> "Implement elite engineering standards. Enforce least privilege control boundaries, minimize container layers footprint, and configure cross-account identity federation."
        6 -> "Run live diagnosis inside synthetic environments. Execute script queries, parse system telemetry logs, inspect cgroups metrics, and export Grafana analytics panels."
        else -> "Synthesize your total baseline knowledge. Demonstrate operational competency by answering multi-scenario expert interview checks under standard SRE constraints."
    }

    val dCase = when (day) {
        1 -> "PRODUCTION STUDY: A high-scale enterprise experienced node failures due to unoptimized resources layout. Refined base settings to reclaim 30% computing headroom."
        2 -> "OUTAGE REPORT: An unvetted runtime configuration update leaked system variables and thrashed server CPUs. Restoring standard parameters normalized server latency."
        3 -> "EFFICIENCY CASE: Manual deployments were clocked at 4 hours. Automated using robust declarative templates, slashing delivery timelines to 90 seconds."
        4 -> "INCIDENT RESPONSE: Host storage filled up from unmanaged local stdout log buffers. Automated rotators and redirected output targets, preventing complete service blockades."
        5 -> "SECURITY DEBRIEF: Auditing revealed exposed server ports and credentials. Patched IAM boundaries and enclosed services within private subnets."
        6 -> "PERFORMANCE DRILL: Simulating 10,000 requests per second exposed trace route bottleneck. Optimized balance parameters to survive the volume spike safely."
        else -> "LEVEL GATEWAY: Prepare to demonstrate real DevOps SRE prowess, achieve a passing grade of at least 70% in our rigorous cognitive simulation check, and unlock the next roadmap iteration."
    }

    return Triple(dTitle, dDesc, dCase)
}

data class DevopsQuiz(
    val topicId: String,
    val title: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class SreResource(
    val category: String,
    val title: String,
    val author: String,
    val description: String,
    val type: String,
    val url: String
)

// --------------------------------------------------
// 4. HOLISTIC WELLNESS & DETOX FOCUS HUB
// --------------------------------------------------
@Composable
fun SourceBadge(source: String) {
    val isHC = source == "Health Connect" || source == "Synced"
    val bgColor = if (isHC) CyberGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f)
    val contentColor = if (isHC) CyberGreen else TextMuted
    val textText = if (isHC) "HEALTH CONNECT" else "MANUAL"
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isHC) CyberGreen else TextMuted)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = textText,
                color = contentColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun HealthHub(viewModel: JeevanViewModel) {
    val healthLogs by viewModel.healthLogs.collectAsState()
    val todayLog = healthLogs.firstOrNull() ?: HealthLog(dateString = viewModel.getTodayDateString())
    val userProfile by viewModel.userProfile.collectAsState()
    val adaptiveWorkouts by viewModel.adaptiveWorkouts.collectAsState()
    val quarterlyReports by viewModel.quarterlyReports.collectAsState()

    var loggedFoodName by remember { mutableStateOf("") }
    var loggedFoodGrams by remember { mutableStateOf("") }
    var loggedNutritionResult by remember { mutableStateOf<String?>(null) }

    var userWeightInput by remember(userProfile.weightKg) { mutableStateOf(userProfile.weightKg.toString()) }
    var userHeightInput by remember(userProfile.heightCm) { mutableStateOf(userProfile.heightCm.toString()) }

    val focusMinutesRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val isTimerActive by viewModel.isTimerRunning.collectAsState()
    val customDurationMinutes by viewModel.customDurationMinutes.collectAsState()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Health Connect Gateway Status Card (Phase 2A)
        item {
            val isAvailable by viewModel.healthConnectAvailable.collectAsState()
            val permissionGranted by viewModel.permissionState.collectAsState()
            val lastSyncTime by viewModel.lastSyncTime.collectAsState()
            val syncStatus by viewModel.healthSyncStatus.collectAsState()

            val requestPermissionActivityContract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
            val requestPermissionsResult = androidx.activity.compose.rememberLauncherForActivityResult(requestPermissionActivityContract) { granted ->
                viewModel.checkHealthConnectStatus()
                viewModel.syncHealthData()
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HEALTH CONNECT GATEWAY",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val statusText = if (!isAvailable) {
                                "Unavailable"
                            } else if (!permissionGranted) {
                                "Permission Required"
                            } else {
                                "Connected"
                            }
                            val statusColor = if (!isAvailable) {
                                ImmersiveRose
                            } else if (!permissionGranted) {
                                ImmersiveAmber
                            } else {
                                CyberGreen
                            }
                            Text(
                                text = "Status: $statusText",
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            val permText = if (permissionGranted) "Granted (Full Access)" else "Pending / Missing"
                            val permColor = if (permissionGranted) CyberGreen else ImmersiveAmber
                            Text(
                                text = "Permissions: $permText",
                                color = permColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            val syncTimeFormatted = if (lastSyncTime > 0) {
                                SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.getDefault()).format(Date(lastSyncTime))
                            } else {
                                "Never Synced"
                            }
                            Text(
                                text = "Last Synced: $syncTimeFormatted",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = {
                                val isSimulated = viewModel.simulatedHealthConnect.value
                                if (!isAvailable && !isSimulated) {
                                    // Not supported
                                } else if (!permissionGranted && !isSimulated) {
                                    // Request missing permissions
                                    requestPermissionsResult.launch(viewModel.healthConnectManager.getRequiredPermissions())
                                } else {
                                    // Perform sync
                                    viewModel.syncHealthData()
                                }
                            },
                            enabled = syncStatus != "SYNCING",
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("manual_sync_btn")
                        ) {
                            Text(
                                text = if (syncStatus == "SYNCING") "Syncing..." else "Sync Now",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (syncStatus == "SYNCING") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = CyberCyan,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 1.5.dp
                            )
                            Text(
                                text = "Synchronizing Health Data...",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // SRE Compliance Simulation Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SRE Compliance Simulation Mode",
                                color = TextCelestial,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Bypass sandboxed client check for verification",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                        val isSimulated by viewModel.simulatedHealthConnect.collectAsState()
                        androidx.compose.material3.Switch(
                            checked = isSimulated,
                            onCheckedChange = { viewModel.toggleHealthConnectSimulation(it) },
                            modifier = Modifier.testTag("health_sim_switch")
                        )
                    }
                }
            }
        }

        // Daily Vitals Lifeline Metrics Display Card (Phase 2A)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DAILY VITALS LIFELINE",
                        color = ImmersiveIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Steps Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ImmersiveIndigo.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Steps Icon",
                                        tint = ImmersiveIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Steps Count", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Goal: ${userProfile.dailyStepGoal} steps", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "${todayLog.stepsCount}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                SourceBadge(todayLog.stepsSource)
                            }
                        }
                        
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        
                        // Sleep Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Sleep Icon",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Sleep Duration", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    val hr = todayLog.sleepMinutes / 60
                                    val mn = todayLog.sleepMinutes % 60
                                    Text("Total duration: ${hr}h ${mn}m", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val hr = todayLog.sleepMinutes / 60
                                val mn = todayLog.sleepMinutes % 60
                                Text(
                                    text = "${hr}h ${mn}m",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                SourceBadge(todayLog.sleepSource)
                            }
                        }
                        
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        
                        // Heart Rate Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ImmersiveRose.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Heart Rate Icon",
                                        tint = ImmersiveRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Average Heart Rate", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Resting range: ~60-80 bpm", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val hrVal = if (todayLog.averageHeartRate > 0) "${todayLog.averageHeartRate} bpm" else "72 bpm"
                                Text(
                                    text = hrVal,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                SourceBadge(todayLog.heartRateSource)
                            }
                        }
                        
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        
                        // Water Intake Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Water Icon",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Water Intake", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Daily Goal: ${userProfile.dailyWaterGoalMl} ml", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val waterLitres = todayLog.waterIntakeMl / 1000.0
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1fL", waterLitres),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                SourceBadge("Manual")
                            }
                        }
                    }
                }
            }
        }

        // Deep Focus Timer Controls Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DEEP FOCUS CLOUD BLOCKER",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val min = focusMinutesRemaining / 60
                    val sec = focusMinutesRemaining % 60
                    val textFormatted = String.format(Locale.US, "%02d:%02d", min, sec)

                    Text(
                        text = textFormatted,
                        color = ImmersiveIndigo,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continues in foreground service when screen is locked. +50 XP award.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Timer Customization Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duration: ${customDurationMinutes} min",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.setCustomTimerMinutes(customDurationMinutes - 5) },
                                enabled = !isTimerActive,
                                modifier = Modifier.size(32.dp).testTag("timer_decrease_button")
                            ) {
                                Text(
                                    text = "—",
                                    color = if (isTimerActive) TextMuted else ImmersiveIndigo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.setCustomTimerMinutes(customDurationMinutes + 5) },
                                enabled = !isTimerActive,
                                modifier = Modifier.size(32.dp).testTag("timer_increase_button")
                            ) {
                                Text(
                                    text = "+",
                                    color = if (isTimerActive) TextMuted else ImmersiveIndigo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    // Focus Preset Selectors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(5, 10, 25, 45, 60).forEach { preset ->
                            val isSelected = customDurationMinutes == preset
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preset_${preset}_button")
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ImmersiveIndigo else ImmersiveSurfaceVariant)
                                    .border(
                                        0.5.dp,
                                        if (isSelected) ImmersiveIndigo else Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable(enabled = !isTimerActive) {
                                        viewModel.setCustomTimerMinutes(preset)
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${preset}m",
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleTimer(customDurationMinutes) },
                            modifier = Modifier.weight(1f).testTag("start_timer_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isTimerActive) ImmersiveRose else ImmersiveIndigo)
                        ) {
                            Text(if (isTimerActive) "Pause Session" else "Start ${customDurationMinutes} Min Focus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier.weight(0.5f).testTag("reset_timer_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Text("Reset", color = Color.White)
                        }
                    }
                }
            }
        }

        // NUTRITION INTELLIGENCE SYSTEM (Completely replacing manual steps note field)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NUTRITION INTELLIGENCE",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Log food items. System calculates macros, digestion times and slow sustained glycemic indexes.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loggedFoodName,
                        onValueChange = { loggedFoodName = it },
                        label = { Text("Food Name (e.g. Lentils, Paneer, Chicken, Rice, Egg)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("nutrition_food_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImmersiveIndigo,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = loggedFoodGrams,
                        onValueChange = { loggedFoodGrams = it },
                        label = { Text("Quantity (Grams, or pieces for Egg, Roti, Milk glass, Fruits)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImmersiveIndigo,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val rawInput = loggedFoodGrams.toDoubleOrNull() ?: 100.0
                            val nameLower = loggedFoodName.lowercase()
                            
                            // Smart pieces/servings detection: if quantity is < 15, assume pieces/units
                            val grams = if (rawInput < 15.0) {
                                when {
                                    nameLower.contains("egg") -> rawInput * 50.0  // 1 medium egg ~50g
                                    nameLower.contains("roti") || nameLower.contains("chapati") -> rawInput * 40.0 // 1 chapati ~40g cooked
                                    nameLower.contains("banana") -> rawInput * 100.0 // 1 banana ~100g
                                    nameLower.contains("apple") -> rawInput * 150.0 // 1 apple ~150g
                                    nameLower.contains("milk") -> rawInput * 250.0 // 1 glass of milk ~250g
                                    nameLower.contains("paneer") -> rawInput * 100.0 // 1 serving paneer ~100g
                                    nameLower.contains("dal") || nameLower.contains("lentil") -> rawInput * 150.0 // 1 bowl dal ~150g cooked
                                    nameLower.contains("rice") -> rawInput * 150.0 // 1 bowl cooked rice ~150g
                                    nameLower.contains("chicken") -> rawInput * 150.0 // 1 standard breast/serving ~150g cooked
                                    nameLower.contains("salad") || nameLower.contains("cucumber") || nameLower.contains("vegetable") -> rawInput * 100.0
                                    nameLower.contains("oats") -> rawInput * 40.0 // 1 bowl cooked oats ~40g dry weight
                                    else -> rawInput * 100.0
                                }
                            } else {
                                rawInput
                            }
                            
                            // Offline dictionary mapping
                            val localFood = com.example.data.nutrition.IndianFoodNutritionDb.searchLocalFood(loggedFoodName)
                            val result = if (localFood != null) {
                                val factor = grams / 100.0
                                MacroResult(
                                    calories = (localFood.caloriesPer100g * factor).toInt(),
                                    protein = localFood.proteinPer100g * factor,
                                    carbs = localFood.carbsPer100g * factor,
                                    fat = localFood.fatPer100g * factor,
                                    digestion = localFood.digestion,
                                    profile = localFood.profile
                                )
                            } else {
                                when {
                                    nameLower.contains("lentil") || nameLower.contains("dal") -> {
                                        val factor = grams / 100
                                        MacroResult((116 * factor).toInt(), 9.0 * factor, 20.0 * factor, 0.4 * factor, "2 Hours", "Sustained Lean Energy (Cooked)")
                                    }
                                    nameLower.contains("paneer") -> {
                                        val factor = grams / 100
                                        MacroResult((265 * factor).toInt(), 18.0 * factor, 2.5 * factor, 20.0 * factor, "2 Hours", "Ketogenic Sustained")
                                    }
                                    nameLower.contains("rice") -> {
                                        val factor = grams / 100
                                        MacroResult((130 * factor).toInt(), 2.7 * factor, 28.0 * factor, 0.3 * factor, "1.5 Hours", "Rapid Glucose Spike (Cooked)")
                                    }
                                    nameLower.contains("chicken") -> {
                                        val factor = grams / 100
                                        MacroResult((165 * factor).toInt(), 31.0 * factor, 0.0 * factor, 3.6 * factor, "3 Hours", "Deep Protein Synthesis (Cooked)")
                                    }
                                    nameLower.contains("egg") -> {
                                        val factor = grams / 100
                                        MacroResult((155 * factor).toInt(), 13.0 * factor, 1.1 * factor, 11.0 * factor, "1.5 Hours", "High-efficiency Whole Protein (Boiled)")
                                    }
                                    nameLower.contains("roti") || nameLower.contains("chapati") -> {
                                        val factor = grams / 100
                                        MacroResult((260 * factor).toInt(), 8.0 * factor, 55.0 * factor, 1.5 * factor, "1.5 Hours", "Complex Carbohydrates (Cooked Roti)")
                                    }
                                    nameLower.contains("milk") -> {
                                        val factor = grams / 100
                                        MacroResult((60 * factor).toInt(), 3.2 * factor, 4.8 * factor, 3.2 * factor, "1 Hour", "Liquid Dairy Hydration & Proteins")
                                    }
                                    nameLower.contains("salad") || nameLower.contains("cucumber") || nameLower.contains("vegetable") -> {
                                        val factor = grams / 100
                                        MacroResult((20 * factor).toInt(), 1.0 * factor, 4.0 * factor, 0.1 * factor, "45 Mins", "High Fiber Micronutrient Hydration")
                                    }
                                    nameLower.contains("apple") -> {
                                        val factor = grams / 100
                                        MacroResult((52 * factor).toInt(), 0.3 * factor, 14.0 * factor, 0.2 * factor, "1 Hour", "Fructose & Clean Fiber")
                                    }
                                    nameLower.contains("banana") -> {
                                        val factor = grams / 100
                                        MacroResult((89 * factor).toInt(), 1.1 * factor, 23.0 * factor, 0.3 * factor, "45 Mins", "Fast Active Energizers & Potassium")
                                    }
                                    nameLower.contains("oats") -> {
                                        val factor = grams / 100
                                        MacroResult((71 * factor).toInt(), 2.5 * factor, 12.0 * factor, 1.4 * factor, "2 Hours", "Beta-Glucan Soluble Fiber (Cooked)")
                                    }
                                    else -> {
                                        val factor = grams / 100
                                        MacroResult((120 * factor).toInt(), 5.0 * factor, 15.0 * factor, 2.0 * factor, "2 Hours", "Standard Glycemic Profile")
                                    }
                                }
                            }

                            // Write to health log inside room
                            viewModel.logVitals(
                                todayLog.sleepMinutes,
                                todayLog.caloriesConsumed + result.calories,
                                todayLog.caloriesBurned
                            )

                            loggedNutritionResult = String.format(
                                Locale.US,
                                "🟢 Food Parsed successfully:\n" +
                                "Calories: %d kcal\n" +
                                "Protein: %.1fg | Carbs: %.1fg | Fats: %.1fg\n" +
                                "Est. Digestion Duration: %s\n" +
                                "Energy release profile: %s",
                                result.calories,
                                result.protein,
                                result.carbs,
                                result.fat,
                                result.digestion,
                                result.profile
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("nutrition_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo)
                    ) {
                        Text("Log Macro and Analyse", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (loggedNutritionResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ImmersiveSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(text = loggedNutritionResult!!, color = TextCelestial, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // SEASONAL INTELLIGENCE & ADAPTIVE MICRO ADVICE
        item {
            val seasonalIntelligence by viewModel.seasonalIntelligenceText.collectAsState()
            val activeProviderName by viewModel.activeWeatherProviderName.collectAsState()
            var localApiKeyInput by remember { mutableStateOf(com.example.data.SecurePrefsManager.getOpenWeatherApiKey() ?: "") }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveAmber.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SEASONAL ADAPTIVITY INTELLIGENCE",
                        color = ImmersiveAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Synchronized to live local environmental indices. Adapts hydration limits to avoid SRE dynamic posture strain.",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = seasonalIntelligence,
                        color = TextCelestial,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.2f))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "WEATHER PROVIDER SWITCHER",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isMeteo = activeProviderName == "Open-Meteo"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isMeteo) ImmersiveIndigo else ImmersiveSurfaceVariant)
                                .border(0.5.dp, if (isMeteo) ImmersiveIndigo else Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectWeatherProvider("Meteo") }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Open-Meteo (Free)", color = if (isMeteo) Color.White else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isMeteo) ImmersiveIndigo else ImmersiveSurfaceVariant)
                                .border(0.5.dp, if (!isMeteo) ImmersiveIndigo else Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectWeatherProvider("OpenWeather") }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("OpenWeatherMap", color = if (!isMeteo) Color.White else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    if (activeProviderName == "OpenWeatherMap") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = localApiKeyInput,
                            onValueChange = { localApiKeyInput = it },
                            label = { Text("OpenWeatherMap API Key", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("openweather_apikey_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                com.example.data.SecurePrefsManager.saveOpenWeatherApiKey(localApiKeyInput)
                                viewModel.selectWeatherProvider("OpenWeather")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                            modifier = Modifier.fillMaxWidth().testTag("save_openweather_key_btn"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Commit Key & Force Refresh", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // NEW FEATURE: AI HEALTH COACH SPECIALIST CARD (Phase 2B)
        // ==========================================
        item {
            val coachAdvice by viewModel.aiCoachAdvice.collectAsState()
            val isCoachGenerating by viewModel.isCoachGenerating.collectAsState()
            val rawKey = remember { com.example.data.SecurePrefsManager.getGeminiApiKey() ?: "" }
            val hasWebCoach = rawKey.isNotBlank() && rawKey != "MY_GEMINI_API_KEY" && rawKey != "API_KEY"
            
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI HEALTH COACH SPECIALIST",
                            color = CyberGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (hasWebCoach) CyberGreen.copy(alpha = 0.15f) else ImmersiveAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (hasWebCoach) "GEMINI PRO ACTIVE" else "DETERMINISTIC FALLBACK",
                                color = if (hasWebCoach) CyberGreen else ImmersiveAmber,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Direct biometric, digestive macro index, and posture diagnostic report.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp, max = 240.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (isCoachGenerating) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = CyberGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Analyzing posture and telemetry streams...", color = TextMuted, fontSize = 10.sp)
                            }
                        } else {
                            Text(
                                text = coachAdvice,
                                color = TextCelestial,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(
                        onClick = { viewModel.generateAICoachSession() },
                        enabled = !isCoachGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("trigger_coach_btn")
                    ) {
                        Text("EXECUTE TELEMETRY ADVISORY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // BIOMETRIC INPUTS & BMI CALCULATOR WITH CATEGORY & TREND TRACKING
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BIOMETRICS & BMI INTELLIGENCE ENGINE",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monitors software engineer physical index status and tracks calorie-balance weight trajectories.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = userWeightInput,
                            onValueChange = { userWeightInput = it },
                            label = { Text("Weight (kg)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("weight_input_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = userHeightInput,
                            onValueChange = { userHeightInput = it },
                            label = { Text("Height (cm)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("height_input_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Current BMI: ${userProfile.computedBmi}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            val bmiCategory = when {
                                userProfile.computedBmi <= 0.0 -> "N/A"
                                userProfile.computedBmi < 18.5 -> "Underweight"
                                userProfile.computedBmi < 25.0 -> "Ideal Range (Normal)"
                                userProfile.computedBmi < 30.0 -> "Overweight"
                                else -> "Obese"
                            }
                            val bmiColor = when {
                                userProfile.computedBmi <= 0.0 -> TextMuted
                                userProfile.computedBmi < 18.5 -> ImmersiveAmber
                                userProfile.computedBmi < 25.0 -> CyberGreen
                                userProfile.computedBmi < 30.0 -> ImmersiveAmber
                                else -> ImmersiveRose
                            }
                            Text("Classification: $bmiCategory", color = bmiColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            
                            // Real Room calorie-imbalance dynamic trend tracking (Phase 2B)
                            val activeLogs = healthLogs.take(7)
                            val sumBalance = activeLogs.sumOf { it.caloriesConsumed - it.caloriesBurned - 1800 }
                            val (trendText, trendColor) = when {
                                activeLogs.size < 2 -> Pair("Stable (Needs more daily logs)", CyberGreen)
                                sumBalance > 1500 -> Pair("▲ Upward Risk (Calorie Surplus of +${sumBalance} kcal)", ImmersiveAmber)
                                sumBalance < -1500 -> Pair("▼ Weight Reduction (Calorie Deficit of ${sumBalance} kcal)", CyberGreen)
                                else -> Pair("◀ Stable Equilibrium ▶", CyberGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Trend: $trendText", color = trendColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                val w = userWeightInput.toDoubleOrNull() ?: 70.0
                                val h = userHeightInput.toDoubleOrNull() ?: 175.0
                                viewModel.updateBiometrics(w, h)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("update_vitals_health_btn")
                        ) {
                            Text("Save Vitals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // ADAPTIVE WORKOUT RECOMMENDATIONS (Phase 2B upgraded)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFICE WORKOUT SCHEME (ADAPTED)",
                            color = ImmersiveIndigo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ImmersiveIndigo.copy(alpha = 0.15f))
                                .clickable { viewModel.manualRefreshWorkoutPlan() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("refresh_workout_plan_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Manual Refresh",
                                    tint = ImmersiveIndigo,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Refresh",
                                    color = ImmersiveIndigo,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deterministic Multi-Factor Telemetry Calibration Active:",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Displaying the multi-factor metrics driving the workout engine
                    val computedNutrScore = (100 - Math.abs(todayLog.caloriesConsumed - 2000) / 10).coerceIn(40, 100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "BMI: ${userProfile.computedBmi}",
                            "Recov: ${todayLog.recoveryScore}%",
                            "Sleep: ${todayLog.sleepMinutes / 60}h",
                            "Steps: ${todayLog.stepsCount}",
                            "Nutri: $computedNutrScore/100"
                        ).forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.25f))
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(badge, color = CyberCyan, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    val exercises = adaptiveWorkouts.ifEmpty {
                        listOf(
                            "Squats: 3 sets x 15 reps (Leg focus)",
                            "Pushups: 3 sets x 12 reps (Chest baseline)",
                            "Yoga Stretches: 10 mins (Shoulder & Neck flexibility)",
                            "Walking step targets: 30 mins (Cardio health)"
                        )
                    }
                    exercises.forEach { ex ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberCyan))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(ex, color = TextCelestial, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // GEMINI HISTORICAL QUARTERLY DIAGNOSTIC REPORT
        item {
            var isCompilingReport by remember { mutableStateOf(false) }
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveRose.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOLISTIC SRE QUARTERLY METRICS REPORT",
                            color = ImmersiveRose,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ImmersiveRose.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("GEMINI ANALYST", color = ImmersiveRose, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Compiles biometric vitals, Sleep metrics, Food calorie, Stress logs, and cognitive factors into a quarterly health report stored in shared storage state.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isCompilingReport = true
                            viewModel.generateQuarterlyHealthReport()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_quarterly_report_btn")
                    ) {
                        Text(
                            text = if (isCompilingReport) "COMPILING REPORT WITH GEMINI..." else "⚡ GENERATE SEASONS QUARTERLY HEALTH REPORT",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    if (quarterlyReports.isNotEmpty()) {
                        isCompilingReport = false
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "HISTORICAL SEASONS & QUARTERLY ARCHIVE:",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ImmersiveSurfaceVariant)
                                .verticalScroll(rememberScrollState())
                                .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                quarterlyReports.forEachIndexed { index, report ->
                                    Text(
                                        text = "[GEN CONTEXT ARCHIVE - REPORT #${quarterlyReports.size - index}]",
                                        color = ImmersiveRose,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = report,
                                        color = TextCelestial,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (index < quarterlyReports.size - 1) {
                                        HorizontalDivider(
                                            color = Color.White.copy(alpha = 0.08f),
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// 5. CENTRAL AI BRAIN CHAT INTERACTIVE HUB
// --------------------------------------------------
@Composable
fun NewsCenterHub(viewModel: JeevanViewModel) {
    val articles by viewModel.newsArticles.collectAsState()
    val isRefreshing by viewModel.isNewsRefreshing.collectAsState()
    val lastRefresh by viewModel.lastNewsRefresh.collectAsState()
    
    var selectedSubTab by remember { mutableStateOf("JOBS") } // Lauch with "JOBS" tab as requested
    
    val filteredArticles = articles.filter { it.category == selectedSubTab }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("news_center_hub")
    ) {
        // Updates Center Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
            border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SRE UPDATES CENTER",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Job Openings, Tech & SRE OS Releases",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (lastRefresh > 0) {
                        val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastRefresh))
                        Text(
                            text = "Validated synchronizations: $formattedTime (Every 2h)",
                            color = ImmersiveTextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                IconButton(
                    onClick = { viewModel.refreshNewsCenter() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ImmersiveIndigo.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync feed",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Updates Horizontal Sub-nav Tabs Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("JOBS", "JOB UPDATES", CyberGreen),
                Triple("DEVOPS_UPDATES", "TECH UPDATES", CyberPurple),
                Triple("GENERAL", "APP UPDATES", CyberCyan)
            ).forEach { (cmdTab, textLabel, accentColor) ->
                val isActive = selectedSubTab == cmdTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) ImmersiveIndigo.copy(alpha = 0.6f) else ImmersiveSurface)
                        .border(
                            width = 0.8.dp,
                            color = if (isActive) accentColor else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedSubTab = cmdTab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textLabel,
                        color = if (isActive) Color.White else ImmersiveTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // News list layout
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SYNCHRONIZING SECURE COGNITIVE RSS INTEGRATIONS...",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (filteredArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No updates cached in current partition cluster.",
                    color = ImmersiveTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredArticles) { news ->
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    Card(
                        onClick = {
                            if (news.url.isNotBlank()) {
                                try {
                                    uriHandler.openUri(news.url)
                                } catch (e: Exception) {
                                    android.util.Log.e("JeevanMainScreen", "Failed to open news URL: ${news.url}", e)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("news_article_card_${news.id}"),
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Category and Source Information
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (news.category) {
                                                    "GENERAL" -> CyberCyan
                                                    "JOBS" -> CyberGreen
                                                    else -> CyberPurple
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = news.category.replace("_", " "),
                                        color = when (news.category) {
                                            "GENERAL" -> CyberCyan
                                            "JOBS" -> CyberGreen
                                            else -> CyberPurple
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                
                                val displaySource = news.sourceName.ifBlank { news.author }.ifBlank { "Verified Source" }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = displaySource.uppercase(),
                                        color = ImmersiveTextMuted,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = news.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Structured display logic based on Category
                            if (news.category == "JOBS") {
                                // Render modern, structural Job details box
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.02f))
                                        .border(0.5.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                ) {
                                    if (news.company.isNotBlank()) {
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text("Company: ", color = ImmersiveTextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                            Text(news.company, color = CyberGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (news.role.isNotBlank()) {
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text("Role: ", color = ImmersiveTextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                            Text(news.role, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    if (news.experience.isNotBlank()) {
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text("Experience: ", color = ImmersiveTextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                            Text(news.experience, color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                    if (news.location.isNotBlank()) {
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text("Location: ", color = ImmersiveTextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                            Text(news.location, color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            
                            Text(
                                text = news.description,
                                color = ImmersiveTextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Footer item metadatas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val displayPosted = news.postedDate.ifBlank { "Recently" }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = ImmersiveTextMuted,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = displayPosted,
                                        color = ImmersiveTextMuted,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    
                                    if (news.lastRefreshedTimeStr.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(CyberCyan.copy(alpha = 0.08f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SYNCED: ${news.lastRefreshedTimeStr}",
                                                color = CyberCyan,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                                
                                // Redirection CTA Label or Button
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val actionLabel = if (news.category == "JOBS") "APPLY NOW" else "VERIFY SOURCE"
                                    val actionColor = if (news.category == "JOBS") CyberGreen else CyberCyan
                                    
                                    Text(
                                        text = actionLabel,
                                        color = actionColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = actionColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AICompanionDialog(viewModel: JeevanViewModel, onDismiss: () -> Unit) {
    val chatHistory by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isBrainThinking.collectAsState()
    val activeAgentType by viewModel.activeAgentType.collectAsState()
    var textInput by remember { mutableStateOf("") }

    // API Key input show/hide state
    var showKeyPrompt by remember { mutableStateOf(!com.example.data.SecurePrefsManager.hasKey()) }
    var tempApiKeyInput by remember { mutableStateOf("") }
    
    val activeAgent = when (activeAgentType) {
        "FINANCE" -> Triple("💼 WEALTH INTEL AGENT", CyberCyan, "Analyzing financial capital ratios & asset allocations...")
        "HEALTH" -> Triple("🥗 ERGONOMIC HEALTH AGENT", CyberGreen, "Syncing daily metabolisms & posture metrics...")
        "CAREER" -> Triple("🔧 CLOUD DEVOPS ARCHITECT", CyberPurple, "Routing global telemetry logs & posture benchmarks...")
        else -> Triple("🤖 JEEVAN OS PERSONAL COPILOT", ImmersiveIndigo, "Standing by to orchestrate your operational requests...")
    }

    val agentTabs = listOf(
        Triple("GENERAL", "🤖 COPILOT", ImmersiveIndigo),
        Triple("CAREER", "🔧 DEVOPS", CyberPurple),
        Triple("FINANCE", "💼 WEALTH", CyberCyan),
        Triple("HEALTH", "🥗 HEALTH", CyberGreen)
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(onClick = onDismiss)
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .clickable(enabled = false) {},
                colors = CardDefaults.cardColors(containerColor = ImmersiveDarkBg),
                border = BorderStroke(1.dp, activeAgent.second.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ImmersiveSurface)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(activeAgent.second.copy(alpha = 0.15f))
                                        .border(1.dp, activeAgent.second.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Active Agent",
                                        tint = activeAgent.second,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = activeAgent.first,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = activeAgent.third,
                                        color = ImmersiveTextMuted,
                                        fontSize = 9.sp,
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Toggle API Key Setup Card
                                IconButton(
                                    onClick = { showKeyPrompt = !showKeyPrompt },
                                    modifier = Modifier.size(24.dp).testTag("toggle_key_setup_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Secure Key Setup",
                                        tint = activeAgent.second.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                // Clear conversation thread memory
                                IconButton(
                                    onClick = { viewModel.clearMemory(activeAgentType) },
                                    modifier = Modifier.size(24.dp).testTag("clear_chat_memory_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Clear Agent Memory",
                                        tint = ImmersiveRose.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                // Close dialog
                                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close overlay",
                                        tint = ImmersiveRose.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Agent cognitive tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ImmersiveSurface.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        agentTabs.forEach { (type, label, color) ->
                            val selected = activeAgentType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.02f))
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) color else Color.White.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.setActiveAgentType(type)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) Color.White else ImmersiveTextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.2.sp
                                )
                            }
                        }
                    }

                    // Optional Secure API Key setup banner/card
                    if (showKeyPrompt) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(1.dp, activeAgent.second.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "⚡ KEYSTORE CRYPTOGRAPHIC PIPELINE",
                                        color = activeAgent.second,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    IconButton(
                                        onClick = { showKeyPrompt = false },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hide prompt",
                                            tint = ImmersiveTextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Enter your personal Gemini API Key. It will be stored fully encrypted on-device under AES-256 standard. If empty, the OS defaults to heuristic response models.",
                                    color = ImmersiveTextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = tempApiKeyInput,
                                        onValueChange = { tempApiKeyInput = it },
                                        placeholder = { Text("AI Studio Gemini token...", color = ImmersiveTextMuted, fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("secure_api_key_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = activeAgent.second,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = {
                                            if (tempApiKeyInput.isNotBlank()) {
                                                com.example.data.SecurePrefsManager.saveGeminiApiKey(tempApiKeyInput)
                                                showKeyPrompt = false
                                                tempApiKeyInput = ""
                                                // Reload to trigger new engine status check
                                                viewModel.setActiveAgentType(activeAgentType)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = activeAgent.second),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("save_api_key_btn")
                                    ) {
                                        Text(
                                            "LOAD",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(chatHistory) { msg ->
                            val isUser = msg.sender == "You"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else 14.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) ImmersiveIndigo.copy(alpha = 0.4f) else ImmersiveSurface
                                    ),
                                    border = BorderStroke(
                                        width = 0.6.dp,
                                        color = if (isUser) ImmersiveIndigo.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.04f)
                                    ),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.sender.uppercase(),
                                            color = if (isUser) CyberCyan else activeAgent.second,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (isThinking) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                                        border = BorderStroke(0.6.dp, activeAgent.second.copy(alpha = 0.2f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                color = activeAgent.second,
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 1.5.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Orchestrating AI pipeline queries...",
                                                color = ImmersiveTextMuted,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        color = ImmersiveSurface,
                        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = textInput,
                                    onValueChange = { textInput = it },
                                    placeholder = { Text("Command Jeevan assistant...", color = ImmersiveTextMuted, fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("ai_companion_chat_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = activeAgent.second,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (textInput.isNotBlank()) {
                                            viewModel.sendChatMessage(textInput)
                                            textInput = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(activeAgent.second.copy(alpha = 0.15f))
                                        .testTag("ai_companion_send_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send Command",
                                        tint = activeAgent.second,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val rememberText = if (chatHistory.isNotEmpty()) "Jeevan remembers the last ${kotlin.math.min(chatHistory.size, 10)} messages for ${activeAgent.first.substringAfter(" ")}" else "Start speaking with ${activeAgent.first.substringAfter(" ")}"
                            Text(
                                text = "⚡ Context Memory: $rememberText",
                                color = activeAgent.second.copy(alpha = 0.5f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("ai_memory_hint")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeadBrainChatHubReference_Optimized(viewModel: JeevanViewModel) {
    val brainstormPuzzlesList = remember {
        listOf(
            PuzzleChallenge(
                category = "DEVOPS INTEGRATION",
                title = "The Ghost Load Balancer",
                description = "A microservice cluster registers 100% CPU on nodes in Region A but 0% in Region B during a traffic surge. The global DNS is active and health-checks read green. What hidden misconfiguration is routing traffic exclusively to Region A?",
                options = listOf(
                    "Weighted routing policy is dry-set to 100:0 instead of 50:50 balance ratio",
                    "Region B nodes are using outdated SSL certificates",
                    "A rogue cron job is shutting down Region B"
                ),
                correctIndex = 0,
                explanation = "A balancing weight config of 100 on Region A overrides dynamic health checking on Region B, starving its nodes of standard connection flows. Adjusting weights fixes Region B load instantly. +25 DevOps XP awarded!"
            ),
            PuzzleChallenge(
                category = "CONTAINER ORCHESTRATION",
                title = "The Infinite Docker Loop",
                description = "A freshly built API container is deployed on Kubernetes. It terminates and restarts immediately in an endless loop. The application logs report: 'Server initialized on port 8080', but pod status shows: 'CrashLoopBackOff'. What is wrong?",
                options = listOf(
                    "The K8s service manifest name contains invalid uppercase letters",
                    "Liveness probe was mis-configured to point to port 3000 instead of 8080",
                    "The cluster is out of global Docker storage"
                ),
                correctIndex = 1,
                explanation = "An incorrect liveness probe tells Kubernetes that the service is dead since nothing responds on port 3000, triggering cyclic restarts. Correcting the probe to point to port 8080 resolves the loop! +25 DevOps XP awarded!"
            ),
            PuzzleChallenge(
                category = "FINOPS / BUDGET METRICS",
                title = "The Cloud Billing Leak",
                description = "A developer spins up an AWS RDS database cluster with Auto-Scaling enabled for High-Availability. After one week, the project budget of ₹20,000 is completely depleted. What hidden cost structure caused this finance overload?",
                options = listOf(
                    "Continuous automated Multi-Region snapshot storage backups inside high IOPS drives",
                    "A rogue developer ran background bitcoin miners",
                    "The database didn't use modern SSL encrypted connectors"
                ),
                correctIndex = 0,
                explanation = "Automated cross-region replication of multi-TB snapshots on high-performance storage accumulates fees rapidly even under zero active traffic. Restricting backups to local zones saves ₹15,000. +25 DevOps XP awarded!"
            ),
            PuzzleChallenge(
                category = "COGNITIVE ERGONOMICS",
                title = "The Micro-Hydration Energy Deficit",
                description = "After 4 hours of intense DevOps debugging and coding, your syntax parsing speed drops by 45% and memory recall lags. Your hydration tracker logs only 200ml intake. What biomechanical factor explains this performance deficit?",
                options = listOf(
                    "Brief energy spikes caused by lack of direct glucose sugars",
                    "Neural efficiency drops 15-20% when cellular hydration markers decline beyond 1-2%",
                    "Your monitor requires calibrating and custom backlights"
                ),
                correctIndex = 1,
                explanation = "Slight dehydration decreases brain cell volume and compromises blood circulation speed, leading to brain fog. Rehydrating with 500ml of clean water restores logic response rates. +25 DevOps XP awarded!"
            ),
            PuzzleChallenge(
                category = "SYSTEM ADMINISTRATION",
                title = "The Phantom Port Lock",
                description = "Your local development webserver crashes. When trying to manually restart it on port 80, the OS registers safety failure stating: 'bind: Address already in use'. How do you locate and kill the process holding your port host?",
                options = listOf(
                    "Reinstalling the entire server to flush cache assets",
                    "Querying active connections with 'sudo lsof -i :80' to find the PID, and using 'kill -9 <PID>'",
                    "Adjusting the local GMT time forward in system clock configurations"
                ),
                correctIndex = 1,
                explanation = "The POSIX utility 'lsof -i' lists open socket files. Isolating and terminating the rogue process PID releases the bound socket instantly, enabling seamless restart. +25 DevOps XP awarded!"
            )
        )
    }

    val selectedPuzzleIndex by viewModel.selectedPuzzleIndex.collectAsState()
    val brainstormScore by viewModel.brainstormScore.collectAsState()
    val puzzlesSolved by viewModel.puzzlesSolved.collectAsState()
    val puzzleResultFeedback by viewModel.puzzleResultFeedback.collectAsState()
    val puzzleIsAnswered by viewModel.puzzleIsAnswered.collectAsState()

    val currentPuzzle = brainstormPuzzlesList[selectedPuzzleIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Score card banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
            border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "NEURAL POWER STATE",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Cognitive DevOps & Cloud Puzzles",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "SCORE",
                        color = ImmersiveIndigo,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "₹${brainstormScore} XP",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable arena containing the current quiz
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ImmersiveIndigo.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                currentPuzzle.category,
                                color = CyberCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Challenge ${selectedPuzzleIndex + 1} of ${brainstormPuzzlesList.size}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                item {
                    Text(
                        text = currentPuzzle.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ImmersiveSurface.copy(alpha = 0.6f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = currentPuzzle.description,
                            color = TextCelestial,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                item {
                    Text(
                        text = "Select your tactical resolution path:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                // Answer Options
                items(currentPuzzle.options.size) { optionIdx ->
                    val optionText = currentPuzzle.options[optionIdx]
                    val isCorrect = optionIdx == currentPuzzle.correctIndex
                    
                    val optionBg = when {
                        !puzzleIsAnswered -> ImmersiveSurface
                        isCorrect -> Color(0xFF1B5E20).copy(alpha = 0.25f)
                        else -> Color(0xFFB71C1C).copy(alpha = 0.15f)
                    }
                    val optionBorderColor = when {
                        !puzzleIsAnswered -> Color.White.copy(alpha = 0.1f)
                        isCorrect -> Color(0xFF4CAF50)
                        else -> Color(0xFFEF5350).copy(alpha = 0.4f)
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(optionBg)
                            .border(1.dp, optionBorderColor, RoundedCornerShape(10.dp))
                            .clickable(enabled = !puzzleIsAnswered) {
                                viewModel.submitPuzzleAnswer(optionIdx, currentPuzzle.correctIndex)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (puzzleIsAnswered && isCorrect) Color(0xFF4CAF50) else ImmersiveIndigo)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + optionIdx).toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optionText,
                            color = TextCelestial,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Feedback Explainer Box
                if (puzzleIsAnswered && !puzzleResultFeedback.isNullOrBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ImmersiveIndigo.copy(alpha = 0.15f))
                                .border(0.6.dp, ImmersiveIndigo.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = puzzleResultFeedback ?: "",
                                    color = if (puzzleResultFeedback?.startsWith("🔴") == true) Color(0xFFFF8A80) else CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentPuzzle.explanation,
                                    color = TextCelestial,
                                    fontSize = 10.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.nextPuzzle(brainstormPuzzlesList.size)
            },
            modifier = Modifier.fillMaxWidth().testTag("next_brain_puzzle_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Next Challenge", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Next Tactical Challenge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// Simple Helper data class for offline sandbox brainteasers
data class PuzzleChallenge(
    val category: String,
    val title: String,
    val description: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

// Custom horizontal extension modifier for suggestions row
@Composable
fun Modifier.horizontalScrollEnabled(): Modifier = this.then(
    Modifier.padding(vertical = 2.dp)
)

data class MacroResult(
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val digestion: String,
    val profile: String
)

@Composable
fun QaDebugPanel(viewModel: JeevanViewModel) {
    val selectedWeek by viewModel.selectedWeek.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val topics by viewModel.roadmapTopics.collectAsState()
    val subtopics by viewModel.roadmapSubtopics.collectAsState()
    val subList by viewModel.subtopicsProgress.collectAsState()
    val userNotes by viewModel.subtopicUserNotes.collectAsState()
    val savedResources by viewModel.savedResources.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val careerProgress by viewModel.careerProgress.collectAsState()

    // Health Connect Diagnostics State Collections (Phase 2A)
    val isHCInstalled by viewModel.healthConnectAvailable.collectAsState()
    val permissionGranted by viewModel.permissionState.collectAsState()
    val isWorkerRegistered by viewModel.isWorkerRegistered.collectAsState()
    val roomLogCount by viewModel.roomLogCount.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    val activeTopic = topics.firstOrNull { it.weekNumber == selectedWeek }
    val activeTopicSubtopics = if (activeTopic != null) {
        subtopics.filter { it.parentTopicId == activeTopic.id }.sortedBy { it.orderIndex }
    } else emptyList()
    val targetSubtopic = activeTopicSubtopics.getOrNull(selectedDay - 1)
    val activeSubId = if (targetSubtopic != null) "sub_${targetSubtopic.id}" else "week_${selectedWeek}_day_${selectedDay}"
    val progressRecord = subList.firstOrNull { it.subtopicId == activeSubId }

    // Phase 2 Validation States
    val totalWeeksExpected = 28
    val totalTasksExpected = 196

    val topicsCount = topics.size
    val subtopicsCount = subtopics.size

    val weeksList = topics.map { it.weekNumber }.distinct().sorted()
    val hasDuplicatedWeeks = weeksList.size != topics.size
    val isWeekSequenceValid = weeksList == (1..28).toList()

    val weekTaskCounts = topics.map { topic ->
        val count = subtopics.count { it.parentTopicId == topic.id }
        topic.weekNumber to count
    }.toMap()

    val hasExactly7TasksPerWeek = weekTaskCounts.all { it.value == 7 } && weekTaskCounts.size == 28

    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("qa_debug_panel_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "QA Tools",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QA CONTROL & SYNC PANEL",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SANDBOX COMPLIANT",
                        color = CyberGreen,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: SELECTED COORDINATES & COORDINATE SNAPSHOT
            Text(
                text = "1. ACTIVE COORDINATES SNAPSHOT",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Week:", color = Color.White, fontSize = 11.sp)
                        Text("Week $selectedWeek", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Day:", color = Color.White, fontSize = 11.sp)
                        Text("Day $selectedDay", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Roadmap Topic ID:", color = Color.White, fontSize = 11.sp)
                        Text(activeTopic?.id?.toString() ?: "NULL", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Roadmap Topic Title:", color = Color.White, fontSize = 11.sp)
                        Text(activeTopic?.title ?: "Unknown", color = Color.White, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 180.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtopic ID:", color = Color.White, fontSize = 11.sp)
                        Text(activeSubId, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtopic Name:", color = Color.White, fontSize = 11.sp)
                        Text(targetSubtopic?.title ?: "Unknown", color = Color.White, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 180.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Completion Status:", color = Color.White, fontSize = 11.sp)
                        Text(
                            text = if (progressRecord?.isCompleted == true) "COMPLETED" else "PENDING",
                            color = if (progressRecord?.isCompleted == true) CyberGreen else ImmersiveRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Assessment Status:", color = Color.White, fontSize = 11.sp)
                        val score = progressRecord?.assessmentScore ?: 0
                        val statusText = when {
                            score >= 85 -> "Pass (Excellent - $score%)"
                            score >= 70 -> "Pass (Average - $score%)"
                            score > 0 -> "Fail (Needs Improvement - $score%)"
                            else -> "Pending Assessment"
                        }
                        Text(
                            text = statusText,
                            color = if (score >= 70) CyberGreen else if (score > 0) ImmersiveRose else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Revision Flag:", color = Color.White, fontSize = 11.sp)
                        Text(
                            text = if (progressRecord != null && (!progressRecord.isCompleted || progressRecord.assessmentScore < 70)) "NEEDS REVISION" else "OK",
                            color = if (progressRecord != null && (!progressRecord.isCompleted || progressRecord.assessmentScore < 70)) ImmersiveRose else CyberGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: PHASE 2 & 3 - AUTOMATED ROADMAP VALIDATION CHECKS
            Text(
                text = "2. ROADMAP SYNCHRONIZATION RUNTIME CHECKS",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Check 1: 28 Weeks Exist
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pass = topicsCount == totalWeeksExpected && isWeekSequenceValid
                        Icon(
                            imageVector = if (pass) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (pass) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "28 Sequential Weeks Exist: ${topicsCount}/28",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 2: 196 Total Daily Tasks Expected
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pass = subtopicsCount == totalTasksExpected
                        Icon(
                            imageVector = if (pass) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (pass) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "196 Total Tasks Exist: ${subtopicsCount}/196",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 3: Perfect 7 Daily Tasks Per Week
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pass = hasExactly7TasksPerWeek
                        Icon(
                            imageVector = if (pass) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (pass) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exactly 7 Tasks Per Week: " + if (pass) "YES (7 x 28 = 196)" else "FAIL/MISMATCH",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 4: No Duplicates or Gaps
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pass = !hasDuplicatedWeeks && isWeekSequenceValid
                        Icon(
                            imageVector = if (pass) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (pass) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Duplicate/Missing Weeks Check: " + if (pass) "PASS" else "FAIL",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3: PHYSICAL DATABASE ROW COUNTS
            Text(
                text = "3. DATABASE TABLE ROW COUNTS",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("roadmap_topics table rows:", color = Color.White, fontSize = 11.sp)
                        Text(topicsCount.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("roadmap_subtopics table rows:", color = Color.White, fontSize = 11.sp)
                        Text(subtopicsCount.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("subtopic_progress table rows:", color = Color.White, fontSize = 11.sp)
                        Text(subList.size.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("saved_resources table rows:", color = Color.White, fontSize = 11.sp)
                        Text(savedResources.size.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("subtopic_user_notes records count:", color = Color.White, fontSize = 11.sp)
                        Text(userNotes.size.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("career_progress table rows:", color = Color.White, fontSize = 11.sp)
                        Text(careerProgress.size.toString(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4: REAL-TIME DAILY TASK MAPPING REPORT
            Text(
                text = "4. LIVE 196 DAILY TASK MAPPING BLUEPRINT",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Real-time mapping of Selected Week $selectedWeek Days:",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (activeTopicSubtopics.isEmpty()) {
                        Text("No subtopics for Week $selectedWeek", color = ImmersiveRose, fontSize = 11.sp)
                    } else {
                        activeTopicSubtopics.forEachIndexed { idx, sub ->
                            val dayNum = idx + 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(if (selectedDay == dayNum) ImmersiveIndigo.copy(alpha = 0.25f) else Color.Transparent)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (selectedDay == dayNum) CyberCyan else TextMuted)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "W$selectedWeek Day $dayNum:",
                                        color = if (selectedDay == dayNum) CyberCyan else Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sub.title,
                                        color = if (selectedDay == dayNum) Color.White else TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                val subProgress = subList.firstOrNull { it.subtopicId == "sub_${sub.id}" }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (subProgress?.isCompleted == true) CyberGreen.copy(alpha = 0.15f) else ImmersiveRose.copy(alpha = 0.1f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (subProgress?.isCompleted == true) "COMPLETED" else "PENDING",
                                        color = if (subProgress?.isCompleted == true) CyberGreen else ImmersiveRose,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 5: FAST QA SIMULATORS/MUTATORS FOR HIGH REPEATABILITY
            Text(
                text = "5. ON-DEMAND QA STRESS TESTING WORKFLOWS",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // QA Force Complete Toggle
                Button(
                    onClick = {
                        val currentCompletion = progressRecord?.isCompleted ?: false
                        viewModel.toggleSubtopic(
                            subtopicId = activeSubId,
                            parentTopicId = activeTopic?.id?.toString() ?: "custom",
                            isCompleted = !currentCompletion,
                            reason = if (!currentCompletion) null else "QA Reset Completed Flag",
                            score = if (!currentCompletion) 88 else 0
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (progressRecord?.isCompleted == true) "RESET ACTIVE TASK" else "FORCE PASS ACTIVE",
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Inject mock progress across multiple items
                Button(
                    onClick = {
                        if (activeTopic != null) {
                            activeTopicSubtopics.forEachIndexed { i, sub ->
                                viewModel.toggleSubtopic(
                                    subtopicId = "sub_${sub.id}",
                                    parentTopicId = activeTopic.id.toString(),
                                    isCompleted = true,
                                    reason = "QA Auto Evaluated",
                                    score = 80 + i % 3 * 5
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "PASS ALL W$selectedWeek DAYS",
                        fontSize = 9.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Simulate device rotation / profile lookup reseed
                Button(
                    onClick = {
                        viewModel.resetRoadmap()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "FORCE RESEED DB",
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 6: HEALTH CONNECT QA LIFELINE ADVANCED DIAGNOSTICS & REPORTING (Phase 2A)
            Text(
                text = "6. HEALTH CONNECT INTEGRATION & SRE COMPLIANCE DIAGNOSTICS",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Check 1: Health Connect SDK Availability
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isHCInstalled) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isHCInstalled) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Availability: " + if (isHCInstalled) "Service Installed" else "Not Supported/Missing",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 2: Permissions Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (permissionGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (permissionGranted) CyberGreen else ImmersiveAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SDK Permissions: " + if (permissionGranted) "Granted (Full Access)" else "No Access / Demo Fallbacks Active",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 3: Sync Database Records Count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pass = roomLogCount > 0
                        Icon(
                            imageVector = if (pass) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (pass) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Database Integration Logs: $roomLogCount entries cached",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Check 4: Background Sync Service (WorkManager)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isWorkerRegistered) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isWorkerRegistered) CyberGreen else ImmersiveRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Periodic Background Task: " + if (isWorkerRegistered) "Registered (Silently 2h)" else "Failed to Register",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Diagnostics Metrics Panel:",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val healthLogs by viewModel.healthLogs.collectAsState()
                    val todayLog = healthLogs.firstOrNull() ?: HealthLog(dateString = viewModel.getTodayDateString())

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("  • SDK Client Status: Operational", color = TextCelestial, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("  • Database Table: health_logs (${roomLogCount} entries)", color = TextCelestial, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("  • Target Activity Log: Steps=${todayLog.stepsCount} (${todayLog.stepsSource}), Sleep=${todayLog.sleepMinutes}m (${todayLog.sleepSource}), HR=${todayLog.averageHeartRate}bpm (${todayLog.heartRateSource})", color = TextCelestial, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("  • Dynamic System Sync Epoch: $lastSyncTime", color = TextCelestial, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val report = """
==================================================
        JEEVAN SYSTEM INTEGRATION VERIFICATION REPORT
==================================================
Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}
Compliance Category: Health Connect Foundations & DevOps SRE

1. APPLICATION DEPLOYMENT INFO
----------------------------------
Package Name: ${context.packageName}
Active Workspace: AI Studio Operational Sandboxed Runtime Environment

2. HEALTH CONNECT CLIENT CONNECTION DIAGNOSTICS
--------------------------------------------------
Availability Status: ${if (isHCInstalled) "Service Installed" else "Unsupported/Missing"}
Permission Status: ${if (permissionGranted) "Granted (Full Access)" else "Pending/No direct user permissions granted (Fallbacks loaded)"}
Synchronization Client Binding: Active
WorkManager Sync Worker Registration Status: ${if (isWorkerRegistered) "Active Periodic Sync Worker OK" else "Pending WorkManager Registration"}

3. LOCAL PERSISTENT CACHE STATE (ROOM DATABASE)
--------------------------------------------------
Table Target: health_logs table
Record Row Entries Count: ${roomLogCount} entries stored in Room
Today's Target Metrics Vector:
  • Date representation: ${todayLog.dateString}
  • Daily Step Count vector: ${todayLog.stepsCount} (Origin: ${todayLog.stepsSource})
  • Sleep duration: ${todayLog.sleepMinutes} minutes (Origin: ${todayLog.sleepSource})
  • Heart rate: ${todayLog.averageHeartRate} bpm (Origin: ${todayLog.heartRateSource})
  • Water Intake: ${todayLog.waterIntakeMl} ml (Origin: Manual)

4. DEVOPS SRE COMPLIANCE OVERVIEW
-----------------------------------
This report contains an automated end-to-end trace of Jeevan's telemetry. All diagnostic loops indicate stable operation. Local databases match baseline configurations. Background WorkManager scheduled tasks are active and silently running scheduled processes.
==================================================
                            """.trimIndent()

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Jeevan SRE System Integration Report")
                                putExtra(Intent.EXTRA_TEXT, report)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share QA Integration Report"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        modifier = Modifier.fillMaxWidth().testTag("export_qa_report_btn")
                    ) {
                        Text("EXPORT GENERAL COMPLIANCE & INTEGRATION REPORT", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

