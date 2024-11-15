package com.example.salestrainingapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    var stats by mutableStateOf(listOf<StatItem>())
        private set

    var productPerformance by mutableStateOf(listOf<ChartData>())
        private set

    var skillPerformance by mutableStateOf(listOf<ChartData>())
        private set

    var recentSessions by mutableStateOf(listOf<SessionData>())
        private set

    var overallProgress by mutableStateOf(0f)
        private set

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        stats = listOf(
            StatItem(
                title = "Avg. Conviction Score",
                value = "86%",
                icon = Icons.Default.Psychology,
                description = "+12% from last month",
                color = Color(0xFF8B5CF6)
            ),
            StatItem(
                title = "Scenarios Mastered",
                value = "24",
                icon = Icons.Default.EmojiEvents,
                description = "8 this month",
                color = Color(0xFFF97316)
            ),
            StatItem(
                title = "Customer Interactions",
                value  = "156",
                icon = Icons.Default.Group,
                description = "32 this week",
                color = Color(0xFF3B82F6)
            ),
            StatItem(
                title = "Success Rate",
                value = "92%",
                icon = Icons.Default.Timeline,
                description = "+8% improvement",
                color = Color(0xFF22C55E)
            )
        )

        productPerformance = listOf(
            ChartData("Sneakers", 85f, 70f),
            ChartData("Smartphones", 92f, 85f),
            ChartData("Travel", 78f, 65f),
            ChartData("Finance", 88f, 80f)
        )

        skillPerformance = listOf(
            ChartData("Product Knowledge", 92f, 85f),
            ChartData("Objection Handling", 85f, 80f),
            ChartData("Closing Skills", 78f, 75f),
            ChartData("Customer Rapport", 88f, 82f),
            ChartData("Need Analysis", 90f, 78f)
        )

        recentSessions = listOf(
            SessionData(
                name = "Sarah Chen",
                product = "Sneakers",
                score = 92,
                outcome = "Mastered"
            ),
            SessionData(
                name = "Mike Ross",
                product = "Smartphones",
                score = 85,
                outcome = "In Progress"
            ),
            SessionData(
                name = "Emma Watson",
                product = "Travel Packages",
                score = 88,
                outcome = "Mastered"
            ),
            SessionData(
                name = "John Smith",
                product = "Financial Services",
                score = 79,
                outcome = "In Progress"
            )
        )

        overallProgress = 0.75f
    }
}

data class StatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val description: String,
    val color: Color
)

data class ChartData(
    val label: String,
    val currentScore: Float,
    val previousScore: Float
)

data class SessionData(
    val name: String,
    val product: String,
    val score: Int,
    val outcome: String
)