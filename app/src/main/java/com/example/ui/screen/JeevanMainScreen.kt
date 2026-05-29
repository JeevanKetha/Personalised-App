package com.example.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Transaction
import com.example.data.entity.UserProfile
import com.example.data.entity.HealthLog
import com.example.data.entity.CareerProgress
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
                            text = "Jeevan",
                            color = ImmersiveTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PERSONAL OS v4.2",
                                color = ImmersiveIndigo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Pulsing status dot
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
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
                            text = "ACTIVE SYNC",
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, ImmersiveIndigo.copy(alpha = 0.3f), CircleShape)
                            .background(Brush.sweepGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))))
                    ) {
                        Text(
                            text = userProfile.name.firstOrNull()?.toString()?.uppercase() ?: "A",
                            color = ImmersiveTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
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
                color = ImmersiveSurface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 12.dp),
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
            animationSpec = tween(250),
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
    val bgAlpha = if (isSelected) ImmersiveIndigo.copy(alpha = 0.12f) else Color.Transparent

    Column(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(16.dp))
            .background(bgAlpha)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .widthIn(min = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = tintColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// --------------------------------------------------
// 1. DASHBOARD HUB (CENTRAL AI BRAIN VIEW)
// --------------------------------------------------
@Composable
fun DashboardHub(viewModel: JeevanViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val syntheticInsights by viewModel.syntheticInsights.collectAsState()
    val healthLogs by viewModel.healthLogs.collectAsState()

    var moodSliderRating by remember { mutableStateOf(4) }
    var journalText by remember { mutableStateOf("") }
    var moodLoggedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.linearGradient(listOf(Color(0x506366F1), Color(0x1508090A))))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                // Glow element simulation
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Brush.radialGradient(listOf(ImmersiveIndigo.copy(alpha = 0.15f), Color.Transparent)))
                        .align(Alignment.TopEnd)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ImmersiveIndigo)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI SYSTEM LIVE",
                            color = Color(0xFFA5B4FC), // Indigo-300
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "Good morning, ${userProfile.name}.",
                        color = ImmersiveTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continuous learning state is active. Location index calibrated. Spending parameters are stable.",
                        color = Color(0xFFCBD5E1), // Slate-300
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            EcosystemIndicatorChip(title = "Wallet", value = "₹${userProfile.balanceAmount}", color = ImmersiveEmerald)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EcosystemIndicatorChip(title = "Study", value = "${userProfile.careerStreak} Days", color = ImmersiveIndigo)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EcosystemIndicatorChip(title = "Level", value = "Lvl 3", color = ImmersiveAmber)
                        }
                    }
                }
            }
        }

        // Today's Primary Mission
        item {
            Text(
                text = "⚡ ADAPTIVE DAILY MISSION",
                color = CyberPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberOrange)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Resolve YAML Node Deployment Bug",
                            color = TextCelestial,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Unlocks +15 cloud proficiency XP inside Kubernetes companion tab.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Central AI Insights
        item {
            Text(
                text = "🤖 SYNTHETIC ECOSYSTEM INSIGHTS",
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (syntheticInsights.isEmpty()) {
                    Text(text = "Synthesizing real-time parameters...", color = TextMuted, fontSize = 12.sp)
                } else {
                    syntheticInsights.forEach { insight ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            border = BorderStroke(0.6.dp, CyberCyan.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Insight Icon",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = insight,
                                    color = TextCelestial,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mood Logger & Journal Unit
        item {
            Text(
                text = "🧠 EMOTIONAL COGNITIVE JOURNAL",
                color = CyberGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "How is your mental focus state?",
                        color = TextCelestial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..5).forEach { score ->
                            val scoreColor = when(score) {
                                1 -> Color(0xFFEF5350)
                                2 -> Color(0xFFFF7043)
                                3 -> Color(0xFFFFCA28)
                                4 -> Color(0xFF66BB6A)
                                5 -> CyberCyan
                                else -> CyberGreen
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (moodSliderRating == score) scoreColor else CyberSurfaceVariant
                                    )
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
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("Log daily learnings or emotional blockers...", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGreen,
                            unfocusedBorderColor = CyberSurfaceVariant,
                            focusedTextColor = TextCelestial,
                            unfocusedTextColor = TextCelestial
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.saveMoodAndJournal(moodSliderRating, journalText)
                            moodLoggedSuccess = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_mood_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Record Mental State", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    
                    AnimatedVisibility(visible = moodLoggedSuccess) {
                        Text(
                            text = "✔ Mind indicator compiled successfully into Jeevan DB cache.",
                            color = CyberGreen,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp),
                            fontFamily = FontFamily.Monospace
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
            .clip(RoundedCornerShape(16.dp))
            .background(ImmersiveSurfaceVariant)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title.uppercase(),
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


// --------------------------------------------------
// 2. FINANCE & WEALTH INTELLIGENCE HUB
// --------------------------------------------------
@Composable
fun FinanceHub(viewModel: JeevanViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var transactTitle by remember { mutableStateOf("") }
    var transactAmount by remember { mutableStateOf("") }
    var transactCategory by remember { mutableStateOf("Food") }
    var transactIsSubscription by remember { mutableStateOf(false) }

    val categories = listOf("Food", "Cloud Bills", "SaaS", "Transport", "Salary", "Coaching")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wealth Stats Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("CURRENT CAPITAL INFRASTRUCTURE", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${userProfile.balanceAmount}",
                        color = CyberCyan,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Monthly allocated limit: ₹${userProfile.monthlyBudgetLimit}", color = TextCelestial, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val spent = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                    val rawProgress = if (userProfile.monthlyBudgetLimit > 0) spent / userProfile.monthlyBudgetLimit else 0.0
                    val budgetProgress = if (rawProgress.isNaN() || rawProgress.isInfinite()) 0f else rawProgress.toFloat().coerceIn(0f, 1f)
                    
                    LinearProgressIndicator(
                        progress = budgetProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (budgetProgress > 0.8) CyberOrange else CyberCyan,
                        trackColor = CyberSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Spent: ₹$spent", color = TextMuted, fontSize = 10.sp)
                        Text(text = "${(budgetProgress * 100).toInt()}% of budget usage", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Add Transaction Widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = BorderStroke(0.6.dp, CyberPurple.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "LOG FINANCIAL LEDGER",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = transactTitle,
                        onValueChange = { transactTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transact_title_input"),
                        label = { Text("Transaction Title (eg cloud bill)", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberSurface,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = transactAmount,
                        onValueChange = { transactAmount = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transact_amount_input"),
                        label = { Text("Amount (₹)", color = TextMuted, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberSurface,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Category selection list
                    Text(text = "Category Token:", color = TextMuted, fontSize = 11.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.take(4).forEach { cat ->
                            Button(
                                onClick = { transactCategory = cat },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (transactCategory == cat) CyberCyan else CyberSurface
                                )
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
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

                    Spacer(modifier = Modifier.height(8.dp))
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_expense_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberOrange),
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_income_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Log Income", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Portfolio & Core Financial Pipeline (Google Sheets Synchronized)
        item {
            var selectedSubTab by remember { mutableStateOf("HOLDINGS") } // "HOLDINGS" or "FUTURE"
            
            val holdings = listOf(
                StockHolding("ICICI Pru Silver ETF", 2, 85.0, 92.5),
                StockHolding("IOC (Indian Oil)", 3, 160.0, 168.2),
                StockHolding("ITC Limited", 2, 420.0, 432.1),
                StockHolding("JSW Energy", 1, 600.0, 622.5),
                StockHolding("NTPC Limited", 2, 350.0, 362.4),
                StockHolding("PowerGrid Corp", 2, 300.0, 312.8),
                StockHolding("Union Bank", 1, 135.0, 142.1),
                StockHolding("ICICI Pru Gold ETF", 5, 65.0, 69.3),
                StockHolding("ICICI Pru Nifty Next 50 MF", 1, 500.0, 528.0),
                StockHolding("ONGC (Oil & Gas Corp)", 2, 282.5, 276.5)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wealth_assets_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💎 STRATEGIC WEALTH ASSETS",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedSubTab == "HOLDINGS") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedSubTab = "HOLDINGS" }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Holdings", color = if (selectedSubTab == "HOLDINGS") CyberCyan else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedSubTab == "FUTURE") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedSubTab = "FUTURE" }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Future Pipeline", color = if (selectedSubTab == "FUTURE") CyberCyan else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (selectedSubTab == "HOLDINGS") {
                        val totalInvested = holdings.sumOf { it.qty * it.avgPrice }
                        val currentValue = holdings.sumOf { it.qty * it.currentPrice }
                        val profit = currentValue - totalInvested
                        val profitPct = if (totalInvested > 0) (profit / totalInvested) * 100 else 0.0
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Valuation", color = TextMuted, fontSize = 9.sp)
                                Text("₹${String.format(Locale.US, "%,.2f", currentValue)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Realized Return", color = TextMuted, fontSize = 9.sp)
                                Text("+₹${String.format(Locale.US, "%,.2f", profit)} (+${String.format(Locale.US, "%.2f", profitPct)}%)", color = CyberGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color.White.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            holdings.forEach { h ->
                                val costVal = h.qty * h.avgPrice
                                val currVal = h.qty * h.currentPrice
                                val r = currVal - costVal
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = h.name, color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${h.qty} Units • Avg ₹${h.avgPrice}", color = TextMuted, fontSize = 9.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "₹${String.format(Locale.US, "%,.1f", currVal)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${if (r >= 0) "+" else ""}₹${String.format(Locale.US, "%,.1f", r)}",
                                            color = if (r >= 0) CyberGreen else CyberOrange,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Future Plans View
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(CyberPurple.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎯", fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Index Funds (Goal: Nifty 50 Allocation)", color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("1. UTI Nifty 50 Index Fund — SIP plan limits: ₹1,000 / month", color = TextMuted, fontSize = 10.sp)
                                    Text("2. Parag Parikh Flexi Cap Fund — SIP plan limits: ₹500 / month", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⚡", fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Nifty Next 50 Index Future Acquisitions", color = TextCelestial, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("• NEXT50BETA ETF (Awaiting entry signal)", color = TextMuted, fontSize = 10.sp)
                                    Text("• NEXT50IETF ETF (Strategic reserve buffer)", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ledger Feed
        item {
            Text(
                text = "📊 CAPITAL LEDGER TRANSACTIONS",
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No financial tokens cached yet.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(0.6.dp, if (tx.type == "INCOME") CyberGreen.copy(alpha = 0.4f) else CyberOrange.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tx.title,
                                    color = TextCelestial,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (tx.isSubscription) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberPurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "SaaS", color = CyberPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "Category: ${tx.category} • " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(tx.date)),
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (tx.type == "INCOME") "+" else "-"} ₹${tx.amount}",
                                color = if (tx.type == "INCOME") CyberGreen else CyberOrange,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete token",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.deleteTransaction(tx) }
                            )
                        }
                    }
                }
            }
        }
    }
}


// --------------------------------------------------
// 3. AI DEVOPS CAREER HUB
// --------------------------------------------------
@Composable
fun CareerHub(viewModel: JeevanViewModel) {
    val progress by viewModel.careerProgress.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val timerSeconds by viewModel.timerSecondsRemaining.collectAsState()
    val quizIndex by viewModel.currentQuizIndex.collectAsState()
    val quizFeedback by viewModel.quizFeedback.collectAsState()
    val yamlCode by viewModel.yamlCodeInput.collectAsState()
    val yamlValidation by viewModel.yamlValidationResult.collectAsState()

    // 4 high-fidelity mock quiz assessments from DevOps
    val quizzes = listOf(
        DevOpsQuiz(
            question = "Which AWS strategy resolves Single Point of Failure (SPOF) inside container deployments?",
            options = listOf("Deploy Multi-AZ with ELB load balancer", "Set up standard Amazon S3 buckets", "Write crontab scripts in EC2", "Increase memory limits"),
            correctIndex = 0
        ),
        DevOpsQuiz(
            question = "How does Kubernetes preserve file states inside highly transient Node failure scenarios?",
            options = listOf("Declare Ephemeral hostPath arrays", "Configure PersistentVolumeClaims backed by EBS", "Re-generate image containers constantly", "Enable swap memories"),
            correctIndex = 1
        ),
        DevOpsQuiz(
            question = "What command correctly alters permission states to execute standard custom script nodes in Linux?",
            options = listOf("chown www-data /bin/script", "chmod +x /opt/script.sh", "cat /var/log/syslog", "systemctl reload daemon"),
            correctIndex = 1
        ),
        DevOpsQuiz(
            question = "Which parameter in Python coordinates concurrent processing arrays without blocking main IO limits?",
            options = listOf("import os", "from concurrent.futures import ThreadPoolExecutor", "def yield() lambda", "sys.argv[0]"),
            correctIndex = 1
        )
    )

    val totalXp = progress.sumOf { it.xp }
    val maxTopicLevel = if (progress.isNotEmpty()) progress.maxOf { it.level } else 1
    val skillTreeCount = progress.filter { it.level >= 2 }.size
    val totalSkillTrees = if (progress.isNotEmpty()) progress.size else 5
    val readinessPct = if (totalXp > 0) (80 + (totalXp / 25).coerceAtMost(20)) else 80

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Engineering Status Metrics
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(0.6.dp, CyberPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "DEVOPS PROFICIENCY SCOREBOARD", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Lvl $maxTopicLevel Engineer", color = CyberCyan, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Job Ready: $readinessPct% ATS compatible", color = TextMuted, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Skill Tree: $skillTreeCount/$totalSkillTrees Complete", color = CyberPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = (readinessPct / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyberPurple,
                        trackColor = CyberSurfaceVariant
                    )
                }
            }
        }

        // ActiveStack Knowledge Base Modules
        item {
            Text(
                text = "🛡 ACTIVE STACK KNOWLEDGE BASE",
                color = CyberPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            val getTopicDisplayName = { tId: String ->
                when (tId) {
                    "linux" -> "Linux Essentials (Core Basis)"
                    "kubernetes" -> "Kubernetes Platform (Orchestration Backend)"
                    "python" -> "Python Scripts Automation (Utility Engine)"
                    "aws" -> "AWS Systems Infrastructure (Cloud Host)"
                    "docker" -> "Docker Containers (Local Packaging)"
                    "week_1_2_linux" -> "Week 1-2: Advanced Linux & Bash Scripting"
                    "week_3_4_networking" -> "Week 3-4: Network Topologies & SSH Protocols"
                    "week_5_6_python" -> "Week 5-6: Python for System Automation Tasks"
                    "week_7_8_git" -> "Week 7-8: Git VCS Internals & Multi-Branching"
                    "week_9_10_cicd" -> "Week 9-10: CI/CD Pipelines with GitHub Actions"
                    "week_11_12_terraform" -> "Week 11-12: Infrastructure as Code (Terraform)"
                    "week_13_14_ansible" -> "Week 13-14: Declarative Config (Ansible Playbooks)"
                    "week_15_16_docker" -> "Week 15-16: Optimization & Multi-Stage Dockerfiles"
                    "week_17_18_k8s" -> "Week 17-18: K8s Storage & StatefulSets Orchestration"
                    "week_19_20_aws" -> "Week 19-20: AWS Multi-AZ Deployment & IAM Security"
                    "week_21_22_security" -> "Week 21-22: Cloud Native Compliance & Secrets"
                    "week_23_24_monitoring" -> "Week 23-24: Prometheus Infrastructure & Grafana Hub"
                    "week_25_26_logging" -> "Week 25-26: Logs Parsing with Elasticsearch & Fluentbit"
                    "week_27_28_capstone" -> "Week 27-28: Capstone AWS Systems & ATS Resume Audit"
                    else -> tId.replace("_", " ").uppercase()
                }
            }

            val coreSkills = progress.filter { !it.topicId.startsWith("week_") }
            val roadmapWeeks = progress.filter { it.topicId.startsWith("week_") }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (coreSkills.isNotEmpty()) {
                    Text(
                        text = "❖ CORE DEVOPS STACK COMPETENCIES",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    coreSkills.forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            border = BorderStroke(0.6.dp, CyberCyan.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleTopicDeployment(item.topicId) }
                                .testTag("toggle_core_${item.topicId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = getTopicDisplayName(item.topicId),
                                        color = TextCelestial,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val progressPct = (item.xp % 100) / 100f
                                    Text(
                                        text = "Level ${item.level} • XP: ${item.xp} pts (Progress: ${item.xp % 100}/100)",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = progressPct.coerceIn(0f, 1f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = CyberCyan,
                                        trackColor = CyberSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (item.level >= 2) CyberGreen.copy(alpha = 0.15f) else CyberPurple.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (item.level >= 2) "DEPLOYED" else "STAGED",
                                        color = if (item.level >= 2) CyberGreen else CyberPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                if (roadmapWeeks.isNotEmpty()) {
                    Text(
                        text = "❖ 28-WEEK ULTIMATE DEVOPS CLOUD ROADMAP (Spreadsheet Sync)",
                        color = CyberPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    roadmapWeeks.forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            border = BorderStroke(0.6.dp, CyberPurple.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleTopicDeployment(item.topicId) }
                                .testTag("toggle_week_${item.topicId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = getTopicDisplayName(item.topicId),
                                        color = TextCelestial,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val progressPct = (item.xp % 100) / 100f
                                    Text(
                                        text = "Milestone • status: ${if (item.level >= 2) "100% Completed" else "In Progress"} (Tap to toggle)",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = if (item.level >= 2) 1.0f else 0.25f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (item.level >= 2) CyberGreen else CyberPurple,
                                        trackColor = CyberSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (item.level >= 2) CyberGreen.copy(alpha = 0.15f) else CyberPurple.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (item.level >= 2) "DEPLOYED" else "STAGED",
                                        color = if (item.level >= 2) CyberGreen else CyberPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Deep Work Focus Clock
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⏱ DEEP WORK PRODUCTIVITY TIMER",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    val minutes = timerSeconds / 60
                    val seconds = timerSeconds % 60
                    val formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    
                    Text(
                        text = formattedTime,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(text = "Aesthetic digital detox is engaged.", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { viewModel.toggleTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) CyberOrange else CyberCyan),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("toggle_timer_button")
                        ) {
                            Text(
                                text = if (isTimerRunning) "Pause focus" else "Engage Deep Focus",
                                color = if (isTimerRunning) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.resetTimer() },
                            border = BorderStroke(1.dp, TextMuted),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("reset_timer_button")
                        ) {
                            Text(text = "Reset", color = TextCelestial, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Daily quiz Assessment Challenge
        item {
            val q = quizzes.getOrElse(quizIndex) { quizzes.first() }
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧠 MOCK ASSESSMENT",
                            color = CyberPurple,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Question ${quizIndex + 1}/${quizzes.size}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = q.question,
                        color = TextCelestial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    q.options.forEachIndexed { optIndex, optText ->
                        Button(
                            onClick = {
                                viewModel.processQuizAnswer("linux", optIndex, q.correctIndex)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("quiz_option_$optIndex"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.6.dp, CyberPurple.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "${optIndex + 1}. $optText",
                                color = TextCelestial,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    quizFeedback?.let { feedback ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = feedback,
                            color = if (feedback.contains("Correct")) CyberGreen else CyberOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("quiz_feedback_result")
                        )
                    }

                    if (quizIndex < quizzes.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Next assessment node ➔",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable { viewModel.setQuizIndex(quizIndex + 1) }
                                .padding(vertical = 4.dp)
                                .testTag("next_quiz_button"),
                            fontWeight = FontWeight.Bold
                        )
                    } else if (quizIndex > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🔄 Restart assessments",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable { viewModel.setQuizIndex(0) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Kubernetes Spec YAML Playground Sandbox
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🛠 KUBERNETES DEPLOYMENT COMPILING SANBOX",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = yamlCode,
                        onValueChange = { viewModel.updateYamlCode(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("yaml_code_editor"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberSurfaceVariant,
                            focusedTextColor = TextCelestial,
                            unfocusedTextColor = TextCelestial
                        ),
                        singleLine = false,
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { viewModel.validateYamlCode() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("validate_yaml_button")
                    ) {
                        Text("Validate Deployment spec", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    yamlValidation?.let { result ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = result,
                            color = if (result.contains("checked successfully")) CyberGreen else CyberOrange,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("yaml_validation_result_text")
                        )
                    }
                }
            }
        }
    }
}

data class DevOpsQuiz(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)


// --------------------------------------------------
// 4. FITNESS & HEALTH INTELLIGENCE HUB
// --------------------------------------------------
@Composable
fun HealthHub(viewModel: JeevanViewModel) {
    val healthLogs by viewModel.healthLogs.collectAsState()
    val todayDate = viewModel.getTodayDateString()
    val todayLog = healthLogs.firstOrNull { it.dateString == todayDate } ?: HealthLog(dateString = todayDate)
    val userProfile by viewModel.userProfile.collectAsState()

    var customStepsInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hydration tracker widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💧 ADAPTIVE HYDRATION MONITOR",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${todayLog.waterIntakeMl} ml / ${userProfile.dailyWaterGoalMl} ml",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val rawWaterProgress = if (userProfile.dailyWaterGoalMl > 0) todayLog.waterIntakeMl.toFloat() / userProfile.dailyWaterGoalMl else 0f
                    val waterProgress = if (rawWaterProgress.isNaN() || rawWaterProgress.isInfinite()) 0f else rawWaterProgress.coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = waterProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = CyberCyan,
                        trackColor = CyberSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.addWater(250) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_glass_water_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("+250ml Glass", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.addWater(750) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_flask_water_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("+750ml Flask", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Daily steps simulator widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🏃 ADAPTIVE STEP PEDOMETER",
                        color = CyberPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${todayLog.stepsCount} Steps",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Target: ${userProfile.dailyStepGoal} Steps", color = TextMuted, fontSize = 11.sp)
                        }
                        
                        val percentage = if (userProfile.dailyStepGoal > 0) (todayLog.stepsCount * 100) / userProfile.dailyStepGoal else 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "$percentage% Done", color = CyberPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = customStepsInput,
                            onValueChange = { customStepsInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("steps_input_field"),
                            placeholder = { Text("Add simulated steps (eg 1500)", color = TextMuted, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberPurple,
                                unfocusedBorderColor = CyberSurfaceVariant,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val steps = customStepsInput.toIntOrNull() ?: 0
                                if (steps > 0) {
                                    viewModel.addSteps(steps)
                                    customStepsInput = ""
                                }
                            },
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("add_steps_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Log Steps", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Seasonal Meal Planning Guide & Nutrients (Indian database)
        item {
            Text(
                text = "🥗 SEASONAL ADAPTIVE FOOD INTELLIGENCE (MAY / SUMMER)",
                color = CyberGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = BorderStroke(1.dp, CyberGreen.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Summer Thermal Recovery Formula:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Because outdoor temperature readings represent extreme May context, cellular metabolic heat must be buffered dynamically.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    BulletPointItem(title = "Split Mung Lentil (Khichdi):", desc = "Super digestible protein that supports thermal homeostasis. Prevents cloud-lab brain fatigue.")
                    BulletPointItem(title = "Organic Amla (Indian Gooseberry):", desc = "Packed with massive vitamin C matrices. Protects cognitive functions from screen glow glare.")
                    BulletPointItem(title = "Cardamom Buttermilk (Chaas):", desc = "Instantly lowers core temperatures. Boosts mental processing by replenish sodium buffers.")
                }
            }
        }

        // Desk-Job stretches indicator guide
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🧘 DESK-JOB ERGONOMIC RECOVERY GUIDELINE",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. **Interscapular stretch:** Flex both shoulder blades backwards for 20 seconds. Frees nerves that monitor keyboard inputs.\n" +
                               "2. **The 20-20-20 visual target:** Every 20 minutes look 20 feet away for 20 seconds to relax ciliary ocular focus muscle blocks.",
                        color = TextCelestial,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BulletPointItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "➢ ", color = CyberGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Column {
            Text(text = title, color = TextCelestial, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}


// --------------------------------------------------
// 5. CENTRAL AI BRAIN CHAT INTERACTIVE HUB
// --------------------------------------------------
@Composable
fun BrainChatHub(viewModel: JeevanViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isBrainThinking.collectAsState()
    var inputQuery by remember { mutableStateOf("") }

    val shortPills = listOf(
        "Simulate CKA Interview",
        "AWS cost optimizations",
        "Is split mung summer friendly?",
        "How can I save ₹10000?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Scrolling Log
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface)
                .border(0.6.dp, CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isJeevan = msg.sender.contains("Jeevan")
                    val cardBg = if (isJeevan) CyberSurfaceVariant else CyberPurple.copy(alpha = 0.15f)
                    val cardBorder = if (isJeevan) CyberCyan.copy(alpha = 0.3f) else CyberPurple.copy(alpha = 0.4f)
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isJeevan) Alignment.Start else Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isJeevan) 0.dp else 12.dp,
                                        bottomEnd = if (isJeevan) 12.dp else 0.dp
                                    )
                                )
                                .background(cardBg)
                                .border(
                                    width = 1.dp,
                                    color = cardBorder,
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isJeevan) 0.dp else 12.dp,
                                        bottomEnd = if (isJeevan) 12.dp else 0.dp
                                    )
                                )
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isJeevan) Icons.Default.Info else Icons.Default.Person,
                                        contentDescription = msg.sender,
                                        tint = if (isJeevan) CyberCyan else CyberPurple,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = msg.sender.uppercase(),
                                        color = if (isJeevan) CyberCyan else CyberPurple,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    color = TextCelestial,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = if (isJeevan) FontFamily.Monospace else FontFamily.Default
                                )
                            }
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Jeevan OS reasoning models active...",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        
        // Command suggestion pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            shortPills.take(2).forEach { pill ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .clickable { inputQuery = pill }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = pill, color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TextInput zone
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field"),
                placeholder = { Text("Ask anything to Jeevan Companion...", color = TextMuted, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputQuery.isNotBlank()) {
                        viewModel.sendChatMessage(inputQuery)
                        inputQuery = ""
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .testTag("send_chat_msg_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send query",
                    tint = Color.Black
                )
            }
        }
    }
}

data class StockHolding(
    val name: String,
    val qty: Int,
    val avgPrice: Double,
    val currentPrice: Double
)
