package com.momentum.app.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class Friend(
    val id: Long,
    val name: String,
    val email: String,
    val status: String = "accepted"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Amigos", "Solicitudes")
    
    val friends = remember {
        mutableStateListOf(
            Friend(1, "María García", "maria@ejemplo.com"),
            Friend(2, "Carlos López", "carlos@ejemplo.com"),
            Friend(3, "Ana Martínez", "ana@ejemplo.com")
        )
    }
    
    val pendingRequests = remember {
        mutableStateListOf(
            Friend(4, "Pedro Sánchez", "pedro@ejemplo.com", "pending"),
            Friend(5, "Laura Torres", "laura@ejemplo.com", "pending")
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Amigos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search_friends") }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Buscar amigos")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> FriendsList(friends = friends)
                1 -> PendingRequestsList(
                    requests = pendingRequests,
                    onAccept = { request ->
                        pendingRequests.remove(request)
                        friends.add(request.copy(status = "accepted"))
                    },
                    onReject = { request ->
                        pendingRequests.remove(request)
                    }
                )
            }
        }
    }
}

@Composable
fun FriendsList(friends: List<Friend>) {
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        isLoading = false
    }
    
    if (isLoading) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) { index ->
                SkeletonCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .animatedFadeIn(delay = getStaggeredDelay(index))
                )
            }
        }
    } else if (friends.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aún no tienes amigos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(friends) { index, friend ->
                FriendCard(
                    friend = friend,
                    modifier = Modifier.animatedSlideUp(delay = getStaggeredDelay(index, 70))
                )
            }
        }
    }
}

@Composable
fun PendingRequestsList(
    requests: List<Friend>,
    onAccept: (Friend) -> Unit = {},
    onReject: (Friend) -> Unit = {}
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay solicitudes pendientes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(requests) { index, request ->
                PendingRequestCard(
                    request = request,
                    onAccept = { onAccept(request) },
                    onReject = { onReject(request) },
                    modifier = Modifier.animatedFadeIn(delay = getStaggeredDelay(index, 70))
                )
            }
        }
    }
}

@Composable
fun FriendCard(friend: Friend, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = friend.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { /* Send message */ }) {
                Icon(Icons.Default.Email, contentDescription = "Enviar mensaje")
            }
        }
    }
}

@Composable
fun PendingRequestCard(
    request: Friend,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .swipeToDismiss(onDismiss = onReject),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
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
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Solicitud de amistad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onAccept,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Aceptar")
                }
                
                IconButton(
                    onClick = onReject,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar")
                }
            }
        }
    }
}