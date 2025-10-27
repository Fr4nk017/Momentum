package com.momentum.app.ui.screens.community

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.ui.animations.*
import java.text.SimpleDateFormat
import java.util.*

data class CommunityPost(
    val id: Long,
    val userName: String,
    val content: String,
    val emotionalState: String,
    val likes: Int,
    val comments: Int,
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    var showNewPostDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Simulate loading
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500)
        isLoading = false
    }
    
    val posts = remember {
        mutableStateListOf(
            CommunityPost(
                1,
                "María García",
                "Hoy completé mi primera semana de meditación diaria. Me siento más tranquila y centrada. ¡Gracias a esta comunidad por el apoyo! 🧘‍♀️",
                "Tranquilo",
                12,
                3,
                System.currentTimeMillis() - 3600000
            ),
            CommunityPost(
                2,
                "Carlos López",
                "Después de un día difícil, el ejercicio de respiración 4-4-6 me ayudó mucho. Es increíble cómo algo tan simple puede cambiar tu perspectiva.",
                "Ansioso",
                8,
                2,
                System.currentTimeMillis() - 7200000
            ),
            CommunityPost(
                3,
                "Ana Martínez",
                "Compartiendo mi progreso: 30 días escribiendo en mi diario. Ha sido transformador ver cómo he crecido emocionalmente. 💚",
                "Feliz",
                15,
                5,
                System.currentTimeMillis() - 10800000
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidad") },
                actions = {
                    IconButton(onClick = { showNewPostDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewPostDialog = true },
                modifier = Modifier.animatedScale(delay = 300)
            ) {
                Icon(Icons.Default.Create, contentDescription = "Nueva publicación")
            }
        }
    ) { padding ->
        if (isLoading) {
            // Loading state with skeleton cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(5) { index ->
                    SkeletonCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .animatedFadeIn(delay = getStaggeredDelay(index))
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WelcomeCard(modifier = Modifier.animatedSlideUp(duration = 600))
                }
                
                itemsIndexed(posts) { index, post ->
                    PostCard(
                        post = post,
                        modifier = Modifier.animatedFadeIn(delay = getStaggeredDelay(index, 80))
                    )
                }
            }
        }
    }
    
    if (showNewPostDialog) {
        NewPostDialog(
            onDismiss = { showNewPostDialog = false },
            onPost = { content, state ->
                posts.add(
                    0,
                    CommunityPost(
                        id = posts.size + 1L,
                        userName = "Tu nombre",
                        content = content,
                        emotionalState = state,
                        likes = 0,
                        comments = 0,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showNewPostDialog = false
            }
        )
    }
}

@Composable
fun WelcomeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Bienvenido a la Comunidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Comparte tu viaje de bienestar y conecta con otros",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PostCard(post: CommunityPost, modifier: Modifier = Modifier) {
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(post.likes) }
    val scale = rememberPulseAnimation()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTimestamp(post.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getStateColor(post.emotionalState)
                ) {
                    Text(
                        text = post.emotionalState,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            // Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Divider()
            
            // Actions
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = {
                        liked = !liked
                        likeCount = if (liked) likeCount + 1 else likeCount - 1
                    },
                    modifier = if (liked) Modifier.scale(scale) else Modifier
                ) {
                    Icon(
                        if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Me gusta",
                        modifier = Modifier.size(18.dp),
                        tint = if (liked) MaterialTheme.colorScheme.error else LocalContentColor.current
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$likeCount")
                }
                
                TextButton(onClick = { /* Open comments */ }) {
                    Icon(
                        Icons.Default.MailOutline,
                        contentDescription = "Comentarios",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.comments}")
                }
                
                TextButton(onClick = { /* Share */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Compartir",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewPostDialog(onDismiss: () -> Unit, onPost: (String, String) -> Unit) {
    var content by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Tranquilo") }
    val states = listOf("Feliz", "Tranquilo", "Ansioso", "Triste", "Irritado", "Con energía")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva publicación") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("¿Qué quieres compartir?") },
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Estado emocional:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    states.take(3).forEach { state ->
                        FilterChip(
                            selected = selectedState == state,
                            onClick = { selectedState = state },
                            label = { Text(state, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    states.drop(3).forEach { state ->
                        FilterChip(
                            selected = selectedState == state,
                            onClick = { selectedState = state },
                            label = { Text(state, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPost(content, selectedState) },
                enabled = content.isNotBlank()
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun getStateColor(state: String): androidx.compose.ui.graphics.Color {
    return when (state) {
        "Feliz" -> MaterialTheme.colorScheme.primaryContainer
        "Tranquilo" -> MaterialTheme.colorScheme.secondaryContainer
        "Ansioso" -> MaterialTheme.colorScheme.tertiaryContainer
        "Triste" -> MaterialTheme.colorScheme.errorContainer
        "Irritado" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Hace un momento"
        diff < 3600000 -> "Hace ${diff / 60000} minutos"
        diff < 86400000 -> "Hace ${diff / 3600000} horas"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}