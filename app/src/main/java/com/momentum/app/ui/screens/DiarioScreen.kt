package com.momentum.app.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.navigation.Routes
import com.momentum.app.ui.animations.bounceClick
import com.momentum.app.viewmodel.DiaryViewModel
import com.momentum.app.ui.viewmodel.RemoteDiaryViewModel
import com.momentum.app.ui.viewmodel.DiaryUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(
    navController: NavController,
    viewModel: BienestarViewModel,
    diaryViewModel: DiaryViewModel = viewModel(),
    remoteDiaryViewModel: RemoteDiaryViewModel? = null
) {
    val estado by viewModel.estado.collectAsState()
    val diaryState by diaryViewModel.uiState.collectAsState()
    val remoteState by (remoteDiaryViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(DiaryUiState.Idle) })
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(estado.perfil.correo) {
        if (estado.perfil.correo.isNotEmpty()) {
            diaryViewModel.initializeUser(estado.perfil.correo)
            remoteDiaryViewModel?.cargarHistorial()
        }
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(diaryState.errorMessage) {
        diaryState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            diaryViewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DiaryHeader(
                totalEntries = diaryState.totalEntries,
                latestMood = diaryState.latestEntry?.moodEmoji
            )

            MoodSelector(
                moods = diaryViewModel.availableMoods,
                selectedMood = diaryState.selectedMood,
                onMoodSelected = { diaryViewModel.selectMood(it) }
            )

            DiaryContentInput(
                content = diaryState.currentContent,
                onContentChange = { diaryViewModel.updateContent(it) },
                isLoading = diaryState.isLoading
            )

            Button(
                onClick = { 
                    diaryViewModel.saveEntry()
                    // También enviar al backend si está disponible
                    remoteDiaryViewModel?.let { remote ->
                        if (diaryState.selectedMood != null && diaryState.currentContent.isNotBlank()) {
                            remote.enviarEntrada(
                                title = "Entrada ${diaryState.selectedMood}",
                                content = diaryState.currentContent
                            )
                        }
                    }
                },
                enabled = !diaryState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick()
            ) {
                if (diaryState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Entrada", fontWeight = FontWeight.Bold)
                }
            }

            if (diaryState.entries.isNotEmpty()) {
                Text(
                    "Entradas anteriores (${diaryState.entries.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(diaryState.entries, key = { it.id }) { entry ->
                        DiaryEntryCard(
                            entry = entry,
                            onDelete = { diaryViewModel.deleteEntry(entry) }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No hay entradas aún",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Mostrar entradas del backend si está disponible
            remoteDiaryViewModel?.let {
                when (val remote = remoteState) {
                    is DiaryUiState.Success -> {
                        if (remote.entries.isNotEmpty()) {
                            Text(
                                "Entradas en el servidor (${remote.entries.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                items(remote.entries) { entry ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = entry.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = entry.content,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "📅 ${entry.date}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                                )
                                            }
                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        entry.id?.let { id ->
                                                            // TODO: Implementar edición de entrada remota
                                                            // remoteDiaryViewModel.editarEntradaRemota(entry)
                                                        }
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Editar entrada")
                                                }
                                                IconButton(
                                                    onClick = {
                                                        entry.id?.let { id ->
                                                            showDeleteDialog = id
                                                        }
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar entrada")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is DiaryUiState.Error -> {
                        Text(
                            "Error al cargar del servidor: ${remote.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    DiaryUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    else -> {}
                }
            }

            BottomNavigationBar(
                currentRoute = Routes.Diario.name,
                navController = navController
            )
        }
    }
    
    // Diálogo de confirmación para eliminar entrada del servidor
    showDeleteDialog?.let { entryId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar entrada del servidor") },
            text = { Text("¿Estás seguro de que deseas eliminar esta entrada del servidor?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        remoteDiaryViewModel?.eliminarEntrada(entryId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DiaryHeader(
    totalEntries: Int,
    latestMood: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Mi Diario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$totalEntries entradas registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            if (latestMood != null) {
                Text(
                    latestMood,
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    }
}

@Composable
fun MoodSelector(
    moods: List<String>,
    selectedMood: String,
    onMoodSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "¿Cómo te sientes hoy?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(moods) { mood ->
                MoodButton(
                    emoji = mood,
                    isSelected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
fun MoodButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "mood_scale"
    )
    
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun DiaryContentInput(
    content: String,
    onContentChange: (String) -> Unit,
    isLoading: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "¿Qué sucedió hoy?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            placeholder = { 
                Text("Escribe tus pensamientos, reflexiones o lo que quieras recordar...") 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            enabled = !isLoading,
            maxLines = 8,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun DiaryEntryCard(
    entry: DiaryEntryEntity,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.moodEmoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Column {
                        Text(
                            text = formatDate(entry.createdAt),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTime(entry.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            HorizontalDivider()
            
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar entrada") },
            text = { Text("¿Estás seguro de que deseas eliminar esta entrada del diario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
