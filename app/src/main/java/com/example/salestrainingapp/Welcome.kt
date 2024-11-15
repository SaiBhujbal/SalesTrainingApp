package com.example.salestrainingapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {
    var scale by remember { mutableStateOf(1f) }
    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(scaleAnimation) {
        scale = scaleAnimation
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate("dashboard") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
                Row {
                    Button(
                        onClick = { /* Handle invite */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Invite",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invite", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /* Handle notifications */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Circle
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .scale(scale)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .clickable { navController.navigate("product_selection") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Hi, Sales Pro 👋",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Tap to start",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(16.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Explore Section
            Text(
                "Explore",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(360.dp)
            ) {
                item {
                    ExploreCard(
                        icon = Icons.Default.DirectionsRun,
                        title = "Practice Scenarios",
                        description = "Hone your skills with realistic sales simulations",
                        color = Color(0xFF8B5CF6)
                    )
                }
                item {
                    ExploreCard(
                        icon = Icons.Default.FlashOn,
                        title = "AI Feedback",
                        description = "Get instant insights to improve your techniques",
                        color = Color(0xFFF97316)
                    )
                }
                item {
                    ExploreCard(
                        icon = Icons.Default.TrendingUp,
                        title = "Progress Tracking",
                        description = "Monitor your improvement over time",
                        color = Color(0xFF22C55E)
                    )
                }
                item {
                    ExploreCard(
                        icon = Icons.Default.School,
                        title = "Learn Strategies",
                        description = "Access expert negotiation tactics and tips",
                        color = Color(0xFF3B82F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Section
            Text(
                "Product Progress",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(360.dp)
            ) {
                item { ProductProgressCard("Sneakers", 0.75f, 3) }
                item { ProductProgressCard("Smartphones", 0.6f, 2) }
                item { ProductProgressCard("Travel Packages", 0.4f, 1) }
                item { ProductProgressCard("Financial Services", 0.3f, 1) }
            }
        }
    }
}

@Composable
fun ExploreCard(icon: ImageVector, title: String, description: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ProductProgressCard(productName: String, progress: Float, level: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                productName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(80.dp),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                )
                Text(
                    "Level\n$level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            Text(
                "${(progress * 100).toInt()}% Complete",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}