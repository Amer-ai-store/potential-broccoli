package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.ProductEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchBar(onCameraClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = onCameraClick) {
                Icon(Icons.Default.Build, contentDescription = "Visual Search")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ProductCard(product: ProductEntity) {
    Card(
        modifier = Modifier.padding(8.dp).width(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = "$${product.retailPrice}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun SkeletonItem() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )
    Card(
        modifier = Modifier.padding(8.dp).width(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.Gray.copy(alpha = alpha))
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).background(Color.Gray.copy(alpha = alpha)))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp).background(Color.Gray.copy(alpha = alpha)))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductGrid(products: List<ProductEntity>, isLoading: Boolean) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            repeat(6) { SkeletonItem() }
        } else {
            products.forEach { product ->
                ProductCard(product)
            }
        }
    }
}
