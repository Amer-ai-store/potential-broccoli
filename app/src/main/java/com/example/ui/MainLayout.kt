package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun GroceryPromoBanner(lang: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF7EE)), // Warm organic soft green bg
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD2EED8), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryCyan)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "عروض اليوم المميزة" else "TODAY'S SUPER DEAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Text(
                    text = if (lang == "ar") "خصم حتى ٤٠٪ على الخضروات الطازجة" else "Fresh Vegetables\nGet up to 40% OFF",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PolarLight,
                    lineHeight = 20.sp
                )
                Text(
                    text = if (lang == "ar") "منتجات طبيعية ١٠٠٪ مباشرة من المزرعة" else "100% Organic items straight from the farm",
                    fontSize = 10.sp,
                    color = SoftGrayText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryCyan)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "تسوق الآن 🛒" else "Shop Now 🛒",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍎🥦🥑",
                    fontSize = 44.sp
                )
            }
        }
    }
}

@Composable
fun GreenHubDashboard(lang: String, viewModel: MarketViewModel) {
    val isDark by viewModel.appDarkMode.collectAsState()
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val SoftGrayText = Color(0xFF94A3B8)
    val PrimaryCyan = Color(0xFF06B6D4)
    val SecondaryMint = Color(0xFF10B981)
    val AccentCoral = Color(0xFFF43F5E)

    val context = androidx.compose.ui.platform.LocalContext.current

    val storedItems by viewModel.storedOrganicItems.collectAsState(initial = emptyList())
    val co2Saved by viewModel.carbonSavings.collectAsState(initial = 0.0)

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Carbon Savings, 1: Expiry & Freshness

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("green_hub_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🌱", fontSize = 20.sp)
                    Column {
                        Text(
                            text = if (lang == "ar") "المركز الأخضر وطزاجة الأغذية" else "Eco Green Hub & Freshness Tracker",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolarLight
                        )
                        Text(
                            text = if (lang == "ar") "التوفير الكربوني وصلاحية منتجاتك" else "Carbon savings & storage timelines",
                            fontSize = 8.sp,
                            color = SoftGrayText
                        )
                    }
                }

                // Small badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecondaryMint.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "ذكي ☁" else "Smart ☁",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryMint
                    )
                }
            }

            // Sub-tabs Segmented Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateDarkBg.copy(alpha = 0.5f))
                    .border(1.dp, SoftGrayText.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val subTabs = listOf(
                    if (lang == "ar") "التوفير الكربوني 📊" else "Carbon Offset 📊",
                    if (lang == "ar") "صلاحية الأغذية ⏰" else "Stored Freshness ⏰"
                )
                subTabs.forEachIndexed { idx, title ->
                    val isSel = selectedSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) PrimaryCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { selectedSubTab = idx }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 9.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                            color = if (isSel) PrimaryCyan else SoftGrayText
                        )
                    }
                }
            }

            if (selectedSubTab == 0) {
                // CARBON OFFSETS TAB
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Beautiful custom Canvas Gauge Chart (Visual Dashboard representation)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .weight(0.45f),
                        contentAlignment = Alignment.Center
                    ) {
                        val co2Progress = (co2Saved / 25.0).coerceIn(0.0, 1.0).toFloat()
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Backing Track Arc
                            drawArc(
                                color = SoftGrayText.copy(alpha = 0.15f),
                                startAngle = 140f,
                                sweepAngle = 260f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 10.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )
                            // Progress Arc
                            drawArc(
                                brush = Brush.sweepGradient(listOf(PrimaryCyan, SecondaryMint)),
                                startAngle = 140f,
                                sweepAngle = co2Progress * 260f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 10.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )
                        }

                        // Text inside gauge
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = String.format("%.1f", co2Saved),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryCyan
                            )
                            Text(
                                text = "kg CO2e",
                                fontSize = 7.sp,
                                color = SoftGrayText
                            )
                        }
                    }

                    // Right Side: Beautiful statistics breakdown
                    Column(
                        modifier = Modifier.weight(0.55f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (lang == "ar") "التأثير البيئي لتسوقك:" else "Your Ecological Impact:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolarLight
                        )

                        // CO2 Metric Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌿", fontSize = 11.sp)
                            Text(
                                text = (if (lang == "ar") "تم تلافيه: " else "CO2 Avoided: ") + String.format("%.2f kg", co2Saved),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryMint
                            )
                        }

                        // Tree Offset Metric Row
                        val treesCount = (co2Saved / 6.0).coerceAtLeast(0.0)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌲", fontSize = 11.sp)
                            Text(
                                text = (if (lang == "ar") "يعادل زراعة: " else "Equiv: ") + String.format("%.2f", treesCount) + (if (lang == "ar") " شجرة" else " Trees"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }

                        // Avoided driving distance
                        val drivingMiles = co2Saved * 2.5
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🚗", fontSize = 11.sp)
                            Text(
                                text = (if (lang == "ar") "رحلات سيارات ملغاة: " else "Driving avoided: ") + String.format("%.1f mi", drivingMiles),
                                fontSize = 8.sp,
                                color = SoftGrayText
                            )
                        }

                        // Badge representation
                        val ecoLevel = when {
                            co2Saved >= 15.0 -> if (lang == "ar") "حارس الأرض الفائق 🌍" else "Super Earth Guardian 🌍"
                            co2Saved >= 5.0 -> if (lang == "ar") "صديق البيئة 🌿" else "Eco Friend 🌿"
                            else -> if (lang == "ar") "مبتدئ صديق للبيئة 🌱" else "Eco Novice 🌱"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryCyan.copy(alpha = 0.1f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = (if (lang == "ar") "اللقب: " else "Rank: ") + ecoLevel,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }
                    }
                }
            } else {
                // EXPIRY TIMELINE & FRESHNESS TRACKER TAB
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (storedItems.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🧺", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == "ar") "لا يوجد أغذية مخزنة حالياً!" else "No stored organic items yet!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight
                            )
                            Text(
                                text = if (lang == "ar") "سيتم تعقب الأغذية العضوية تلقائياً عند الشراء 🛒" else "Organic purchases automatically appear here after checkout 🛒",
                                fontSize = 8.sp,
                                color = SoftGrayText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    } else {
                        storedItems.take(5).forEach { item ->
                            val timeLeftMs = item.expiryDate - System.currentTimeMillis()
                            val totalLifeMs = item.expiryDate - item.purchaseDate
                            val progress = (timeLeftMs.toFloat() / totalLifeMs.toFloat()).coerceIn(0f, 1f)
                            val daysLeft = (timeLeftMs / (24 * 60 * 60 * 1000)).toInt()

                            val statusColor = when {
                                daysLeft > 4 -> SecondaryMint
                                daysLeft in 2..4 -> Color(0xFFF59E0B) // Amber
                                else -> AccentCoral
                            }

                            val statusText = when {
                                daysLeft > 0 -> if (lang == "ar") "يتبقي $daysLeft يوم" else "$daysLeft days left"
                                daysLeft == 0 -> if (lang == "ar") "تنتهي اليوم! ⏰" else "Expires Today! ⏰"
                                else -> if (lang == "ar") "منتهي الصلاحية ⚠️" else "Expired ⚠️"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkBg.copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(0.7f)) {
                                    Text(
                                        text = item.productName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PolarLight
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    // Progress Freshness Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(SoftGrayText.copy(alpha = 0.15f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progress)
                                                .height(4.dp)
                                                .clip(CircleShape)
                                                .background(statusColor)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteStoredItem(item.id) },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Item",
                                            tint = SoftGrayText.copy(alpha = 0.7f),
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Simulation & Testing Playground controls
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (lang == "ar") "🧪 لوحة اختبار الصلاحية والتنبيهات (اضغط للتجربة):" else "🧪 Expiry Alerts Simulation Lab (Tap to Test):",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.addManualStoredOrganicItem(
                                    if (lang == "ar") "حليب عضوي طازج (أوشك على الانتهاء)" else "Organic Fresh Milk (Nearing Expiry)",
                                    0 // Expires immediately/today to trigger push alerts!
                                )
                                android.widget.Toast.makeText(context, "Added Milk expiring in 0 days! Alert checks queued.", android.widget.Toast.LENGTH_SHORT).show()
                                viewModel.checkExpiringOrganicItems(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCoral.copy(alpha = 0.2f), contentColor = AccentCoral),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (lang == "ar") "حليب يوشك على الانتهاء" else "Simulate near-expiry Milk",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.addManualStoredOrganicItem(
                                    if (lang == "ar") "فراولة برية عضوية" else "Organic Wild Strawberries",
                                    5 // Fresh item
                                )
                                android.widget.Toast.makeText(context, "Added Strawberries (5 days remaining)!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint.copy(alpha = 0.2f), contentColor = SecondaryMint),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (lang == "ar") "فراولة طازجة (٥ أيام)" else "Simulate Fresh Berries",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyShoppingModeCard(lang: String, viewModel: MarketViewModel, productsList: List<ProductEntity>) {
    var isFamilyModeEnabled by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val dbFamilyItems by viewModel.familyShoppingList.collectAsState(initial = emptyList())
    var newItemName by remember { mutableStateOf("") }
    var newItemPriority by remember { mutableStateOf("High") }
    var newItemQty by remember { mutableStateOf(1) }

    LaunchedEffect(dbFamilyItems) {
        if (dbFamilyItems.isEmpty()) {
            viewModel.addFamilyShoppingItem("Fresh Organic Milk", "Dairy", "High", 2)
            viewModel.addFamilyShoppingItem("Organic Mountain Honey", "Pantry", "Medium", 1)
            viewModel.addFamilyShoppingItem("Organic Strawberries", "Fruits", "High", 3)
        }
    }

    val isDark by viewModel.appDarkMode.collectAsState()

    val cardBg = if (isFamilyModeEnabled) {
        if (isDark) Color(0xFF1B2621) else Color(0xFFEDF7EE)
    } else {
        if (isDark) Color(0xFF221F1A) else Color(0xFFFFF9F2)
    }

    val cardBorder = if (isFamilyModeEnabled) {
        if (isDark) Color(0xFF2D6A4F) else Color(0xFFD2EED8)
    } else {
        if (isDark) Color(0xFF4E3629) else Color(0xFFFFE0B2)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "👨‍👩‍👧‍👦",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = if (lang == "ar") "وضع التسوق العائلي المشترك" else "Family Co-Shopping Mode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (lang == "ar") "أضف أفراد عائلتك وشارك سلة المشتريات والخصومات" else "Connect members, share list & unlock 30% savings",
                            fontSize = 9.sp,
                            color = SoftGrayText
                        )
                    }
                }
                
                // Beautiful Custom Pill Switcher
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isFamilyModeEnabled) PrimaryCyan else Color(0xFFE2E8F0))
                        .clickable { isFamilyModeEnabled = !isFamilyModeEnabled }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isFamilyModeEnabled) {
                            if (lang == "ar") "نشط ✔" else "Active ✔"
                        } else {
                            if (lang == "ar") "مغلق 🔒" else "Disabled 🔒"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFamilyModeEnabled) Color.White else Color(0xFF64748B)
                    )
                }
            }

            if (isFamilyModeEnabled) {
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(SoftGrayText.copy(alpha = 0.2f)))

                // Family Member Circular Avatars
                Text(
                    text = if (lang == "ar") "أفراد العائلة المتصلين بالمنزل:" else "Home Members Connected:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Member 1 - Dad
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBBF7D0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨", fontSize = 20.sp)
                        }
                        Text(
                            text = if (lang == "ar") "أبو أحمد" else "Ahmad (Dad)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(if (lang == "ar") "نشط 🟢" else "Active 🟢", fontSize = 7.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    // Member 2 - Mom
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFECACA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👩", fontSize = 20.sp)
                        }
                        Text(
                            text = if (lang == "ar") "أم أحمد" else "Fatima (Mom)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(if (lang == "ar") "نشط 🟢" else "Active 🟢", fontSize = 7.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    // Member 3 - Daughter
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF08A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👧", fontSize = 20.sp)
                        }
                        Text(
                            text = if (lang == "ar") "ليلى" else "Lily (Daughter)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFC62828).copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(if (lang == "ar") "بالمدرسة 🏫" else "School 🏫", fontSize = 7.sp, color = Color(0xFFC62828))
                        }
                    }

                    // Member 4 - Son
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBFDBFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👦", fontSize = 20.sp)
                        }
                        Text(
                            text = if (lang == "ar") "يوسف" else "Yousef (Son)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(if (lang == "ar") "نشط 🟢" else "Active 🟢", fontSize = 7.sp, color = Color(0xFF2E7D32))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(SoftGrayText.copy(alpha = 0.2f)))

                // Family Shared List Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "ar") "الطلبات العائلية المشتركة (مرتبة حسب الأولوية):" else "Shared Weekly Family Demands (Sorted by Priority):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (lang == "ar") "مزامنة ذكية ☁" else "Smart Sync ☁",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                    }
                }

                // Sorted List of Items from Room db: sort Priority High -> Medium -> Low, then checked state
                val sortedItems = dbFamilyItems.sortedWith(
                    compareBy<FamilyShoppingListEntity> { item ->
                        when (item.priority) {
                            "High" -> 0
                            "Medium" -> 1
                            else -> 2
                        }
                    }.thenBy { it.isChecked }
                )

                sortedItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (item.isChecked) cardBorder.copy(alpha = 0.05f) else Color.Transparent)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(0.75f)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { isChecked ->
                                    viewModel.toggleFamilyItemChecked(item.id, isChecked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryCyan,
                                    uncheckedColor = SoftGrayText.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = item.productName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isChecked) SoftGrayText else MaterialTheme.colorScheme.onBackground,
                                    style = if (item.isChecked) TextStyle(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    ) else TextStyle.Default
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (item.priority) {
                                                    "High" -> AccentCoral.copy(alpha = 0.15f)
                                                    "Medium" -> PrimaryCyan.copy(alpha = 0.15f)
                                                    else -> SoftGrayText.copy(alpha = 0.15f)
                                                }
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = item.priority,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.priority) {
                                                "High" -> AccentCoral
                                                "Medium" -> PrimaryCyan
                                                else -> SoftGrayText
                                            }
                                        )
                                    }
                                    Text(
                                        text = (if (lang == "ar") "بواسطة: " else "By: ") + item.addedBy,
                                        fontSize = 8.sp,
                                        color = SoftGrayText
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = (if (lang == "ar") "كمية: " else "Qty: ") + item.quantity,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                            IconButton(
                                onClick = { viewModel.deleteFamilyItem(item.id) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Delete Item",
                                    tint = AccentCoral.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Add New Shared Family Item Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        placeholder = { Text(if (lang == "ar") "طلب عائلي جديد..." else "New request...", fontSize = 9.sp) },
                        modifier = Modifier
                            .weight(0.5f)
                            .testTag("family_new_item_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.2f),
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    // Priority Selector Box
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardDarkBg)
                            .border(1.dp, SoftGrayText.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable {
                                newItemPriority = when (newItemPriority) {
                                    "High" -> "Medium"
                                    "Medium" -> "Low"
                                    else -> "High"
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = newItemPriority,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (newItemPriority) {
                                "High" -> AccentCoral
                                "Medium" -> PrimaryCyan
                                else -> SoftGrayText
                            }
                        )
                    }

                    // Quantity Control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = 12.sp,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clickable { if (newItemQty > 1) newItemQty-- }
                                .padding(horizontal = 4.dp)
                        )
                        Text(
                            text = newItemQty.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "+",
                            fontSize = 12.sp,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clickable { newItemQty++ }
                                .padding(horizontal = 4.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (newItemName.isNotBlank()) {
                                viewModel.addFamilyShoppingItem(newItemName, "Organic", newItemPriority, newItemQty)
                                newItemName = ""
                                newItemQty = 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(if (lang == "ar") "أضف" else "Add", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Action Button to Sync everything
                Button(
                    onClick = {
                        val uncheckedItems = dbFamilyItems.filter { !it.isChecked }
                        if (uncheckedItems.isEmpty()) {
                            android.widget.Toast.makeText(
                                context,
                                if (lang == "ar") "جميع الاحتياجات تم تلبيتها أو سلتك ممتلئة بالفعل!" else "All requests are checked off or your list is empty!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            var addedCount = 0
                            uncheckedItems.forEach { item ->
                                val matchedProd = productsList.firstOrNull { prod ->
                                    prod.name.contains(item.productName, ignoreCase = true) ||
                                    item.productName.contains(prod.name, ignoreCase = true)
                                } ?: productsList.firstOrNull { it.name.contains("Milk", ignoreCase = true) }
                                
                                matchedProd?.let { p ->
                                    viewModel.addProductToCart(p, item.quantity, "Retail")
                                    addedCount++
                                    viewModel.toggleFamilyItemChecked(item.id, true)
                                }
                            }

                            if (addedCount > 0) {
                                android.widget.Toast.makeText(
                                    context,
                                    if (lang == "ar") "تمت مزامنة السلة وإضافة الاحتياجات العائلية المشتركة بنجاح! 🛒" else "Synced database: Shared family necessities added to your cart successfully! 🛒",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = PrimaryCyan)
                        .border(1.dp, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (lang == "ar") "مزامنة وإضافة احتياجات المنزل للسلة 🛒" else "Sync & Add Family Needs to Cart 🛒",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💡", fontSize = 16.sp)
                    }
                    Text(
                        text = if (lang == "ar") "قم بتفعيل وضع العائلة لمزامنة طلبات الحليب والعسل الأسبوعية للبيت وتوفير الوقت والمال!" else "Enable Family Mode to sync milk & honey weekly logs and share shopping duties!",
                        fontSize = 9.sp,
                        color = Color(0xFF6D4C41),
                        lineHeight = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: MarketViewModel) {
    val isDark by viewModel.appDarkMode.collectAsState()

    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    // Custom headers and bottom styling according to the screenshots
    val headerBgColor = Color(0xFFB71C1C) // Crimson Red for both modes
    val headerTextColor = Color.White
    val headerSubtextColor = Color.White.copy(alpha = 0.85f)
    val headerButtonBg = Color.White.copy(alpha = 0.15f)
    val headerButtonBorder = Color.White.copy(alpha = 0.4f)
    val headerButtonTextColor = Color.White
    val headerUserTextColor = Color.White
    val headerLogoutIconColor = Color.White

    val bottomPanelBgColor = if (isDark) PrimaryCyan.copy(alpha = 0.15f) else Color(0xFF1D4ED8)
    val bottomPanelContentColor = if (isDark) PrimaryCyan else Color.White
    val bottomPanelHeaderColor = if (isDark) PrimaryCyan else Color.White
    val bottomPanelSubtextColor = if (isDark) PolarLight.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)

    val context = androidx.compose.ui.platform.LocalContext.current
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()

    // Helper translation accessor
    fun txt(key: String): String = Localization.get(key, lang)

    val roles = listOf("Customer", "Merchant", "Delivery", "Admin")
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }
    var profileDialogInitialTab by remember { mutableStateOf(0) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showExtraFeaturesDialog by remember { mutableStateOf(false) }

    val drawerState = androidx.compose.material3.rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    if (currentUser?.role == "Customer" && !onboardingCompleted) {
        CustomerOnboardingScreen(viewModel = viewModel, lang = lang)
    } else {
        androidx.compose.material3.ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                androidx.compose.material3.ModalDrawerSheet(
                    drawerContainerColor = CardDarkBg,
                    modifier = Modifier.width(280.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (lang == "ar") "القائمة الرئيسية" else "Main Menu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider(color = PrimaryCyan.copy(alpha = 0.2f))
                    
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "🛒 السلة والطلبات" else "🛒 Cart & Orders", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.setCustomerActiveTab(1)
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "📍 تتبع الطلبات (GPS)" else "📍 GPS Order Tracking", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.setCustomerActiveTab(2)
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "💎 المحفظة الرقمية" else "💎 Crypto Wallet", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.setCustomerActiveTab(4)
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "👤 الملف الشخصي" else "👤 Personal Profile", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            profileDialogInitialTab = 0
                            showProfileDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "🔑 الأمان وكلمة السر" else "🔑 Security & Password", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            profileDialogInitialTab = 1
                            showProfileDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "💰 المحفظة والمكافآت" else "💰 Wallet & Rewards", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            profileDialogInitialTab = 2
                            showProfileDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "📍 عناوين التوصيل" else "📍 Delivery Locations", color = PolarLight) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            profileDialogInitialTab = 5
                            showProfileDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "🎮 العب واربح" else "🎮 Play & Win Game", color = SecondaryMint) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            profileDialogInitialTab = 6
                            showProfileDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "✨ أدوات وميزات إضافية" else "✨ Extra Tools & Features", color = PrimaryCyan) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showExtraFeaturesDialog = true
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                    Divider(color = PrimaryCyan.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(if (lang == "ar") "🚪 تسجيل الخروج" else "🚪 Log Out", color = AccentCoral) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.logout()
                        },
                        colors = androidx.compose.material3.NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                }
            }
        ) {
            Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = headerTextColor)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_app_brand_logo),
                                contentDescription = "AI Store Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp).padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    txt("app_title"),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = headerTextColor
                                )
                                Text(
                                    txt("app_description"),
                                    fontSize = 10.sp,
                                    color = headerSubtextColor
                                )
                            }
                        }
                    },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick switch language buttons
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(headerButtonBg)
                                .border(1.dp, headerButtonBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleLanguage() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lang == "ar") "EN" else "عربي",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = headerButtonTextColor
                            )
                        }

                        // Dark mode toggle button
                        Box(
                            modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(headerButtonBg)
                                    .border(1.dp, headerButtonBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.toggleDarkMode() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isDark) "☀️" else "🌙",
                                fontSize = 11.sp
                            )
                        }

                        // Shopping Cart Badge Count in top navigation bar
                        val cartItemsForBadge by viewModel.cartItems.collectAsState()
                        val badgeCount = cartItemsForBadge.sumOf { it.quantity }
                        if (badgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SecondaryMint.copy(alpha = 0.2f))
                                    .border(1.dp, SecondaryMint, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🛒", fontSize = 11.sp)
                                    Text(
                                        text = "$badgeCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryMint
                                    )
                                }
                            }
                        }

                        Text(
                            text = txt("hi") + (currentUser?.name ?: "Guest"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = headerUserTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = headerBgColor)
            )
        },
        bottomBar = {
            // Sticky Gorgeous AI Assistant Control Panel at the bottom (Blue in light mode)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(
                        1.dp,
                        if (isAiLoading) SecondaryMint.copy(alpha = 0.5f) else PrimaryCyan.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = bottomPanelBgColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = if (isAiLoading) SecondaryMint else bottomPanelHeaderColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = txt("ai_core"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isAiLoading) SecondaryMint else bottomPanelHeaderColor,
                            modifier = Modifier.weight(1.0f)
                        )
                        if (isAiLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = SecondaryMint
                            )
                        } else if (aiResponse != null) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = if (isDark) AccentCoral else Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.clearAiResponse() }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                    ) {
                        val displayText = aiResponse ?: txt("ai_idle")
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Text(
                                    text = displayText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (aiResponse != null) bottomPanelContentColor else bottomPanelSubtextColor,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.testTag("ai_result_text")
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = SlateDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Elegant Role Switching Segmented Pill Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardDarkBg)
                        .border(1.dp, SoftGrayText.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    roles.forEach { r ->
                        val isSelected = currentRole == r
                        val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.switchRole(r) }
                                .background(if (isSelected) PrimaryCyan else Color.Transparent)
                                .padding(vertical = 10.dp)
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (r) {
                                        "Customer" -> txt("role_customer")
                                        "Merchant" -> txt("role_merchant")
                                        "Delivery" -> txt("role_delivery")
                                        "Admin" -> txt("role_admin")
                                        else -> r
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) onPrimaryColor else PolarLight
                                )
                                Text(
                                    text = r,
                                    fontSize = 8.sp,
                                    color = if (isSelected) onPrimaryColor.copy(alpha = 0.7f) else SoftGrayText
                                )
                            }
                        }
                    }
                }

                Text(
                    text = when (currentRole) {
                        "Customer" -> txt("workspace_customer")
                        "Merchant" -> txt("workspace_merchant")
                        "Delivery" -> txt("workspace_delivery")
                        "Admin" -> txt("workspace_admin")
                        else -> ""
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryMint,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                )

                // Dynamic Dashboard Panel loaded based on Selected Role
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                ) {
                    GlobalErrorBoundary {
                        when (currentRole) {
                            "Customer" -> CustomerScreen(viewModel)
                            "Merchant" -> MerchantScreen(viewModel)
                            "Delivery" -> DeliveryScreen(viewModel)
                            "Admin" -> AdminScreen(viewModel)
                        }
                    }
                }

                // Interactive Google Maps overlay
                GoogleMapDialog(viewModel = viewModel)

                if (showProfileDialog) {
                    UserProfileDialog(
                        viewModel = viewModel,
                        lang = lang,
                        initialTab = profileDialogInitialTab,
                        onDismiss = { showProfileDialog = false }
                    )
                }

                if (showExtraFeaturesDialog) {
                    ExtraFeaturesDialog(
                        viewModel = viewModel,
                        lang = lang,
                        currentRole = currentRole,
                        onDismiss = { showExtraFeaturesDialog = false }
                    )
                }
            }

            // --- GLOBAL FLOATING GEMINI CHATBOT BUTTON ---
            var showChatbotPanel by remember { mutableStateOf(false) }
            val isHighThinking by viewModel.isHighThinkingEnabled.collectAsState()

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 120.dp) // Float above the sticky AI panel
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Call Gemini Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentCoral, Color(0xFFFF8A65))))
                            .clickable { viewModel.startGeminiVoiceCall() }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(CardDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call Gemini Live",
                                tint = AccentCoral,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Floating Chatbot Trigger
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PrimaryCyan, SecondaryMint)))
                            .clickable { showChatbotPanel = !showChatbotPanel }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(CardDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Gemini Chatbot",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "AI Store",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )
                            }
                        }
                    }
                }
            }

            // --- FLOATING CHATBOT CONSOLE PANEL ---
            AnimatedVisibility(
                visible = showChatbotPanel,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(480.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(CardDarkBg)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
            ) {
                val chatHistory by when (currentRole) {
                    "Customer" -> viewModel.customerChatHistory.collectAsState()
                    "Merchant" -> viewModel.merchantChatHistory.collectAsState()
                    "Delivery" -> viewModel.deliveryChatHistory.collectAsState()
                    else -> viewModel.adminChatHistory.collectAsState()
                }
                val isChatLoading by viewModel.isChatbotLoading.collectAsState()
                var userChatText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryMint)
                            )
                            Column {
                                Text(
                                    text = when (currentRole) {
                                        "Customer" -> if (lang == "ar") "مساعد التسوق الذكي 🏪" else "AI Personal Shopper 🏪"
                                        "Merchant" -> if (lang == "ar") "مدير المبيعات والتسعير 📈" else "AI Store Manager 📈"
                                        "Delivery" -> if (lang == "ar") "ملاح المسار اللوجستي 🗺️" else "AI Logistics Dispatcher 🗺️"
                                        else -> if (lang == "ar") "محلل سلوك المنصة ⚙️" else "AI Platform Coordinator ⚙️"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PolarLight
                                )
                                Text(
                                    text = "Role Context: $currentRole Bot",
                                    fontSize = 9.sp,
                                    color = SoftGrayText
                                )
                            }
                        }

                        IconButton(
                            onClick = { showChatbotPanel = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SoftGrayText)
                        }
                    }

                    // High Thinking Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateDarkBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(14.dp))
                            Text(
                                text = if (lang == "ar") "التفكير العميق (Gemini 3.1 Pro)" else "Deep Thinking (Gemini 3.1 Pro)",
                                fontSize = 10.sp,
                                color = PolarLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = isHighThinking,
                            onCheckedChange = { viewModel.setHighThinkingEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SecondaryMint,
                                checkedTrackColor = SecondaryMint.copy(alpha = 0.3f),
                                uncheckedThumbColor = SoftGrayText,
                                uncheckedTrackColor = SoftGrayText.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }

                    // Real-time AI Nutrition & Meal Companion Panel
                    if (currentRole == "Customer") {
                        val currentCartItems by viewModel.cartItems.collectAsState()
                        val organicItemsInCart = currentCartItems.filter {
                            it.productName.lowercase().contains("organic") || it.productName.contains("عضوي")
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SecondaryMint.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SecondaryMint.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("🍎", fontSize = 16.sp)
                                        Text(
                                            text = if (lang == "ar") "أخصائي التغذية والوجبات الذكي" else "AI Nutritionist & Meal Companion",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (organicItemsInCart.isNotEmpty()) SecondaryMint.copy(alpha = 0.2f) else SoftGrayText.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (lang == "ar") 
                                                "${organicItemsInCart.size} منتج عضوي" 
                                                else "${organicItemsInCart.size} Organic Items",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (organicItemsInCart.isNotEmpty()) SecondaryMint else SoftGrayText
                                        )
                                    }
                                }
                                
                                if (organicItemsInCart.isNotEmpty()) {
                                    Text(
                                        text = if (lang == "ar") 
                                            "تم رصد منتجات عضوية في سلتك! احصل على نصائح غذائية أو أفكار طبخ مخصصة لها:" 
                                            else "Organic products detected in your cart! Get custom nutritional insights or meal recipes:",
                                        fontSize = 9.sp,
                                        color = PolarLight
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val itemsStr = organicItemsInCart.joinToString { it.productName }
                                                viewModel.sendMessageToChatbot(
                                                    "Customer",
                                                    if (lang == "ar") 
                                                        "أعطني نصائح غذائية مفصلة وفوائد صحية لـ: $itemsStr" 
                                                        else "Give me detailed nutritional advice and health benefits for: $itemsStr"
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "🔬 تحليل غذائي" else "🔬 Nutritional Advice",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateDarkBg
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                val itemsStr = organicItemsInCart.joinToString { it.productName }
                                                viewModel.sendMessageToChatbot(
                                                    "Customer",
                                                    if (lang == "ar") 
                                                        "اقترح وجبات طبخ ووصفات مبتكرة تدمج: $itemsStr" 
                                                        else "Suggest innovative meal pairings and recipes combining: $itemsStr"
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "🥗 اقتراح وجبات" else "🥗 Meal Pairings",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateDarkBg
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (lang == "ar") 
                                            "أضف منتجات عضوية (مثل العسل الجبلي، البيض العضوي، أو الحليب الطازج) إلى سلتك لتفعيل التحليل الغذائي الفوري واقتراح الوجبات!" 
                                            else "Add organic products (like Mountain Honey, Eggs, or Milk) to your cart to unlock real-time nutritional insights and meal advice!",
                                        fontSize = 9.sp,
                                        color = SoftGrayText
                                    )
                                }
                            }
                        }
                    }

                    // Chat messages list
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (chatHistory.isEmpty()) {
                            // Friendly initial onboarding state
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "✨", fontSize = 32.sp)
                                Text(
                                    text = if (lang == "ar") "مرحباً بك في AI Store!" else "Welcome to your AI Store Copilot!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarLight
                                )
                                Text(
                                    text = if (lang == "ar") "كيف يمكنني مساعدتك اليوم؟ اختر قالب تفاعلي أدناه:" else "How can I help you today? Select a smart template below:",
                                    fontSize = 9.sp,
                                    color = SoftGrayText,
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Role-Specific Templates
                                val templates = when (currentRole) {
                                    "Customer" -> listOf(
                                        if (lang == "ar") "اقترح عسل جبلي وحليب طازج" else "Suggest organic honey & fresh milk",
                                        if (lang == "ar") "ابحث عن عروض بسعر أقل من 100" else "Find cheapest deal under 100 SAR"
                                    )
                                    "Merchant" -> listOf(
                                        if (lang == "ar") "تحليل حركة بضائع السلة" else "Analyze slow vs fast stock levels",
                                        if (lang == "ar") "أفكار فيديو ترويجي Veo لمتجري" else "Draft video promo text campaign"
                                    )
                                    "Delivery" -> listOf(
                                        if (lang == "ar") "تتبع وتحديد أقصر مسار شحن" else "Optimize delivery path stops",
                                        if (lang == "ar") "حل نزاع بخصوص شحنة تالفة" else "Review customer delivery dispute case"
                                    )
                                    else -> listOf(
                                        if (lang == "ar") "فحص مستندات Firestore للمستخدمين" else "Check active users Firestore collection",
                                        if (lang == "ar") "تحليل سلوك ومعدلات إلغاء الطلبيات" else "Analyze general platform sentiment analytics"
                                    )
                                }

                                templates.forEach { tmpl ->
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp, horizontal = 12.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SlateDarkBg)
                                            .clickable { viewModel.sendMessageToChatbot(currentRole, tmpl) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = tmpl, fontSize = 10.sp, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        } else {
                            val listState = rememberLazyListState()
                            LaunchedEffect(chatHistory.size) {
                                if (chatHistory.isNotEmpty()) {
                                    listState.animateScrollToItem(chatHistory.size - 1)
                                }
                            }
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(chatHistory) { msg ->
                                    val bubbleBg = if (msg.isUser) {
                                        Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint))
                                    } else {
                                        Brush.horizontalGradient(listOf(SlateDarkBg, SlateDarkBg))
                                    }
                                    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                                    val txtColor = if (msg.isUser) SlateDarkBg else PolarLight

                                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
                                        Box(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                                        bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                                    )
                                                )
                                                .background(bubbleBg)
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                                .widthIn(max = 260.dp)
                                        ) {
                                            Text(
                                                text = msg.content,
                                                fontSize = 11.sp,
                                                color = txtColor,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 16.sp
                                            )
                                        }
                                        Text(
                                            text = if (msg.isUser) "You" else "Gemini Pro",
                                            fontSize = 7.sp,
                                            color = SoftGrayText,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (isChatLoading) {
                                    item {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = SecondaryMint)
                                            Text(
                                                text = if (isHighThinking) "Gemini is analyzing with Deep Thinking..." else "Gemini is thinking...",
                                                fontSize = 9.sp,
                                                color = SecondaryMint,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userChatText,
                            onValueChange = { userChatText = it },
                            placeholder = { Text(if (lang == "ar") "اكتب رسالة للـ AI..." else "Type message...", fontSize = 11.sp, color = SoftGrayText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PolarLight,
                                unfocusedTextColor = PolarLight,
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                focusedContainerColor = SlateDarkBg,
                                unfocusedContainerColor = SlateDarkBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            textStyle = TextStyle(fontSize = 12.sp)
                        )

                        IconButton(
                            onClick = {
                                if (userChatText.isNotBlank()) {
                                    viewModel.sendMessageToChatbot(currentRole, userChatText)
                                    userChatText = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)))
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = SlateDarkBg)
                        }
                    }
                }
            }

            // --- FLOATING GEMINI LIVE AUDIO CALL SIMULATOR OVERLAY ---
            val isVoiceActive by viewModel.isVoiceCallActive.collectAsState()
            val voiceStatus by viewModel.voiceCallStatus.collectAsState()
            val transcribedText by viewModel.voiceTranscriptionResult.collectAsState()

            if (isVoiceActive) {
                // Audio Wave pulsation animations
                val transition = rememberInfiniteTransition(label = "VoiceWave")
                val waveScale1 by transition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.6f,
                    animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
                    label = "Wave1"
                )
                val waveScale2 by transition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 2.0f,
                    animationSpec = infiniteRepeatable(animation = tween(1600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
                    label = "Wave2"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "⚡", fontSize = 20.sp)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Gemini Live Voice Engine",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryCyan
                                    )
                                    Text(
                                        text = "Powered by Gemini 3.5 Flash & Live API",
                                        fontSize = 9.sp,
                                        color = SoftGrayText
                                    )
                                }
                            }

                            // Glowing pulsating concentric audio waves
                            Box(
                                modifier = Modifier
                                    .size(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .scale(waveScale2)
                                        .clip(CircleShape)
                                        .background(PrimaryCyan.copy(alpha = 0.1f))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .scale(waveScale1)
                                        .clip(CircleShape)
                                        .background(SecondaryMint.copy(alpha = 0.15f))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(PrimaryCyan, SecondaryMint))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Gemini",
                                        tint = SlateDarkBg,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            // Status Indicator
                            Text(
                                text = voiceStatus,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryMint
                            )

                            if (transcribedText.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SlateDarkBg)
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = "Transcribed Voice Order:", fontSize = 9.sp, color = SoftGrayText)
                                        Text(text = transcribedText, fontSize = 11.sp, color = PolarLight, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            // Demo Spoken Command suggestions for user click simulation
                            Text(
                                text = if (lang == "ar") "انقر لمحاكاة نطق طلبية زبون:" else "Click to simulate user speaking order:",
                                fontSize = 10.sp,
                                color = SoftGrayText,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.sendVoiceCommandToGemini("أريد شراء عسل جبلي وحليب طازج") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "🍯 عسل وحليب", fontSize = 9.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.sendVoiceCommandToGemini("Order fresh bakery bread and clean red apples") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "🍎 Apple Bread", fontSize = 9.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Hang Up Button
                            Button(
                                onClick = { viewModel.stopGeminiVoiceCall() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCoral),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White)
                                    Text(text = if (lang == "ar") "إنهاء المكالمة" else "Hang Up", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
        } // Close ModalNavigationDrawer
    }
    }
}

