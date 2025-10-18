package com.momentum.app.feature_wellbeing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.navigation.Routes

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
                label = "Inicio",
                isSelected = currentRoute == Routes.PuenteEmocional.name,
                onClick = { navController.navigate(Routes.PuenteEmocional.name) }
            )
            
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Diario",
                isSelected = currentRoute == Routes.Diario.name,
                onClick = { navController.navigate(Routes.Diario.name) }
            )
            
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                label = "Chat",
                isSelected = currentRoute == Routes.Chat.name,
                onClick = { navController.navigate(Routes.Chat.name) }
            )
            
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Progreso",
                isSelected = currentRoute == Routes.Progreso.name,
                onClick = { navController.navigate(Routes.Progreso.name) }
            )
            
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp)
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