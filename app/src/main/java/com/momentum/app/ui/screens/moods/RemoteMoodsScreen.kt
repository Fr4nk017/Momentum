package com.momentum.app.ui.screens.moods

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.momentum.app.ui.viewmodel.MoodUiState
import com.momentum.app.ui.viewmodel.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteMoodsScreen(
    viewModel: MoodViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var emotion by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarHistorial()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado emocional (Backend)") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "UserId actual: ${viewModel.userId}",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = emotion,
                onValueChange = { emotion = it },
                label = { Text("Emoción (ej: Feliz)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Nota") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (emotion.isNotBlank()) {
                            if (editingId != null) {
                                viewModel.actualizarMood(editingId!!, emotion, note.ifBlank { null })
                                editingId = null
                            } else {
                                viewModel.enviarMood(emotion, note.ifBlank { null })
                            }
                            emotion = ""
                            note = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingId != null) "Actualizar" else "Guardar en backend")
                }
                
                if (editingId != null) {
                    OutlinedButton(
                        onClick = {
                            editingId = null
                            emotion = ""
                            note = ""
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                MoodUiState.Idle -> {
                    Text("Ingresa una emoción y guarda para ver el historial.")
                }
                MoodUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is MoodUiState.Error -> {
                    Text("Error: ${state.message}")
                }
                is MoodUiState.Success -> {
                    Text("Historial:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        items(state.moods) { mood ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Emoción: ${mood.emotion}")
                                        if (!mood.note.isNullOrBlank()) {
                                            Text("Nota: ${mood.note}")
                                        }
                                        Text("Fecha: ${mood.date}")
                                    }
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                mood.id?.let { id ->
                                                    editingId = id
                                                    emotion = mood.emotion
                                                    note = mood.note ?: ""
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        
                                        IconButton(
                                            onClick = {
                                                mood.id?.let { showDeleteDialog = it }
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
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

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { moodId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar entrada") },
            text = { Text("¿Estás seguro de que deseas eliminar esta entrada de mood?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarMood(moodId)
                        showDeleteDialog = null
                    }
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
