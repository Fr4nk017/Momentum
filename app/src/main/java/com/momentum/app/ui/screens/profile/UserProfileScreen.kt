package com.momentum.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.ui.animations.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController, userId: Long = 1L) {
    val scrollState = rememberScrollState()
    var isEditing by remember { mutableStateOf(false) }
    var bio by remember { mutableStateOf("Explorando mi viaje de bienestar emocional 🌱") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Guardar" else "Editar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .animatedScale(duration = 600),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Juan Pérez",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.animatedFadeIn(delay = 200)
            )
            
            Text(
                text = "juan@ejemplo.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.animatedFadeIn(delay = 300)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedSlideUp(delay = 400),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(value = "24", label = "Publicaciones")
                StatCard(value = "15", label = "Amigos")
                StatCard(value = "45", label = "Días activo")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bio
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedFadeIn(delay = 500),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Biografía",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Biografía",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (isEditing) {
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    } else {
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick actions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedSlideUp(delay = 600),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Acciones rápidas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Default.Person,
                        text = "Ver mis amigos",
                        onClick = { navController.navigate("friends") }
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Default.Add,
                        text = "Buscar amigos",
                        onClick = { navController.navigate("search_friends") }
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Default.Email,
                        text = "Mis publicaciones",
                        onClick = { /* Navigate to posts */ }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProfileActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}