package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
                                text = "COGNITIVE ASSISTANT v5",
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
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ImmersiveIndigo.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "STREAK: ${userProfile.careerStreak}D",
                            color = ImmersiveIndigo,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
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
                        label = "Brain",
                        icon = Icons.Default.Search,
                        isSelected = activeTab == "BRAIN",
                        onClick = { viewModel.setActiveTab("BRAIN") },
                        tag = "tab_brain"
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
                "BRAIN" -> BrainChatHub(viewModel)
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
                        Text(
                            text = "Good morning, ${userProfile.name}.",
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
                    EcosystemIndicatorChip("STUDY STREAK", "${userProfile.careerStreak} Days", CyberGreen)
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

        // News Journal
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEWS JOURNAL",
                    color = ImmersiveTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = if (showAddNewsView) "Close Panel" else "+ Add Article",
                    color = ImmersiveIndigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddNewsView = !showAddNewsView }
                )
            }
        }

        if (showAddNewsView) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                    border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ARCHIVE EXTERNAL SOURCE", color = ImmersiveTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newsTitle,
                            onValueChange = { newsTitle = it },
                            label = { Text("Title", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("news_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Category Index:", color = ImmersiveTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("DevOps", "Finance", "Job Openings").forEach { cat ->
                                val selected = newsCat == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) ImmersiveIndigo else ImmersiveSurface)
                                        .border(0.5.dp, if (selected) CyberCyan else Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .clickable { newsCat = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(cat.uppercase(), color = if (selected) Color.White else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newsUrl,
                            onValueChange = { newsUrl = it },
                            label = { Text("URL / Resource Node", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newsDesc,
                            onValueChange = { newsDesc = it },
                            label = { Text("Insights / Key updates", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveIndigo,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (newsTitle.isNotBlank() && newsDesc.isNotBlank()) {
                                    viewModel.bookmarkNews(newsTitle, newsCat, newsUrl, newsDesc)
                                    newsTitle = ""
                                    newsUrl = ""
                                    newsDesc = ""
                                    showAddNewsView = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                            modifier = Modifier.fillMaxWidth().testTag("news_submit_button")
                        ) {
                            Text("Bookmark into Local Journal", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List Bookmarks
        if (newsBookmarks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No articles bookmarked in database.", color = ImmersiveTextMuted, fontSize = 11.sp)
                }
            }
        } else {
            items(newsBookmarks) { news ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = news.title,
                                color = ImmersiveTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.85f)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove bookmark",
                                tint = ImmersiveRose.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.removeNewsBookmark(news) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ImmersiveIndigo.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(news.category.uppercase(), color = ImmersiveIndigo, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            if (news.url.isNotBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = news.url,
                                    color = ImmersiveTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = news.description,
                            color = ImmersiveTextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
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

        // INTEGRATED CAREER INVESTMENT GOALS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Career Savings Icon",
                        tint = ImmersiveAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CAREER INVESTMENT GOALS",
                        color = ImmersiveTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (showAddGoalPanel) "Cancel" else "+ Add Goal",
                    color = ImmersiveAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddGoalPanel = !showAddGoalPanel }
                )
            }
        }

        if (showAddGoalPanel) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                    border = BorderStroke(1.dp, ImmersiveAmber.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ESTABLISH CAREER TARGET FUND", color = ImmersiveAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newGoalName,
                            onValueChange = { newGoalName = it },
                            label = { Text("Goal Name (e.g. AWS Certification)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("career_goal_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImmersiveAmber,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = newGoalTarget,
                                onValueChange = { newGoalTarget = it },
                                label = { Text("Target (₹)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("career_goal_target_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ImmersiveAmber,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = newGoalCurrent,
                                onValueChange = { newGoalCurrent = it },
                                label = { Text("Current Initial Saved (₹)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("career_goal_current_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ImmersiveAmber,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val targetAmt = newGoalTarget.toDoubleOrNull() ?: 0.0
                                val curAmt = newGoalCurrent.toDoubleOrNull() ?: 0.0
                                if (newGoalName.isNotBlank() && targetAmt > 0) {
                                    viewModel.addCareerGoalFund(newGoalName, targetAmt, curAmt)
                                    newGoalName = ""
                                    newGoalTarget = ""
                                    newGoalCurrent = ""
                                    showAddGoalPanel = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveAmber),
                            modifier = Modifier.fillMaxWidth().testTag("career_goal_add_button")
                        ) {
                            Text("Spawn Target Fund", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Render Career Goal Funds Items
        if (careerGoalFunds.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No career investment funds established.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            items(careerGoalFunds) { fund ->
                val progressRatio = (fund.currentAmount / fund.targetAmount).coerceIn(0.0, 1.0)
                val isCompleted = fund.currentAmount >= fund.targetAmount
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(0.6.dp, ImmersiveAmber.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = fund.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    if (isCompleted) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(ImmersiveEmerald.copy(alpha = 0.15f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("SECURED", color = ImmersiveEmerald, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = "Saved: ₹${fund.currentAmount} / Target: ₹${fund.targetAmount}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteCareerGoalFund(fund) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Career Fund",
                                    tint = ImmersiveRose.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(
                                progress = progressRatio.toFloat(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = ImmersiveAmber,
                                trackColor = ImmersiveSurfaceVariant
                            )
                            Text(
                                text = "${(progressRatio * 100).toInt()}%",
                                color = ImmersiveAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Funding shortcut keys
                        if (!isCompleted) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val option1 = 1000.0
                                val option2 = 5000.0
                                val canFundOpt1 = userProfile.balanceAmount >= option1
                                val canFundOpt2 = userProfile.balanceAmount >= option2

                                Button(
                                    onClick = { viewModel.contributeToCareerGoal(fund, option1) },
                                    enabled = canFundOpt1,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ImmersiveAmber.copy(alpha = 0.15f),
                                        contentColor = ImmersiveAmber
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("+ Dedux ₹1,000", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.contributeToCareerGoal(fund, option2) },
                                    enabled = canFundOpt2,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ImmersiveAmber.copy(alpha = 0.15f),
                                        contentColor = ImmersiveAmber
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("+ Dedux ₹5,000", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
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

                    val finalNews = generatePersonalizedNewsForHoldings(portfolios)

                    finalNews.forEach { news ->
                        Column(modifier = Modifier.padding(vertical = 5.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(news.first.uppercase(), color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Holding News", color = TextMuted, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = news.second, color = ImmersiveTextPrimary, fontSize = 11.sp)
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.04f), modifier = Modifier.padding(vertical = 4.dp))
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

    var activeSubTab by remember { mutableStateOf("ROADMAP") } // "ROADMAP" or "ARCHIVE"
    var selectedValidationSubtopic by remember { mutableStateOf<String?>(null) }
    var skippedReasonInput by remember { mutableStateOf("Busy") }
    var quizGradedScoreInput by remember { mutableStateOf("90") }

    val coreTopics = listOf(
        Pair("aws", "AWS Cloud Services"),
        Pair("docker", "Docker Infrastructure"),
        Pair("kubernetes", "Kubernetes Clustering")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Job Readiness Dashboard card
        item {
            val totalSub = subList.size.coerceAtLeast(1)
            val completeSub = subList.count { it.isCompleted }
            val dynamicReadinessRatio = (completeSub.toDouble() / totalSub * 100).toInt()

            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.7f)) {
                        Text("DYNAMIC JOB READINESS DEVOPS INDEX", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Level ${ (completeSub/2).coerceAtLeast(1) } Platform Engineer",
                            color = CyberCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Based on $completeSub completed subtopics validated across learning engines.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                progress = dynamicReadinessRatio / 100f,
                                color = CyberCyan,
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$dynamicReadinessRatio%", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Hub Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { activeSubTab = "ROADMAP" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeSubTab == "ROADMAP") CyberCyan else ImmersiveSurfaceVariant
                    )
                ) {
                    Text("ROADMAP TRACKS", fontSize = 11.sp, color = if (activeSubTab == "ROADMAP") Color.Black else Color.White)
                }
                Button(
                    onClick = { activeSubTab = "ARCHIVE" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeSubTab == "ARCHIVE") CyberCyan else ImmersiveSurfaceVariant
                    )
                ) {
                    Text("COMPLETED ARCHIVE", fontSize = 11.sp, color = if (activeSubTab == "ARCHIVE") Color.Black else Color.White)
                }
            }
        }

        if (activeSubTab == "ROADMAP") {
            // Relational Expanding Modules display
            coreTopics.forEach { (topicId, topicTitle) ->
                item {
                    Text(
                        text = topicTitle.uppercase(),
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                val subtopicsOfThisTopic = subList.filter { it.parentTopicId == topicId }
                items(subtopicsOfThisTopic) { sub ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.04f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val pretty = sub.subtopicId.replace("_", " ").uppercase()
                                Text(pretty, color = TextCelestial, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                if (sub.isCompleted) {
                                    Text("Status: Valued score -> ${sub.assessmentScore}%", color = CyberGreen, fontSize = 11.sp)
                                } else {
                                    val logReason = sub.reasonNotCompleted ?: "Awaiting verification"
                                    Text("Status: Incomplete (${logReason})", color = ImmersiveAmber, fontSize = 11.sp)
                                }
                            }
                            Checkbox(
                                checked = sub.isCompleted,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedValidationSubtopic = sub.subtopicId
                                    } else {
                                        viewModel.toggleSubtopic(sub.subtopicId, topicId, false, "Need Revision", 0)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = CyberGreen)
                            )
                        }
                    }
                }
            }

            // Quick scenario validation panel
            if (selectedValidationSubtopic != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                        border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "VALIDATE DEVOPS MODULE: ${selectedValidationSubtopic?.uppercase()}",
                                color = CyberGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = quizGradedScoreInput,
                                onValueChange = { quizGradedScoreInput = it },
                                label = { Text("Enter graded assessment score (0 - 100)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("subtopic_score_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberGreen,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("If not fully completed, select blockers reason:", color = TextMuted, fontSize = 10.sp)
                            val reasons = listOf("Busy", "Difficult Topic", "Lack of Time", "Need Revision")
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                reasons.forEach { reas ->
                                    Button(
                                        onClick = { skippedReasonInput = reas },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (skippedReasonInput == reas) CyberGreen else ImmersiveSurface
                                        )
                                    ) {
                                        Text(reas, fontSize = 7.sp, color = if (skippedReasonInput == reas) Color.Black else Color.White)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        val parent = when {
                                            selectedValidationSubtopic?.contains("aws") == true -> "aws"
                                            selectedValidationSubtopic?.contains("docker") == true -> "docker"
                                            else -> "kubernetes"
                                        }
                                        viewModel.toggleSubtopic(
                                            selectedValidationSubtopic!!,
                                            parent,
                                            true,
                                            null,
                                            quizGradedScoreInput.toIntOrNull() ?: 90
                                        )
                                        selectedValidationSubtopic = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                                    modifier = Modifier.weight(1f).testTag("confirm_subtopic_verification")
                                ) {
                                    Text("Verify Complete (+40 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        val parent = when {
                                            selectedValidationSubtopic?.contains("aws") == true -> "aws"
                                            selectedValidationSubtopic?.contains("docker") == true -> "docker"
                                            else -> "kubernetes"
                                        }
                                        viewModel.toggleSubtopic(
                                            selectedValidationSubtopic!!,
                                            parent,
                                            false,
                                            skippedReasonInput,
                                            0
                                        )
                                        selectedValidationSubtopic = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveRose),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Save as Blocked", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // DevOps Interview Mini Lab
            item {
                Text(
                    text = "DEVOPS TROUBLESHOOTING QUIZ",
                    color = ImmersiveIndigo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                val index by viewModel.currentQuizIndex.collectAsState()
                val quizFeedback by viewModel.quizFeedback.collectAsState()

                val quizScenarios = listOf(
                    DevopsQuiz("aws", "Active-passive failover", "A multi-region EC2 web application crashes on subnet failure. Correct strategy to resolve?", listOf("Configure Route 53 latency records", "Route 53 active-passive Failover policies to secondary region", "Manually build warm standby instances"), 1),
                    DevopsQuiz("kubernetes", "OOMKilled Troubleshooting", "Pods display 'OOMKilled' during container bootstrap. Cause?", listOf("Subnet limits reached", "Container exceeded declared limits.resources.limits.memory limits", "ConfigMap namespace conflicts"), 1)
                )

                val activeQuiz = quizScenarios.getOrNull(index) ?: quizScenarios.first()

                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "SCENARIO: ${activeQuiz.title.uppercase()}", color = ImmersiveIndigo, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = activeQuiz.question, color = TextCelestial, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))

                        activeQuiz.options.forEachIndexed { optIndex, optionText ->
                            Button(
                                onClick = { viewModel.processQuizAnswer(activeQuiz.topicId, optIndex, activeQuiz.correctAnswerIndex) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSurfaceVariant),
                                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Text(text = optionText, color = TextCelestial, fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }

                        if (quizFeedback != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = quizFeedback!!, color = if (quizFeedback!!.contains("Correct")) CyberGreen else ImmersiveRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Prev Scenario", 
                                color = ImmersiveIndigo, 
                                fontSize = 11.sp, 
                                modifier = Modifier.clickable { viewModel.setQuizIndex(0) }
                            )
                            Text(
                                "Next Scenario", 
                                color = ImmersiveIndigo, 
                                fontSize = 11.sp, 
                                modifier = Modifier.clickable { viewModel.setQuizIndex(1) }
                            )
                        }
                    }
                }
            }

            // Spaced Repetition Revision Schedule
            item {
                Text(
                    text = "SPACED REPETITION REVISION SCHEDULE",
                    color = ImmersiveIndigo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                val completedSubList = subList.filter { it.isCompleted }
                val sdf = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()) }
                val context = androidx.compose.ui.platform.LocalContext.current

                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "To maximize retention, reviews are scheduled for 1-Day (24h) and 7-Day (1 Week) post-study intervals.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (completedSubList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(8.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No completed subtopics yet. Check subtopics in the Roadmap above to add items to your revision log.",
                                    color = ImmersiveAmber,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            completedSubList.forEach { sub ->
                                val compTime = sub.completionDate ?: System.currentTimeMillis()
                                val nextDayDue = compTime + 24 * 60 * 60 * 1000L
                                val weekDue = compTime + 7 * 24 * 60 * 60 * 1000L
                                val now = System.currentTimeMillis()

                                val name = sub.subtopicId.replace("_", " ").uppercase()
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(ImmersiveSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name,
                                            color = TextCelestial,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(0.7f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberGreen.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "COMPLETED",
                                                color = CyberGreen,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Learned on: ${sdf.format(java.util.Date(compTime))}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    androidx.compose.material3.HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Checkpoint 1: 1 Day Review
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(0.6f)) {
                                            Text("1-DAY ACTIVE RECALL", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            val isPastNextDay = now >= nextDayDue
                                            val nextDayText = if (isPastNextDay) "⚠️ REVIEW ACTIVE" else "⌛ Due: ${sdf.format(java.util.Date(nextDayDue))}"
                                            Text(
                                                text = nextDayText,
                                                color = if (isPastNextDay) ImmersiveRose else CyberCyan,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.awardXp(sub.parentTopicId, 15)
                                                android.widget.Toast.makeText(context, "Completed 1-Day recall checkpoint! +15 XP rewarded in ${sub.parentTopicId.uppercase()}.", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Mark Recalled", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Checkpoint 2: 7 Day Review
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(0.6f)) {
                                            Text("7-DAY MASTER REVISION", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            val isPastWeek = now >= weekDue
                                            val weekText = if (isPastWeek) "⚠️ MASTER REVISION ACTIVE" else "⌛ Due: ${sdf.format(java.util.Date(weekDue))}"
                                            Text(
                                                text = weekText,
                                                color = if (isPastWeek) ImmersiveRose else CyberCyan,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.awardXp(sub.parentTopicId, 30)
                                                android.widget.Toast.makeText(context, "Completed 7-Day master retention milestone! +30 XP rewarded in ${sub.parentTopicId.uppercase()}.", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Mark Retained", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // COMPLETED ARCHIVE TAB
            val completedSubList = subList.filter { it.isCompleted }
            if (completedSubList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No completed modules registered in archive DB.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            } else {
                items(completedSubList) { sub ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                        border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                val cleanName = sub.subtopicId.replace("_", " ").uppercase()
                                Text(cleanName, color = TextCelestial, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Score: ${sub.assessmentScore}%", color = CyberGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val dateString = if (sub.completionDate != null) Date(sub.completionDate).toString() else "Seeded module"
                            Text("Completed On: $dateString", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

data class DevopsQuiz(
    val topicId: String,
    val title: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

// --------------------------------------------------
// 4. HOLISTIC WELLNESS & DETOX FOCUS HUB
// --------------------------------------------------
@Composable
fun HealthHub(viewModel: JeevanViewModel) {
    val healthLogs by viewModel.healthLogs.collectAsState()
    val todayLog = healthLogs.firstOrNull() ?: HealthLog(dateString = viewModel.getTodayDateString())

    var journalText by remember { mutableStateOf("") }
    var moodSliderRating by remember { mutableStateOf(todayLog.moodScore) }
    var mentalStateSavedMessage by remember { mutableStateOf(false) }

    var loggedFoodName by remember { mutableStateOf("") }
    var loggedFoodGrams by remember { mutableStateOf("") }
    var loggedNutritionResult by remember { mutableStateOf<String?>(null) }

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

        // COGNITIVE MOOD JOURNAL (Moved precisely out of Dashboard)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = BorderStroke(1.dp, ImmersiveIndigo.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COGNITIVE MOOD LOCKER",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mood Sliders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        (1..5).forEach { score ->
                            val scoreColor = when(score) {
                                1 -> ImmersiveRose
                                2 -> ImmersiveRose.copy(alpha = 0.7f)
                                3 -> ImmersiveAmber
                                4 -> ImmersiveEmerald.copy(alpha = 0.7f)
                                5 -> ImmersiveEmerald
                                else -> ImmersiveIndigo
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (moodSliderRating == score) scoreColor else ImmersiveSurfaceVariant)
                                    .clickable { moodSliderRating = score }
                                    .border(
                                        width = 1.5.dp,
                                        color = if (moodSliderRating == score) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = score.toString(),
                                    color = if (moodSliderRating == score) Color.Black else TextCelestial,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("Log daily cognitive blockers, DevOps stress factors...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("journal_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImmersiveIndigo,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.saveMoodAndJournal(moodSliderRating, journalText)
                            mentalStateSavedMessage = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("submit_mood_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveIndigo),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Record Mental State", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (mentalStateSavedMessage) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✔ Mental indicators successfully compiled into database context.",
                            color = CyberGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
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
                        text = "Location status: New Delhi (Warm Summer season active).\n" +
                        "Suggested foods: Local cucumber grids, light yogurt fluids, cooling watermelon plates.\n" +
                        "Seasonal dehydration danger coefficient: 85%. Increase target water intake to 3500ml.",
                        color = TextCelestial,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
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
                    Text(
                        text = "OFFICE WORKOUT SCHEME (ADAPTED)",
                        color = ImmersiveIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val exercises = listOf(
                        "Squats: 3 sets x 15 reps (Leg focus)",
                        "Pushups: 3 sets x 12 reps (Chest baseline)",
                        "Yoga Stretches: 10 mins (Shoulder & Neck flexibility)",
                        "Walking step targets: 30 mins (Cardio health)"
                    )
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
    }
}

// --------------------------------------------------
// 5. CENTRAL AI BRAIN CHAT INTERACTIVE HUB
// --------------------------------------------------
@Composable
fun BrainChatHub(viewModel: JeevanViewModel) {
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
