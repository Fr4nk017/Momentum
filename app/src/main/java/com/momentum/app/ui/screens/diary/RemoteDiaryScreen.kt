package com.momentum.app.ui.screens.diary

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
import com.momentum.app.ui.viewmodel.DiaryUiState
import com.momentum.app.ui.viewmodel.RemoteDiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteDiaryScreen(
    viewModel: RemoteDiaryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarHistorial()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diario (Backend)") }
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        if (editingId != null) {
                            viewModel.actualizarEntrada(editingId!!, title, content)
                            editingId = null
                        } else {
                            viewModel.enviarEntrada(title, content)
                        }
                        title = ""
                        content = ""
                    }
                }
            ) {
                Text(if (editingId != null) "Actualizar" else "Guardar en diario (backend)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                DiaryUiState.Idle -> {
                    Text("Escribe una entrada y guarda para ver el historial.")
                }
                DiaryUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DiaryUiState.Error -> {
                    Text("Error: ${state.message}")
                }
                is DiaryUiState.Success -> {
                    Text("Historial:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        items(state.entries) { entry ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
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
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = entry.content,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Fecha: ${entry.date}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                entry.id?.let { id ->
                                                    editingId = id
                                                    title = entry.title
                                                    content = entry.content
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(
                                            onClick = {
                                                entry.id?.let { id ->
                                                    showDeleteDialog = id
                                                }
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
    showDeleteDialog?.let { entryId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar entrada") },
            text = { Text("¿Estás seguro de que deseas eliminar esta entrada del diario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarEntrada(entryId)
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
