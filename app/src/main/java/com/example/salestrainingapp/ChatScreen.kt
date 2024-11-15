package com.example.salestrainingapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class Message(
    val id: Int,
    val content: String,
    val sender: String,
    val mood: String? = null,
    val convictionScore: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, productName: String, personName: String) {
    var messages by remember { mutableStateOf(listOf(
        Message(1, "Our premium $productName are designed with cutting-edge technology to provide maximum comfort and performance. They feature advanced cushioning, breathable materials, and ergonomic support.", "ai", "Informative", 30),
        Message(2, "That sounds interesting, but I'm concerned about the price. Can you tell me more about the value proposition?", "user"),
        Message(3, "I understand your concern about the price. While our $productName are a premium product, they offer exceptional value. The advanced technology and high-quality materials ensure durability, meaning they'll last longer than average $productName.", "ai", "Persuasive", 45)
    )) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showLevelCompletionPopup by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E20))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1E1E20))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text(
                            text = "Level 1",
                            color = Color.Black,
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        IconButton(onClick = { /* TODO: Implement refresh */ }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Convince $personName about $productName",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Chat Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFF2A2A2B))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // AI Label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("★", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sales AI", color = Color.White, fontSize = 14.sp)
                    }

                    // Messages
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(messages) { message ->
                            MessageBubble(message)
                        }
                    }

                    // Input Area
                    var inputText by remember { mutableStateOf("") }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Type your message...", color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xFF3A3A3B), CircleShape),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.White
                            )
                        )
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val newMessage = Message(
                                        id = messages.size + 1,
                                        content = inputText,
                                        sender = "user"
                                    )
                                    messages = messages + newMessage
                                    inputText = ""
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                    // Simulate AI being convinced after 5 messages
                                    if (messages.size >= 5) {
                                        showLevelCompletionPopup = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Level Completion Popup
        if (showLevelCompletionPopup) {
            Dialog(onDismissRequest = { showLevelCompletionPopup = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Level 1 Passed!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Congratulations! You've successfully completed Level 1.",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { /* TODO: Navigate to next level */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Continue to Level 2")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { navController.navigate("welcome") }) {
                            Text("Go to Home Page", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .scale(animatedScale),
        horizontalAlignment = if (message.sender == "user") Alignment.End else Alignment.Start
    ) {
        Row(
            horizontalArrangement = if (message.sender == "user") Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (message.sender == "ai") {
                Avatar(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Surface(
                    color = if (message.sender == "user") Color.White else Color(0xFF3A3A3B),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (message.sender == "user") Color.Black else Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                if (message.mood != null && message.convictionScore != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "Mood: ${message.mood}",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Conviction: ${message.convictionScore}%",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            if (message.sender == "user") {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
        }
    }
}

@Composable
fun Avatar(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}