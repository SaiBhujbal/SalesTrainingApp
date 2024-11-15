package com.example.salestrainingapp

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, dashboardViewModel: DashboardViewModel = viewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Performance") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                StatCardsGrid(dashboardViewModel.stats)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                PerformanceAnalyticsCard(dashboardViewModel)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                RecentTrainingSessions(dashboardViewModel.recentSessions)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                OverallProgressCard(dashboardViewModel.overallProgress)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("product_selection") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "Start New Training Session",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun StatCardsGrid(stats: List<StatItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(240.dp)
    ) {
        items(stats) { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
fun StatCard(stat: StatItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(stat.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    stat.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                stat.title,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                stat.value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    stat.description,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PerformanceAnalyticsCard(viewModel: DashboardViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Performance Analytics",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(200.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("By Product") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("By Skill") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                when (selectedTab) {
                    0 -> ProductPerformanceChart(
                        data = viewModel.productPerformance,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.secondary,
                        gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    1 -> SkillPerformanceChart(
                        data = viewModel.skillPerformance,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.secondary,
                        gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentTrainingSessions(sessions: List<SessionData>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Training Sessions",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* View all */ }) {
                    Text("View All", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            sessions.forEach { session ->
                SessionItem(session)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SessionItem(session: SessionData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    session.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    session.product,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${session.score}%",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                session.outcome,
                color = if (session.outcome == "Mastered") {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun OverallProgressCard(progress: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Overall Progress",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${(progress * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Overall Mastery",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatBox(
                    value = "156",
                    label = "Total Sessions",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    value = "24",
                    label = "Scenarios Mastered",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}