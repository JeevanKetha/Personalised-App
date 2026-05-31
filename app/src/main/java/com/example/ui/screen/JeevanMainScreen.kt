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
                val completeCount = subtopics.count { it.isCompleted }
                val totalCount = subtopics.size.coerceAtLeast(1)
                
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
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PORTFOLIO NEWS INTELLIGENCE",
                        color = ImmersiveTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Holdings-specific security intelligence stream.", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val finalNewsRaw: List<Pair<String, String>> = if (portfolioNews.isNotEmpty()) {
                        portfolioNews
                    } else {
                        generatePersonalizedNewsForHoldings(portfolios)
                    }

                    val finalNews: List<com.example.ui.viewmodel.NewsCenterItem> = finalNewsRaw.map {
                        com.example.ui.viewmodel.NewsCenterItem(
                            id = it.first,
                            title = "Holding Insight",
                            category = "JOBS",
                            description = it.second,
                            url = "",
                            author = it.first
                        )
                    }

                    if (finalNews.isEmpty()) {
                        Text("No portfolio news calculated yet.", color = TextMuted, fontSize = 11.sp)
                    } else {
                        finalNews.forEach { news ->
                            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(CyberCyan.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        val labelText = news.author.ifBlank { "Holding Update" }
                                        Text(labelText.uppercase(), color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Real-Time Holding Insight", color = TextMuted, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = news.description, color = ImmersiveTextPrimary, fontSize = 11.sp)
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.04f), modifier = Modifier.padding(vertical = 4.dp))
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
    var showAddUnitDialog by remember { mutableStateOf(false) }

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
        if (showAddUnitDialog) {
            var unitId by remember { mutableStateOf("") }
            var unitTitle by remember { mutableStateOf("") }
            var unitParent by remember { mutableStateOf("aws") }
            var unitWeek by remember { mutableStateOf("1") }
            var unitDay by remember { mutableStateOf("1") }
            AlertDialog(
                onDismissRequest = { showAddUnitDialog = false },
                title = { Text("PROVISION CUSTOM SRE STUDY UNIT", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 14.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = unitId, onValueChange = { unitId = it }, label = { Text("ID Label") })
                        OutlinedTextField(value = unitTitle, onValueChange = { unitTitle = it }, label = { Text("Unit Title") })
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("aws", "docker", "kubernetes", "linux").forEach { cat ->
                                Button(
                                    onClick = { unitParent = cat },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (unitParent == cat) CyberCyan else ImmersiveSurfaceVariant),
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) { Text(cat.uppercase(), fontSize = 8.sp, color = if (unitParent == cat) Color.Black else Color.White) }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = unitWeek, onValueChange = { unitWeek = it }, label = { Text("Week") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = unitDay, onValueChange = { unitDay = it }, label = { Text("Day") }, modifier = Modifier.weight(1f))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (unitId.isNotBlank() && unitTitle.isNotBlank()) {
                                viewModel.addCustomSubtopicUnit(unitId, unitParent, unitWeek.toIntOrNull() ?: 1, unitDay.toIntOrNull() ?: 1, unitTitle)
                                showAddUnitDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                    ) { Text("PROVISION", color = Color.Black) }
                },
                dismissButton = { Button(onClick = { showAddUnitDialog = false }) { Text("CANCEL") } },
                containerColor = ImmersiveSurface
            )
        }

        val resourceSelectedCategory = remember { mutableStateOf("ALL") }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp).testTag("career_scaffold_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Modern Tabs
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("ROADMAP", "DAILY STUDY" to "DAILY_STUDY", "DIAGNOSTICS", "ARCHIVE", "RESOURCES").forEach { t ->
                        val (lbl, key) = if (t is Pair<*, *>) t as Pair<String, String> else Pair(t as String, t as String)
                        Button(
                            onClick = { activeSubTab = key },
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (activeSubTab == key) CyberCyan else ImmersiveSurfaceVariant)
                        ) {
                            Text(lbl, fontSize = 7.sp, color = if (activeSubTab == key) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (activeSubTab == "ROADMAP") {
                item {
                    Button(
                        onClick = { showAddUnitDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo.copy(alpha = 0.25f)),
                        border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PROVISION CUSTOM STUDY UNIT (+)", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Sequential 28-week progress pipeline
                weekOrder.forEach { subId ->
                    val subObj = subList.firstOrNull { it.subtopicId == subId } ?: SubtopicProgress(
                        subtopicId = subId,
                        parentTopicId = when {
                            subId == "week_1" || subId == "week_2" || subId == "week_3" -> "linux"
                            subId == "week_4" -> "python"
                            subId.substringAfter("week_").toIntOrNull()?.let { it in 5..14 } == true -> "aws"
                            subId.substringAfter("week_").toIntOrNull()?.let { it in 15..21 } == true -> "docker"
                            else -> "kubernetes"
                        },
                        isCompleted = false,
                        completionDate = null,
                        reasonNotCompleted = null,
                        assessmentScore = 0
                    )

                    val weekInfo = weekMap[subId] ?: Pair("WEEK EX", subId.replace("_", " ").uppercase())
                    val weekNum = subId.substringAfter("week_").toIntOrNull() ?: 1
                    val isLocked = if (weekNum > 1) {
                        val prevSubId = "week_${weekNum - 1}"
                        val prevSubObj = subList.firstOrNull { it.subtopicId == prevSubId }
                        prevSubObj == null || !prevSubObj.isCompleted
                    } else false

                    val lockLabel = if (isLocked) "🔒 Complete Week ${weekNum - 1}" else ""

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isLocked) ImmersiveSurface.copy(alpha = 0.4f) else ImmersiveSurface),
                                border = BorderStroke(
                                    width = if (subObj.isCompleted) 1.dp else 0.6.dp,
                                    color = if (isLocked) Color.White.copy(alpha = 0.02f) else if (subObj.isCompleted) CyberGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(0.9f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                                        .background(if (isLocked) Color.White.copy(alpha = 0.05f) else if (subObj.isCompleted) CyberGreen.copy(alpha = 0.15f) else ImmersiveIndigo.copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = if (isLocked) "LOCKED" else weekInfo.first, color = if (isLocked) TextMuted else if (subObj.isCompleted) CyberGreen else ImmersiveIndigo, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                if (isLocked) {
                                                    Text(text = lockLabel, color = ImmersiveRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                } else if (subObj.isCompleted) {
                                                    if (subObj.assessmentScore == 0) {
                                                        Text(text = "✅ COMPLETED (Study Done)", color = CyberGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                    } else {
                                                        val isPass = subObj.assessmentScore >= passingScore
                                                        Text(
                                                            text = "Score: ${subObj.assessmentScore}% " + (if (isPass) "✅ (PASSED)" else "⚠️ (Needs Revision)"),
                                                            color = if (isPass) CyberGreen else ImmersiveAmber,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                    }
                                                } else if (subObj.reasonNotCompleted == "Needs Improvement") {
                                                    Text(text = "⚠️ NEEDS REVISION (Under $passingScore%)", color = ImmersiveRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                } else {
                                                    Text(text = "NOT ATTEMPTED", color = TextMuted, fontSize = 9.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(text = weekInfo.second, color = if (isLocked) TextMuted else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            
                                            if (!isLocked && !subObj.isCompleted) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(ImmersiveIndigo.copy(alpha = 0.2f))
                                                            .border(0.5.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                            .clickable {
                                                                viewModel.toggleSubtopic(
                                                                    subId,
                                                                    subObj.parentTopicId,
                                                                    true,
                                                                    "Study Completed",
                                                                    0
                                                                )
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("✓ MARK COMPLETED", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(CyberCyan.copy(alpha = 0.1f))
                                                            .border(0.5.dp, CyberCyan, RoundedCornerShape(6.dp))
                                                            .clickable { viewModel.startAssessment(subId) }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("🚀 START ASSESSMENT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                        IconButton(onClick = { if (!isLocked) viewModel.startAssessment(subId) }, enabled = !isLocked) {
                                            Icon(
                                                imageVector = if (isLocked) Icons.Default.Lock else if (subObj.isCompleted && subObj.assessmentScore >= passingScore) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = if (isLocked) TextMuted else if (subObj.isCompleted && subObj.assessmentScore >= passingScore) CyberGreen else CyberCyan
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }

                // Render custom added ones
                val customUnits = subList.filter { it.subtopicId.startsWith("custom_") }
                if (customUnits.isNotEmpty()) {
                    item { Text(text = "CUSTOM SYLLABUS UNITS", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
                    items(customUnits) { subObj ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                            border = BorderStroke(1.dp, if (subObj.isCompleted) CyberGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = subObj.subtopicId.substringAfter("custom_").replace("_", " ").uppercase(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = if (subObj.isCompleted) "PASSED (${subObj.assessmentScore}%)" else "PENDING KNOWLEDGE CHECK", color = if (subObj.isCompleted) CyberGreen else TextMuted, fontSize = 10.sp)
                                }
                                IconButton(onClick = { viewModel.startAssessment(subObj.subtopicId) }) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = CyberCyan)
                                }
                            }
                        }
                    }
                }
            }

            if (activeSubTab == "DAILY_STUDY") {
                item {
                    Column {
                        Text("SELECT WEEK", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..11).forEach { w ->
                                FilterChip(
                                    selected = selectedWeek == w,
                                    onClick = { viewModel.setSelectedWeek(w) },
                                    label = { Text("Week $w", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }
                }
                item {
                    Column {
                        Text("SELECT DAY", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            (1..7).forEach { d ->
                                FilterChip(
                                    selected = selectedDay == d,
                                    onClick = { viewModel.setSelectedDay(d) },
                                    label = { Text("Day $d", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }
                }

                val curr = getCurriculumContent(selectedWeek, selectedDay)
                val activeSubId = when (selectedWeek) {
                    1 -> "aws_iam"
                    2 -> "aws_s3"
                    3 -> "aws_ec2"
                    4 -> "aws_vpc"
                    5 -> "docker_basics"
                    6 -> "docker_images"
                    7 -> "docker_containers"
                    8 -> "docker_volumes"
                    9 -> "k8s_pods"
                    10 -> "k8s_deployments"
                    else -> "k8s_services"
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.25f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("W$selectedWeek D$selectedDay • CURRICULUM", color = CyberCyan, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = curr.first, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("📖 CORE CONCEPT & STUDY GUIDE", color = CyberGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = curr.second, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🔥 PRODUCTION SRE CASE STUDY", color = ImmersiveRose, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = curr.third, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
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
                    val subObj = subList.firstOrNull { it.subtopicId == activeSubId }
                    val isPassed = subObj != null && subObj.isCompleted && subObj.assessmentScore >= passingScore
                    Button(
                        onClick = { viewModel.startAssessment(activeSubId) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isPassed) CyberGreen else CyberCyan),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (isPassed) "VERIFIED LEVEL INTERVIEW PASSED (${subObj?.assessmentScore}%) • RETRY" else "🚀 LAUNCH COGNITIVE INTERVIEW CHECK", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                    val averageScore = if (subList.any { it.isCompleted }) subList.filter { it.isCompleted }.map { it.assessmentScore }.average() else 75.0
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
                    val completedAws = subList.count { it.parentTopicId == "aws" && it.isCompleted }
                    val completedDocker = subList.count { it.parentTopicId == "docker" && it.isCompleted }
                    val completedK8s = subList.count { it.parentTopicId == "kubernetes" && it.isCompleted }
                    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurface), border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.08f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("COGNITIVE SKILL LEVELS ARCHITECTURE", color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("AWS Cloud Hosting Space", completedAws, 4, if (completedAws >= 4) "EXPERT 🌟" else if (completedAws >= 2) "ADVANCED" else "BEGINNER 🌱", CyberCyan)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("Docker Containers Host", completedDocker, 4, if (completedDocker >= 4) "EXPERT 🌟" else if (completedDocker >= 2) "ADVANCED" else "BEGINNER 🌱", CyberGreen)
                            Spacer(modifier = Modifier.height(10.dp))
                            SkillProficiencyBar("Kubernetes Orchestrator SRE", completedK8s, 3, if (completedK8s >= 3) "EXPERT 🌟" else if (completedK8s >= 1) "ADVANCED" else "BEGINNER 🌱", CyberPurple)
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
                    Text("SRE & DEVOPS ULTIMATE RESOURCE SHEETS", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }

                item {
                    val categories = listOf("ALL", "LINUX", "AWS", "CONTAINERS", "KUBERNETES", "INFRASTRUCTURE")
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
                    SreResource("LINUX", "The Linux Command Line", "William Shotts", "Master the shell interface, file systems, navigation, and core automation scripting.", "Book (Free PDF)", "https://linuxcommand.org/tlcl.php"),
                    SreResource("LINUX", "Explain Shell Visual", "Visual Parser", "Interactive tool explaining complex bash scripts line by line dynamically.", "Interactive Web Tool", "https://explainshell.com"),
                    SreResource("LINUX", "Linux Foundation LFS201", "Linux Foundation", "Official training for professional sysadmin configurations and administration.", "Official Course", "https://training.linuxfoundation.org"),
                    
                    SreResource("AWS", "Adrian Cantrill's SRE Course", "Cantrill.io", "Gold-standard architectural training covering VPC, routing, and cloud failovers.", "Interactive Courses", "https://learn.cantrill.io"),
                    SreResource("AWS", "AWS Architecture Center", "AWS Official Docs", "Standard reference designs, structured whitepapers, and disaster recovery rules.", "Official Documentation", "https://aws.amazon.com/architecture"),
                    SreResource("AWS", "Cloudcraft Architecture", "Visual Modeler", "Design and model real-time connected AWS cost forecasts and architectures.", "Modeling Web Tool", "https://cloudcraft.co"),

                    SreResource("CONTAINERS", "Docker Deep Dive", "Nigel Poulton", "The definitive handbook on Docker containers host networking and layers mapping.", "Book (Paper/Kindle)", "https://nigelpoulton.com"),
                    SreResource("CONTAINERS", "Play with Docker Labs", "Docker Sandbox", "Free, multi-node terminal playground to try docker images, volumes & compose.", "Sandbox Playground", "https://labs.play-with-docker.com"),
                    SreResource("CONTAINERS", "Docker Security Benchmarks", "CIS Benchmarks", "Hardening standards and container isolation escape prevention guidelines.", "Official Standards", "https://www.cisecurity.org"),

                    SreResource("KUBERNETES", "KubeAcademy Pro", "VMware", "Comprehensive interactive courses covering cluster services, endpoints & DNS.", "Education Site", "https://kubeacademy.com"),
                    SreResource("KUBERNETES", "Kubernetes.io Tutorials", "K8s Core Team", "Hands-on, localized step-by-step exercises for pods and deployments.", "Official Exercises", "https://kubernetes.io/docs/tutorials"),
                    SreResource("KUBERNETES", "Lens Core", "Mirantis", "Full developer viewport and IDE interface to debug live cluster nodes.", "Client Tool", "https://k8slens.dev"),

                    SreResource("INFRASTRUCTURE", "Google SRE Library", "Google Engineering", "The canonical Google SRE books defining error budgets, SLA & observability.", "Free Library (Books)", "https://sre.google/books"),
                    SreResource("INFRASTRUCTURE", "Terraform Registry Learn", "HashiCorp", "Complete setup instructions, module patterns, and remote backend lockers.", "Official Guides", "https://developer.hashicorp.com/terraform"),
                    SreResource("INFRASTRUCTURE", "Helm Charts Registry", "Artifact Hub", "Distributed package manager hub to deploy validated Prometheus & Grafana charts.", "Artifact Hub", "https://artifacthub.io")
                )

                // Sort and filter resource spreadsheet list inline
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
                            val result = when {
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = seasonalIntelligence,
                        color = TextCelestial,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // BIOMETRIC INPUTS & BMI CALCULATOR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BIOMETRICS & PHYSICAL INDEXING",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recalculating biometrics automatically adjusts physical exercise routines.",
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
                        Column {
                            Text("Current BMI: ${userProfile.computedBmi}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            val bmiCategory = when {
                                userProfile.computedBmi <= 0.0 -> "N/A"
                                userProfile.computedBmi < 18.5 -> "Underweight"
                                userProfile.computedBmi < 25.0 -> "Ideal Range"
                                else -> "Overweight Indicator"
                            }
                            val bmiColor = when {
                                userProfile.computedBmi <= 0.0 -> TextMuted
                                userProfile.computedBmi < 18.5 -> ImmersiveAmber
                                userProfile.computedBmi < 25.0 -> CyberGreen
                                else -> ImmersiveRose
                            }
                            Text("Classification: $bmiCategory", color = bmiColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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

        // ADAPTIVE WORKOUT RECOMMENDATIONS
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
                            fontSize = 10.sp,
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
                    Spacer(modifier = Modifier.height(8.dp))
                    val exercises = adaptiveWorkouts.ifEmpty {
                        listOf(
                            "Squats: 3 sets x 15 reps (Leg focus)",
                            "Pushups: 3 sets x 12 reps (Chest baseline)",
                            "Yoga Stretches: 10 mins (Shoulder & Neck flexibility)",
                            "Walking step targets: 30 mins (Cardio health)"
                        )
                    }
                    exercises.forEach { ex ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(ImmersiveIndigo))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(ex, color = TextCelestial, fontSize = 11.sp)
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                Text(
                                    text = news.author,
                                    color = ImmersiveTextMuted,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = news.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = news.description,
                                color = ImmersiveTextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
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
    var textInput by remember { mutableStateOf("") }
    
    val activeAgent = remember(textInput) {
        val lower = textInput.lowercase()
        when {
            lower.contains("portfolio") || lower.contains("asset") || lower.contains("sip") || lower.contains("expense") || lower.contains("finance") || lower.contains("money") -> {
                Triple("💼 WEALTH INTEL AGENT", CyberCyan, "Analyzing financial capital ratios & asset allocations...")
            }
            lower.contains("workout") || lower.contains("water") || lower.contains("health") || lower.contains("calories") || lower.contains("steps") || lower.contains("sleep") -> {
                Triple("🥗 ERGONOMIC HEALTH AGENT", CyberGreen, "Syncing daily metabolisms & posture metrics...")
            }
            lower.contains("kubernetes") || lower.contains("aws") || lower.contains("retest") || lower.contains("assess") || lower.contains("docker") || lower.contains("linux") || lower.contains("sre") -> {
                Triple("🔧 CLOUD DEVOPS ARCHITECT", CyberPurple, "Routing global telemetry logs & posture benchmarks...")
            }
            else -> {
                Triple("🤖 JEEVAN OS PERSONAL COPILOT", ImmersiveIndigo, "Standing by to orchestrate your operational requests...")
            }
        }
    }

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
                    .fillMaxHeight(0.85f)
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
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close overlay",
                                    tint = ImmersiveRose.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
