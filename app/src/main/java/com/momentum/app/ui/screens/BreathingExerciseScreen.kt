package com.momentum.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun BreathingAnimation() {
    var isInhaling by remember { mutableStateOf(true) }
    var scale by remember { mutableStateOf(1f) }
    
    val animatable = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            // Inhalar - 4 segundos
            isInhaling = true
            animatable.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(durationMillis = 4000)
            )
            
            // Mantener - 4 segundos
            delay(4000)
            
            // Exhalar - 6 segundos
            isInhaling = false
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 6000)
            )
        }
    }
    
    scale = animatable.value
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(200.dp)
                .scale(scale),
            shape = CircleShape,
            color = if (isInhaling) Color(0xFF4CAF50) else Color(0xFF2196F3)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isInhaling) "Inhala" else "Exhala",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