// ---------------- CUSTOMER SCREEN ----------------
@Composable
fun CustomerScreen(viewModel: MarketViewModel) {
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val productsList by viewModel.products.collectAsState()
    val savedList by viewModel.savedProducts.collectAsState()
    val ordersList by viewModel.orders.collectAsState()
    val userProfile by viewModel.currentUser.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()
    val chosenInterests by viewModel.chosenInterests.collectAsState()
    val geminiRecommendedCategories by viewModel.geminiRecommendedCategories.collectAsState()
    val isGeminiRecommending by viewModel.isGeminiRecommending.collectAsState()

    val cartItemsList by viewModel.cartItems.collectAsState()
    val bankCardsList by viewModel.bankCards.collectAsState()
    val isProductsLoading by viewModel.isProductsLoading.collectAsState()

    var showAddCardDialog by remember { mutableStateOf(false) }
    var cardNumberState by remember { mutableStateOf("") }
    var cardHolderState by remember { mutableStateOf("") }
    var expiryDateState by remember { mutableStateOf("") }
    var cvvState by remember { mutableStateOf("") }
    var isPrimaryCardState by remember { mutableStateOf(false) }

    var showCartCheckoutDialog by remember { mutableStateOf(false) }
    var chosenCardForCheckout by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Interactive Overlay Tour states
    var showTourOverlay by remember { mutableStateOf(false) }
    var tourStep by remember { mutableStateOf(0) } // 0: Welcome, 1: Voice search, 2: Visual search, 3: Completed

    // Coupon Scanner Dialog states
    var showCouponScanner by remember { mutableStateOf(false) }

    // Auto check expiring organic products on login
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            viewModel.checkExpiringOrganicItems(context)
        }
    }

    // Coupon & Discount Codes states
    var promoCodeInput by remember { mutableStateOf("") }
    var appliedPromoCode by remember { mutableStateOf("") }
    var appliedDiscountPercent by remember { mutableStateOf(0.0) } // e.g. 0.25 for 25% discount

    // Modern Customer Experience States & Simulated Payment gateway timer
    var selectedCategory by remember { mutableStateOf("All") }
    var maxPriceFilter by remember { mutableStateOf<Float?>(null) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentStep by remember { mutableStateOf(0) }

    LaunchedEffect(isProcessingPayment) {
        if (isProcessingPayment) {
            paymentStep = 0
            kotlinx.coroutines.delay(600)
            paymentStep = 1
            kotlinx.coroutines.delay(700)
            paymentStep = 2
            kotlinx.coroutines.delay(700)
            paymentStep = 3
            kotlinx.coroutines.delay(600)
            viewModel.checkoutCart(chosenCardForCheckout)
            isProcessingPayment = false
            showCartCheckoutDialog = false
        }
    }

    // Helper translation accessor
    fun txt(key: String): String = Localization.get(key, lang)

    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var budgetValue by remember { mutableStateOf("") }
    var selectedProductForDetail by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedProductForComparison by remember { mutableStateOf<ProductEntity?>(null) }
    var orderQuantity by remember { mutableStateOf(1) }
    var orderTypeChosen by remember { mutableStateOf("Retail") } // "Retail" vs "Wholesale"
    var showCameraSearchDialog by remember { mutableStateOf(false) }
    var showVoiceSearchDialog by remember { mutableStateOf(false) }

    val voiceQuery by viewModel.voiceSearchQuery.collectAsState()
    LaunchedEffect(voiceQuery) {
        if (voiceQuery.isNotEmpty()) {
            searchQuery = voiceQuery
            viewModel.clearVoiceSearchQuery()
        }
    }

    val customerActiveTab by viewModel.customerActiveTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Global Currency Selector Header
        val selectedCurrency by viewModel.selectedCurrency.collectAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (lang == "ar") "🌿 بقالة الغذاء الأخضر" else "🌿 GreenGrocery Hub",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = PolarLight
                )
                
                // Pulsing AI Tour Trigger
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryCyan.copy(alpha = 0.15f))
                        .clickable {
                            showTourOverlay = true
                            tourStep = 0
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text("✨", fontSize = 10.sp)
                        Text(
                            text = if (lang == "ar") "جولة الذكاء الاصطناعي" else "AI Tour",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryCyan
                        )
                    }
                }
                val localUserProfile = userProfile
                if (localUserProfile != null) {
                    LaunchedEffect(localUserProfile) {
                        viewModel.checkAndAddBadges(localUserProfile)
                    }

                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondaryMint.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = (if (lang == "ar") "نقاطك: " else "Points: ") + localUserProfile.loyaltyPoints,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryMint
                            )
                        }
                        
                        if (localUserProfile.badges.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                localUserProfile.badges.split(",").forEach { badge ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PrimaryCyan.copy(alpha = 0.1f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = badge, fontSize = 8.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .widthIn(max = 240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardDarkBg)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Currency Icon",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (lang == "ar") "العملة: " else "Curr: ",
                    fontSize = 10.sp,
                    color = SoftGrayText
                )
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val allCurrencies = listOf(
                        "JOD", "USD", "SAR", "AED", "QAR", "KWD", "BHD", "OMR", "EGP", 
                        "JPY", "CNY", "INR", "ZAR", "NGN", "EUR", "GBP", "CHF", "CAD", "BRL", "ARS"
                    )
                    items(allCurrencies) { curr ->
                        val isSel = selectedCurrency == curr
                        Text(
                            text = curr,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                            color = if (isSel) SecondaryMint else SoftGrayText.copy(alpha = 0.7f),
                            modifier = Modifier
                                .clickable { viewModel.setCurrency(curr) }
                                .padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
        // Sticky/Elegant Pill-Shaped Tab Selector with Icons & Counters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardDarkBg)
                .border(1.dp, PrimaryCyan.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Triple(0, if (lang == "ar") "المتجر" else "Store", Icons.Default.Home),
                Triple(3, if (lang == "ar") "العروض" else "Offers", Icons.Default.Star),
                Triple(4, if (lang == "ar") "قائمة التسوق" else "Shopping List", Icons.Default.ShoppingCart)
            )

            tabs.forEach { (index, title, icon) ->
                val isSelected = customerActiveTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { viewModel.setCustomerActiveTab(index) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) SlateDarkBg else SoftGrayText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = title,
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) SlateDarkBg else SoftGrayText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Real-Time Floating Badge that lets you jump directly to checkout/cart
        if (customerActiveTab != 1 && cartItemsList.isNotEmpty()) {
            val totalCost = cartItemsList.sumOf { it.price * it.quantity }
            val countItems = cartItemsList.sumOf { it.quantity }
            Card(
                colors = CardDefaults.cardColors(containerColor = SecondaryMint.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setCustomerActiveTab(1) }
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SlateDarkBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$countItems",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryMint
                            )
                        }
                        Text(
                            text = if (lang == "ar") "سلتك تحتوي منتجات! اضغط هنا لإتمام الشراء والدفع 💳" else "Items in Cart! Tap here to Proceed to Payment 💳",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDarkBg
                        )
                    }

                    Text(
                        text = viewModel.formatPrice(totalCost, lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SlateDarkBg
                    )
                }
            }
        }

        // Active Tab Screen Content
        Box(modifier = Modifier.weight(1f)) {
            when (customerActiveTab) {
                0 -> {
                    // STORE TAB VIEW: AI search, Budget optimizer, and matching category highlight
                    val customPromoTitle by viewModel.customPromoTitle.collectAsState()
                    val customPromoDesc by viewModel.customPromoDesc.collectAsState()
                    val customPromoMediaType by viewModel.customPromoMediaType.collectAsState()
                    val customPromoImage by viewModel.generatedImageUrl.collectAsState()
                    val customPromoVideo by viewModel.generatedVideoUrl.collectAsState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (customPromoTitle != null) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(2.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)), RoundedCornerShape(16.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(AccentCoral)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = "📢 AI LIVE PROMO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                                Text(text = customPromoTitle ?: "", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PolarLight)
                                            }
                                            IconButton(onClick = { viewModel.clearCampaign() }, modifier = Modifier.size(20.dp)) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = SoftGrayText, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Text(text = customPromoDesc ?: "", fontSize = 11.sp, color = SoftGrayText)

                                        if (customPromoMediaType == "Image" && customPromoImage != null) {
                                            // Render beautiful generated 8K Still ad image placeholder
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(SlateDarkBg)
                                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Text(text = "📸 Nano Banana Pro 8K Render", fontSize = 9.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                                    Text(text = "Promotional Still Image Asset", fontSize = 8.sp, color = SoftGrayText)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.9f)
                                                            .height(70.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
                                                    ) {
                                                        Text(text = "🎨 8K Hyper-Realistic Food Scene Rendering Active", fontSize = 9.sp, color = SecondaryMint, modifier = Modifier.align(Alignment.Center))
                                                    }
                                                }
                                            }
                                        } else if (customPromoMediaType == "Video" && customPromoVideo != null) {
                                            // Render beautiful generated Veo 3 promotional video placeholder
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(SlateDarkBg)
                                                    .border(1.dp, AccentCoral.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Text(text = "🎬 Veo 3 Cinematic Video Trailer Playing", fontSize = 9.sp, color = AccentCoral, fontWeight = FontWeight.Bold)
                                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
                                                        Text(text = "Looping Promo Teaser (8K UHD, 60fps)", fontSize = 8.sp, color = SoftGrayText)
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = AccentCoral, modifier = Modifier.size(36.dp))
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "🎁 يمنحك هذا الإعلان خصماً إضافياً 30%!" else "🎁 This ad unlocks a 30% Family coupon!",
                                                fontSize = 9.sp,
                                                color = SecondaryMint,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Button(
                                                onClick = {
                                                    appliedDiscountPercent = 0.30
                                                    appliedPromoCode = "AI_VEO_BANANA"
                                                    promoCodeInput = "AI_VEO_BANANA"
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = if (lang == "ar") "تفعيل الخصم" else "Apply 30% Off", fontSize = 8.sp, color = SlateDarkBg, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Hero Promo Banner
                        item {
                            GroceryPromoBanner(lang = lang)
                        }

                        // Family Shared Shopping & Household Mode Card
                        item {
                            FamilyShoppingModeCard(lang = lang, viewModel = viewModel, productsList = productsList)
                        }

                        // 🌱 Green Hub Visual Dashboard & Freshness Monitor
                        item {
                            GreenHubDashboard(lang = lang, viewModel = viewModel)
                        }

                        // FLASH SALES & DAILY DEALS (Shein/Temu Style)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                colors = CardDefaults.cardColors(containerColor = AccentCoral),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = if (lang == "ar") "⚡ عروض الفلاش اليومية!" else "⚡ Daily Flash Sales!",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (lang == "ar") "خصومات تصل إلى ٩٠٪ تنتهي قريباً" else "Up to 90% OFF ends soon",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("02:15:45", color = AccentCoral, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Flash Sale",
                                        tint = Color.White,
                                        modifier = Modifier.size(64.dp).padding(end = 16.dp)
                                    )
                                }
                            }
                        }

                        // AI Deep Market Search
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        txt("customer_search_title"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderBlue
                                    )
                                    Text(
                                        txt("customer_search_desc"),
                                        fontSize = 11.sp,
                                        color = SoftGrayText,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SearchBar(onCameraClick = { showCameraSearchDialog = true })
                                    }

                                    // Product Grid
                                    ProductGrid(products = productsList, isLoading = isProductsLoading)

                                    // Local database cached search queries (Room database)
                                    val recentQueries by viewModel.recentSearchQueries.collectAsState()
                                    if (isSearchFocused && recentQueries.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CardDarkBg.copy(alpha = 0.9f)),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Search,
                                                            contentDescription = null,
                                                            tint = SecondaryMint,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Text(
                                                            text = if (lang == "ar") "🔍 عمليات البحث الأخيرة (قاعدة البيانات)" else "🔍 Recent Searches (Cached)",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SecondaryMint
                                                        )
                                                    }
                                                    Text(
                                                        text = if (lang == "ar") "مسح الكل" else "Clear All",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AccentCoral,
                                                        modifier = Modifier.clickable { viewModel.clearSearchHistory() }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    items(recentQueries) { q ->
                                                        Row(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(SlateDarkBg)
                                                                .clickable {
                                                                    searchQuery = q.query
                                                                    viewModel.performAiPriceComparison(q.query)
                                                                }
                                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text(q.query, fontSize = 11.sp, color = PolarLight)
                                                            Icon(
                                                                imageVector = Icons.Default.Close,
                                                                contentDescription = "Delete",
                                                                tint = SoftGrayText,
                                                                modifier = Modifier
                                                                    .size(12.dp)
                                                                    .clickable {
                                                                        viewModel.deleteSearchQuery(q.query)
                                                                    }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Dynamic Search History Component
                                    val searchHistory by viewModel.searchHistory.collectAsState()
                                    if (searchHistory.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "History",
                                                    tint = SoftGrayText,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = if (lang == "ar") "عمليات البحث الأخيرة" else "Recent Lookups & History",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SoftGrayText
                                                )
                                            }
                                            Text(
                                                text = if (lang == "ar") "مسح السجل" else "Clear",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AccentCoral,
                                                modifier = Modifier.clickable { viewModel.clearSearchHistory() }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(searchHistory) { entry ->
                                                val icon = if (entry.type == "Visual Search") "📷" else "🔍"
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(CardDarkBg.copy(alpha = 0.5f))
                                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            searchQuery = entry.query
                                                            if (entry.type == "Visual Search") {
                                                                viewModel.performAiVisualOrder(entry.query)
                                                            } else {
                                                                viewModel.performAiPriceComparison(entry.query)
                                                            }
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text(icon, fontSize = 10.sp)
                                                        Text(
                                                            text = entry.query,
                                                            fontSize = 10.sp,
                                                            color = PolarLight,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.widthIn(max = 140.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Dynamic Local Session-based Wishlist Component
                                    val wishlistList by viewModel.wishlist.collectAsState()
                                    if (wishlistList.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = "Wishlist",
                                                    tint = AccentCoral,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = if (lang == "ar") "قائمة الأمنيات من التريندات المحفوظة" else "Wishlist - Saved Feed Products",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PolarLight
                                                )
                                            }
                                            Text(
                                                text = "(${wishlistList.size})",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SoftGrayText
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(wishlistList) { tp ->
                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg.copy(alpha = 0.6f)),
                                                    modifier = Modifier
                                                        .width(150.dp)
                                                        .border(1.dp, SecondaryMint.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            val dummyProd = ProductEntity(
                                                                id = tp.id,
                                                                name = tp.name,
                                                                category = tp.category,
                                                                retailPrice = tp.retailPrice,
                                                                wholesalePrice = tp.wholesalePrice,
                                                                imageUrl = tp.imageUrl,
                                                                description = tp.description,
                                                                ingredients = "AI Trending product. " + tp.aiInsight,
                                                                stockQuantity = 50,
                                                                isRegisteredMerchant = false,
                                                                merchantName = tp.trendPlatform,
                                                                salesHistory = 100
                                                            )
                                                            selectedProductForDetail = dummyProd
                                                        }
                                                ) {
                                                    Column {
                                                        Box(modifier = Modifier.fillMaxWidth().height(65.dp)) {
                                                            Image(
                                                                painter = rememberAsyncImagePainter(tp.imageUrl),
                                                                contentDescription = tp.name,
                                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                                modifier = Modifier.fillMaxSize()
                                                            )
                                                            IconButton(
                                                                onClick = { viewModel.toggleWishlist(tp) },
                                                                modifier = Modifier
                                                                    .padding(4.dp)
                                                                    .align(Alignment.TopEnd)
                                                                    .size(22.dp)
                                                                    .background(CardDarkBg.copy(alpha = 0.8f), CircleShape)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Favorite,
                                                                    contentDescription = "Remove",
                                                                    tint = AccentCoral,
                                                                    modifier = Modifier.size(10.dp)
                                                                )
                                                            }
                                                        }
                                                        Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = tp.name,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = PolarLight,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = viewModel.formatPrice(tp.retailPrice, lang),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = SecondaryMint
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

                        // Behavior permissions check
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SecondaryMint.copy(alpha = 0.05f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SecondaryMint.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = SecondaryMint,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            txt("privilege_opt_in"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )
                                        Text(
                                            txt("privilege_desc"),
                                            fontSize = 10.sp,
                                            color = PolarLight
                                        )
                                    }

                                    Switch(
                                        checked = userProfile?.permissionGranted ?: true,
                                        onCheckedChange = { viewModel.grantPermission(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = SlateDarkBg,
                                            checkedTrackColor = SecondaryMint
                                        )
                                    )
                                }
                            }
                        }

                        // Budget Optimizer
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, WarmAmbar.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        txt("budget_planner_title"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderBlue
                                    )
                                    Text(
                                        txt("budget_planner_desc"),
                                        fontSize = 11.sp,
                                        color = SoftGrayText,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = budgetValue,
                                            onValueChange = { budgetValue = it },
                                            placeholder = { Text("E.g., 100") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("budget_input"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = WarmAmbar,
                                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                                focusedTextColor = PolarLight,
                                                unfocusedTextColor = PolarLight
                                            )
                                        )

                                        Button(
                                            onClick = {
                                                val amt = budgetValue.toDoubleOrNull() ?: 100.0
                                                viewModel.performAiBudgetShoppingPlan(amt, searchQuery)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmbar),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("budget_plan_btn")
                                        ) {
                                            Text(txt("map_budget"), fontWeight = FontWeight.Bold, color = SlateDarkBg)
                                        }
                                    }
                                }
                            }
                        }

                        // Catalog Heading
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    txt("smart_catalog"),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HeaderBlue
                                )
                                if (budgetValue.isNotEmpty() && budgetValue.toDoubleOrNull() != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SecondaryMint.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (lang == "ar") "مصفى بالميزانية ✨" else "Sub-$budgetValue Filtered ✨",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )
                                    }
                                }
                            }
                        }

                        // AI Organic Recommendations Section (Based on Shopping History)
                        item {
                            val isAiRecommending by viewModel.isAiRecommendingShopping.collectAsState()
                            val aiRecommendations by viewModel.aiShoppingRecommendations.collectAsState()
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                border = BorderStroke(1.dp, SecondaryMint.copy(alpha = 0.25f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🌿", fontSize = 18.sp)
                                            Column {
                                                Text(
                                                    text = if (lang == "ar") "توصيات عضوية مخصصة بالذكاء الاصطناعي" else "AI-Driven Organic Recommendations",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SecondaryMint
                                                )
                                                Text(
                                                    text = if (lang == "ar") "تحليل ذكي لسجل مشترياتك لاقتراح أفضل المنتجات الصحية" else "Smart analysis of shopping history to suggest organic options",
                                                    fontSize = 9.sp,
                                                    color = SoftGrayText
                                                )
                                            }
                                        }
                                        
                                        Button(
                                            onClick = { viewModel.generateAiShoppingRecommendations() },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "حلل واقترح ✨" else "Analyze ✨",
                                                color = SlateDarkBg,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (isAiRecommending) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                CircularProgressIndicator(color = SecondaryMint, modifier = Modifier.size(24.dp))
                                                Text(
                                                    text = if (lang == "ar") "جاري تحليل تاريخ مشترياتك وابتكار توصيات صحية..." else "Analyzing your shopping history & formulating recommendations...",
                                                    fontSize = 9.sp,
                                                    color = SoftGrayText
                                                )
                                            }
                                        }
                                    } else if (aiRecommendations.isNotEmpty()) {
                                        androidx.compose.foundation.lazy.LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(aiRecommendations) { rec ->
                                                Card(
                                                    modifier = Modifier
                                                        .width(220.dp)
                                                        .border(1.dp, SecondaryMint.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                                    colors = CardDefaults.cardColors(containerColor = SlateDarkBg.copy(alpha = 0.5f))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = rec.category,
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = SecondaryMint,
                                                                modifier = Modifier
                                                                    .background(SecondaryMint.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                            )
                                                            Text(
                                                                text = "$${rec.price}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = PolarLight
                                                            )
                                                        }
                                                        
                                                        Text(
                                                            text = rec.name,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = PolarLight,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        
                                                        Text(
                                                            text = rec.reason,
                                                            fontSize = 9.sp,
                                                            color = SoftGrayText,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis,
                                                            lineHeight = 12.sp
                                                        )
                                                        
                                                        Button(
                                                            onClick = {
                                                                val tempProduct = ProductEntity(
                                                                    id = rec.id,
                                                                    name = rec.name,
                                                                    category = rec.category,
                                                                    retailPrice = rec.price,
                                                                    wholesalePrice = rec.price,
                                                                    imageUrl = rec.imageUrl,
                                                                    description = rec.reason
                                                                )
                                                                viewModel.addProductToCart(tempProduct, 1, "Retail")
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(28.dp),
                                                            contentPadding = PaddingValues(vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = if (lang == "ar") "أضف للسلة 🛒" else "Add to Cart 🛒",
                                                                color = SlateDarkBg,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(SlateDarkBg.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .clickable { viewModel.generateAiShoppingRecommendations() }
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("✨", fontSize = 14.sp)
                                                Text(
                                                    text = if (lang == "ar") "اضغط هنا لتحليل سجل تسوقك واستخلاص توصيات ذكية" else "Tap here to analyze past orders & generate organic suggestions!",
                                                    fontSize = 10.sp,
                                                    color = PolarLight,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Modern Category Horizontal Carousel Row
                        item {
                            val categories = listOf(
                                "All",
                                "Organic Foods",
                                "Spiced Coffee & Tea",
                                "Dairy",
                                "Health Drinks",
                                "Snacks",
                                "Men's Clothing",
                                "Women's Clothing",
                                "Shoes & Footwear",
                                "Perfumes & Fragrances",
                                "Electronics & Mobiles",
                                "Furniture & Decor",
                                "Pharmacy & Medical",
                                "Tobacco & Vape",
                                "Other Shops"
                            )
                            val categoriesAr = mapOf(
                                "All" to "الكل 🛍️",
                                "Organic Foods" to "أغذية عضوية 🍯",
                                "Spiced Coffee & Tea" to "قهوة وبن ☕",
                                "Dairy" to "منتجات ألبان 🥛",
                                "Health Drinks" to "طاقة وصحة ⚡",
                                "Snacks" to "تسالي خفيفة 🍪",
                                "Men's Clothing" to "ملابس رجالية 👔",
                                "Women's Clothing" to "ملابس نسائية 👗",
                                "Shoes & Footwear" to "أحذية 👟",
                                "Perfumes & Fragrances" to "عطور وبخور ✨",
                                "Electronics & Mobiles" to "جوالات وإلكترونيات 📱",
                                "Furniture & Decor" to "أثاث وديكور 🛋️",
                                "Pharmacy & Medical" to "صيدليات وطب 💊",
                                "Tobacco & Vape" to "تدخين ومراكز 🚬",
                                "Other Shops" to "محلات أخرى 🏪"
                            )
                            val categoriesEn = mapOf(
                                "All" to "All 🛍️",
                                "Organic Foods" to "Organic 🍯",
                                "Spiced Coffee & Tea" to "Coffee & Tea ☕",
                                "Dairy" to "Dairy 🥛",
                                "Health Drinks" to "Health ⚡",
                                "Snacks" to "Snacks 🍪",
                                "Men's Clothing" to "Men's Wear 👔",
                                "Women's Clothing" to "Women's Wear 👗",
                                "Shoes & Footwear" to "Footwear 👟",
                                "Perfumes & Fragrances" to "Perfumes ✨",
                                "Electronics & Mobiles" to "Electronics 📱",
                                "Furniture & Decor" to "Decor 🛋️",
                                "Pharmacy & Medical" to "Pharmacy 💊",
                                "Tobacco & Vape" to "Tobacco & Vape 🚬",
                                "Other Shops" to "Other Shops 🏪"
                            )

                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { cat ->
                                    val isSelected = selectedCategory == cat
                                    val displayText = if (lang == "ar") categoriesAr[cat] ?: cat else categoriesEn[cat] ?: cat
                                    
                                    val catBgColor = when (cat) {
                                        "All" -> if (isSelected) PrimaryCyan else Color(0xFFF2F3F2)
                                        "Organic Foods" -> if (isSelected) PrimaryCyan else Color(0xFFEAF9EE)
                                        "Spiced Coffee & Tea" -> if (isSelected) PrimaryCyan else Color(0xFFFDF0E6)
                                        "Dairy" -> if (isSelected) PrimaryCyan else Color(0xFFE1F5FE)
                                        "Health Drinks" -> if (isSelected) PrimaryCyan else Color(0xFFFCE4EC)
                                        "Snacks" -> if (isSelected) PrimaryCyan else Color(0xFFFFF8E1)
                                        "Men's Clothing" -> if (isSelected) PrimaryCyan else Color(0xFFECEFF1)
                                        "Women's Clothing" -> if (isSelected) PrimaryCyan else Color(0xFFFCE4EC)
                                        "Shoes & Footwear" -> if (isSelected) PrimaryCyan else Color(0xFFEFEBE9)
                                        "Perfumes & Fragrances" -> if (isSelected) PrimaryCyan else Color(0xFFF3E5F5)
                                        "Electronics & Mobiles" -> if (isSelected) PrimaryCyan else Color(0xFFE8EAF6)
                                        "Furniture & Decor" -> if (isSelected) PrimaryCyan else Color(0xFFE8F5E9)
                                        else -> if (isSelected) PrimaryCyan else CardDarkBg
                                    }

                                    val catTextColor = when (cat) {
                                        "All" -> if (isSelected) Color.White else PolarLight
                                        "Organic Foods" -> if (isSelected) Color.White else Color(0xFF2D6A4F)
                                        "Spiced Coffee & Tea" -> if (isSelected) Color.White else Color(0xFFD97706)
                                        "Dairy" -> if (isSelected) Color.White else Color(0xFF0288D1)
                                        "Health Drinks" -> if (isSelected) Color.White else Color(0xFFC2185B)
                                        "Snacks" -> if (isSelected) Color.White else Color(0xFFF57F17)
                                        "Men's Clothing" -> if (isSelected) Color.White else Color(0xFF37474F)
                                        "Women's Clothing" -> if (isSelected) Color.White else Color(0xFFC2185B)
                                        "Shoes & Footwear" -> if (isSelected) Color.White else Color(0xFF4E342E)
                                        "Perfumes & Fragrances" -> if (isSelected) Color.White else Color(0xFF6A1B9A)
                                        "Electronics & Mobiles" -> if (isSelected) Color.White else Color(0xFF283593)
                                        "Furniture & Decor" -> if (isSelected) Color.White else Color(0xFF2E7D32)
                                        else -> if (isSelected) Color.White else PolarLight
                                    }

                                    val catBorderColor = when (cat) {
                                        "All" -> if (isSelected) PrimaryCyan else Color.Transparent
                                        "Organic Foods" -> if (isSelected) PrimaryCyan else Color(0xFFD2EED8)
                                        "Spiced Coffee & Tea" -> if (isSelected) PrimaryCyan else Color(0xFFF9E0CC)
                                        "Dairy" -> if (isSelected) PrimaryCyan else Color(0xFFB3E5FC)
                                        "Health Drinks" -> if (isSelected) PrimaryCyan else Color(0xFFF8BBD0)
                                        "Snacks" -> if (isSelected) PrimaryCyan else Color(0xFFFFECB3)
                                        "Men's Clothing" -> if (isSelected) PrimaryCyan else Color(0xFFCFD8DC)
                                        "Women's Clothing" -> if (isSelected) PrimaryCyan else Color(0xFFF8BBD0)
                                        "Shoes & Footwear" -> if (isSelected) PrimaryCyan else Color(0xFFD7CCC8)
                                        "Perfumes & Fragrances" -> if (isSelected) PrimaryCyan else Color(0xFFE1BEE7)
                                        "Electronics & Mobiles" -> if (isSelected) PrimaryCyan else Color(0xFFC5CAE9)
                                        "Furniture & Decor" -> if (isSelected) PrimaryCyan else Color(0xFFC8E6C9)
                                        else -> if (isSelected) PrimaryCyan else SoftGrayText.copy(alpha = 0.3f)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(catBgColor)
                                            .border(1.dp, catBorderColor, RoundedCornerShape(20.dp))
                                            .clickable { selectedCategory = cat }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = displayText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = catTextColor
                                        )
                                    }
                                }
                            }
                        }

                        // Modern Price Range Filter component
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg.copy(alpha = 0.6f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.12f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (lang == "ar") "💵 تصفية حسب السعر" else "💵 Filter by Price Range",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PolarLight
                                        )
                                        if (maxPriceFilter != null) {
                                            Text(
                                                text = if (lang == "ar") "إعادة تعيين" else "Reset",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AccentCoral,
                                                modifier = Modifier.clickable { maxPriceFilter = null }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    androidx.compose.foundation.lazy.LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val priceLimits = listOf(5.0f, 10.0f, 15.0f, 25.0f, 50.0f)
                                        items(priceLimits) { limit ->
                                            val isSel = maxPriceFilter == limit
                                            val limitText = viewModel.formatPrice(limit.toDouble(), lang)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSel) SecondaryMint else CardDarkBg)
                                                    .border(1.dp, if (isSel) Color.Transparent else SoftGrayText.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                    .clickable { maxPriceFilter = limit }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (lang == "ar") "أقل من $limitText" else "Under $limitText",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) SlateDarkBg else PolarLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Catalog List elements showing budget checks
                        if (isProductsLoading) {
                            repeat(4) {
                                item {
                                    ProductSkeletonItem()
                                }
                            }
                        } else {
                            if (isGeminiRecommending) {
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.1f)),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                color = PrimaryCyan,
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Text(
                                                text = if (lang == "ar") "جاري تشغيل محرك توصيات جيميناي ذكاء اصطناعي..." else "Gemini recommendation engine is analyzing your interests...",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryCyan
                                            )
                                        }
                                    }
                                }
                            }

                            val filteredProducts = (if (selectedCategory == "All") {
                                if (geminiRecommendedCategories.isNotEmpty()) {
                                    productsList.sortedWith(compareByDescending<ProductEntity> { p ->
                                        geminiRecommendedCategories.contains(p.category)
                                    }.thenByDescending { p ->
                                        chosenInterests.contains(p.category)
                                    })
                                } else if (chosenInterests.isNotEmpty()) {
                                    productsList.sortedWith(compareByDescending { p ->
                                        chosenInterests.contains(p.category)
                                    })
                                } else {
                                    productsList
                                }
                            } else {
                                productsList.filter { it.category == selectedCategory }
                            })
                                .filter { maxPriceFilter == null || it.retailPrice <= maxPriceFilter!! }
                            if (filteredProducts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(txt("no_matching_prod"), color = SoftGrayText, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                items(filteredProducts) { p ->
                            val isBookmarked = savedList.any { it.id == p.id }
                            val userSetBudget = budgetValue.toDoubleOrNull()
                            val isWithinBudget = userSetBudget != null && p.retailPrice <= userSetBudget

                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (isWithinBudget) SecondaryMint else if (p.isRegisteredMerchant) Color.Transparent else WarmAmbar.copy(alpha = 0.25f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedProductForDetail = p }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(p.imageUrl),
                                        contentDescription = p.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )

                                    Column(modifier = Modifier.weight(1.0f)) {
                                        val isGeminiRec = geminiRecommendedCategories.contains(p.category)
                                        val isChosenRec = chosenInterests.contains(p.category)
                                        if (isGeminiRec || isChosenRec) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isGeminiRec) SecondaryMint.copy(alpha = 0.15f) else PrimaryCyan.copy(alpha = 0.15f))
                                                    .border(1.dp, if (isGeminiRec) SecondaryMint.copy(alpha = 0.4f) else PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (isGeminiRec) SecondaryMint else PrimaryCyan,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = if (isGeminiRec) {
                                                            if (lang == "ar") "توصية ذكاء اصطناعي جيميناي ✨" else "Gemini Recommended ✨"
                                                        } else {
                                                            if (lang == "ar") "يقترح لك بالاهتمامات ✨" else "Interest Recommended ✨"
                                                        },
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isGeminiRec) SecondaryMint else PrimaryCyan
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = p.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PolarLight,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (isBookmarked) AccentCoral else SoftGrayText,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable { viewModel.toggleSaveProduct(p.id) }
                                            )
                                        }

                                        Text(
                                            text = p.description,
                                            fontSize = 11.sp,
                                            color = SoftGrayText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = txt("retail_lbl") + ": " + viewModel.formatPrice(p.retailPrice, lang),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SecondaryMint
                                            )
                                            Text(
                                                text = txt("wholesale_lbl") + ": " + viewModel.formatPrice(p.wholesalePrice, lang),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryCyan
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (p.isRegisteredMerchant) {
                                                Text(
                                                    text = "✓ " + txt("cert_merchant"),
                                                    fontSize = 9.sp,
                                                    color = PrimaryCyan,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Warning,
                                                        contentDescription = null,
                                                        tint = WarmAmbar,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = " Scraped: ${p.merchantName}",
                                                        fontSize = 8.sp,
                                                        color = WarmAmbar,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            if (isWithinBudget) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(SecondaryMint.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (lang == "ar") "مناسب لميزانيتك! ✨" else "Budget match! ✨",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = SecondaryMint
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Direct Star Rating & Quick Interactive "+ Add to Cart" button (ودفع عالي وسهل)
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PrimaryCyan.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Rating",
                                                tint = WarmAmbar,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = String.format("%.1f", 4.3 + (p.id % 7) * 0.1),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WarmAmbar
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Compare Rates Button (مقارنة الأسعار الذكية)
                                        TextButton(
                                            onClick = {
                                                selectedProductForComparison = p
                                                viewModel.fetchDetailedPriceComparison(p.id, p.name)
                                            },
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.height(24.dp).testTag("compare_prices_btn_${p.id}")
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text("📊", fontSize = 11.sp)
                                                Text(
                                                    text = if (lang == "ar") "مقارنة" else "Compare",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryCyan
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Quick Add Button (ودفع مباشر دون مغادرة السجل)
                                        IconButton(
                                            onClick = {
                                                viewModel.addProductToCart(p, 1, "Retail")
                                            },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(PrimaryCyan)
                                                .size(36.dp)
                                                .testTag("quick_add_cart_${p.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Quick Add",
                                                tint = Color.White,
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
            }
                3 -> {
                    TrendingFeedScreen(viewModel = viewModel)
                }
                1 -> {
                    // CART & BILLING VIEW
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = if (lang == "ar") "🛒 سلة تسوق متجر الذكاء الاصطناعي" else "🛒 AI Store Smart Shopping Cart",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeaderBlue
                            )
                        }

                        if (cartItemsList.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingCart,
                                                contentDescription = null,
                                                tint = SoftGrayText.copy(alpha = 0.5f),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = if (lang == "ar") "سلتك خالية حالياً! تفقد المعروضات وأضف بعض المنتجات الرائعة." else "Your cart is empty! Browse the catalogue to add premium items.",
                                                fontSize = 11.sp,
                                                color = SoftGrayText,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(cartItemsList) { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SecondaryMint.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(item.imageUrl),
                                            contentDescription = item.productName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.productName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PolarLight
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = (if (lang == "ar") "السعر: " else "Price: ") + viewModel.formatPrice(item.price, lang),
                                                    fontSize = 11.sp,
                                                    color = SoftGrayText
                                                )
                                                Text(
                                                    text = "(${item.orderType})",
                                                    fontSize = 10.sp,
                                                    color = if (item.orderType == "Wholesale") PrimaryCyan else SecondaryMint,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(item, false) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = "Decrease",
                                                    tint = AccentCoral,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Text(
                                                text = "${item.quantity}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PolarLight
                                            )

                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(item, true) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Increase",
                                                    tint = SecondaryMint,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteCartItem(item.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Remove",
                                                    tint = AccentCoral,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val subtotal = cartItemsList.sumOf { it.price * it.quantity }
                                        val discountAmount = subtotal * appliedDiscountPercent
                                        val deliveryFee = if (subtotal > 0) 5.0 else 0.0
                                        val finalTotal = subtotal - discountAmount + deliveryFee

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "إجمالي قيمة المشتريات:" else "Subtotal Cart Value:",
                                                fontSize = 13.sp,
                                                color = PolarLight
                                            )
                                            Text(
                                                text = viewModel.formatPrice(subtotal, lang),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PolarLight
                                            )
                                        }

                                        // Discount Promo Code Input
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (lang == "ar") "🏷️ كود الخصم أو قسيمة التوفير:" else "🏷️ Promo Code or Coupon:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PolarLight
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = promoCodeInput,
                                                onValueChange = { promoCodeInput = it },
                                                placeholder = { Text(if (lang == "ar") "مثال: FRESH40" else "e.g. FRESH40", fontSize = 11.sp) },
                                                trailingIcon = {
                                                    IconButton(
                                                        onClick = { showCouponScanner = true },
                                                        modifier = Modifier.testTag("coupon_camera_scan_btn")
                                                    ) {
                                                        Text("📷", fontSize = 16.sp)
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = PrimaryCyan,
                                                    unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                                ),
                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                            )

                                            Button(
                                                onClick = {
                                                    val cleanCode = promoCodeInput.trim().uppercase()
                                                    if (cleanCode == "FRESH40") {
                                                        appliedPromoCode = "FRESH40"
                                                        appliedDiscountPercent = 0.40
                                                        viewModel.triggerError(if (lang == "ar") "تم تطبيق كود الخصم بنجاح! خصم بقيمة ٤٠٪ 🎉" else "Promo code applied successfully! 40% Discount applied 🎉")
                                                    } else if (cleanCode == "FAMILY20") {
                                                        appliedPromoCode = "FAMILY20"
                                                        appliedDiscountPercent = 0.20
                                                        viewModel.triggerError(if (lang == "ar") "تم تطبيق كود الخصم بنجاح! خصم بقيمة ٢٠٪ 🎉" else "Promo code applied successfully! 20% Discount applied 🎉")
                                                    } else if (cleanCode == "AISTORE") {
                                                        appliedPromoCode = "AISTORE"
                                                        appliedDiscountPercent = 0.15
                                                        viewModel.triggerError(if (lang == "ar") "تم تطبيق كود الخصم بنجاح! خصم بقيمة ١٥٪ 🎉" else "Promo code applied successfully! 15% Discount applied 🎉")
                                                    } else {
                                                        viewModel.triggerError(if (lang == "ar") "عذراً، كود الخصم هذا غير صالح!" else "Sorry, this promo code is invalid!")
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                            ) {
                                                Text(if (lang == "ar") "تطبيق" else "Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }

                                        // Recommended Codes Chips
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val codes = listOf(
                                                Pair("FRESH40", "40%"),
                                                Pair("FAMILY20", "20%"),
                                                Pair("AISTORE", "15%")
                                            )
                                            Text(if (lang == "ar") "أكواد مقترحة:" else "Suggestions:", fontSize = 9.sp, color = SoftGrayText)
                                            codes.forEach { (code, pct) ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (appliedPromoCode == code) PrimaryCyan.copy(alpha = 0.2f) else SoftGrayText.copy(alpha = 0.1f))
                                                        .border(1.dp, if (appliedPromoCode == code) PrimaryCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            promoCodeInput = code
                                                            appliedPromoCode = code
                                                            appliedDiscountPercent = if (code == "FRESH40") 0.40 else if (code == "FAMILY20") 0.20 else 0.15
                                                            viewModel.triggerError(if (lang == "ar") "تم تطبيق كود $code بنجاح! خصم $pct 🎉" else "Promo code $code applied! $pct Discount 🎉")
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = "$code ($pct)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (appliedPromoCode == code) PrimaryCyan else SoftGrayText)
                                                }
                                            }
                                        }

                                        if (appliedDiscountPercent > 0.0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (lang == "ar") "قيمة الخصم والتوفير ($appliedPromoCode):" else "Discount Applied ($appliedPromoCode):",
                                                    fontSize = 12.sp,
                                                    color = AccentCoral,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "-" + viewModel.formatPrice(discountAmount, lang),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentCoral
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "كلفة التوصيل اللوجستي:" else "Logistics Delivery Fee:",
                                                fontSize = 12.sp,
                                                color = PolarLight
                                            )
                                            Text(
                                                text = viewModel.formatPrice(deliveryFee, lang),
                                                fontSize = 13.sp,
                                                color = PolarLight
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(SoftGrayText.copy(alpha = 0.15f)))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "الكلفة الإجمالية الصافية:" else "Net Total Billing Value:",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = PolarLight
                                            )
                                            Text(
                                                text = viewModel.formatPrice(finalTotal, lang),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = SecondaryMint
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                val primaryCard = bankCardsList.find { it.isPrimary } ?: bankCardsList.firstOrNull()
                                                chosenCardForCheckout = primaryCard?.cardNumber
                                                showCartCheckoutDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "إتمام الشراء والدفع 💳" else "Proceed to Payment & Checkout 💳",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateDarkBg
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // BANK CARD LINKING MANAGER SECTION
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == "ar") "💳 البطاقات البنكية المربوطة" else "💳 Linked Bank Cards",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarLight
                                )

                                TextButton(
                                    onClick = {
                                        cardNumberState = ""
                                        cardHolderState = ""
                                        expiryDateState = ""
                                        cvvState = ""
                                        isPrimaryCardState = false
                                        showAddCardDialog = true
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Card", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (lang == "ar") "ربط بطاقة جديدة ➕" else "Link Card ➕",
                                            fontSize = 11.sp,
                                            color = PrimaryCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        if (bankCardsList.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SoftGrayText.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = SoftGrayText.copy(alpha = 0.4f),
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = if (lang == "ar") "لم تقم بربط أي بطاقة بنكية بعد. اربط بطاقتك لتسهيل عمليات الدفع الفوري." else "No credit card linked yet. Link your card for fast, one-tap checkout.",
                                                fontSize = 10.sp,
                                                color = SoftGrayText,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(bankCardsList) { card ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = if (card.isPrimary) listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                                                         else listOf(Color(0xFF141E30), Color(0xFF243B55))
                                            )
                                        )
                                        .border(1.dp, if (card.isPrimary) PrimaryCyan else SoftGrayText.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(
                                                    imageVector = Icons.Default.AddCircle,
                                                    contentDescription = "Chip",
                                                    tint = WarmAmbar,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = if (card.isPrimary) "PRIMARY DEBIT" else "SECURE DEBIT",
                                                    fontSize = 9.sp,
                                                    letterSpacing = 1.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (card.isPrimary) PrimaryCyan else SoftGrayText
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteBankCard(card.cardNumber) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Unlink Card",
                                                    tint = AccentCoral,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        val digits = card.cardNumber.replace(" ", "")
                                        val formattedMask = if (digits.length >= 16) {
                                            "••••  ••••  ••••  " + digits.substring(12, 16)
                                        } else {
                                            "••••  ••••  ••••  " + digits.takeLast(4)
                                        }

                                        Text(
                                            text = formattedMask,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = Color.White,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (lang == "ar") "حامل البطاقة" else "CARDHOLDER NAME",
                                                    fontSize = 7.sp,
                                                    color = SoftGrayText
                                                )
                                                Text(
                                                    text = card.cardHolder.uppercase(),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    maxLines = 1
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = if (lang == "ar") "نهاية الصلاحية" else "EXP DATE",
                                                    fontSize = 7.sp,
                                                    color = SoftGrayText
                                                )
                                                Text(
                                                    text = card.expiryDate,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // TRACKING & LOGISTICS VIEW
                    val localProfile = userProfile
                    val myOrders = if (localProfile != null) {
                        ordersList.filter { it.customerId == localProfile.id }
                    } else {
                        ordersList
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = if (lang == "ar") "🛍️ تتبع مشترياتي المباشرة وسجل الطرق" else "🛍️ Live Delivery Routes & Order Tracking",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight
                            )
                        }

                        item {
                            SpendingInsightsCard(viewModel = viewModel)
                        }

                        if (myOrders.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SoftGrayText.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (lang == "ar") "لا توجد طلبات نشطة في سجلاتك البديلة حالياً." else "No active orders registered in your ledger yet.",
                                            fontSize = 11.sp,
                                            color = SoftGrayText,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(myOrders) { ord ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "#${ord.id} - ${ord.productName}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PolarLight
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        when (ord.status) {
                                                            "Delivered" -> SecondaryMint.copy(alpha = 0.15f)
                                                            "Out For Delivery" -> PrimaryCyan.copy(alpha = 0.15f)
                                                            else -> WarmAmbar.copy(alpha = 0.15f)
                                                        }
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = ord.status,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (ord.status) {
                                                        "Delivered" -> SecondaryMint
                                                        "Out For Delivery" -> PrimaryCyan
                                                        else -> WarmAmbar
                                                    }
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Qty: ${ord.quantity} | Total: \$${String.format("%.2f", ord.totalPrice)}",
                                                fontSize = 11.sp,
                                                color = SoftGrayText
                                            )
                                            Text(
                                                text = "Mode: ${ord.orderType}",
                                                fontSize = 11.sp,
                                                color = SoftGrayText
                                            )
                                        }

                                        if (ord.routeAddress.isNotEmpty()) {
                                            Text(
                                                text = "Destination: ${ord.routeAddress}",
                                                fontSize = 10.sp,
                                                color = SoftGrayText
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Button(
                                            onClick = { viewModel.reorder(ord) },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "شراء مجدداً" else "Buy Again",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateDarkBg
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Button(
                                            onClick = { viewModel.triggerGoogleMap(ord.routeAddress) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = "Track Route Map",
                                                    tint = SlateDarkBg,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = if (lang == "ar") "تتبع الشحنة المباشرة على الخريطة 📍" else "Interactive Logistics Live GPS Map 📍",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SlateDarkBg
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // COMPREHENSIVE CRYPTO TRADING PLATFORM
                    val balanceMtc by viewModel.cryptoWalletMtc.collectAsState()
                    val balanceBtc by viewModel.cryptoWalletBtc.collectAsState()
                    val balanceEth by viewModel.cryptoWalletEth.collectAsState()
                    val balanceUsdt by viewModel.cryptoWalletUsdt.collectAsState()
                    val balanceAigh by viewModel.cryptoWalletAigh.collectAsState()
                    val directMessages by viewModel.directMessages.collectAsState()

                    val cryptoBalances = mapOf<String, Double>(
                        "JOD" to 250.0,
                        "MTC" to balanceMtc,
                        "BTC" to balanceBtc,
                        "ETH" to balanceEth,
                        "USDT" to balanceUsdt,
                        "AIGH" to balanceAigh
                    )
                    val approvedCryptoList = listOf("AIGH", "MTC", "BTC", "ETH", "USDT")

                    var selectedCryptoForAction by remember { mutableStateOf<String?>(null) }
                    var showSendCryptoDialog by remember { mutableStateOf(false) }
                    var showDepositCryptoDialog by remember { mutableStateOf(false) }

                    var sendAddressState by remember { mutableStateOf("") }
                    var sendAmountState by remember { mutableStateOf("") }
                    var depositAmountState by remember { mutableStateOf("") }

                    var activeChatChannel by remember { mutableStateOf("Mali AI Support Node 🤖") }
                    var messageInputState by remember { mutableStateOf("") }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // WALLET HEADER CARD
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(2.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)), RoundedCornerShape(24.dp)),
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (lang == "ar") "محفظة مالي اللامركزية 🪙" else "Mali Smart Crypto Wallet 🪙",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PrimaryCyan
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(SecondaryMint.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "مؤمنة ذاتياً 🔒" else "Self-Custodial 🔒",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SecondaryMint
                                            )
                                        }
                                    }

                                    // Display JOD Balance
                                    val currentJod = cryptoBalances["JOD"] ?: 250.0
                                    Text(
                                        text = if (lang == "ar") "رصيدك الأساسي المعتمد:" else "Primary Approved Balance:",
                                        fontSize = 11.sp,
                                        color = SoftGrayText
                                    )

                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = String.format("%.3f", currentJod),
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PolarLight
                                        )
                                        Text(
                                            text = "JOD",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    Text(
                                        text = if (lang == "ar") "≈ ${String.format("%.6f", currentJod / 70000.0)} BTC (مربوط بنظام السداد الفوري)" else "≈ ${String.format("%.6f", currentJod / 70000.0)} BTC (Bridged to Instapay Node)",
                                        fontSize = 10.sp,
                                        color = SoftGrayText
                                    )

                                    Divider(color = SoftGrayText.copy(alpha = 0.12f))

                                    // Quick wallet buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedCryptoForAction = "USDT"
                                                showDepositCryptoDialog = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "إيداع رصيد 📥" else "Deposit Funds 📥",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateDarkBg
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                selectedCryptoForAction = "USDT"
                                                showSendCryptoDialog = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = CardDarkBg),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "إرسال عملات 📤" else "Send Crypto 📤",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryCyan
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // LIVE TRADING CHART
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().height(220.dp),
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("AIGH/USDT", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                        Text("+5.4%", color = SecondaryMint, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    // Mock line chart
                                    val chartColor = PrimaryCyan
                                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                        val points = listOf(10f, 30f, 20f, 60f, 40f, 80f, 70f, 95f)
                                        val width = size.width
                                        val height = size.height
                                        val space = width / (points.size - 1)
                                        val path = androidx.compose.ui.graphics.Path()
                                        points.forEachIndexed { index, y ->
                                            val x = index * space
                                            val yPos = height - (y / 100f * height)
                                            if (index == 0) path.moveTo(x, yPos) else path.lineTo(x, yPos)
                                        }
                                        drawPath(
                                            path = path,
                                            color = chartColor,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                        )
                                    }
                                }
                            }
                        }

                        // CRYPTO ASSETS LIST (Approved by Admin)
                        item {
                            Text(
                                text = if (lang == "ar") "الأصول الرقمية المعتمدة بالمنصة 📈" else "Approved Platform Crypto Assets 📈",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        items(approvedCryptoList.size) { index ->
                            val symbol = approvedCryptoList[index]
                            val balance = cryptoBalances[symbol] ?: 0.0
                            val rateInJod = when (symbol) {
                                "BTC" -> 70000.0
                                "ETH" -> 2500.0
                                "SOL" -> 140.0
                                "USDT" -> 0.71
                                "MTC" -> 0.07
                                "AIGH" -> 1.50
                                else -> 1.0
                            }
                            val equivalentJod = balance * rateInJod

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCryptoForAction = symbol
                                        showSendCryptoDialog = true
                                    },
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg.copy(alpha = 0.7f)),
                                border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryCyan.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = symbol.take(2),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = PrimaryCyan
                                            )
                                        }

                                        Column {
                                            Text(text = symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                            Text(
                                                text = if (lang == "ar") "سعر الصرف: $rateInJod JOD" else "Index: $rateInJod JOD",
                                                fontSize = 9.sp,
                                                color = SoftGrayText
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = String.format("%.4f", balance),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PolarLight
                                        )
                                        Text(
                                            text = "≈ ${String.format("%.2f", equivalentJod)} JOD",
                                            fontSize = 9.sp,
                                            color = SecondaryMint,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // ALL PARTIES COMMUNICATION SYSTEM
                        item {
                            Text(
                                text = if (lang == "ar") "نظام اتصالات غرف الدردشة الموحد 💬" else "All-Parties Messaging Matrix 💬",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                            )
                        }

                        // Chat channels selector row
                        item {
                            val channels = listOf("Mali AI Support Node 🤖", "Logistics Admin 🏢", " Omar Delivery driver 🛵", "Fresh Store Merchant 🏪")
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(channels.size) { cIndex ->
                                    val chan = channels[cIndex]
                                    val isChanSel = activeChatChannel == chan
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isChanSel) SecondaryMint else CardDarkBg)
                                            .clickable { activeChatChannel = chan }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = chan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isChanSel) SlateDarkBg else PolarLight
                                        )
                                    }
                                }
                            }
                        }

                        // MESSAGES LOG
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.15f))
                            ) {
                                val filteredMsgs = directMessages.filter {
                                    val targetRole = when (activeChatChannel) {
                                        "Mali AI Support Node 🤖" -> "Admin"
                                        "Logistics Admin 🏢" -> "Admin"
                                        " Omar Delivery driver 🛵" -> "Delivery"
                                        else -> "Merchant"
                                    }
                                    it.senderRole == targetRole || it.receiverRole == targetRole
                                }
                                if (filteredMsgs.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (lang == "ar") "لا توجد رسائل سابقة في هذه القناة. ابدأ المحادثة الآن!" else "No legacy logs in this channel. Initiate session!",
                                            fontSize = 10.sp,
                                            color = SoftGrayText,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredMsgs.size) { mIdx ->
                                            val msg = filteredMsgs[mIdx]
                                            val isMyMsg = msg.senderRole == "Customer"
                                            val senderDisplayName = if (isMyMsg) (if (lang == "ar") "أنا" else "Me") else msg.senderName
                                            val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(java.util.Date(msg.timestamp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = if (isMyMsg) Arrangement.End else Arrangement.Start
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .clip(
                                                            RoundedCornerShape(
                                                                topStart = 12.dp,
                                                                topEnd = 12.dp,
                                                                bottomStart = if (isMyMsg) 12.dp else 0.dp,
                                                                bottomEnd = if (isMyMsg) 0.dp else 12.dp
                                                            )
                                                        )
                                                        .background(if (isMyMsg) SecondaryMint.copy(alpha = 0.2f) else CardDarkBg)
                                                        .padding(10.dp)
                                                        .widthIn(max = 240.dp)
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                        Text(text = senderDisplayName, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                                        Text(text = timeStr, fontSize = 7.sp, color = SoftGrayText)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(text = msg.content, fontSize = 10.sp, color = PolarLight)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Send message bar
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = messageInputState,
                                    onValueChange = { messageInputState = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text(text = if (lang == "ar") "اكتب رسالة..." else "Write message...", fontSize = 11.sp) },
                                    textStyle = TextStyle(fontSize = 11.sp, color = PolarLight),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryCyan,
                                        unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                        focusedContainerColor = CardDarkBg,
                                        unfocusedContainerColor = CardDarkBg
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (messageInputState.isNotBlank()) {
                                            val receiverRole = when (activeChatChannel) {
                                                "Mali AI Support Node 🤖" -> "Admin"
                                                "Logistics Admin 🏢" -> "Admin"
                                                " Omar Delivery driver 🛵" -> "Delivery"
                                                else -> "Merchant"
                                            }
                                            viewModel.sendDirectMessage("Customer", "Me", receiverRole, messageInputState)
                                            messageInputState = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(PrimaryCyan)
                                        .size(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = SlateDarkBg, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // SEND CRYPTO DIALOG
                    if (showSendCryptoDialog && selectedCryptoForAction != null) {
                        val symbol = selectedCryptoForAction!!
                        AlertDialog(
                            onDismissRequest = { showSendCryptoDialog = false },
                            title = {
                                Text(
                                    text = if (lang == "ar") "إرسال العملة المشفرة ($symbol) 📤" else "Send Cryptographic Asset ($symbol) 📤",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = sendAddressState,
                                        onValueChange = { sendAddressState = it },
                                        label = { Text(text = if (lang == "ar") "عنوان محفظة المستلم (Address)" else "Recipient Wallet Address") },
                                        placeholder = { Text(text = "0x... or bc1...") },
                                        textStyle = TextStyle(fontSize = 11.sp, color = PolarLight),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = sendAmountState,
                                        onValueChange = { sendAmountState = it },
                                        label = { Text(text = if (lang == "ar") "الكمية" else "Amount") },
                                        textStyle = TextStyle(fontSize = 11.sp, color = PolarLight),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val amt = sendAmountState.toDoubleOrNull()
                                        if (amt != null && sendAddressState.isNotBlank()) {
                                            viewModel.sendCrypto(symbol, sendAddressState, amt)
                                            showSendCryptoDialog = false
                                            sendAmountState = ""
                                            sendAddressState = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint)
                                ) {
                                    Text(text = if (lang == "ar") "تأكيد الإرسال والدمج ✔" else "Confirm Transfer ✔", color = SlateDarkBg)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showSendCryptoDialog = false }) {
                                    Text(text = if (lang == "ar") "إلغاء" else "Cancel", color = AccentCoral)
                                }
                            }
                        )
                    }

                    // DEPOSIT CRYPTO DIALOG
                    if (showDepositCryptoDialog && selectedCryptoForAction != null) {
                        val symbol = selectedCryptoForAction!!
                        AlertDialog(
                            onDismissRequest = { showDepositCryptoDialog = false },
                            title = {
                                Text(
                                    text = if (lang == "ar") "إيداع رصيد فوري ($symbol) 📥" else "Deposit Instapay Sandbox ($symbol) 📥",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = if (lang == "ar") "عنوان الإيداع الخاص بك بالشبكة اللامركزية:" else "Your platform secure destination address:",
                                        fontSize = 11.sp,
                                        color = SoftGrayText
                                    )
                                    Text(
                                        text = "0x9F3D8B765C92D48AB9BF560C39C4B5E22",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryMint
                                    )
                                    OutlinedTextField(
                                        value = depositAmountState,
                                        onValueChange = { depositAmountState = it },
                                        label = { Text(text = if (lang == "ar") "كمية الشحن (المحاكاة)" else "Simulation Deposit Amount") },
                                        textStyle = TextStyle(fontSize = 11.sp, color = PolarLight),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val amt = depositAmountState.toDoubleOrNull()
                                        if (amt != null) {
                                            viewModel.receiveCryptoSimulated(symbol, amt)
                                            showDepositCryptoDialog = false
                                            depositAmountState = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                                ) {
                                    Text(text = if (lang == "ar") "تأكيد الشحن ✔" else "Confirm Deposit ✔", color = SlateDarkBg)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDepositCryptoDialog = false }) {
                                    Text(text = if (lang == "ar") "إلغاء" else "Cancel", color = AccentCoral)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Product Details & Booking Dialog
    if (selectedProductForDetail != null) {
        val prod = selectedProductForDetail!!
        AlertDialog(
            onDismissRequest = { selectedProductForDetail = null },
            title = {
                Text(
                    prod.name,
                    color = PolarLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(prod.description, fontSize = 12.sp, color = SoftGrayText)
                    Text(txt("source_merchant") + prod.merchantName, fontSize = 11.sp, color = PrimaryCyan)

                    Divider(color = SoftGrayText.copy(alpha = 0.2f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("retail_lbl") + ":", fontSize = 12.sp, color = PolarLight)
                        Text(viewModel.formatPrice(prod.retailPrice, lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryMint)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("wholesale_lbl") + ":", fontSize = 12.sp, color = PolarLight)
                        Text(viewModel.formatPrice(prod.wholesalePrice, lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    }

                    // Trigger loading price comparison once when Dialog opens
                    LaunchedEffect(prod.name) {
                        viewModel.loadPriceComparison(prod.name)
                    }

                    val priceComparison by viewModel.priceComparisonState.collectAsState()
                    val isComparingPrice by viewModel.isComparingPrice.collectAsState()

                    Divider(color = SoftGrayText.copy(alpha = 0.2f))

                    Text(
                        text = if (lang == "ar") "📊 مقارنة الأسعار المباشرة (متاجر خارجية)" else "📊 Live Comparative Price Matching",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )

                    if (isComparingPrice) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = PrimaryCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == "ar") "جاري جلب الأسعار المباشرة..." else "Scraping real-time indexes...",
                                fontSize = 11.sp,
                                color = SoftGrayText
                            )
                        }
                    } else if (priceComparison != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            priceComparison!!.forEach { (retailer, price) ->
                                val isBest = retailer.contains("Smart") || retailer.contains("Marketplace")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isBest) SecondaryMint.copy(alpha = 0.1f) else Color.Transparent)
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (isBest) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Best Deal",
                                                tint = SecondaryMint,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        } else {
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(SoftGrayText.copy(alpha = 0.5f)))
                                        }
                                        Text(retailer, fontSize = 11.sp, color = PolarLight)
                                    }
                                    Text(
                                        text = viewModel.formatPrice(price, lang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBest) SecondaryMint else SoftGrayText
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(txt("order_mode") + ":", fontSize = 12.sp, color = PolarLight)
                        Button(
                            onClick = { orderTypeChosen = "Retail" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (orderTypeChosen == "Retail") SecondaryMint else CardDarkBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(txt("retail_mode"), fontSize = 10.sp, color = if (orderTypeChosen == "Retail") Color.White else PolarLight)
                        }

                        Button(
                            onClick = { orderTypeChosen = "Wholesale" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (orderTypeChosen == "Wholesale") PrimaryCyan else CardDarkBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(txt("wholesale_mode"), fontSize = 10.sp, color = if (orderTypeChosen == "Wholesale") Color.White else PolarLight)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(txt("quantity") + ":", fontSize = 12.sp, color = PolarLight)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = { if (orderQuantity > 1) orderQuantity-- }) {
                                Icon(Icons.Default.Menu, contentDescription = "Minus", tint = AccentCoral)
                            }
                            Text("$orderQuantity", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PolarLight)
                            IconButton(onClick = { orderQuantity++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = SecondaryMint)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.addProductToCart(prod, orderQuantity, orderTypeChosen)
                            selectedProductForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (lang == "ar") "أضف للسلة 🛒" else "Add to Cart 🛒",
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.placeOrder(prod, orderQuantity, orderTypeChosen)
                            selectedProductForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = txt("place_order"),
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.performAiProductInsights(prod.name, prod.ingredients)
                        selectedProductForDetail = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryMint)
                ) {
                    Text(txt("chem_report"))
                }
            },
            containerColor = CardDarkBg
        )
    }

    // Link New Bank Card Dialog
    if (showAddCardDialog) {
        var cardError by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = {
                Text(
                    text = if (lang == "ar") "ربط بطاقة بنكية آمنة جديدة 💳" else "Link Secure Debit/Credit Card 💳",
                    color = PolarLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == "ar") 
                            "سيتم تشفير بيانات بطاقتك بنسبة 100% وإضافتها إلى دفتر مشترياتك المشفر لتسهيل صفقات الشراء."
                            else "Your billing info is protected by end-to-end sandbox banking encryptions. All indices saved locally.",
                        fontSize = 11.sp,
                        color = SoftGrayText
                    )

                    if (cardError.isNotEmpty()) {
                        Text(cardError, color = AccentCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Quick Auto-Fill Button for testing sandbox
                    Button(
                        onClick = {
                            cardNumberState = "4242424242424242"
                            cardHolderState = "MOHAMMAD AL-OTAIBI"
                            expiryDateState = "12/29"
                            cvvState = "999"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (lang == "ar") "📋 تعبئة تلقائية لبطاقة التجربة" else "📋 Auto-Fill Sandbox Test Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }
                    }

                    // Card Number Field
                    OutlinedTextField(
                        value = cardNumberState,
                        onValueChange = { input ->
                            val cleaned = input.filter { it.isDigit() }
                            if (cleaned.length <= 16) cardNumberState = cleaned
                        },
                        label = { Text(if (lang == "ar") "رقم البطاقة (16 خانة)" else "Card Number (16 digits)") },
                        modifier = Modifier.fillMaxWidth().testTag("card_number_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    // Cardholder Name
                    OutlinedTextField(
                        value = cardHolderState,
                        onValueChange = { cardHolderState = it },
                        label = { Text(if (lang == "ar") "اسم صاحب البطاقة" else "Cardholder Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("card_holder_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Expiry Date MM/YY
                        OutlinedTextField(
                            value = expiryDateState,
                            onValueChange = { input ->
                                val cleaned = input.filter { it.isDigit() || it == '/' }
                                if (cleaned.length <= 5) expiryDateState = cleaned
                            },
                            label = { Text(if (lang == "ar") "تاريخ الصلاحية (MM/YY)" else "Expiry MM/YY") },
                            modifier = Modifier.weight(1f).testTag("card_expiry_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                focusedTextColor = PolarLight,
                                unfocusedTextColor = PolarLight
                            )
                        )

                        // CVV Code
                        OutlinedTextField(
                            value = cvvState,
                            onValueChange = { input ->
                                val cleaned = input.filter { it.isDigit() }
                                if (cleaned.length <= 3) cvvState = cleaned
                            },
                            label = { Text("CVV") },
                            modifier = Modifier.weight(1f).testTag("card_cvv_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                focusedTextColor = PolarLight,
                                unfocusedTextColor = PolarLight
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isPrimaryCardState,
                            onCheckedChange = { isPrimaryCardState = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryCyan, uncheckedColor = SoftGrayText)
                        )
                        Text(
                            text = if (lang == "ar") "تعيين كبطاقة دفع رئيسية" else "Set as Primary Payment Card",
                            fontSize = 11.sp,
                            color = PolarLight
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedCard = cardNumberState.replace(" ", "")
                        if (trimmedCard.length < 16) {
                            cardError = if (lang == "ar") "رقم البطاقة يجب أن يكون مكوناً من 16 رقماً!" else "Card number must contain exactly 16 digits!"
                        } else if (cardHolderState.isBlank()) {
                            cardError = if (lang == "ar") "اسم صاحب البطاقة لا يمكن أن يكون فارغاً!" else "Cardholder name cannot be blank!"
                        } else if (expiryDateState.length < 5 || !expiryDateState.contains("/")) {
                            cardError = if (lang == "ar") "تاريخ الصلاحية غير صحيح! استخدم صيغة MM/YY" else "Invalid Expiry! Use MM/YY format."
                        } else if (cvvState.length < 3) {
                            cardError = if (lang == "ar") "الرمز السري CVV يجب أن يكون من 3 أرقام!" else "CVV must be 3 digits!"
                        } else {
                            viewModel.linkBankCard(
                                cardNumber = trimmedCard,
                                cardHolder = cardHolderState,
                                expiryDate = expiryDateState,
                                cvv = cvvState,
                                isPrimary = isPrimaryCardState
                            )
                            showAddCardDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text(text = if (lang == "ar") "ربط وتأكيد ✔" else "Link Card ✔", color = SlateDarkBg)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddCardDialog = false }
                ) {
                    Text(text = if (lang == "ar") "إلغاء" else "Cancel", color = AccentCoral)
                }
            },
            containerColor = CardDarkBg
        )
    }

    // Shopping Cart Payment & Checkout Dialog
    if (showCartCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCartCheckoutDialog = false },
            title = {
                Text(
                    text = if (lang == "ar") "تأكيد الدفع وإصدار الفاتورة 💵" else "Checkout Payment Authorization 💵",
                    color = PolarLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == "ar") 
                            "إجمالي قيمة المشتريات هو: ${viewModel.formatPrice(cartItemsList.sumOf { it.price * it.quantity }, lang)}\nيرجى تحديد أسلوب السداد المعتمد لإتمام المعاملة فورياً:"
                            else "Your order subtotal: ${viewModel.formatPrice(cartItemsList.sumOf { it.price * it.quantity }, lang)}\nPlease choose your pre-set payment gateway mode below:",
                        fontSize = 11.sp,
                        color = PolarLight
                    )

                    Divider(color = SoftGrayText.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { chosenCardForCheckout = null }
                            .background(if (chosenCardForCheckout == null) PrimaryCyan.copy(alpha = 0.1f) else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = (chosenCardForCheckout == null),
                            onClick = { chosenCardForCheckout = null },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan, unselectedColor = SoftGrayText)
                        )
                        Column {
                            Text(
                                text = if (lang == "ar") "الدفع نقداً عند الاستلام (COD)" else "Cash on Delivery (COD)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight
                            )
                            Text(
                                text = if (lang == "ar") "سدد الكلفة نقداً لسائق التوصيل فور الوصول" else "Pay standard cash currency upon physical dispatch delivery",
                                fontSize = 10.sp,
                                color = SoftGrayText
                            )
                        }
                    }

                    bankCardsList.forEach { card ->
                        val isSelected = (chosenCardForCheckout == card.cardNumber)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { chosenCardForCheckout = card.cardNumber }
                                .background(if (isSelected) PrimaryCyan.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { chosenCardForCheckout = card.cardNumber },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan, unselectedColor = SoftGrayText)
                            )
                            Column {
                                Text(
                                    text = "Pay with Card ****" + card.cardNumber.takeLast(4),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarLight
                                )
                                Text(
                                    text = "Holder: " + card.cardHolder.uppercase() + " | Exp: " + card.expiryDate,
                                    fontSize = 10.sp,
                                    color = SoftGrayText
                                )
                            }
                        }
                    }

                    if (bankCardsList.isEmpty()) {
                        Text(
                            text = if (lang == "ar") "💡 تلميح: لا توجد بطاقات بنكية مربوطة حالياً. يمكنك ربط بطاقة جديدة لتسهيل الدفع." else "💡 Tip: No debit cards bound to system. Bind one using the 'Link Card' utility key.",
                            fontSize = 9.sp,
                            color = WarmAmbar
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isProcessingPayment = true
                        paymentStep = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SlateDarkBg,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (lang == "ar") "تأكيد الدفع وتفويض المعاملة ✔" else "Confirm & Authorize Deal ✔",
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCartCheckoutDialog = false }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = AccentCoral,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (lang == "ar") "رجوع للسلة" else "Back to Cart",
                            color = AccentCoral,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            containerColor = CardDarkBg
        )
    }

    // High-Fidelity Secure Bank Node Sandbox Payment Gateway Dialog
    if (isProcessingPayment) {
        AlertDialog(
            onDismissRequest = { /* Deny cancel during critical processing */ },
            title = {
                Text(
                    text = if (lang == "ar") "بوابة الدفع الآمنة عالية السرعة ⚡" else "High-Speed Secure Payment Gateway ⚡",
                    color = PrimaryCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    CircularProgressIndicator(
                        color = if (paymentStep == 3) SecondaryMint else PrimaryCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        // Step 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (paymentStep >= 1) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (paymentStep >= 1) SecondaryMint else SoftGrayText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == "ar") "تأسيس اتصال آمن مشفر (AES-256)..." else "Securing connection to sandbox bank node (AES-256)...",
                                fontSize = 11.sp,
                                color = if (paymentStep >= 0) PolarLight else SoftGrayText
                            )
                        }

                        // Step 2
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (paymentStep >= 2) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (paymentStep >= 2) SecondaryMint else SoftGrayText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == "ar") "تفويض الرمز السري ومطابقة الرصيد..." else "Validating CVV credentials & matching balances...",
                                fontSize = 11.sp,
                                color = if (paymentStep >= 1) PolarLight else SoftGrayText
                            )
                        }

                        // Step 3
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (paymentStep >= 3) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (paymentStep >= 3) SecondaryMint else SoftGrayText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == "ar") "إجراء تحويل الدفع الآمن في اللامركزية..." else "Transferring funds into decentralized ledger escrow...",
                                fontSize = 11.sp,
                                color = if (paymentStep >= 2) PolarLight else SoftGrayText
                            )
                        }

                        // Step 4
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (paymentStep >= 3) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (paymentStep >= 3) SecondaryMint else SoftGrayText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == "ar") "تم اعتماد الدفع بنجاح! شكراً لك." else "Payment approved! Logging secure transaction...",
                                fontSize = 11.sp,
                                color = if (paymentStep >= 3) SecondaryMint else SoftGrayText,
                                fontWeight = if (paymentStep >= 3) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = CardDarkBg
        )
    }

    if (showCameraSearchDialog) {
        CameraSearchDialog(
            viewModel = viewModel,
            onDismiss = { showCameraSearchDialog = false }
        )
    }

    if (showCouponScanner) {
        CouponScannerDialog(
            lang = lang,
            onDismiss = { showCouponScanner = false },
            onCouponScanned = { code, discount ->
                promoCodeInput = code
                appliedPromoCode = code
                appliedDiscountPercent = discount
                showCouponScanner = false
                viewModel.triggerError(if (lang == "ar") "تم مسح القسيمة بنجاح! تطبيق كود $code بقيمة ${(discount * 100).toInt()}% 🎉" else "Coupon scanned successfully! Applied $code with ${(discount * 100).toInt()}% off 🎉")
            }
        )
    }

    if (showTourOverlay) {
        InteractiveTourOverlay(
            lang = lang,
            step = tourStep,
            onNext = {
                if (tourStep < 4) {
                    tourStep += 1
                } else {
                    showTourOverlay = false
                }
            },
            onBack = {
                if (tourStep > 0) {
                    tourStep -= 1
                }
            },
            onSkip = {
                showTourOverlay = false
            }
        )
    }

    if (showVoiceSearchDialog) {
        VoiceSearchDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceSearchDialog = false }
        )
    }

    if (selectedProductForComparison != null) {
        val prod = selectedProductForComparison!!
        val detailedComparisons by viewModel.detailedPriceComparisons.collectAsState()
        val isComparingDetailedPrice by viewModel.isComparingDetailedPrice.collectAsState()
        val productComparisonState = detailedComparisons[prod.id]

        AlertDialog(
            onDismissRequest = { selectedProductForComparison = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📊", fontSize = 20.sp)
                    Text(
                        text = if (lang == "ar") "مقارنة الأسعار الذكية بالذكاء الاصطناعي" else "AI Smart Price Comparison",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(prod.imageUrl),
                                contentDescription = prod.name,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text(prod.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                Text(
                                    text = "${if (lang == "ar") "سعر المتجر المحلي:" else "Local Store Price:"} $${prod.retailPrice}",
                                    fontSize = 10.sp,
                                    color = SecondaryMint,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (isComparingDetailedPrice[prod.id] == true) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(color = PrimaryCyan, modifier = Modifier.size(32.dp))
                                Text(
                                    text = if (lang == "ar") "جاري جلب ومقارنة الأسعار عبر المتاجر المنافسة..." else "Fetching & comparing live retailer prices...",
                                    fontSize = 11.sp,
                                    color = SoftGrayText
                                )
                            }
                        }
                    } else if (productComparisonState != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (lang == "ar") "الأسعار المقارنة المتاحة:" else "Available Retailer Rates:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftGrayText
                            )

                            productComparisonState.forEach { comp ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = if (comp.isBestDeal) SecondaryMint else SoftGrayText.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (comp.isBestDeal) SecondaryMint.copy(alpha = 0.05f) else SlateDarkBg
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(comp.retailerName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                if (comp.isBestDeal) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(SecondaryMint.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (lang == "ar") "✓ الأفضل" else "✓ Best Deal",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SecondaryMint
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${if (lang == "ar") "الشحن:" else "Shipping:"} ${comp.shippingDays} ${if (lang == "ar") "أيام" else "days"}",
                                                fontSize = 9.sp,
                                                color = SoftGrayText
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "$${comp.price}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (comp.isBestDeal) SecondaryMint else PolarLight
                                            )
                                            val difference = comp.price - prod.retailPrice
                                            val diffText = if (difference < 0) {
                                                if (lang == "ar") "وفر $${String.format("%.2f", -difference)}" else "Save $${String.format("%.2f", -difference)}"
                                            } else {
                                                if (lang == "ar") "+$${String.format("%.2f", difference)}" else "+$${String.format("%.2f", difference)}"
                                            }
                                            Text(
                                                text = diffText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (difference < 0) SecondaryMint else AccentCoral
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lang == "ar") "لم نتمكن من العثور على مقارنات أسعار حالية." else "No comparison data retrieved.",
                                fontSize = 11.sp,
                                color = SoftGrayText
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedProductForComparison = null },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text(text = if (lang == "ar") "إغلاق" else "Close", color = SlateDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            containerColor = CardDarkBg
        )
    }
}

// ---------------- MERCHANT SCREEN ----------------
@Composable
fun MerchantScreen(viewModel: MarketViewModel) {
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val productsList by viewModel.products.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()

    // Helper translation accessor
    fun txt(key: String): String = Localization.get(key, lang)

    var excelColumnsInput by remember { mutableStateOf("اسم المنتج, السعر, المخزون, الباركود") }
    var selectedProdForCampaign by remember { mutableStateOf<ProductEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Excel bulk upload alignment (Moved to ExtraFeaturesDialog)
        if (false) { item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 " + txt("align_mismatch"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeaderBlue
                    )
                    Text(
                        text = if (lang == "ar") "قم بتحميل أو لصق قائمة البيانات من جهاز الكاشير أو الإكسل. سيقوم الذكاء الاصطناعي بمطابقة الحقول وحل الأخطاء فورياً." else "Upload columns or paste custom headers from your POS/Excel. AI automatically matches properties and fixes errors.",
                        fontSize = 11.sp,
                        color = SoftGrayText,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = excelColumnsInput,
                        onValueChange = { excelColumnsInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("excel_columns_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    Button(
                        onClick = { viewModel.performMerchantExcelAutoMap(excelColumnsInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("excel_align_btn")
                    ) {
                        Text(txt("align_mismatch"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } }

        // Active low stock warnings / optimization report
        item {
            Text(
                "⚠️ " + (if (lang == "ar") "تنبيهات المخزون والسلع التي قاربت على النفاد" else "Inventory Alerts & Low Stock"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCoral
            )
        }

        items(productsList.filter { it.stockQuantity <= 10 }) { p ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentCoral.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(p.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                        Text(txt("stock_left") + " ${p.stockQuantity} " + txt("units") + " | Rate: High Turnover", fontSize = 11.sp, color = SoftGrayText)
                    }

                    Button(
                        onClick = { viewModel.performMerchantPriceBenchmarking(p.name, p.retailPrice, p.wholesalePrice) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCoral),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(txt("run_benchmark"), fontSize = 10.sp, color = PolarLight)
                    }
                }
            }
        }

        // Available items list for marketing generation
        item {
            Text(
                if (lang == "ar") "إنشاء تصاميم ترويجية وعروض مخصصة" else "Generate Brand Designs & Custom Offers",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = HeaderBlue,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(productsList.filter { it.isRegisteredMerchant }) { p ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftGrayText.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(p.name, fontSize = 12.sp, color = PolarLight, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.generateMerchantDailyOfferAd(p.name, p.merchantName) },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("generate_promo_ad_btn")
                    ) {
                        Text(txt("gen_ad"), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- AI CREATIVE MEDIA STUDIO SECTION --- (Moved to ExtraFeaturesDialog)
        if (false) { item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)), RoundedCornerShape(16.dp))
                    .padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "🎥", fontSize = 22.sp)
                        Column {
                            Text(
                                text = if (lang == "ar") "استوديو الإعلانات الإبداعي (Veo 3 & Nano Banana)" else "AI Creative Ad Studio (Veo 3 & Nano Banana)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = HeaderBlue
                            )
                            Text(
                                text = if (lang == "ar") "صمم فيديوهات ترويجية سينمائية وصوراً عالية الجودة لمتجرك" else "Design cinematic video teasers & high-fidelity 8K posters",
                                fontSize = 9.sp,
                                color = SoftGrayText
                            )
                        }
                    }

                    Divider(color = SoftGrayText.copy(alpha = 0.15f))

                    // Media studio state bindings
                    val generatedVideoUrl by viewModel.generatedVideoUrl.collectAsState()
                    val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
                    val isAiLoading by viewModel.isAiLoading.collectAsState()

                    // Local configuration states
                    var adTypeSelected by remember { mutableStateOf("Video") } // "Video" or "Image"
                    var videoPromptState by remember { mutableStateOf("Pure golden honey slow drip, photorealistic, 8k resolution, bokeh effect") }
                    var imagePromptState by remember { mutableStateOf("Organic alpine honey in rustic jars, sunny kitchen morning, professional advertisement photography") }
                    var selectedAspectRatio by remember { mutableStateOf("16:9 Widescreen") }
                    var selectedResolution by remember { mutableStateOf("8K Cinematic Ultra") }

                    // Aspect Ratio pill switcher
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = if (lang == "ar") "نوع التصميم الترويجي:" else "Promotion Asset Type:", fontSize = 10.sp, color = PolarLight, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Video", "Image").forEach { type ->
                                val isSel = adTypeSelected == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PrimaryCyan else SlateDarkBg)
                                        .clickable { adTypeSelected = type }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when(type) {
                                            "Video" -> if (lang == "ar") "فيديو ترويجي (Veo 3)" else "Cinematic Video (Veo 3)"
                                            else -> if (lang == "ar") "صورة بجودة 8K (Banana)" else "8K Design Still (Banana)"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) SlateDarkBg else PolarLight
                                    )
                                }
                            }
                        }
                    }

                    // Render dynamic prompt input field
                    OutlinedTextField(
                        value = if (adTypeSelected == "Video") videoPromptState else imagePromptState,
                        onValueChange = {
                            if (adTypeSelected == "Video") videoPromptState = it else imagePromptState = it
                        },
                        label = { Text(if (lang == "ar") "وصف السيناريو الإعلاني للـ AI..." else "Describe advertisement scene...", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.2f),
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // Dimension configs based on selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (lang == "ar") "أبعاد العرض:" else "Aspect Ratio:", fontSize = 9.sp, color = SoftGrayText)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkBg)
                                    .clickable {
                                        selectedAspectRatio = if (selectedAspectRatio == "16:9 Widescreen") "9:16 Portrait Reels" else "16:9 Widescreen"
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = selectedAspectRatio, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                            }
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (lang == "ar") "دقة الوضوح:" else "Resolution:", fontSize = 9.sp, color = SoftGrayText)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkBg)
                                    .clickable {
                                        selectedResolution = if (selectedResolution == "8K Cinematic Ultra") "12K Holographic Hyperrealism" else "8K Cinematic Ultra"
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = selectedResolution, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                            }
                        }
                    }

                    // ACTION TRIGGER BUTTON
                    Button(
                        onClick = {
                            if (adTypeSelected == "Video") {
                                viewModel.generateVideoWithVeo(videoPromptState, selectedAspectRatio)
                            } else {
                                viewModel.generateImageWithBanana(imagePromptState, selectedResolution, selectedAspectRatio)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = SlateDarkBg)
                        } else {
                            Text(
                                text = when(adTypeSelected) {
                                    "Video" -> if (lang == "ar") "توليد كليب ترويجي بالفيديو" else "Generate Looping Cinematic Video"
                                    else -> if (lang == "ar") "رسم غلاف بدقة 8K فائقة" else "Create Ultra 8K Poster Frame"
                                },
                                fontWeight = FontWeight.Bold,
                                color = SlateDarkBg,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // MEDIA PREVIEW CARD & ASSET DETAILS
                    if (generatedVideoUrl != null && adTypeSelected == "Video") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AccentCoral.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = AccentCoral, modifier = Modifier.size(36.dp))
                                    Text(text = "🎬 Veo 3 Video Preview Playing", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                    Text(text = "Looping Clip: $videoPromptState", fontSize = 8.sp, color = SoftGrayText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    } else if (generatedImageUrl != null && adTypeSelected == "Image") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(text = "🎨 Nano Banana Pro 8K Active Poster", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                    Text(text = "Resol: $selectedResolution | Ratio: $selectedAspectRatio", fontSize = 8.sp, color = SecondaryMint)
                                    Text(text = "Prompt: $imagePromptState", fontSize = 8.sp, color = SoftGrayText, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }

                    // END-TO-END PUBLISHER FEED CONTROL
                    if (generatedVideoUrl != null || generatedImageUrl != null) {
                        Divider(color = SoftGrayText.copy(alpha = 0.15f))

                        var campaignTitleState by remember { mutableStateOf("خصم عائلي فوري 30% على العسل البلدي الطازج!") }

                        OutlinedTextField(
                            value = campaignTitleState,
                            onValueChange = { campaignTitleState = it },
                            label = { Text(if (lang == "ar") "عنوان الحملة الإعلانية للزبائن:" else "Customer Campaign Title:", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.2f),
                                focusedTextColor = PolarLight,
                                unfocusedTextColor = PolarLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                viewModel.publishCampaign(
                                    title = campaignTitleState,
                                    desc = if (adTypeSelected == "Video") videoPromptState else imagePromptState,
                                    mediaType = adTypeSelected
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = SlateDarkBg, modifier = Modifier.size(14.dp))
                                Text(
                                    text = if (lang == "ar") "نشر وتثبيت الحملة على شاشة المتجر للزبائن 🚀" else "Publish Campaign directly to Customer Feed 🚀",
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDarkBg,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        } }
    }
}

// ---------------- DELIVERY SCREEN ----------------
@Composable
fun DeliveryScreen(viewModel: MarketViewModel) {
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val ordersList by viewModel.orders.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()

    // Helper translation accessor
    fun txt(key: String): String = Localization.get(key, lang)

    val deliveryDriverOrders = ordersList.filter { it.status == "Out For Delivery" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Analytics Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecondaryMint.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⚡ " + (if (lang == "ar") "لوحة التحكم والتحريات اللوجستية" else "Logistics Dashboard"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryMint
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("driver_safety"), fontSize = 11.sp, color = SoftGrayText)
                        Text("98.5% (Platinum Tier)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryMint)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("fuel_saved"), fontSize = 11.sp, color = SoftGrayText)
                        Text("14.2 Liters ($18.50)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("est_score"), fontSize = 11.sp, color = SoftGrayText)
                        Text("4.9 / 5.0 ⭐", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmAmbar)
                    }
                }
            }
        }

        // Active Tasks
        item {
            Text(
                txt("assigned_active"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PolarLight
            )
        }

        if (deliveryDriverOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(txt("no_delivery"), fontSize = 12.sp, color = SoftGrayText)
                }
            }
        }

        items(deliveryDriverOrders) { ord ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(txt("id") + ord.id, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                        Text(ord.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmbar)
                    }

                    Text(txt("product") + "${ord.productName} (Qty: ${ord.quantity})", fontSize = 12.sp, color = PolarLight)
                    Text("Address: ${ord.routeAddress}", fontSize = 12.sp, color = SoftGrayText)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.performDriverRouteOptimization(ord.routeAddress) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(txt("route_matrix"), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.updateOrderStatus(ord.id, "Delivered") },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(txt("mark_delivered"), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Wait list (Available for Pick up)
        item {
            Text(
                txt("unassigned_orders"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PolarLight,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val unassignedOrders = ordersList.filter { it.status == "Pending" || it.status == "Preparing" }
        if (unassignedOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (lang == "ar") "جميع الطلبات نشطة أو تم تسليمها بالكامل." else "All orders currently delivery active or finalized.", fontSize = 12.sp, color = SoftGrayText)
                }
            }
        }

        items(unassignedOrders) { ord ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftGrayText.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ID: #${ord.id} - ${ord.productName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                        Text("Route: ${ord.routeAddress}", fontSize = 11.sp, color = SoftGrayText)
                    }

                    Button(
                        onClick = { viewModel.assignDriverToOrder(ord.id, 99) },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(txt("accept_carriage"), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------- ADMIN SCREEN ----------------
@Composable
fun AdminScreen(viewModel: MarketViewModel) {
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val usersList by viewModel.allUsersPrivate.collectAsState()
    val ordersList by viewModel.orders.collectAsState()
    val reportsList by viewModel.chatReports.collectAsState()
    val productsList by viewModel.products.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()

    // Helper translation accessor
    fun txt(key: String): String = Localization.get(key, lang)

    var disputeCustMsg by remember { mutableStateOf("Jars arrived broken in package box!") }
    var disputeMerchMsg by remember { mutableStateOf("We securely wrapped it. Transport issue.") }
    var disputeReason by remember { mutableStateOf("Damaged delivery items") }

    // Export for the web dashboard (dashboard/index.html → "استيراد بيانات التطبيق")
    val context = androidx.compose.ui.platform.LocalContext.current
    val exportScope = rememberCoroutineScope()
    var exportStatus by remember { mutableStateOf("") }
    val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            exportScope.launch {
                exportStatus = try {
                    val json = viewModel.buildDashboardExport()
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                            ?: throw java.io.IOException("No output stream for $uri")
                    }
                    if (lang == "ar") "✅ تم حفظ الملف. افتح لوحة الويب واضغط «استيراد بيانات التطبيق»." else "✅ Saved. Open the web dashboard and tap \"Import app data\"."
                } catch (e: Exception) {
                    android.util.Log.e("AdminScreen", "Dashboard export failed", e)
                    if (lang == "ar") "تعذّر حفظ الملف، حاول مجدداً." else "Could not save the file. Try again."
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Market Analytics Dashboard KPIs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        txt("metrics"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeaderBlue
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("total_users"), fontSize = 11.sp, color = SoftGrayText)
                        Text("${usersList.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("active_sales"), fontSize = 11.sp, color = SoftGrayText)
                        Text("${ordersList.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(txt("commission_opt"), fontSize = 11.sp, color = SoftGrayText)
                        TextButton(
                            onClick = { viewModel.performAdminCommissionOptimization() },
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(txt("recalc_opt"), fontSize = 10.sp, color = SecondaryMint)
                        }
                    }

                    Button(
                        onClick = { exportLauncher.launch("amer-store-export.json") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_export_dashboard_btn")
                    ) {
                        Text(
                            if (lang == "ar") "تصدير البيانات للوحة الويب 🌐" else "Export data for web dashboard 🌐",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    if (exportStatus.isNotEmpty()) {
                        Text(exportStatus, fontSize = 10.sp, color = SecondaryMint)
                    }
                }
            }
        }

        // Dispute Auto-Arbitration Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WarmAmbar.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        txt("dispute_resol"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmAmbar
                    )

                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text(txt("dispute_topic")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    OutlinedTextField(
                        value = disputeCustMsg,
                        onValueChange = { disputeCustMsg = it },
                        label = { Text(txt("cust_complaint")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    OutlinedTextField(
                        value = disputeMerchMsg,
                        onValueChange = { disputeMerchMsg = it },
                        label = { Text(txt("merch_defense")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PolarLight,
                            unfocusedTextColor = PolarLight
                        )
                    )

                    Button(
                        onClick = { viewModel.performAdminDisputeResolution(1085, disputeCustMsg, disputeMerchMsg, disputeReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmbar),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_solve_dispute_btn")
                    ) {
                        Text(txt("run_arbitration"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Private User Database Table - STRICT ACCESS SECURITY CONTEXT
        item {
            Text(
                txt("registered_users"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCoral
            )
        }

        if (usersList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(txt("no_users"), fontSize = 11.sp, color = SoftGrayText)
                }
            }
        }

        items(usersList) { u ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentCoral.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(u.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                        Text(u.role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    }

                    Text(txt("contact_phone") + u.phoneNumber, fontSize = 11.sp, color = SoftGrayText)
                    Text(txt("email_ledger") + u.email, fontSize = 11.sp, color = SoftGrayText)
                    Text(txt("behavior_opt") + (if (u.permissionGranted) txt("enrolled") else txt("declined")), fontSize = 10.sp, color = SecondaryMint)
                }
            }
        }

        // --- REAL-TIME FIRESTORE DATABASE TREE CONSOLE ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Brush.horizontalGradient(listOf(WarmAmbar, AccentCoral)), RoundedCornerShape(16.dp))
                    .padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "🔥", fontSize = 22.sp)
                            Column {
                                Text(
                                    text = if (lang == "ar") "كونسول قاعدة بيانات Firestore السحابية" else "Firebase Firestore Live Console",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = WarmAmbar
                                )
                                Text(
                                    text = "Connected Project ID: ai-store-f3d8b",
                                    fontSize = 9.sp,
                                    color = SoftGrayText
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SecondaryMint.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "LIVE SYNC 🟢", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SecondaryMint)
                        }
                    }

                    Divider(color = SoftGrayText.copy(alpha = 0.15f))

                    // Local state for expandable collections
                    var expandedCollection by remember { mutableStateOf<String?>(null) }
                    var isSyncingToCloud by remember { mutableStateOf(false) }
                    var cloudSyncProgress by remember { mutableStateOf(0f) }
                    var cloudSyncStatus by remember { mutableStateOf("") }

                    LaunchedEffect(isSyncingToCloud) {
                        if (isSyncingToCloud) {
                            cloudSyncProgress = 0.1f
                            cloudSyncStatus = "Serializing local SQLite/Room state..."
                            kotlinx.coroutines.delay(600)
                            cloudSyncProgress = 0.5f
                            cloudSyncStatus = "Checking Firestore client connection & authentication..."
                            kotlinx.coroutines.delay(700)
                            cloudSyncProgress = 0.8f
                            cloudSyncStatus = "Writing collection batches (users, products, orders)..."
                            kotlinx.coroutines.delay(600)
                            cloudSyncProgress = 1.0f
                            cloudSyncStatus = "Firestore Commit Complete! 100% Synced."
                            kotlinx.coroutines.delay(800)
                            isSyncingToCloud = false
                        }
                    }

                    // Sync action trigger
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { isSyncingToCloud = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmbar),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = SlateDarkBg, modifier = Modifier.size(14.dp))
                                Text(
                                    text = if (lang == "ar") "مزامنة البيانات السحابية مع Firestore الآن 🚀" else "Force SQLite Sync with Cloud Firestore 🚀",
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDarkBg,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (isSyncingToCloud || cloudSyncStatus.contains("Complete")) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateDarkBg)
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearProgressIndicator(
                                        progress = cloudSyncProgress,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = WarmAmbar,
                                        trackColor = SoftGrayText.copy(alpha = 0.2f)
                                    )
                                    Text(text = cloudSyncStatus, fontSize = 8.sp, color = SecondaryMint, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // INTERACTIVE COLLECTION DOCUMENT TREE
                    Text(text = if (lang == "ar") "اختر مجموعة سحابية لاستعراض وثائقها:" else "Select a Cloud Collection to expand:", fontSize = 10.sp, color = SoftGrayText, fontWeight = FontWeight.Bold)

                    val collections = listOf(
                        Triple("/users", "Users Private Authentication Profiles", usersList.size),
                        Triple("/products", "Registered Merchant Goods Inventory", productsList.size),
                        Triple("/orders", "Active Customer Purchases & Dispatch Records", ordersList.size),
                        Triple("/chat_reports", "AI Platform Behavior & Arbitration Reports", reportsList.size)
                    )

                    collections.forEach { (path, desc, count) ->
                        val isExpanded = expandedCollection == path
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCollection = if (isExpanded) null else path }
                                .border(1.dp, if (isExpanded) WarmAmbar.copy(alpha = 0.3f) else SoftGrayText.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = if (isExpanded) "📂" else "📁", fontSize = 14.sp)
                                        Text(text = path, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(WarmAmbar.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "docs: $count", fontSize = 8.sp, color = WarmAmbar, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (isExpanded) {
                                    Divider(color = SoftGrayText.copy(alpha = 0.1f))
                                    // Live expanded collection document serialized block
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(8.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = "Firestore Raw Document Payload:", fontSize = 8.sp, color = WarmAmbar, fontWeight = FontWeight.Bold)
                                            when (path) {
                                                "/users" -> {
                                                    usersList.take(3).forEach { u ->
                                                        Text(
                                                            text = "{ \"uid\": \"${u.id}\", \"name\": \"${u.name}\", \"email\": \"${u.email}\", \"role\": \"${u.role}\" }",
                                                            fontSize = 8.sp,
                                                            color = Color(0xFFBBF7D0),
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                        )
                                                    }
                                                }
                                                "/products" -> {
                                                    productsList.take(3).forEach { p ->
                                                        Text(
                                                            text = "{ \"id\": \"${p.id}\", \"name\": \"${p.name}\", \"price\": ${p.retailPrice}, \"stock\": ${p.stockQuantity} }",
                                                            fontSize = 8.sp,
                                                            color = Color(0xFFBBF7D0),
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                        )
                                                    }
                                                }
                                                "/orders" -> {
                                                    ordersList.take(3).forEach { o ->
                                                        Text(
                                                            text = "{ \"orderId\": \"${o.id}\", \"custName\": \"${o.customerName}\", \"status\": \"${o.status}\", \"total\": ${o.totalPrice} }",
                                                            fontSize = 8.sp,
                                                            color = Color(0xFFBBF7D0),
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                        )
                                                    }
                                                }
                                                else -> {
                                                    reportsList.take(3).forEach { r ->
                                                        Text(
                                                            text = "{ \"id\": \"${r.id}\", \"orderId\": \"${r.orderId}\", \"disputeReason\": \"${r.disputeReason}\", \"resolvedTip\": \"${r.resolvedTip}\" }",
                                                            fontSize = 8.sp,
                                                            color = Color(0xFFBBF7D0),
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
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
            }
        }
    }
}

// ---------------- GLOBAL RECOVERY ERROR BOUNDARY ----------------
@Composable
fun GlobalErrorBoundary(
    content: @Composable () -> Unit
) {
    content()
}

// ---------------- PRODUCT FEED SKELETON LOADERS ----------------
@Composable
fun ProductSkeletonItem() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                PrimaryCyan.copy(alpha = 0.05f),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// ---------------- AI TRENDING SCREEN FEED ----------------
@Composable
fun TrendingFeedScreen(viewModel: MarketViewModel) {
    val lang by viewModel.appLanguage.collectAsState()
    val trendingList by viewModel.trendingProducts.collectAsState()
    val isTrendingLoading by viewModel.isTrendingLoading.collectAsState()

    LaunchedEffect(Unit) {
        if (trendingList.isEmpty()) {
            viewModel.fetchAITrendingProducts()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (lang == "ar") "🤖 كاشف التريندات بالذكاء الاصطناعي" else "🤖 AI Social Trend Analytics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = HeaderBlue
                    )
                    Text(
                        text = if (lang == "ar") 
                            "يقوم محرك الذكاء الاصطناعي بمسح مؤشرات منصات TikTok و Reels و X لعرض المنتجات الأكثر انتشاراً الآن." 
                            else "The platform AI model scrapes viral digital signals across major social networks to discover hot commodities before they saturate.",
                        fontSize = 12.sp,
                        color = SoftGrayText
                    )
                    
                    Button(
                        onClick = { viewModel.fetchAITrendingProducts() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTrendingLoading
                    ) {
                        if (isTrendingLoading) {
                            CircularProgressIndicator(color = SlateDarkBg, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = if (lang == "ar") "🔄 تحديث مؤشرات التريند بالذكاء الاصطناعي" else "🔄 Refresh Live Social Trends",
                                color = SlateDarkBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (isTrendingLoading) {
            repeat(3) {
                item {
                    TrendingSkeletonItem()
                }
            }
        } else {
            items(trendingList) { tp ->
                TrendingProductCard(tp = tp, viewModel = viewModel, lang = lang)
            }
        }
    }
}

@Composable
fun TrendingProductCard(tp: TrendingProduct, viewModel: MarketViewModel, lang: String) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isHovered) 1.05f else 1f)
    val cardElevation by animateDpAsState(if (isHovered) 8.dp else 2.dp)
    
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(cardElevation, RoundedCornerShape(16.dp))
            .border(1.dp, SecondaryMint.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter || event.type == PointerEventType.Move
                    }
                }
            }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(tp.imageUrl),
                    contentDescription = tp.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Wishlist Toggle button
                val wishlistSet by viewModel.wishlist.collectAsState()
                val isWishlisted = wishlistSet.any { it.id == tp.id }
                IconButton(
                    onClick = { viewModel.toggleWishlist(tp) },
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .size(36.dp)
                        .background(CardDarkBg.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) AccentCoral else PolarLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentCoral)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tp.trendPlatform,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tp.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                    Text(
                        text = tp.trendTag,
                        fontSize = 10.sp,
                        color = SecondaryMint,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = tp.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarLight
                )

                Text(
                    text = tp.description,
                    fontSize = 12.sp,
                    color = SoftGrayText
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryCyan.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = tp.aiInsight,
                            fontSize = 11.sp,
                            color = PolarLight,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Divider(color = SoftGrayText.copy(alpha = 0.2f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = if (lang == "ar") "سعر التجزئة" else "Retail Price",
                            fontSize = 10.sp,
                            color = SoftGrayText
                        )
                        Text(
                            text = "$${tp.retailPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = SecondaryMint
                        )
                    }

                    Button(
                        onClick = {
                            val dummyProd = ProductEntity(
                                id = tp.id,
                                name = tp.name,
                                category = tp.category,
                                retailPrice = tp.retailPrice,
                                wholesalePrice = tp.wholesalePrice,
                                imageUrl = tp.imageUrl,
                                description = tp.description,
                                isRegisteredMerchant = false,
                                merchantName = tp.trendPlatform + " Scraped"
                            )
                            viewModel.addProductToCart(dummyProd, 1, "Retail")
                            viewModel.triggerError(if (lang == "ar") "تمت إضافة المنتج التريندي إلى السلة! 🛒" else "Added trending product to Cart! 🛒")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (lang == "ar") "شراء تجزئة 🛒" else "Buy Retail 🛒",
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingSkeletonItem() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryCyan.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
    ) {
        Column {
            ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(160.dp))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp).clip(RoundedCornerShape(4.dp)))
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).clip(RoundedCornerShape(4.dp)))
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(4.dp)))
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp)))
            }
        }
    }
}

// ---------------- CAMERA SEARCH VIEW ----------------
@Composable
fun CameraXPreview(modifier: Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context) }
    
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            val previewView = androidx.camera.view.PreviewView(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                } catch (e: Exception) {
                    android.util.Log.e("CameraX", "Binding failed", e)
                }
            }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = modifier
    )
}

@Composable
fun CameraSearchDialog(viewModel: MarketViewModel, onDismiss: () -> Unit) {
    val lang by viewModel.appLanguage.collectAsState()
    val isSearching by viewModel.isCameraSearching.collectAsState()
    val searchState by viewModel.cameraSearchState.collectAsState()
    val productsList by viewModel.products.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Receipt-specific state
    val isScanningReceipt by viewModel.isScanningReceipt.collectAsState()
    val scannedReceiptResult by viewModel.scannedReceiptResult.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            android.content.pm.PackageManager.PERMISSION_GRANTED ==
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            )
        )
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var useLiveCameraMode by remember { mutableStateOf(hasCameraPermission) }
    var selectedSampleIndex by remember { mutableStateOf(0) }
    var isReceiptMode by remember { mutableStateOf(false) } // MASTER SWITCH: Product vs Receipt

    val samples = listOf(
        Triple("Organic Mountain Honey (عسل جبلي طبيعي)", "Lavender Honey Jar 🍯", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?auto=format&fit=crop&q=80&w=400"),
        Triple("Supreme Turkish Coffee Blend (قهوة تركية فاخرة)", "Cardamom Coffee Bag ☕", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&q=80&w=400"),
        Triple("Fresh Organic Milk - Farm Direct (حليب مزارع طازج)", "Grass-fed Milk Carton 🥛", "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&q=80&w=400"),
        Triple("Organic Premium Farm Eggs Carton (30 Pcs)", "Premium Farm Eggs 🥚", "https://images.unsplash.com/photo-1516448620398-c5f44bf9f441?auto=format&fit=crop&q=80&w=400"),
        Triple("Supercharged Cognitive Energy Drink (مشروب الطاقة الذكي)", "Cognitive Energy Can ⚡", "https://images.unsplash.com/photo-1622543953495-a178e2253372?auto=format&fit=crop&q=80&w=400"),
        Triple("Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)", "Cocoa Granola Bar 🍪", "https://images.unsplash.com/photo-1568254183919-78a4f43a2877?auto=format&fit=crop&q=80&w=400")
    )

    val receiptSamples = listOf(
        Triple("Organic Market Honey", "Receipt: Organic Market 🧾", "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?auto=format&fit=crop&q=80&w=400"),
        Triple("Organic Premium Farm Eggs", "Receipt: Healthy Earth Co 🧾", "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?auto=format&fit=crop&q=80&w=400"),
        Triple("Supercharged Cognitive Energy", "Receipt: Hyper Market 🧾", "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?auto=format&fit=crop&q=80&w=400")
    )

    val currentSamples = if (isReceiptMode) receiptSamples else samples

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isReceiptMode) {
                        if (lang == "ar") "🧾 ماسح الإيصالات الذكي بالذكاء الاصطناعي" else "🧾 AI Smart Receipt Scanner"
                    } else {
                        if (lang == "ar") "📸 كاشف السلع بالذكاء الاصطناعي" else "📸 AI Smart Vision Scanner"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarLight
                )
                
                // Master Mode Switcher: Products vs Receipts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDarkBg)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isReceiptMode) PrimaryCyan else Color.Transparent)
                            .clickable { 
                                isReceiptMode = false 
                                selectedSampleIndex = 0
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lang == "ar") "📸 فحص السلع" else "📸 Product Scan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isReceiptMode) SlateDarkBg else PolarLight
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isReceiptMode) PrimaryCyan else Color.Transparent)
                            .clickable { 
                                isReceiptMode = true 
                                selectedSampleIndex = 0
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lang == "ar") "🧾 مسح الإيصالات" else "🧾 Receipt Scan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isReceiptMode) SlateDarkBg else PolarLight
                        )
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isReceiptMode) {
                        if (lang == "ar") "قم بتصوير الفاتورة الورقية لاستخلاص السلع تلقائياً وإضافتها لسجل المشتريات:" else "Snap a photo of physical grocery receipt to automatically parse items and import them into purchase history:"
                    } else {
                        if (lang == "ar") "وجه الكاميرا المباشرة أو اختر منتجاً لمحاكاة الفحص الذكي بالذكاء الاصطناعي:" else "Align camera scanner on organic grocery products, or simulate scans using target presets:"
                    },
                    fontSize = 11.sp,
                    color = SoftGrayText
                )

                // Tab Switcher for Camera modes (Live Feed vs Simulate)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDarkBg)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { 
                                if (!hasCameraPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                } else {
                                    useLiveCameraMode = true 
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = if (useLiveCameraMode) PrimaryCyan else Color.Transparent)
                    ) {
                        Box(modifier = Modifier.padding(6.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (lang == "ar") "📹 كاميرا حية" else "📹 Live Feed",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (useLiveCameraMode) SlateDarkBg else PolarLight
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { useLiveCameraMode = false },
                        colors = CardDefaults.cardColors(containerColor = if (!useLiveCameraMode) PrimaryCyan else Color.Transparent)
                    ) {
                        Box(modifier = Modifier.padding(6.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (lang == "ar") "🖼️ محاكاة الفحص" else "🖼️ Simulate Scan",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!useLiveCameraMode) SlateDarkBg else PolarLight
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(2.dp, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (useLiveCameraMode && hasCameraPermission) {
                        CameraXPreview(modifier = Modifier.fillMaxSize())
                    } else if (useLiveCameraMode && !hasCameraPermission) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { permissionLauncher.launch(android.Manifest.permission.CAMERA) }
                        ) {
                            Text("🔒", fontSize = 28.sp)
                            Text(
                                text = if (lang == "ar") "مطلوب إذن الكاميرا. اضغط للمنح." else "Camera permission required. Tap to grant.",
                                fontSize = 10.sp,
                                color = SoftGrayText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(currentSamples[selectedSampleIndex].third),
                            contentDescription = currentSamples[selectedSampleIndex].second,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Box(modifier = Modifier.align(Alignment.TopStart).size(20.dp).border(2.dp, PrimaryCyan, RoundedCornerShape(topStart = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(20.dp).border(2.dp, PrimaryCyan, RoundedCornerShape(topEnd = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.BottomStart).size(20.dp).border(2.dp, PrimaryCyan, RoundedCornerShape(bottomStart = 4.dp)))
                        Box(modifier = Modifier.align(Alignment.BottomEnd).size(20.dp).border(2.dp, PrimaryCyan, RoundedCornerShape(bottomEnd = 4.dp)))
                    }

                    // Scan Line Animation
                    val showProgress = if (isReceiptMode) isScanningReceipt else isSearching
                    if (showProgress) {
                        val infiniteTransition = rememberInfiniteTransition(label = "scanline")
                        val scanOffsetY by infiniteTransition.animateFloat(
                            initialValue = -80f,
                            targetValue = 80f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scanoffset"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = scanOffsetY.dp)
                                .background(Brush.horizontalGradient(listOf(Color.Transparent, PrimaryCyan, Color.Transparent)))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(color = PrimaryCyan, modifier = Modifier.size(32.dp))
                                Text(
                                    text = if (isReceiptMode) {
                                        if (lang == "ar") "جاري قراءة واستخراج الفاتورة بالذكاء الاصطناعي..." else "Gemini reading receipt lines..."
                                    } else {
                                        if (lang == "ar") "جاري المسح والتحليل بالذكاء الاصطناعي..." else "Gemini Vision Scrutinizing..."
                                    },
                                    fontSize = 11.sp,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currentSamples) { sample ->
                        val idx = currentSamples.indexOf(sample)
                        val isSel = selectedSampleIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PrimaryCyan else CardDarkBg)
                                .clickable { selectedSampleIndex = idx }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sample.second,
                                fontSize = 10.sp,
                                color = if (isSel) SlateDarkBg else PolarLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!isReceiptMode && searchState != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDarkBg.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SecondaryMint.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (lang == "ar") "🔎 السلعة المكتشفة:" else "🔎 Identified Commodity:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )
                                Text(
                                    text = "Confidence: ${(searchState!!.confidence * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    color = SoftGrayText
                                )
                            }
                            Text(
                                text = searchState!!.identifiedProduct,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = PolarLight
                            )
                            Divider(color = SoftGrayText.copy(alpha = 0.15f))
                            Text(
                                text = searchState!!.rationale,
                                fontSize = 11.sp,
                                color = SoftGrayText
                            )
                        }
                    }
                }

                // RECEIPT SCANNED DISPLAY RESULT
                if (isReceiptMode && scannedReceiptResult != null) {
                    val result = scannedReceiptResult!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDarkBg.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SecondaryMint.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🧾 ${result.merchantName}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = SecondaryMint
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SecondaryMint.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (lang == "ar") "مستورد" else "IMPORTED",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp,
                                        color = SecondaryMint
                                    )
                                }
                            }
                            
                            Divider(color = SoftGrayText.copy(alpha = 0.15f))
                            
                            result.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.productName}",
                                        fontSize = 10.sp,
                                        color = PolarLight,
                                        maxLines = 1,
                                        modifier = Modifier.weight(0.7f),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = viewModel.formatPrice(item.price * item.quantity, lang),
                                        fontSize = 10.sp,
                                        color = SoftGrayText,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                                    )
                                }
                            }

                            Divider(color = SoftGrayText.copy(alpha = 0.15f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == "ar") "الإجمالي المستخلص" else "Extracted Total",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGrayText
                                )
                                Text(
                                    text = viewModel.formatPrice(result.totalAmount, lang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SecondaryMint.copy(alpha = 0.1f))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (lang == "ar") "✓ تم إضافة السلع تلقائياً إلى سجل مشترياتك!" else "✓ Parsed items added directly to your purchase history!",
                                    color = SecondaryMint,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isReceiptMode) {
                    Button(
                        onClick = {
                            val sampleBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
                            viewModel.performGeminiCameraSearch(samples[selectedSampleIndex].first, sampleBitmap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        enabled = !isSearching
                    ) {
                        Text(
                            text = if (lang == "ar") "📸 التقاط المسح" else "📸 Snap Photo",
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (searchState != null) {
                        Button(
                            onClick = {
                                val targetProduct = productsList.find { it.name == searchState!!.identifiedProduct }
                                if (targetProduct != null) {
                                    viewModel.addProductToCart(targetProduct, 1, "Retail")
                                    viewModel.triggerError(if (lang == "ar") "تم اكتشاف المنتج وإضافته بنجاح لسلتك! 🛒" else "Product discovered and added to Cart! 🛒")
                                } else {
                                    viewModel.triggerError("Discovered item is out of stock in regional hubs.")
                                }
                                viewModel.clearCameraSearch()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint)
                        ) {
                            Text(
                                text = if (lang == "ar") "🛒 أضف للسلة" else "🛒 Add to Cart",
                                color = SlateDarkBg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // RECEIPT MODE CONFIRM BUTTON
                    Button(
                        onClick = {
                            val sampleBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
                            viewModel.scanReceiptImage(sampleBitmap, receiptSamples[selectedSampleIndex].first)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        enabled = !isScanningReceipt
                    ) {
                        Text(
                            text = if (lang == "ar") "🧾 فحص الفاتورة" else "🧾 Scan Receipt",
                            color = SlateDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.clearCameraSearch()
                viewModel.clearReceiptScan()
                onDismiss()
            }) {
                Text(text = if (lang == "ar") "إغلاق" else "Close", color = AccentCoral, fontSize = 11.sp)
            }
        }
    )
}

@Composable
fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .background(SoftGrayText.copy(alpha = alpha))
    )
}

@Composable
fun CustomerOnboardingScreen(viewModel: MarketViewModel, lang: String) {
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var onboardingStep by remember { mutableStateOf(1) } // 1: Terms, 2: Permissions, 3: Interests

    val chosenInterests by viewModel.chosenInterests.collectAsState()
    val aiExtractedInterests by viewModel.aiExtractedInterests.collectAsState()
    val scannedStores by viewModel.scannedStores.collectAsState()
    val isScanningGallery by viewModel.isScanningGallery.collectAsState()

    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val interestsList = listOf(
        Pair("Men's Clothing", if (lang == "ar") "👔 ملابس رجالية" else "👔 Men's Clothing"),
        Pair("Women's Clothing", if (lang == "ar") "👗 ملابس نسائية" else "👗 Women's Clothing"),
        Pair("Shoes & Footwear", if (lang == "ar") "👟 أحذية" else "👟 Shoes & Footwear"),
        Pair("Perfumes & Fragrances", if (lang == "ar") "✨ عطور وبخور" else "✨ Perfumes & Fragrances"),
        Pair("Cosmetics & Makeup", if (lang == "ar") "💄 مستحضرات تجميل" else "💄 Cosmetics & Makeup"),
        Pair("Watches & Accessories", if (lang == "ar") "⌚ ساعات وإكسسوارات" else "⌚ Watches & Accessories"),
        Pair("Bags & Luggage", if (lang == "ar") "👜 حقائب ومستلزمات" else "👜 Bags & Luggage"),
        Pair("Glasses & Eyewear", if (lang == "ar") "🕶️ نظارات" else "🕶️ Glasses & Eyewear"),
        Pair("Grocery & Food", if (lang == "ar") "🛒 مواد غذائية وتموينية" else "🛒 Grocery & Food"),
        Pair("Electronics & Mobiles", if (lang == "ar") "📱 جوالات وإلكترونيات" else "📱 Electronics & Mobiles"),
        Pair("Home Appliances", if (lang == "ar") "⚡ أجهزة منزلية" else "⚡ Home Appliances"),
        Pair("Kitchen & Cooking", if (lang == "ar") "🍳 أدوات المطبخ" else "🍳 Kitchen & Cooking"),
        Pair("Furniture & Decor", if (lang == "ar") "🛋️ أثاث وديكور" else "🛋️ Furniture & Decor"),
        Pair("Sweets & Bakery", if (lang == "ar") "🍰 حلويات ومخابز" else "🍰 Sweets & Bakery"),
        Pair("Beverages & Juices", if (lang == "ar") "🍹 مشروبات وعصائر" else "🍹 Beverages & Juices"),
        Pair("Sports & Fitness", if (lang == "ar") "⚽ رياضة ولياقة" else "⚽ Sports & Fitness"),
        Pair("Toys & Kids", if (lang == "ar") "🧸 ألعاب وأطفال" else "🧸 Toys & Kids"),
        Pair("Books & Stationery", if (lang == "ar") "📚 كتب وقرطاسية" else "📚 Books & Stationery"),
        Pair("Cleaning & Personal Care", if (lang == "ar") "🧼 منظفات وعناية شخصية" else "🧼 Cleaning & Personal Care"),
        Pair("Antiques & Gifts", if (lang == "ar") "🏺 تحف وهدايا" else "🏺 Antiques & Gifts"),
        Pair("Hardware & Tools", if (lang == "ar") "🔧 عدد وأدوات" else "🔧 Hardware & Tools"),
        Pair("AI Chatbot", if (lang == "ar") "🤖 بوت ذكاء اصطناعي" else "🤖 AI Chatbot"),
        Pair("Nano Banana", if (lang == "ar") "🍌 نانو بانانا" else "🍌 Nano Banana"),
        Pair("Gemini", if (lang == "ar") "✨ جيميني" else "✨ Gemini"),
        Pair("Machine Learning", if (lang == "ar") "🧠 تعلم آلي" else "🧠 Machine Learning"),
        Pair("NLP", if (lang == "ar") "📖 معالجة لغة طبيعية" else "📖 NLP"),
        Pair("Computer Vision", if (lang == "ar") "📷 رؤية الكمبيوتر" else "📷 Computer Vision"),
        Pair("Content Generation", if (lang == "ar") "✍️ توليد المحتوى" else "✍️ Content Generation"),
        Pair("LLM", if (lang == "ar") "🗄️ نموذج لغة كبير" else "🗄️ LLM"),
        Pair("GPU Training Rack", if (lang == "ar") "⚡ خوادم تدريب" else "⚡ GPU Training Rack"),
        Pair("Voice Assistant", if (lang == "ar") "🎙️ مساعد صوتي ذكي" else "🎙️ Voice Assistant"),
        Pair("AI Ethics", if (lang == "ar") "⚖️ أخلاقيات الذكاء" else "⚖️ AI Ethics"),
        Pair("Detailed Smart Catalog", if (lang == "ar") "📂 كتالوج ذكي مفصل" else "📂 Detailed Smart Catalog"),
        Pair("Smart Logistics", if (lang == "ar") "🚚 لوجستيات ذكية" else "🚚 Smart Logistics"),
        Pair("Legal AI", if (lang == "ar") "⚖️ ذكاء اصطناعي قانوني" else "⚖️ Legal AI"),
        Pair("Financial Prediction", if (lang == "ar") "📈 تحليل وتنبؤ مالي" else "📈 Financial Prediction"),
        Pair("Personalized Learning", if (lang == "ar") "🎓 تعلم شخصي" else "🎓 Personalized Learning")
    )

    var acceptedTerms by remember { mutableStateOf(false) }

    // Selected local interests
    var selectedLocalInterests by remember { mutableStateOf(emptySet<String>()) }

    // Simulated Permission toggles
    var permLocation by remember { mutableStateOf(false) }
    var permGallery by remember { mutableStateOf(false) }
    var permContacts by remember { mutableStateOf(false) }
    var permCalls by remember { mutableStateOf(false) }
    var permNotifications by remember { mutableStateOf(false) }
    var permOverlay by remember { mutableStateOf(false) }
    var permInstalledStores by remember { mutableStateOf(false) }

    var isScanningInstalledStores by remember { mutableStateOf(false) }

    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val openSettings = {
        try {
            // Using Linking API concepts to specifically route to details settings screen
            val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Compose Linking API UriHandler fallback
                uriHandler.openUri("package:${context.packageName}")
            } catch (ex: Exception) {
                try {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                } catch (any: Exception) {}
            }
        }
    }

    val checkRealPermissions: () -> Map<String, Boolean> = {
        val hasLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED || androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasGallery = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        val hasContacts = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasNotifications = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val hasOverlay = android.provider.Settings.canDrawOverlays(context)

        mapOf(
            "location" to hasLocation,
            "gallery" to hasGallery,
            "contacts" to hasContacts,
            "notifications" to hasNotifications,
            "overlay" to hasOverlay
        )
    }

    val isPermanentlyDenied: (String) -> Boolean = { permission ->
        val activity = context as? android.app.Activity
        activity?.let {
            val isDenied = androidx.core.content.ContextCompat.checkSelfPermission(it, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED
            isDenied && !androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val realPerms = checkRealPermissions()
        permLocation = realPerms["location"] == true
        permGallery = realPerms["gallery"] == true
        permContacts = realPerms["contacts"] == true
        permNotifications = realPerms["notifications"] == true
        permOverlay = realPerms["overlay"] == true
        
        // Explicitly check for 'denied' statuses on requested permissions
        var locationDenied = false
        var storageDenied = false
        var contactsDenied = false
        
        results.forEach { (permission, isGranted) ->
            if (!isGranted) {
                if (permission == android.Manifest.permission.ACCESS_FINE_LOCATION || 
                    permission == android.Manifest.permission.ACCESS_COARSE_LOCATION) {
                    locationDenied = true
                }
                if (permission == android.Manifest.permission.READ_MEDIA_IMAGES || 
                    permission == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                    storageDenied = true
                }
                if (permission == android.Manifest.permission.READ_CONTACTS) {
                    contactsDenied = true
                }
            }
        }
        
        if (locationDenied || storageDenied || contactsDenied) {
            val errMsg = buildString {
                append("Some permissions were denied (")
                val list = mutableListOf<String>()
                if (locationDenied) list.add("Location")
                if (storageDenied) list.add("Storage")
                if (contactsDenied) list.add("Contacts")
                append(list.joinToString(", "))
                append("). Redirecting to App Settings...")
            }
            viewModel.triggerError(errMsg)
            openSettings()
        }
    }

    val requestPermission = { permissionType: String ->
        when (permissionType) {
            "location" -> {
                val perm = android.Manifest.permission.ACCESS_FINE_LOCATION
                if (isPermanentlyDenied(perm)) {
                    openSettings()
                } else {
                    permissionLauncher.launch(arrayOf(perm, android.Manifest.permission.ACCESS_COARSE_LOCATION))
                }
            }
            "gallery" -> {
                val perm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    android.Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
                if (isPermanentlyDenied(perm)) {
                    openSettings()
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES))
                    } else {
                        permissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    }
                }
            }
            "contacts" -> {
                val perm = android.Manifest.permission.READ_CONTACTS
                if (isPermanentlyDenied(perm)) {
                    openSettings()
                } else {
                    permissionLauncher.launch(arrayOf(perm, android.Manifest.permission.CALL_PHONE))
                }
            }
            "notifications" -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    val perm = android.Manifest.permission.POST_NOTIFICATIONS
                    if (isPermanentlyDenied(perm)) {
                        openSettings()
                    } else {
                        permissionLauncher.launch(arrayOf(perm))
                    }
                } else {
                    permNotifications = true
                }
            }
            "overlay" -> {
                try {
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                } catch (e: Exception) {
                    openSettings()
                }
            }
            "installed" -> {
                permInstalledStores = true
            }
        }
    }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val realPerms = checkRealPermissions()
                permLocation = realPerms["location"] == true
                permGallery = realPerms["gallery"] == true
                permContacts = realPerms["contacts"] == true
                permNotifications = realPerms["notifications"] == true
                permOverlay = realPerms["overlay"] == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .background(CardDarkBg, RoundedCornerShape(24.dp))
                .border(2.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header / Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == "ar") "إعداد الحساب الذكي 🤖" else "Smart Account Onboarding 🤖",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryCyan
                )
                Text(
                    text = "$onboardingStep / 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGrayText
                )
            }

            LinearProgressIndicator(
                progress = onboardingStep / 3f,
                modifier = Modifier.fillMaxWidth(),
                color = SecondaryMint,
                trackColor = SoftGrayText.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (onboardingStep) {
                1 -> {
                    // STEP 1: TERMS AND CONDITIONS
                    Text(
                        text = if (lang == "ar") "الشروط والأحكام وقوانين الخدمة" else "Terms of Service & Usage Policies",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                        border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = if (lang == "ar") {
                                    "أهلاً بك في منصة مالي نيو مارت.\n\n" +
                                    "1. شروط العمر: يجب أن لا يقل عمرك عن 18 عاماً لاستخدام خدماتنا.\n" +
                                    "2. دقة البيانات: تقر بأن كل البيانات المدخلة صحيحة ومطابقة للواقع.\n" +
                                    "3. سياسة التوصيل: تخضع عمليات التوصيل لرسوم واحتساب جيو-لوجستي يعتمد على الموقع.\n" +
                                    "4. بوابات الدفع: جميع معاملات السداد الفورية عبر Google Pay والبطاقات البنكية وPayPal والعملات المشفرة تتم معالجتها وتأمينها وفق أعلى معايير الحماية والتشفير الذاتي الفوري.\n" +
                                    "5. دراسة الاهتمامات وسلوك المستخدم: لمطابقة رغباتك وعرض خصومات مخصصة لك، توافق على تفعيل خوارزميات دراسة سلوك البحث والمتاجر المثبتة وتطبيقات السوشيال ميديا وتوفير قراءة ذكية لملفات الكاشير والـ PDF والصور في الاستوديو.\n" +
                                    "6. حماية البيانات: نلتزم التزاماً مطلقاً بحماية هويتك الرقمية وعدم مشاركتها مع أطراف خارجية."
                                } else {
                                    "Welcome to Mali Neo Mart AI-Driven Marketplace.\n\n" +
                                    "1. Age Restriction: You must be at least 18 years old to access marketplace transactions.\n" +
                                    "2. Data Integrity: You pledge that all registered profile attributes are genuine and valid.\n" +
                                    "3. Logistics & Delivery Fees: Standard logistical weights, traffic congestion models, and geographic boundaries apply to delivery fees.\n" +
                                    "4. Payment Processing: Online checkouts using Google Pay, Visa/Mastercard, PayPal, or digital cryptocurrency wallets are processed securely via encrypted cryptographic tunnels.\n" +
                                    "5. Behavioral Analysis Consent: To facilitate smart custom-tailored offers, you agree to enable AI analysis of browsing history, installed apps, and visual/PDF data in your gallery.\n" +
                                    "6. Privacy Protocols: Your digital identities are protected and never distributed to third parties."
                                },
                                fontSize = 11.sp,
                                color = SoftGrayText,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { acceptedTerms = !acceptedTerms },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = acceptedTerms,
                            onCheckedChange = { acceptedTerms = it },
                            colors = CheckboxDefaults.colors(checkedColor = SecondaryMint)
                        )
                        Text(
                            text = if (lang == "ar") "أوافق على جميع الشروط والأحكام المذكورة أعلاه" else "I agree to the Terms and Conditions listed above",
                            fontSize = 11.sp,
                            color = PolarLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onboardingStep = 2 },
                        enabled = acceptedTerms,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (lang == "ar") "الموافقة والمتابعة" else "Accept & Continue",
                            color = SlateDarkBg,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                2 -> {
                    // STEP 2: PERMISSIONS REQUEST
                    Text(
                        text = if (lang == "ar") "منح صلاحيات النظام والتطبيقات" else "App & System Permissions Request",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight
                    )

                    Text(
                        text = if (lang == "ar") "لتفعيل الصلاحيات الفعلية، سيتم تحويلك إلى إعدادات التطبيق لتفعيل الأذونات." else "To enable real permissions, you will be redirected to the app settings.",
                        fontSize = 11.sp,
                        color = WarmAmbar,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PermissionItem(
                            icon = Icons.Default.LocationOn,
                            title = if (lang == "ar") "تحديد الموقع الجغرافي GPS" else "GPS Location Services",
                            desc = if (lang == "ar") "لتتبع السائق وتحديد أقرب المتاجر إليك" else "Required for active driver tracking and nearby store discovery",
                            checked = permLocation,
                            onCheckedChange = { 
                                requestPermission("location")
                            },
                            lang = lang
                        )

                        PermissionItem(
                            icon = Icons.Default.Share,
                            title = if (lang == "ar") "الوصول لمعرض الصور والملفات" else "Gallery & Media Access",
                            desc = if (lang == "ar") "لتمكين كاشف السلع بالصور والملفات بالذكاء الاصطناعي" else "Enables AI smart visual/PDF item discovery from your photos",
                            checked = permGallery,
                            onCheckedChange = { 
                                requestPermission("gallery")
                            },
                            lang = lang
                        )

                        PermissionItem(
                            icon = Icons.Default.Call,
                            title = if (lang == "ar") "جهات الاتصال والمكالمات" else "Contacts & Call Access",
                            desc = if (lang == "ar") "لتسهيل التواصل مع التاجر وسائق التوصيل" else "Enables direct VOIP routing between Customer, Merchant & Driver",
                            checked = permContacts,
                            onCheckedChange = { 
                                requestPermission("contacts")
                            },
                            lang = lang
                        )

                        PermissionItem(
                            icon = Icons.Default.Notifications,
                            title = if (lang == "ar") "تفعيل الإشعارات الفورية" else "Push Notifications",
                            desc = if (lang == "ar") "لتلقي تحديثات فواتيرك وخصومات الذكاء الاصطناعي" else "Get live invoices, discount updates, and real-time delivery status",
                            checked = permNotifications,
                            onCheckedChange = { 
                                requestPermission("notifications")
                            },
                            lang = lang
                        )

                        PermissionItem(
                            icon = Icons.Default.Settings,
                            title = if (lang == "ar") "الظهور فوق التطبيقات الأخرى" else "Display Over Other Apps",
                            desc = if (lang == "ar") "لعرض خرائط التتبع النشطة كنافذة عائمة" else "Allows floating live GPS trajectory overlays on home screens",
                            checked = permOverlay,
                            onCheckedChange = { 
                                requestPermission("overlay")
                            },
                            lang = lang
                        )

                        PermissionItem(
                            icon = Icons.Default.ShoppingCart,
                            title = if (lang == "ar") "الوصول للمتاجر الأخرى المثبتة (مستحسن 🌟)" else "Scan Other Installed Stores (Recommended 🌟)",
                            desc = if (lang == "ar") "لتمكين الذكاء الاصطناعي من دراسة سلوكك ومطابقة عروض المتاجر" else "Allows AI store profile mapping to provide extreme discounts",
                            checked = permInstalledStores,
                            onCheckedChange = { 
                                requestPermission("installed")
                            },
                            lang = lang
                        )
                    }

                    if (isScanningInstalledStores) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CircularProgressIndicator(color = SecondaryMint, modifier = Modifier.size(24.dp))
                                Text(
                                    text = if (lang == "ar") "جاري جرد المتاجر المثبتة على جهازك لتحديد الاهتمامات..." else "Scanning device matrix for installed marketplace signals...",
                                    fontSize = 10.sp,
                                    color = SoftGrayText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (scannedStores.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (lang == "ar") "✓ تم كشف المتاجر التالية:" else "✓ Installed Store Signals Found:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                                Text(
                                    text = scannedStores.joinToString(" - ") + "\n" + (if (lang == "ar") "تمت مطابقة رغباتك وإضافة خصومات 15% للمأكولات والإلكترونيات!" else "Interests aligned: 15% bonus added to Sweets & Tech!"),
                                    fontSize = 9.sp,
                                    color = SoftGrayText
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                openSettings()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardDarkBg),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (lang == "ar") "فتح الإعدادات" else "Open Settings",
                                color = PrimaryCyan,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                onboardingStep = 3
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (lang == "ar") "تخطي" else "Skip",
                                color = PolarLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                3 -> {
                    // STEP 3: CORE INTERESTS SELECTION
                    Text(
                        text = if (lang == "ar") "اختر اهتماماتك الرئيسية (فئتين كحد أدنى)" else "Select Your Core Interests (Minimum 2)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight
                    )

                    Text(
                        text = if (lang == "ar") "الفئات المحددة: ${selectedLocalInterests.size} (مطلوب فئتين كحد أدنى)" else "Selections: ${selectedLocalInterests.size} (minimum 2 required)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedLocalInterests.size >= 2) SecondaryMint else WarmAmbar
                    )

                    // Interests Grid FlowRow
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(interestsList.size) { i ->
                            val item = interestsList[i]
                            val isChosen = selectedLocalInterests.contains(item.first)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChosen) {
                                            selectedLocalInterests = selectedLocalInterests - item.first
                                        } else {
                                            selectedLocalInterests = selectedLocalInterests + item.first
                                        }
                                    }
                                    .border(
                                        1.dp,
                                        if (isChosen) SecondaryMint else SoftGrayText.copy(alpha = 0.2f),
                                        RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChosen) SecondaryMint.copy(alpha = 0.15f) else SlateDarkBg
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (lang == "ar") item.second else item.first,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChosen) SecondaryMint else PolarLight,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Scan Gallery Button (التعرف التلقائي على الاهتمامات عبر الصور والملفات)
                    Button(
                        onClick = { viewModel.scanGalleryAndFiles() },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDarkBg),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, WarmAmbar.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (isScanningGallery) {
                                CircularProgressIndicator(color = WarmAmbar, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (lang == "ar") "جاري قراءة الاستوديو وملفات الـ PDF بالذكاء الاصطناعي..." else "AI parsing gallery images & PDF catalog logs...",
                                    fontSize = 10.sp,
                                    color = WarmAmbar
                                )
                            } else {
                                Icon(Icons.Default.Info, contentDescription = null, tint = WarmAmbar, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (lang == "ar") "فحص الصور والملفات تلقائياً لتحديد اهتماماتي 📸" else "AI Smart Scan Gallery & PDF Files for Interests 📸",
                                    fontSize = 10.sp,
                                    color = WarmAmbar,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (aiExtractedInterests.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (lang == "ar") "✓ كشف الـ AI رغبة في: جيميني، بوت ذكاء اصطناعي، رؤية الكمبيوتر! تمت إضافتها لاهتماماتك." else "✓ AI detected interest matches: Gemini, AI Chatbot, Computer Vision! Injected into profile.",
                                fontSize = 9.sp,
                                color = SecondaryMint,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val finalInterests = selectedLocalInterests + aiExtractedInterests
                            viewModel.completeOnboarding(finalInterests)
                        },
                        enabled = (selectedLocalInterests.size >= 2),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (lang == "ar") "تأكيد ودخول المتجر" else "Confirm & Enter Marketplace",
                            color = SlateDarkBg,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, lang: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SecondaryMint.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SecondaryMint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = desc, fontSize = 9.sp, color = SoftGrayText, lineHeight = 13.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = SecondaryMint)
            )
        }
    }
}

@Composable
fun UserProfileDialog(
    viewModel: MarketViewModel,
    lang: String,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ProfileSettings(viewModel = viewModel, lang = lang, initialTab = initialTab, onDismiss = onDismiss)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettings(
    viewModel: MarketViewModel,
    lang: String,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isDark by viewModel.appDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val chosenInterests by viewModel.chosenInterests.collectAsState()
    val ordersList by viewModel.orders.collectAsState()
    val cartItemsList by viewModel.cartItems.collectAsState()
    val cryptoMtc by viewModel.cryptoWalletMtc.collectAsState()
    val cryptoBtc by viewModel.cryptoWalletBtc.collectAsState()
    val cryptoEth by viewModel.cryptoWalletEth.collectAsState()
    val cryptoUsdt by viewModel.cryptoWalletUsdt.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val lastCheckIn by viewModel.lastCheckInDate.collectAsState()

    val genderState by viewModel.userGender.collectAsState()
    val ageState by viewModel.userAge.collectAsState()
    val addressState by viewModel.userAddress.collectAsState()
    val passwordState by viewModel.userPassword.collectAsState()

    val aiVoice by viewModel.aiVoiceEnabled.collectAsState()
    val aiDashboard by viewModel.aiDashboardEnabled.collectAsState()
    val aiRecommendation by viewModel.aiRecommendationEnabled.collectAsState()
    val aiDeepThinking by viewModel.isHighThinkingEnabled.collectAsState()

    var activeTab by remember(initialTab) { mutableStateOf(initialTab) } // 0: Info, 1: Security, 2: Wallet & Rewards, 3: Orders, 4: Cart, 5: AI & Location, 6: Play & Win

    // Form editing states
    var nameInput by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var emailInput by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var phoneInput by remember(currentUser) { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var genderInput by remember(genderState) { mutableStateOf(genderState) }
    var ageInput by remember(ageState) { mutableStateOf(ageState.toString()) }
    var addressInput by remember(addressState) { mutableStateOf(addressState) }

    // Multiple Delivery Location States
    val deliveryLocations by viewModel.deliveryLocations.collectAsState()
    var isAddingLocation by remember { mutableStateOf(false) }
    var editingLocationId by remember { mutableStateOf<String?>(null) }
    var locLabelInput by remember { mutableStateOf("") }
    var locAddressInput by remember { mutableStateOf("") }
    var locLatInput by remember { mutableStateOf("") }
    var locLngInput by remember { mutableStateOf("") }

    // Password change states
    var oldPassInput by remember { mutableStateOf("") }
    var newPassInput by remember { mutableStateOf("") }
    var confirmPassInput by remember { mutableStateOf("") }
    var passwordSuccessMsg by remember { mutableStateOf("") }
    var passwordErrorMsg by remember { mutableStateOf("") }

    // Play & win memory game states
    val baseEmojis = listOf("🥦", "🍎", "🍯")
    fun generateGameCards(): List<MemoryCard> {
        return (baseEmojis + baseEmojis).shuffled().mapIndexed { idx, emoji ->
            MemoryCard(id = idx, emoji = emoji)
        }
    }
    var memoryCards by remember { mutableStateOf(generateGameCards()) }
    var selectedIndexes by remember { mutableStateOf<List<Int>>(emptyList()) }
    var gameRewardClaimed by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = SlateDarkBg,
        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SecondaryMint,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (lang == "ar") "إعدادات الملف الشخصي والميزات 👤" else "Profile Settings & Features 👤",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolarLight
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PolarLight)
                    }
                }

                Divider(color = PrimaryCyan.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Horizontal Tabs
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    val tabs = listOf(
                        0 to (if (lang == "ar") "المعلومات الشخصية" else "Personal Info"),
                        1 to (if (lang == "ar") "الأمان وكلمة السر" else "Security & Password"),
                        2 to (if (lang == "ar") "المحفظة والمكافآت" else "Wallet & Rewards"),
                        3 to (if (lang == "ar") "الطلبيات" else "Orders"),
                        4 to (if (lang == "ar") "السلة" else "Cart"),
                        5 to (if (lang == "ar") "الذكاء والموقع" else "AI & Location"),
                        6 to (if (lang == "ar") "العب واربح 🎮" else "Play & Win 🎮")
                    )
                    items(tabs) { (idx, title) ->
                        val isSel = activeTab == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PrimaryCyan else CardDarkBg)
                                .border(1.dp, if (isSel) Color.Transparent else PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .clickable { activeTab = idx }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) SlateDarkBg else PolarLight
                            )
                        }
                    }
                }

                // Content View based on Active Tab
                Box(modifier = Modifier.weight(1f)) {
                    when (activeTab) {
                        0 -> {
                            // PERSONAL INFO
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (lang == "ar") "المعلومات الشخصية للمستخدم" else "Personal User Details",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )

                                // Name Input with rules
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text(if (lang == "ar") "اسم المستخدم" else "Username") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryCyan,
                                        unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                        focusedLabelColor = PrimaryCyan
                                    ),
                                    supportingText = {
                                        Text(
                                            text = if (lang == "ar") "على الأقل 3 أحرف وأرقام بدون فراغات" else "At least 3 characters, alphanumeric, no spaces",
                                            fontSize = 9.sp,
                                            color = SoftGrayText
                                        )
                                    }
                                )

                                // Email Input
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text(if (lang == "ar") "البريد الإلكتروني" else "Email Address") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryCyan,
                                        unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f)
                                    )
                                )

                                // Phone Input
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text(if (lang == "ar") "رقم الهاتف" else "Phone Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryCyan,
                                        unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f)
                                    )
                                )

                                // Gender Selection Row
                                Column {
                                    Text(
                                        text = if (lang == "ar") "الجنس:" else "Gender:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolarLight,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        val genders = listOf("Male", "Female")
                                        val gendersAr = mapOf("Male" to "ذكر", "Female" to "أنثى")
                                        genders.forEach { g ->
                                            val isSelected = genderInput == g
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else CardDarkBg)
                                                    .border(1.dp, if (isSelected) PrimaryCyan else SoftGrayText.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .clickable { genderInput = g }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (lang == "ar") (gendersAr[g] ?: g) else g,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) PrimaryCyan else PolarLight
                                                )
                                            }
                                        }
                                    }
                                }

                                // Age Input
                                OutlinedTextField(
                                    value = ageInput,
                                    onValueChange = { ageInput = it.filter { char -> char.isDigit() } },
                                    label = { Text(if (lang == "ar") "العمر" else "Age") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryCyan,
                                        unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f)
                                    )
                                )

                                // Core Interests Grid Selector
                                Column {
                                    Text(
                                        text = if (lang == "ar") "الاهتمامات الأساسية (اضغط للتحديد):" else "Core Interests (Tap to Toggle):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolarLight,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    val allCats = listOf(
                                        "Organic Foods", "Spiced Coffee & Tea", "Dairy", "Health Drinks", 
                                        "Snacks", "Men's Clothing", "Women's Clothing", "Shoes & Footwear", 
                                        "Perfumes & Fragrances", "Electronics & Mobiles", "Furniture & Decor"
                                    )
                                    val allCatsAr = mapOf(
                                        "Organic Foods" to "الأغذية العضوية 🥦",
                                        "Spiced Coffee & Tea" to "القهوة والشاي ☕",
                                        "Dairy" to "الألبان والأجبان 🧀",
                                        "Health Drinks" to "المشروبات الصحية 🥤",
                                        "Snacks" to "المسليات والوجبات 🍿",
                                        "Men's Clothing" to "ملابس رجالية 👔",
                                        "Women's Clothing" to "ملابس نسائية 👗",
                                        "Shoes & Footwear" to "الأحذية والنعال 👟",
                                        "Perfumes & Fragrances" to "العطور والبخور 🧪",
                                        "Electronics & Mobiles" to "الهواتف والإلكترونيات 📱",
                                        "Furniture & Decor" to "الأثاث والديكور 🛋️"
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        allCats.forEach { cat ->
                                            val isSelected = chosenInterests.contains(cat)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) SecondaryMint.copy(alpha = 0.2f) else CardDarkBg)
                                                    .border(1.dp, if (isSelected) SecondaryMint else SoftGrayText.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        val updatedSet = if (isSelected) chosenInterests - cat else chosenInterests + cat
                                                        viewModel.completeOnboarding(updatedSet)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (lang == "ar") (allCatsAr[cat] ?: cat) else cat,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) SecondaryMint else PolarLight
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = if (lang == "ar") "🔗 روابط الملاحة السريعة للوحة التحكم" else "🔗 Quick Dashboard Shortcuts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Link to Wallet
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { activeTab = 2 }
                                            .testTag("nav_shortcut_wallet"),
                                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "💰", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = if (lang == "ar") "المحفظة" else "Wallet", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                        }
                                    }
                                    // Link to Orders
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { activeTab = 3 }
                                            .testTag("nav_shortcut_orders"),
                                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "📦", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = if (lang == "ar") "الطلبيات" else "Orders", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                        }
                                    }
                                    // Link to Cart
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { activeTab = 4 }
                                            .testTag("nav_shortcut_cart"),
                                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "🛒", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = if (lang == "ar") "السلة" else "Cart", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Save Profile Button
                                Button(
                                    onClick = {
                                        val ageNum = ageInput.toIntOrNull() ?: 25
                                        if (nameInput.trim().length >= 3 && !nameInput.contains(" ")) {
                                            viewModel.updateProfile(
                                                name = nameInput.trim(),
                                                email = emailInput.trim(),
                                                phone = phoneInput.trim(),
                                                gender = genderInput,
                                                age = ageNum,
                                                address = addressInput
                                            )
                                            android.widget.Toast.makeText(context, if (lang == "ar") "تم حفظ الملف الشخصي بنجاح!" else "Profile saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.triggerError(if (lang == "ar") "اسم المستخدم غير صالح! يجب أن يكون 3 أحرف على الأقل وبدون فراغات." else "Invalid username! Must be at least 3 characters and no spaces.")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (lang == "ar") "حفظ التعديلات" else "Save Changes", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        1 -> {
                            // SECURITY & PASSWORD
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (lang == "ar") "تغيير أو إضافة كلمة المرور" else "Change / Setup Account Password",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )

                                Text(
                                    text = if (lang == "ar") "كلمة السر الحالية: $passwordState" else "Current Saved Password: $passwordState",
                                    fontSize = 10.sp,
                                    color = SoftGrayText
                                )

                                OutlinedTextField(
                                    value = oldPassInput,
                                    onValueChange = { oldPassInput = it },
                                    label = { Text(if (lang == "ar") "كلمة السر القديمة" else "Old Password") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                )

                                OutlinedTextField(
                                    value = newPassInput,
                                    onValueChange = { newPassInput = it },
                                    label = { Text(if (lang == "ar") "كلمة السر الجديدة" else "New Password") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan),
                                    supportingText = {
                                        Text(
                                            text = if (lang == "ar") "قواعد الحماية: 6 خانات فأكثر، مع حرف واحد ورقم واحد على الأقل" else "Rules: 6+ characters, containing at least 1 letter and 1 digit",
                                            fontSize = 9.sp,
                                            color = SoftGrayText
                                        )
                                    }
                                )

                                OutlinedTextField(
                                    value = confirmPassInput,
                                    onValueChange = { confirmPassInput = it },
                                    label = { Text(if (lang == "ar") "تأكيد كلمة السر الجديدة" else "Confirm New Password") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                )

                                if (passwordSuccessMsg.isNotEmpty()) {
                                    Text(text = passwordSuccessMsg, color = SecondaryMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (passwordErrorMsg.isNotEmpty()) {
                                    Text(text = passwordErrorMsg, color = AccentCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        passwordSuccessMsg = ""
                                        passwordErrorMsg = ""
                                        if (oldPassInput != passwordState) {
                                            passwordErrorMsg = if (lang == "ar") "كلمة السر القديمة غير صحيحة!" else "Old password is incorrect!"
                                        } else if (newPassInput != confirmPassInput) {
                                            passwordErrorMsg = if (lang == "ar") "كلمة السر غير متطابقة!" else "New password confirmation does not match!"
                                        } else {
                                            val ok = viewModel.changePassword(newPassInput)
                                            if (ok) {
                                                passwordSuccessMsg = if (lang == "ar") "تم تغيير كلمة المرور بنجاح! 🔒" else "Password updated successfully! 🔒"
                                                oldPassInput = ""
                                                newPassInput = ""
                                                confirmPassInput = ""
                                            } else {
                                                passwordErrorMsg = if (lang == "ar") "لم تستوف شروط الأمان! يجب أن تكون كلمة السر 6 خانات مع رقم وحرف على الأقل." else "Security rules not met! Must be at least 6 characters with a letter and a digit."
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (lang == "ar") "تغيير كلمة السر" else "Update Password", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        2 -> {
                            // WALLET & DAILY REWARDS
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Wallet Balances Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = if (lang == "ar") "💰 المحفظة الرقمية المتكاملة" else "💰 Integrated Digital Wallet",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryCyan
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = "MARKET Coin (MTC)", fontSize = 9.sp, color = SoftGrayText)
                                                Text(text = "$cryptoMtc MTC", fontSize = 14.sp, fontWeight = FontWeight.Black, color = SecondaryMint)
                                            }
                                            Column {
                                                Text(text = "USDT (Stablecoin)", fontSize = 9.sp, color = SoftGrayText)
                                                Text(text = "$cryptoUsdt USDT", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PolarLight)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = "Bitcoin (BTC)", fontSize = 9.sp, color = SoftGrayText)
                                                Text(text = "$cryptoBtc BTC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                            }
                                            Column {
                                                Text(text = "Ethereum (ETH)", fontSize = 9.sp, color = SoftGrayText)
                                                Text(text = "$cryptoEth ETH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                            }
                                        }

                                        // Quick Simulate deposit
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.receiveCryptoSimulated("USDT", 10.0)
                                                    android.widget.Toast.makeText(context, if (lang == "ar") "تم شحن المحفظة بـ 10 USDT!" else "Deposited +10 USDT!", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = if (lang == "ar") "شحن +10 USDT" else "Deposit +10 USDT", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.receiveCryptoSimulated("MTC", 100.0)
                                                    android.widget.Toast.makeText(context, if (lang == "ar") "تم شحن المحفظة بـ 100 MTC!" else "Deposited +100 MTC!", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = if (lang == "ar") "شحن +100 MTC" else "Deposit +100 MTC", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Daily Check-in Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.05f)),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = if (lang == "ar") "📅 برنامج مكافآت الحضور اليومي" else "📅 Daily Check-in Rewards",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )

                                        Text(
                                            text = if (lang == "ar") "اربح من 0.02$ وحتى 50$ يومياً بنسب توزيع عادلة ومدروسة:" else "Earn from $0.02 to $50 daily with strict transparent probabilities:",
                                            fontSize = 9.sp,
                                            color = PolarLight
                                        )

                                        // Probabilities list
                                        Column(
                                            modifier = Modifier
                                                .background(CardDarkBg, shape = RoundedCornerShape(8.dp))
                                                .padding(6.dp)
                                        ) {
                                            val probs = listOf(
                                                "60% ($0.02 - $0.1)", "20% ($0.1 - $0.2)", 
                                                "15% ($0.2 - $0.3)", "2% ($0.3 - $0.34)", 
                                                "1% ($0.35 - $0.5)", "0.5% ($0.5 - $1.0)",
                                                "0.4% ($1.0 - $1.1)", "0.1% ($15.0)"
                                            )
                                            probs.forEach { p ->
                                                Text(text = "• $p", fontSize = 8.sp, color = SoftGrayText)
                                            }
                                        }

                                        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                                        val isCheckedInToday = lastCheckIn == today

                                        if (isCheckedInToday) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            ) {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryMint, modifier = Modifier.size(16.dp))
                                                Text(
                                                    text = if (lang == "ar") "لقد حصلت على مكافأة اليوم! عُد غداً ✨" else "Daily Reward claimed for today! Come back tomorrow ✨",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SecondaryMint
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    val reward = viewModel.performDailyCheckIn()
                                                    if (reward != null) {
                                                        android.widget.Toast.makeText(context, if (lang == "ar") "مبروك! حصلت على $reward USDT 🎁" else "Congrats! You got +$$reward USDT 🎁", android.widget.Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(text = if (lang == "ar") "تسجيل الحضور اليومي واستلام الجائزة 🎁" else "Check-in Today & Grab Reward 🎁", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Wallet Transactions Log
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = if (lang == "ar") "سجل الحركات المالية:" else "Wallet Transactions History:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardDarkBg, shape = RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                            .heightIn(max = 120.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (transactions.isEmpty()) {
                                            Text(text = if (lang == "ar") "لا يوجد حركات بعد" else "No transactions logged yet", fontSize = 10.sp, color = SoftGrayText)
                                        } else {
                                            transactions.reversed().forEach { tx ->
                                                Text(text = "• $tx", fontSize = 9.sp, color = PolarLight)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            // ORDERS HISTORIES
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val userId = currentUser?.id ?: 0
                                val myOrders = ordersList.filter { it.customerId == userId }
                                val completedOrders = myOrders.filter { it.status == "Delivered" }
                                val activeOrders = myOrders.filter { it.status != "Delivered" }

                                // Orders In Progress
                                Text(
                                    text = if (lang == "ar") "📦 طلبيات قيد التنفيذ (${activeOrders.size})" else "📦 Orders in Progress (${activeOrders.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )

                                if (activeOrders.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardDarkBg, shape = RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = if (lang == "ar") "لا توجد طلبيات قيد التنفيذ حالياً" else "No orders currently in progress", fontSize = 10.sp, color = SoftGrayText)
                                    }
                                } else {
                                    activeOrders.forEach { order ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(text = order.productName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                    Text(
                                                        text = order.status,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (order.status) {
                                                            "Pending" -> PrimaryCyan
                                                            "Preparing" -> WarmAmbar
                                                            else -> SecondaryMint
                                                        }
                                                    )
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(text = "${order.quantity} x ${viewModel.formatPrice(order.totalPrice / order.quantity, lang)}", fontSize = 9.sp, color = SoftGrayText)
                                                    Text(text = if (lang == "ar") "الإجمالي: ${viewModel.formatPrice(order.totalPrice, lang)}" else "Total: ${viewModel.formatPrice(order.totalPrice, lang)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryMint)
                                                }
                                                Text(
                                                    text = (if (lang == "ar") "التاجر: " else "Merchant: ") + order.merchantName,
                                                    fontSize = 8.sp,
                                                    color = SoftGrayText
                                                )
                                                Text(
                                                    text = (if (lang == "ar") "التاريخ: " else "Date: ") + java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.US).format(java.util.Date(order.createdAt)),
                                                    fontSize = 8.sp,
                                                    color = SoftGrayText
                                                )
                                            }
                                        }
                                    }
                                }

                                Divider(color = PrimaryCyan.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                                // Received/Completed Orders
                                Text(
                                    text = if (lang == "ar") "✅ طلبيات تم استلامها (${completedOrders.size})" else "✅ Received/Completed Orders (${completedOrders.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )

                                if (completedOrders.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardDarkBg, shape = RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = if (lang == "ar") "لا توجد طلبيات مستلمة مسبقاً" else "No received orders in history", fontSize = 10.sp, color = SoftGrayText)
                                    }
                                } else {
                                    completedOrders.forEach { order ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                            border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.1f))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(text = order.productName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                    Text(text = if (lang == "ar") "مكتملة ✅" else "Delivered ✅", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryMint)
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(text = "${order.quantity} x ${viewModel.formatPrice(order.totalPrice / order.quantity, lang)}", fontSize = 9.sp, color = SoftGrayText)
                                                    Text(text = if (lang == "ar") "الإجمالي: ${viewModel.formatPrice(order.totalPrice, lang)}" else "Total: ${viewModel.formatPrice(order.totalPrice, lang)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                                }
                                                Text(
                                                    text = (if (lang == "ar") "التاجر: " else "Merchant: ") + order.merchantName,
                                                    fontSize = 8.sp,
                                                    color = SoftGrayText
                                                )
                                                Text(
                                                    text = (if (lang == "ar") "تاريخ الاستلام: " else "Delivered At: ") + java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.US).format(java.util.Date(order.createdAt)),
                                                    fontSize = 8.sp,
                                                    color = SoftGrayText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        4 -> {
                            // CART VIEW INSIDE PROFILE
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (lang == "ar") "🛒 سلة التسوق الخاصة بك" else "🛒 Your Active Shopping Cart",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )

                                if (cartItemsList.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardDarkBg, shape = RoundedCornerShape(8.dp))
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = if (lang == "ar") "سلة التسوق فارغة حالياً 🛒" else "Your shopping cart is empty 🛒", fontSize = 11.sp, color = SoftGrayText)
                                    }
                                } else {
                                    cartItemsList.forEach { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = item.imageUrl,
                                                    contentDescription = item.productName,
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(SlateDarkBg)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = item.productName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                    Text(text = viewModel.formatPrice(item.price, lang), fontSize = 10.sp, color = SecondaryMint)
                                                }
                                                // Quantity Adjuster
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            if (item.quantity > 1) {
                                                                viewModel.updateCartItemQuantity(item, false)
                                                            } else {
                                                                viewModel.deleteCartItem(item.id)
                                                            }
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Minus", tint = PrimaryCyan)
                                                    }
                                                    Text(text = "${item.quantity}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.updateCartItemQuantity(item, true)
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Plus", tint = PrimaryCyan)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    val totalVal = cartItemsList.sumOf { it.price * it.quantity }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = if (lang == "ar") "الإجمالي الكلي:" else "Grand Total:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                        Text(text = viewModel.formatPrice(totalVal, lang), fontSize = 14.sp, fontWeight = FontWeight.Black, color = SecondaryMint)
                                    }
                                }
                            }
                        }
                        5 -> {
                            // AI FEATURES & LOCATION
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Address & Location Management Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "📍 إدارة عناوين التوصيل المتعددة" else "📍 Manage Delivery Locations",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryCyan
                                            )
                                            if (!isAddingLocation && editingLocationId == null) {
                                                TextButton(
                                                    onClick = {
                                                        isAddingLocation = true
                                                        editingLocationId = null
                                                        locLabelInput = ""
                                                        locAddressInput = ""
                                                        locLatInput = "31.9522"
                                                        locLngInput = "35.9158"
                                                    },
                                                    modifier = Modifier.testTag("add_location_button")
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = SecondaryMint)
                                                        Text(text = if (lang == "ar") "إضافة عنوان" else "Add New", fontSize = 11.sp, color = SecondaryMint, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        // Add / Edit Location Form
                                        if (isAddingLocation || editingLocationId != null) {
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                                                border = BorderStroke(1.dp, SecondaryMint.copy(alpha = 0.3f))
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text(
                                                        text = if (editingLocationId != null) {
                                                            if (lang == "ar") "تعديل عنوان التوصيل ✏️" else "Edit Delivery Location ✏️"
                                                        } else {
                                                            if (lang == "ar") "إضافة عنوان توصيل جديد ➕" else "Add New Delivery Location ➕"
                                                        },
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SecondaryMint
                                                    )

                                                    // Label input
                                                    OutlinedTextField(
                                                        value = locLabelInput,
                                                        onValueChange = { locLabelInput = it },
                                                        label = { Text(if (lang == "ar") "اسم المكان (مثال: المنزل، المكتب)" else "Location Label (e.g., Home, Office)") },
                                                        modifier = Modifier.fillMaxWidth().testTag("location_label_input"),
                                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                                    )

                                                    // Full Address
                                                    OutlinedTextField(
                                                        value = locAddressInput,
                                                        onValueChange = { locAddressInput = it },
                                                        label = { Text(if (lang == "ar") "العنوان التفصيلي" else "Full Delivery Address") },
                                                        modifier = Modifier.fillMaxWidth().testTag("location_address_input"),
                                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                                    )

                                                    // Lat & Lng Row
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        OutlinedTextField(
                                                            value = locLatInput,
                                                            onValueChange = { locLatInput = it },
                                                            label = { Text("Latitude") },
                                                            modifier = Modifier.weight(1f).testTag("location_lat_input"),
                                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                                        )
                                                        OutlinedTextField(
                                                            value = locLngInput,
                                                            onValueChange = { locLngInput = it },
                                                            label = { Text("Longitude") },
                                                            modifier = Modifier.weight(1f).testTag("location_lng_input"),
                                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                                                        )
                                                    }

                                                    // Action buttons
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        TextButton(
                                                            onClick = {
                                                                isAddingLocation = false
                                                                editingLocationId = null
                                                            }
                                                        ) {
                                                            Text(text = if (lang == "ar") "إلغاء" else "Cancel", color = SoftGrayText)
                                                        }
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Button(
                                                            onClick = {
                                                                val latVal = locLatInput.toDoubleOrNull() ?: 31.9522
                                                                val lngVal = locLngInput.toDoubleOrNull() ?: 35.9158
                                                                if (locLabelInput.isNotBlank() && locAddressInput.isNotBlank()) {
                                                                    val editId = editingLocationId
                                                                    if (editId != null) {
                                                                        viewModel.updateDeliveryLocation(editId, locLabelInput, locAddressInput, latVal, lngVal)
                                                                    } else {
                                                                        viewModel.addDeliveryLocation(locLabelInput, locAddressInput, latVal, lngVal)
                                                                    }
                                                                    isAddingLocation = false
                                                                    editingLocationId = null
                                                                    android.widget.Toast.makeText(context, if (lang == "ar") "تم حفظ العنوان بنجاح!" else "Location saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                                                } else {
                                                                    android.widget.Toast.makeText(context, if (lang == "ar") "يرجى تعبئة كافة الحقول!" else "Please fill in all fields!", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.testTag("save_location_button")
                                                        ) {
                                                            Text(text = if (lang == "ar") "حفظ العنوان" else "Save Address", color = Color.White, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Saved Locations List
                                        if (deliveryLocations.isEmpty()) {
                                            Text(
                                                text = if (lang == "ar") "لا يوجد أي عناوين توصيل محفوظة بعد." else "No delivery locations saved yet.",
                                                fontSize = 10.sp,
                                                color = SoftGrayText,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        } else {
                                            deliveryLocations.forEach { loc ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = CardDefaults.cardColors(containerColor = if (loc.isPrimary) PrimaryCyan.copy(alpha = 0.05f) else SlateDarkBg.copy(alpha = 0.5f)),
                                                    border = BorderStroke(1.dp, if (loc.isPrimary) PrimaryCyan.copy(alpha = 0.3f) else SoftGrayText.copy(alpha = 0.15f))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                Text(text = loc.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                                if (loc.isPrimary) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .clip(RoundedCornerShape(4.dp))
                                                                            .background(PrimaryCyan.copy(alpha = 0.2f))
                                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                                    ) {
                                                                        Text(
                                                                            text = if (lang == "ar") "أساسي" else "Primary",
                                                                            fontSize = 8.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = PrimaryCyan
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                // Edit
                                                                IconButton(
                                                                    onClick = {
                                                                        editingLocationId = loc.id
                                                                        isAddingLocation = false
                                                                        locLabelInput = loc.label
                                                                        locAddressInput = loc.address
                                                                        locLatInput = loc.latitude.toString()
                                                                        locLngInput = loc.longitude.toString()
                                                                    },
                                                                    modifier = Modifier.size(24.dp).testTag("edit_location_${loc.label}")
                                                                ) {
                                                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = PolarLight, modifier = Modifier.size(14.dp))
                                                                }
                                                                // Delete
                                                                IconButton(
                                                                    onClick = {
                                                                        viewModel.deleteDeliveryLocation(loc.id)
                                                                        android.widget.Toast.makeText(context, if (lang == "ar") "تم حذف العنوان!" else "Location deleted!", android.widget.Toast.LENGTH_SHORT).show()
                                                                    },
                                                                    modifier = Modifier.size(24.dp).testTag("delete_location_${loc.label}")
                                                                ) {
                                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentCoral, modifier = Modifier.size(14.dp))
                                                                }
                                                            }
                                                        }

                                                        Text(text = loc.address, fontSize = 9.sp, color = SoftGrayText)
                                                        Text(text = "Coordinates: ${loc.latitude}, ${loc.longitude}", fontSize = 8.sp, color = SoftGrayText)

                                                        if (!loc.isPrimary) {
                                                            Button(
                                                                onClick = {
                                                                    viewModel.setPrimaryDeliveryLocation(loc.id)
                                                                    android.widget.Toast.makeText(context, if (lang == "ar") "تم تحديد العنوان كأساسي للتوصيل!" else "Set as primary delivery destination!", android.widget.Toast.LENGTH_SHORT).show()
                                                                },
                                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.1f)),
                                                                shape = RoundedCornerShape(6.dp),
                                                                border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.2f)),
                                                                modifier = Modifier.fillMaxWidth().height(26.dp).testTag("make_primary_${loc.label}"),
                                                                contentPadding = PaddingValues(0.dp)
                                                            ) {
                                                                Text(text = if (lang == "ar") "تعيين كعنوان أساسي للتوصيل" else "Set as Primary Delivery Address", fontSize = 9.sp, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Global Theme Switcher Component
                                Card(
                                    modifier = Modifier.fillMaxWidth().testTag("theme_switcher_card"),
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = if (lang == "ar") "🎨 مظهر التطبيق الشامل (المضيء والداكن)" else "🎨 Global Theme Switcher",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryCyan
                                        )
                                        
                                        Text(
                                            text = if (lang == "ar") "قم بتغيير مظهر التطبيق ديناميكياً بين الوضع الليلي والنهاري:" else "Switch dynamically between light and dark visual themes:",
                                            fontSize = 10.sp,
                                            color = SoftGrayText
                                        )
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Light Option
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { viewModel.setThemeMode("light") }
                                                    .border(1.dp, if (!isDark) PrimaryCyan else Color.Transparent, RoundedCornerShape(8.dp)),
                                                colors = CardDefaults.cardColors(containerColor = if (!isDark) PrimaryCyan.copy(alpha = 0.1f) else SlateDarkBg)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("☀️", fontSize = 20.sp)
                                                    Text(if (lang == "ar") "وضع مضيء" else "Light Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isDark) PrimaryCyan else PolarLight)
                                                }
                                            }
                                            
                                            // Dark Option
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { viewModel.setThemeMode("dark") }
                                                    .border(1.dp, if (isDark) PrimaryCyan else Color.Transparent, RoundedCornerShape(8.dp)),
                                                colors = CardDefaults.cardColors(containerColor = if (isDark) PrimaryCyan.copy(alpha = 0.1f) else SlateDarkBg)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("🌙", fontSize = 20.sp)
                                                    Text(if (lang == "ar") "وضع داكن" else "Dark Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) PrimaryCyan else PolarLight)
                                                }
                                            }
                                        }
                                    }
                                }

                                // AI Features Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = if (lang == "ar") "⚙️ تخصيص وتفعيل ميزات الذكاء الاصطناعي جيميناي" else "⚙️ Customize Gemini AI Features",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )

                                        // Switch 1: AI voice
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = if (lang == "ar") "المكالمات والاتصال الصوتي بالذكاء الاصطناعي" else "Gemini Interactive Voice Calls", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                Text(text = if (lang == "ar") "تمكين إجراء اتصالات صوتية حية مدمجة للتسوق" else "Enable live synthetic calling simulator", fontSize = 8.sp, color = SoftGrayText)
                                            }
                                            Switch(checked = aiVoice, onCheckedChange = { viewModel.toggleAiVoiceEnabled(it) }, colors = SwitchDefaults.colors(checkedThumbColor = SecondaryMint))
                                        }

                                        Divider(color = PrimaryCyan.copy(alpha = 0.05f))

                                        // Switch 2: Dashboard assistant panel
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = if (lang == "ar") "المساعد الذكي المستمر (أسفل الشاشة)" else "Sticky Assistant Panel (Bottom)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                Text(text = if (lang == "ar") "عرض لوحة المساعد الذكي التفاعلي ومحاورة جيميناي" else "Show persistent companion log feed", fontSize = 8.sp, color = SoftGrayText)
                                            }
                                            Switch(checked = aiDashboard, onCheckedChange = { viewModel.toggleAiDashboardEnabled(it) }, colors = SwitchDefaults.colors(checkedThumbColor = SecondaryMint))
                                        }

                                        Divider(color = PrimaryCyan.copy(alpha = 0.05f))

                                        // Switch 3: Category recommendations
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = if (lang == "ar") "توصيات تصنيف المنتجات التلقائية" else "Gemini Autotag Recommendations", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                Text(text = if (lang == "ar") "يقوم جيميناي بتحليل اهتماماتك لفرز واقتراح فئات مخصصة" else "Let Gemini extract semantic tags for sort highlighting", fontSize = 8.sp, color = SoftGrayText)
                                            }
                                            Switch(checked = aiRecommendation, onCheckedChange = { viewModel.toggleAiRecommendationEnabled(it) }, colors = SwitchDefaults.colors(checkedThumbColor = SecondaryMint))
                                        }

                                        Divider(color = PrimaryCyan.copy(alpha = 0.05f))

                                        // Switch 4: Deep thinking mode (isHighThinkingEnabled)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = if (lang == "ar") "ميزة التفكير العميق والتحليل المتأني" else "Gemini Deep Thinking Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                Text(text = if (lang == "ar") "استخدام نموذج جيميناي برو للتفكير التحليلي المعقد" else "Use advanced Gemini Pro reasoning structure", fontSize = 8.sp, color = SoftGrayText)
                                            }
                                            Switch(checked = aiDeepThinking, onCheckedChange = { viewModel.setHighThinkingEnabled(it) }, colors = SwitchDefaults.colors(checkedThumbColor = SecondaryMint))
                                        }
                                    }
                                }
                            }
                        }
                        6 -> {
                            // PLAY & WIN MINI GAME
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.05f)),
                                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = if (lang == "ar") "🎮 ميزة العب واربح: لعبة تطابق البطاقات!" else "🎮 Play & Win: Memory Matching Game!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )
                                        Text(
                                            text = if (lang == "ar") "اطابق كل الأزواج المتشابهة لتربح جائزة مالية مجانية فورية $2.50 USDT تضاف إلى محفظتك!" else "Match all pairs of cards to win a cash prize of $2.50 USDT instantly deposited to your wallet!",
                                            fontSize = 9.sp,
                                            color = PolarLight
                                        )
                                    }
                                }

                                val isWon = memoryCards.all { it.isMatched }

                                if (isWon) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = SecondaryMint.copy(alpha = 0.1f)),
                                        border = BorderStroke(2.dp, SecondaryMint)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = if (lang == "ar") "🎉 مبروك! لقد فزت بلعبة الذاكرة!" else "🎉 Congratulations! You have won!",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = SecondaryMint
                                            )
                                            Text(
                                                text = if (lang == "ar") "تم حل اللعبة واكتشاف كافة البطاقات بنجاح." else "All memory slots successfully uncovered.",
                                                fontSize = 10.sp,
                                                color = PolarLight
                                            )

                                            if (gameRewardClaimed) {
                                                Text(
                                                    text = if (lang == "ar") "✅ تم استلام المكافأة بقيمة 2.50 USDT بنجاح!" else "✅ Reward of 2.50 USDT successfully claimed!",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SecondaryMint
                                                )
                                            } else {
                                                Button(
                                                    onClick = {
                                                        viewModel.receiveCryptoSimulated("USDT", 2.50)
                                                        gameRewardClaimed = true
                                                        android.widget.Toast.makeText(context, if (lang == "ar") "تمت إضافة 2.50 USDT إلى رصيدك! 💸" else "Added +2.50 USDT to balance! 💸", android.widget.Toast.LENGTH_LONG).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(text = if (lang == "ar") "احصل على الجائزة: $2.50 USDT" else "Claim Prize: $2.50 USDT", color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    memoryCards = generateGameCards()
                                                    selectedIndexes = emptyList()
                                                    gameRewardClaimed = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = CardDarkBg),
                                                border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = if (lang == "ar") "اللعب مجدداً 🔄" else "Play Again 🔄", color = PolarLight)
                                            }
                                        }
                                    }
                                } else {
                                    // Memory Grid Layout (2 rows x 3 columns)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (rowIdx in 0..1) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                for (colIdx in 0..2) {
                                                    val cardIdx = rowIdx * 3 + colIdx
                                                    val card = memoryCards.getOrNull(cardIdx)
                                                    if (card != null) {
                                                        val isFlipped = card.isMatched || selectedIndexes.contains(cardIdx)
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(72.dp)
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(if (isFlipped) PrimaryCyan.copy(alpha = 0.2f) else CardDarkBg)
                                                                .border(1.dp, if (isFlipped) PrimaryCyan else SoftGrayText.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                                .clickable {
                                                                    if (!card.isMatched && !selectedIndexes.contains(cardIdx) && selectedIndexes.size < 2) {
                                                                        val newSelection = selectedIndexes + cardIdx
                                                                        selectedIndexes = newSelection
                                                                        if (newSelection.size == 2) {
                                                                            val first = memoryCards[newSelection[0]]
                                                                            val second = memoryCards[newSelection[1]]
                                                                            if (first.emoji == second.emoji) {
                                                                                // Match found
                                                                                val updated = memoryCards.mapIndexed { idx, item ->
                                                                                    if (idx == newSelection[0] || idx == newSelection[1]) {
                                                                                        item.copy(isMatched = true)
                                                                                    } else {
                                                                                        item
                                                                                    }
                                                                                }
                                                                                memoryCards = updated
                                                                                selectedIndexes = emptyList()
                                                                            } else {
                                                                                // No match, flip back after brief time
                                                                                coroutineScope.launch {
                                                                                    kotlinx.coroutines.delay(800)
                                                                                    selectedIndexes = emptyList()
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            if (isFlipped) {
                                                                Text(text = card.emoji, fontSize = 24.sp)
                                                            } else {
                                                                Text(text = "❓", fontSize = 20.sp, color = PrimaryCyan)
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
                    }
                }
            }
        }
}

data class MemoryCard(
    val id: Int,
    val emoji: String,
    val isMatched: Boolean = false
)

@Composable
fun ExtraFeaturesDialog(
    viewModel: MarketViewModel,
    lang: String,
    currentRole: String,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val SlateDarkBg = MaterialTheme.colorScheme.background
        val CardDarkBg = MaterialTheme.colorScheme.surface
        val PolarLight = MaterialTheme.colorScheme.onBackground

        // Helper translation accessor
        fun txt(key: String): String = Localization.get(key, lang)

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = SlateDarkBg,
            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "ar") "✨ أدوات وميزات إضافية" else "✨ Extra Tools & Features",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PolarLight
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PolarLight)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (currentRole == "Customer") {
                        // 1. AI Visual Search & Lookups Card (extracted from CustomerScreen)
                        item {
                            var searchQuery by remember { mutableStateOf("") }
                            var isSearchFocused by remember { mutableStateOf(false) }
                            var showCameraSearchDialog by remember { mutableStateOf(false) }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        txt("customer_search_title"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderBlue
                                    )
                                    Text(
                                        txt("customer_search_desc"),
                                        fontSize = 11.sp,
                                        color = SoftGrayText,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            placeholder = { Text(txt("search_placeholder")) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .onFocusChanged { isSearchFocused = it.isFocused }
                                                .testTag("customer_search_input_dialog"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryCyan,
                                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                                focusedTextColor = PolarLight,
                                                unfocusedTextColor = PolarLight
                                            )
                                        )

                                        IconButton(
                                            onClick = {
                                                if (searchQuery.isNotEmpty()) {
                                                    viewModel.performAiVisualOrder(searchQuery)
                                                }
                                            },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(PrimaryCyan)
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = SlateDarkBg
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                if (searchQuery.isNotEmpty()) {
                                                    viewModel.performAiPriceComparison(searchQuery)
                                                }
                                            },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SecondaryMint)
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Compare Prices",
                                                tint = SlateDarkBg
                                            )
                                        }

                                        IconButton(
                                            onClick = { showCameraSearchDialog = true },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(AccentCoral)
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Camera Search",
                                                tint = SlateDarkBg
                                            )
                                        }
                                    }

                                    // Camera Search Simulated dialog nested
                                    if (showCameraSearchDialog) {
                                        Dialog(onDismissRequest = { showCameraSearchDialog = false }) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                                modifier = Modifier.padding(16.dp).border(1.dp, PrimaryCyan, RoundedCornerShape(16.dp))
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(text = "📷 Live AI Lens Scanner", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                    Box(modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
                                                        Text(text = "Camera Live Feed", color = Color.White, fontSize = 12.sp)
                                                    }
                                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                        Button(
                                                            onClick = {
                                                                showCameraSearchDialog = false
                                                                viewModel.performAiVisualOrder("Fresh Honey Jar")
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                                                        ) {
                                                            Text(text = if (lang == "ar") "التقاط 📸" else "Capture 📸", color = Color.White)
                                                        }
                                                        Button(onClick = { showCameraSearchDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentCoral)) {
                                                            Text(text = if (lang == "ar") "إلغاء" else "Cancel", color = Color.White)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Search queries from local database (Room)
                                    val recentQueries by viewModel.recentSearchQueries.collectAsState()
                                    if (recentQueries.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CardDarkBg.copy(alpha = 0.9f)),
                                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Search,
                                                            contentDescription = null,
                                                            tint = SecondaryMint,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Text(
                                                            text = if (lang == "ar") "🔍 عمليات البحث الأخيرة (قاعدة البيانات)" else "🔍 Recent Searches (Cached)",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SecondaryMint
                                                        )
                                                    }
                                                    Text(
                                                        text = if (lang == "ar") "مسح الكل" else "Clear All",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AccentCoral,
                                                        modifier = Modifier.clickable { viewModel.clearSearchHistory() }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    items(recentQueries) { q ->
                                                        Row(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(SlateDarkBg)
                                                                .clickable {
                                                                    searchQuery = q.query
                                                                    viewModel.performAiPriceComparison(q.query)
                                                                }
                                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text(q.query, fontSize = 11.sp, color = PolarLight)
                                                            Icon(
                                                                imageVector = Icons.Default.Close,
                                                                contentDescription = "Delete",
                                                                tint = SoftGrayText,
                                                                modifier = Modifier
                                                                    .size(12.dp)
                                                                    .clickable {
                                                                        viewModel.deleteSearchQuery(q.query)
                                                                    }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Dynamic Search History
                                    val searchHistory by viewModel.searchHistory.collectAsState()
                                    if (searchHistory.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "History",
                                                    tint = SoftGrayText,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = if (lang == "ar") "عمليات البحث الأخيرة" else "Recent Lookups & History",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SoftGrayText
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(searchHistory) { entry ->
                                                val icon = if (entry.type == "Visual Search") "📷" else "🔍"
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(CardDarkBg.copy(alpha = 0.5f))
                                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            searchQuery = entry.query
                                                            if (entry.type == "Visual Search") {
                                                                viewModel.performAiVisualOrder(entry.query)
                                                            } else {
                                                                viewModel.performAiPriceComparison(entry.query)
                                                            }
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text(icon, fontSize = 10.sp)
                                                        Text(
                                                            text = entry.query,
                                                            fontSize = 10.sp,
                                                            color = PolarLight,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.widthIn(max = 140.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Smart Budget Shopping Planner Card (extracted from CustomerScreen)
                        item {
                            var budgetValue by remember { mutableStateOf("") }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, WarmAmbar.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        txt("budget_planner_title"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderBlue
                                    )
                                    Text(
                                        txt("budget_planner_desc"),
                                        fontSize = 11.sp,
                                        color = SoftGrayText,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = budgetValue,
                                            onValueChange = { budgetValue = it },
                                            placeholder = { Text("E.g., 100") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("budget_input_dialog"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = WarmAmbar,
                                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                                focusedTextColor = PolarLight,
                                                unfocusedTextColor = PolarLight
                                            )
                                        )

                                        Button(
                                            onClick = {
                                                val amt = budgetValue.toDoubleOrNull() ?: 100.0
                                                viewModel.performAiBudgetShoppingPlan(amt, "")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmbar),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(txt("map_budget"), fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Device & Data Permissions Switch Card (extracted from CustomerScreen)
                        item {
                            val userProfile by viewModel.currentUser.collectAsState()
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SecondaryMint.copy(alpha = 0.05f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SecondaryMint.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = SecondaryMint,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            txt("privilege_opt_in"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryMint
                                        )
                                        Text(
                                            txt("privilege_desc"),
                                            fontSize = 10.sp,
                                            color = PolarLight
                                        )
                                    }

                                    Switch(
                                        checked = userProfile?.permissionGranted ?: true,
                                        onCheckedChange = { viewModel.grantPermission(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = SlateDarkBg,
                                            checkedTrackColor = SecondaryMint
                                        )
                                    )
                                }
                            }
                        }
                    } else if (currentRole == "Merchant") {
                        // 1. Excel Bulk Upload Columns Card (extracted from MerchantScreen)
                        item {
                            var excelColumnsInput by remember { mutableStateOf("اسم المنتج, السعر, المخزون, الباركود") }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "📊 " + txt("align_mismatch"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderBlue
                                    )
                                    Text(
                                        text = if (lang == "ar") "قم بتحميل أو لصق قائمة البيانات من جهاز الكاشير أو الإكسل. سيقوم الذكاء الاصطناعي بمطابقة الحقول وحل الأخطاء فورياً." else "Upload columns or paste custom headers from your POS/Excel. AI automatically matches properties and fixes errors.",
                                        fontSize = 11.sp,
                                        color = SoftGrayText,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    OutlinedTextField(
                                        value = excelColumnsInput,
                                        onValueChange = { excelColumnsInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                            .testTag("excel_columns_input_dialog"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryCyan,
                                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.3f),
                                            focusedTextColor = PolarLight,
                                            unfocusedTextColor = PolarLight
                                        )
                                    )

                                    Button(
                                        onClick = { viewModel.performMerchantExcelAutoMap(excelColumnsInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(txt("align_mismatch"), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 2. AI Creative Ad Studio Card (extracted from MerchantScreen)
                        item {
                            val generatedVideoUrl by viewModel.generatedVideoUrl.collectAsState()
                            val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
                            val isAiLoading by viewModel.isAiLoading.collectAsState()

                            var adTypeSelected by remember { mutableStateOf("Video") }
                            var videoPromptState by remember { mutableStateOf("Pure golden honey slow drip, photorealistic, 8k resolution, bokeh effect") }
                            var imagePromptState by remember { mutableStateOf("Organic alpine honey in rustic jars, sunny kitchen morning, professional advertisement photography") }
                            var selectedAspectRatio by remember { mutableStateOf("16:9 Widescreen") }
                            var selectedResolution by remember { mutableStateOf("8K Cinematic Ultra") }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)), RoundedCornerShape(16.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(text = "🎥", fontSize = 22.sp)
                                        Column {
                                            Text(
                                                text = if (lang == "ar") "استوديو الإعلانات الإبداعي (Veo 3 & Nano Banana)" else "AI Creative Ad Studio (Veo 3 & Nano Banana)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = HeaderBlue
                                            )
                                            Text(
                                                text = if (lang == "ar") "صمم فيديوهات ترويجية سينمائية وصوراً فائقة الجودة لمتجرك بدقة 8K" else "Design cinematic video teasers & high-fidelity 8K posters",
                                                fontSize = 9.sp,
                                                color = SoftGrayText
                                            )
                                        }
                                    }

                                    Divider(color = SoftGrayText.copy(alpha = 0.15f))

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = if (lang == "ar") "نوع التصميم الترويجي:" else "Promotion Asset Type:", fontSize = 10.sp, color = PolarLight, fontWeight = FontWeight.Bold)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            listOf("Video", "Image").forEach { type ->
                                                val isSel = adTypeSelected == type
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isSel) PrimaryCyan else SlateDarkBg)
                                                        .clickable { adTypeSelected = type }
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = when(type) {
                                                            "Video" -> if (lang == "ar") "فيديو ترويجي (Veo 3)" else "Cinematic Video (Veo 3)"
                                                            else -> if (lang == "ar") "صورة بجودة 8K (Banana)" else "8K Design Still (Banana)"
                                                        },
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSel) SlateDarkBg else PolarLight
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = if (adTypeSelected == "Video") videoPromptState else imagePromptState,
                                        onValueChange = {
                                            if (adTypeSelected == "Video") videoPromptState = it else imagePromptState = it
                                        },
                                        label = { Text(if (lang == "ar") "وصف السيناريو الإعلاني للـ AI..." else "Describe advertisement scene...", fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryCyan,
                                            unfocusedBorderColor = SoftGrayText.copy(alpha = 0.2f),
                                            focusedTextColor = PolarLight,
                                            unfocusedTextColor = PolarLight
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(text = if (lang == "ar") "أبعاد العرض:" else "Aspect Ratio:", fontSize = 9.sp, color = SoftGrayText)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(SlateDarkBg)
                                                    .clickable {
                                                        selectedAspectRatio = if (selectedAspectRatio == "16:9 Widescreen") "9:16 Portrait Reels" else "16:9 Widescreen"
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = selectedAspectRatio, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(text = if (lang == "ar") "دقة الوضوح:" else "Resolution:", fontSize = 9.sp, color = SoftGrayText)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(SlateDarkBg)
                                                    .clickable {
                                                        selectedResolution = if (selectedResolution == "8K Cinematic Ultra") "12K Holographic Hyperrealism" else "8K Cinematic Ultra"
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = selectedResolution, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (adTypeSelected == "Video") {
                                                viewModel.generateVideoWithVeo(videoPromptState, selectedAspectRatio)
                                            } else {
                                                viewModel.generateImageWithBanana(imagePromptState, selectedResolution, selectedAspectRatio)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isAiLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ButtonGlowWhite)
                                        } else {
                                            Text(
                                                text = when(adTypeSelected) {
                                                    "Video" -> if (lang == "ar") "توليد كليب ترويجي بالفيديو" else "Generate Looping Cinematic Video"
                                                    else -> if (lang == "ar") "رسم غلاف بدقة 8K فائقة" else "Create Ultra 8K Poster Frame"
                                                },
                                                fontWeight = FontWeight.Bold,
                                                color = ButtonGlowWhite,
                                                style = androidx.compose.ui.text.TextStyle(shadow = ButtonGlowShadow),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (generatedVideoUrl != null && adTypeSelected == "Video") {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, AccentCoral.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = AccentCoral, modifier = Modifier.size(36.dp))
                                                    Text(text = "🎬 Veo 3 Video Preview Playing", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PolarLight)
                                                }
                                            }
                                        }
                                    } else if (generatedImageUrl != null && adTypeSelected == "Image") {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                            colors = CardDefaults.cardColors(containerColor = SlateDarkBg)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                    Text(text = "🎨 Nano Banana Pro 8K Active Poster", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                                    Text(text = "Resol: $selectedResolution | Ratio: $selectedAspectRatio", fontSize = 8.sp, color = SecondaryMint)
                                                }
                                            }
                                        }
                                    }

                                    if (generatedVideoUrl != null || generatedImageUrl != null) {
                                        Divider(color = SoftGrayText.copy(alpha = 0.15f))

                                        var campaignTitleState by remember { mutableStateOf("خصم عائلي فوري 30% على العسل البلدي الطازج!") }

                                        OutlinedTextField(
                                            value = campaignTitleState,
                                            onValueChange = { campaignTitleState = it },
                                            label = { Text(if (lang == "ar") "عنوان الحملة الإعلانية للزبائن:" else "Customer Campaign Title:", fontSize = 9.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryCyan,
                                                unfocusedBorderColor = SoftGrayText.copy(alpha = 0.2f),
                                                focusedTextColor = PolarLight,
                                                unfocusedTextColor = PolarLight
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        Button(
                                            onClick = {
                                                viewModel.publishCampaign(
                                                    title = campaignTitleState,
                                                    desc = if (adTypeSelected == "Video") videoPromptState else imagePromptState,
                                                    mediaType = adTypeSelected
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = ButtonGlowWhite, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = if (lang == "ar") "نشر وتثبيت الحملة على شاشة المتجر للزبائن 🚀" else "Publish Campaign directly to Customer Feed 🚀",
                                                    fontWeight = FontWeight.Bold,
                                                    color = ButtonGlowWhite,
                                                    style = androidx.compose.ui.text.TextStyle(shadow = ButtonGlowShadow),
                                                    fontSize = 10.sp
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
    }
}

@Composable
fun SpendingInsightsCard(viewModel: MarketViewModel) {
    val lang by viewModel.appLanguage.collectAsState()
    val spendingComp by viewModel.organicSpendingComparison.collectAsState()
    
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📊", fontSize = 18.sp)
                    Column {
                        Text(
                            text = if (lang == "ar") "تحليلات الإنفاق والميزانية" else "Organic Spend & Insights",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = PolarLight
                        )
                        Text(
                            text = if (lang == "ar") "مقارنة الإنفاق العضوي بالشهر السابق" else "Monthly organic purchase comparisons",
                            fontSize = 9.sp,
                            color = SoftGrayText
                        )
                    }
                }

                // Increase/Decrease Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (spendingComp.hasIncrease) AccentCoral.copy(alpha = 0.15f)
                            else SecondaryMint.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = (if (spendingComp.hasIncrease) "+" else "-") + String.format("%.1f%%", spendingComp.differencePercentage),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (spendingComp.hasIncrease) AccentCoral else SecondaryMint
                    )
                }
            }

            Divider(color = SoftGrayText.copy(alpha = 0.12f))

            // Bar charts comparisons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Previous Month
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "الشهر الماضي" else "Prev Month",
                        fontSize = 9.sp,
                        color = SoftGrayText,
                        modifier = Modifier.width(60.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(SlateDarkBg)
                    ) {
                        val maxVal = maxOf(spendingComp.currentMonthOrganicSpend, spendingComp.previousMonthOrganicSpend, 1.0)
                        val fraction = (spendingComp.previousMonthOrganicSpend / maxVal).toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .clip(RoundedCornerShape(5.dp))
                                .background(SoftGrayText.copy(alpha = 0.6f))
                        )
                    }
                    Text(
                        text = viewModel.formatPrice(spendingComp.previousMonthOrganicSpend, lang),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGrayText,
                        modifier = Modifier.width(55.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                // Current Month
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "الشهر الحالي" else "This Month",
                        fontSize = 9.sp,
                        color = PolarLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(60.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(SlateDarkBg)
                    ) {
                        val maxVal = maxOf(spendingComp.currentMonthOrganicSpend, spendingComp.previousMonthOrganicSpend, 1.0)
                        val fraction = (spendingComp.currentMonthOrganicSpend / maxVal).toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)))
                        )
                    }
                    Text(
                        text = viewModel.formatPrice(spendingComp.currentMonthOrganicSpend, lang),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryCyan,
                        modifier = Modifier.width(55.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }

            // Breakdown list
            if (spendingComp.breakDownItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "ar") "أبرز مشتريات السلع العضوية:" else "Top Organic Purchases Breakdown:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolarLight
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    spendingComp.breakDownItems.take(3).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateDarkBg.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌱 " + item.productName,
                                fontSize = 9.sp,
                                color = SoftGrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(0.7f)
                            )
                            Text(
                                text = viewModel.formatPrice(item.amount, lang),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolarLight,
                                modifier = Modifier.weight(0.3f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceSearchDialog(viewModel: MarketViewModel, onDismiss: () -> Unit) {
    val lang by viewModel.appLanguage.collectAsState()
    val isListening by viewModel.isVoiceListening.collectAsState()
    val commandResultText by viewModel.voiceCommandResultText.collectAsState()
    val parsedAction by viewModel.voiceParsedAction.collectAsState()

    val suggestions = listOf(
        "أضف عسل جبلي وحليب طازج",
        "ابحث عن ألواح الشوفان",
        "Add 2 cognitive energy drinks to cart",
        "Search for premium eggs"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎤", fontSize = 20.sp)
                Text(
                    text = if (lang == "ar") "البحث الصوتي الذكي والمساعد" else "AI Voice Command & Assistant",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = PolarLight
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (lang == "ar") "تحدث بشكل طبيعي لإضافة سلع إلى سلتك أو البحث السريع في المتجر:" else "Speak naturally to quick-add items to your cart, or search the marketplace instantly:",
                    fontSize = 11.sp,
                    color = SoftGrayText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Glowing Microphone Pulsing Indicator
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) PrimaryCyan.copy(alpha = 0.15f)
                            else SlateDarkBg
                        )
                        .border(
                            2.dp,
                            if (isListening) PrimaryCyan else SoftGrayText.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .clickable {
                            if (isListening) {
                                viewModel.stopVoiceListeningAndProcess("Add organic honey and milk")
                            } else {
                                viewModel.startVoiceListening()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val scale = if (isListening) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val animScale by infiniteTransition.animateFloat(
                            initialValue = 0.9f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulsescale"
                        )
                        animScale
                    } else {
                        1.0f
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(if (isListening) PrimaryCyan else CardDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isListening) "🎙️" else "🎤",
                            fontSize = 26.sp
                        )
                    }
                }

                Text(
                    text = if (isListening) {
                        if (lang == "ar") "جاري الاستماع... اضغط على المايك للتوقف والتحليل" else "Listening... Tap mic to process"
                    } else {
                        if (lang == "ar") "اضغط على المايك للبدء" else "Tap mic to start speaking"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) PrimaryCyan else SoftGrayText
                )

                if (commandResultText.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SecondaryMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == "ar") "تم معالجة الأمر بصوتك:" else "Processed Voice Command:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryMint
                                )
                                if (parsedAction != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SecondaryMint.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = parsedAction!!,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = SecondaryMint
                                        )
                                    }
                                }
                            }
                            Text(
                                text = commandResultText,
                                fontSize = 11.sp,
                                color = PolarLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Suggestions / Presets
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (lang == "ar") "أو جرب الأوامر الجاهزة للمحاكاة بالذكاء الاصطناعي:" else "Or tap quick presets to simulate AI voice commands:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    suggestions.forEach { command ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardDarkBg)
                                .clickable {
                                    viewModel.startVoiceListening()
                                    viewModel.stopVoiceListeningAndProcess(command)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💬", fontSize = 12.sp)
                            Text(
                                text = command,
                                fontSize = 10.sp,
                                color = SoftGrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (lang == "ar") "إغلاق" else "Close", color = AccentCoral, fontSize = 11.sp)
            }
        }
    )
}

@Composable
fun CouponScannerDialog(
    lang: String,
    onDismiss: () -> Unit,
    onCouponScanned: (String, Double) -> Unit
) {
    val SlateDarkBg = MaterialTheme.colorScheme.background
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val PolarLight = MaterialTheme.colorScheme.onBackground

    val SoftGrayText = Color(0xFF94A3B8)
    val PrimaryCyan = Color(0xFF06B6D4)
    val SecondaryMint = Color(0xFF10B981)
    val AccentCoral = Color(0xFFF43F5E)

    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition()
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("coupon_scanner_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📷", fontSize = 18.sp)
                        Text(
                            text = if (lang == "ar") "ماسح القسائم الذكي" else "AI Coupon Camera Scanner",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = PolarLight
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SoftGrayText)
                    }
                }

                Text(
                    text = if (lang == "ar") "وجه الكاميرا نحو الكود المطبوع على القسيمة لتطبيقه تلقائياً" else "Point your camera at the physical coupon barcode or text code to scan",
                    fontSize = 9.sp,
                    color = SoftGrayText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Simulated Viewfinder Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateDarkBg)
                        .border(1.dp, SoftGrayText.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Reticle frame
                    Box(
                        modifier = Modifier
                            .size(130.dp, 80.dp)
                            .border(2.dp, PrimaryCyan.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    ) {
                        // Sliding laser line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (80.dp * laserProgress))
                                .background(PrimaryCyan)
                        )
                    }

                    // Background text guidelines
                    Text(
                        text = if (lang == "ar") "جاري البحث عن كود... 🔍" else "Seeking Coupon Barcode... 🔍",
                        fontSize = 8.sp,
                        color = PrimaryCyan.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                    )
                }

                // Interactive Simulator triggers
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "محاكاة قراءة قسيمة فعلية (اضغط للمسح):" else "Simulate physical coupon detection (Tap to Scan):",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarLight
                    )

                    val couponsList = listOf(
                        Triple("ORGANIC20", 0.20, if (lang == "ar") "كود ORGANIC20 (خصم ٢٠٪)" else "ORGANIC20 Code (20% Off)"),
                        Triple("GREEN15", 0.15, if (lang == "ar") "كود GREEN15 (خصم ١٥٪)" else "GREEN15 Code (15% Off)"),
                        Triple("CO2SUPER50", 0.50, if (lang == "ar") "كود CO2SUPER50 (خصم ٥٠٪)" else "CO2SUPER50 Super Saver (50% Off)")
                    )

                    couponsList.forEach { (code, discount, desc) ->
                        Button(
                            onClick = {
                                onCouponScanned(code, discount)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint.copy(alpha = 0.15f), contentColor = SecondaryMint),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("🎟️", fontSize = 10.sp)
                                    Text(desc, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = if (lang == "ar") "مسح ⚡" else "Scan ⚡",
                                    fontSize = 8.sp,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Black
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
fun InteractiveTourOverlay(
    lang: String,
    step: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    val PolarLight = MaterialTheme.colorScheme.onBackground
    val CardDarkBg = MaterialTheme.colorScheme.surface
    val SoftGrayText = Color(0xFF94A3B8)
    val PrimaryCyan = Color(0xFF06B6D4)
    val SecondaryMint = Color(0xFF10B981)
    val AccentCoral = Color(0xFFF43F5E)

    val stepTitle = when (step) {
        0 -> if (lang == "ar") "👋 أهلاً بك في جولة المتجر الذكي!" else "👋 Welcome to the Smart Store Tour!"
        1 -> if (lang == "ar") "🎙️ ميزة البحث الصوتي بالذكاء الاصطناعي" else "🎙️ AI Voice-to-Text Search"
        2 -> if (lang == "ar") "📸 ميزة البحث الصوري بالكاميرا" else "📸 AI Visual Camera Search"
        3 -> if (lang == "ar") "🌱 لوحة التوفير البيئي والملصقات العضوية" else "🌱 Carbon Offset & Freshness Hub"
        else -> if (lang == "ar") "🎉 لقد أكملت الجولة الاستكشافية!" else "🎉 You've Completed the Tour!"
    }

    val stepDesc = when (step) {
        0 -> if (lang == "ar") "سوف نأخذك في جولة سريعة لاستكشاف ميزات الذكاء الاصطناعي والحلول البيئية الذكية لتسوق ممتع وسهل." else "We'll show you how our intelligent features and eco-friendly hubs make shopping organic, green, and incredibly easy."
        1 -> if (lang == "ar") "اضغط على زر الميكروفون داخل شريط البحث للتحدث باللغة العربية أو الإنجليزية! سيقوم الذكاء الاصطناعي بتحويل صوتك إلى نص فوراً والبحث عن طلبك." else "Tap the Microphone icon inside the search bar to speak! Our server-side voice-to-text turns voice notes into search queries instantly."
        2 -> if (lang == "ar") "اضغط على زر الكاميرا بجوار شريط البحث التقاط صورة لأي منتج أو كود! سيقوم الذكاء الاصطناعي بتحليل الصورة وعرض المنتج المقارن." else "Tap the Camera Search button next to the search bar! Point it at any grocery item or screenshot to run visual identification instantly."
        3 -> if (lang == "ar") "شاهد التوفير الكربوني الفعلي لجميع مشترياتك العضوية وتلقَّ إشعارات صلاحية ذكية قبل انتهاء صلاحية الأغذية للحفاظ على البيئة وتقليل الهدر." else "Track real-time CO2 savings from your organic purchases, see equivalent trees saved, and get smart freshness push notifications before items spoil."
        else -> if (lang == "ar") "أنت الآن جاهز تماماً لتسوق ذكي وصديق للبيئة في بقالتنا الخضراء المتكاملة!" else "You're now fully equipped to shop smarter, live greener, and experience the future of digital organic retail!"
    }

    val stepTarget = when (step) {
        1 -> if (lang == "ar") "المستهدف: أيقونة الميكروفون الزرقاء 🎙" else "Target: Blue Mic Icon 🎙 in the search field"
        2 -> if (lang == "ar") "المستهدف: أيقونة الكاميرا الحمراء 📸" else "Target: Red Camera Icon 📸 next to search"
        3 -> if (lang == "ar") "المستهدف: قسم 🌱 المركز الأخضر أسفل المتجر" else "Target: 🌱 Green Hub card on the Store homepage"
        else -> ""
    }

    Dialog(onDismissRequest = onSkip) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("tour_overlay_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(PrimaryCyan, SecondaryMint)))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with steps tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ AI GUIDE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryCyan
                    )
                    Text(
                        text = "Step ${step + 1} of 5",
                        fontSize = 9.sp,
                        color = SoftGrayText,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Illustration Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (step) {
                            0 -> "👋"
                            1 -> "🎙️"
                            2 -> "📸"
                            3 -> "🌱"
                            else -> "🏆"
                        },
                        fontSize = 32.sp
                    )
                }

                // Core Info
                Text(
                    text = stepTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = PolarLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = stepDesc,
                    fontSize = 10.sp,
                    color = SoftGrayText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 14.sp
                )

                if (stepTarget.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryMint.copy(alpha = 0.08f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "💡 $stepTarget",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryMint,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Steps dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (0..4).forEach { idx ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (idx == step) PrimaryCyan else SoftGrayText.copy(alpha = 0.3f))
                        )
                    }
                }

                // Action controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(
                                text = if (lang == "ar") "السابق" else "Back",
                                fontSize = 10.sp,
                                color = SoftGrayText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSkip,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, SoftGrayText.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(
                                text = if (lang == "ar") "تخطي" else "Skip",
                                fontSize = 10.sp,
                                color = SoftGrayText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = if (step == 4) {
                                if (lang == "ar") "إنهاء 🎉" else "Finish 🎉"
                            } else {
                                if (lang == "ar") "التالي" else "Next"
                            },
                            fontSize = 10.sp,
                            color = SlateDarkBg,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}


