package com.momentum.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.navigation.Routes
import androidx.compose.ui.res.stringResource
import com.momentum.app.R

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = stringResource(id = R.string.nav_puente_emocional),
                isSelected = currentRoute == Routes.PuenteEmocional.name,
                onClick = { navController.navigate(Routes.PuenteEmocional.name) }
            )
            
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = stringResource(id = R.string.nav_diario),
                isSelected = currentRoute == Routes.Diario.name,
                onClick = { navController.navigate(Routes.Diario.name) }
            )
            
            BottomNavItem(
                icon = Icons.Default.Park,
                label = "Aire Libre",
                isSelected = currentRoute == Routes.OutdoorActivities.name,
                onClick = { navController.navigate(Routes.OutdoorActivities.name) }
            )
            
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = stringResource(id = R.string.nav_progreso),
                isSelected = currentRoute == Routes.Progreso.name,
                onClick = { navController.navigate(Routes.Progreso.name) }
            )
            
            BottomNavItem(
                icon = Icons.Default.Person,
                label = stringResource(id = R.string.nav_perfil),
                isSelected = currentRoute == Routes.Perfil.name,
                onClick = { navController.navigate(Routes.Perfil.name) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navItemScale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(4.dp)
            .scale(scale)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}