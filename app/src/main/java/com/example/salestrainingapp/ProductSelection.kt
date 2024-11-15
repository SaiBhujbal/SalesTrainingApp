package com.example.salestrainingapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Product(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionScreen(navController: NavController) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var animationComplete by remember { mutableStateOf(false) }
    var showProgressPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val products = listOf(
        Product(1, "Sneakers", Icons.Default.DirectionsRun, Color(0xFF8B5CF6)),
        Product(2, "Smartphones", Icons.Default.Smartphone, Color(0xFFF97316)),
        Product(3, "Travel\nPackages", Icons.Default.FlightTakeoff, Color(0xFF22C55E)),
        Product(4, "Financial\nServices", Icons.Default.AccountBalance, Color(0xFF3B82F6))
    )

    LaunchedEffect(Unit) {
        delay(100)
        animationComplete = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Select a Product",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Product Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    ProductCardWithAnimation(
                        product = product,
                        isSelected = selectedProduct == product,
                        onSelect = { selectedProduct = product },
                        visible = animationComplete
                    )
                }
            }

            // Start Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val buttonAlpha by animateFloatAsState(
                    targetValue = if (selectedProduct != null) 1f else 0f,
                    animationSpec = tween(durationMillis = 500)
                )

                Button(
                    onClick = {
                        selectedProduct?.let { product ->
                            coroutineScope.launch {
                                val progress = checkProgress(product.name)
                                if (progress.isNullOrEmpty()) {
                                    navController.navigate("chat/${product.name}/Ravi")
                                } else {
                                    showProgressPopup = true
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 160.dp, max = 200.dp)
                        .alpha(buttonAlpha)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                ) {
                    Text(
                        "Start Training",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Progress Popup
        if (showProgressPopup && selectedProduct != null) {
            Dialog(onDismissRequest = { showProgressPopup = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Previous Progress Found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "We found your previous training progress for ${selectedProduct?.name}. Would you like to continue where you left off or start over?",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showProgressPopup = false
                                    selectedProduct?.let { product ->
                                        navController.navigate("chat/${product.name}/Ravi")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text(
                                    "Reset",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = {
                                    showProgressPopup = false
                                    selectedProduct?.let { product ->
                                        navController.navigate("chat/${product.name}/Ravi")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text(
                                    "Continue",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    maxLines = 1
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
fun ProductCardWithAnimation(
    product: Product,
    isSelected: Boolean,
    onSelect: () -> Unit,
    visible: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500)
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    ProductCard(
        product = product,
        isSelected = isSelected,
        onSelect = onSelect,
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
    )
}

@Composable
fun ProductCard(
    product: Product,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, product.color.copy(alpha = 0.2f))
                    )
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(product.color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(product.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    product.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Master the art of selling ${product.name.toLowerCase().replace("\n", " ")}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

suspend fun checkProgress(product: String): String? {
    // Simulating API call
    delay(1000)
    return if (product == "Sneakers") "Level 2" else null
}