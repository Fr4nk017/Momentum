package com.momentum.app.ui.screens.moods

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

            Button(
                onClick = {
                    if (emotion.isNotBlank()) {
                        viewModel.enviarMood(emotion, note.ifBlank { null })
                    }
                }
            ) {
                Text("Guardar en backend")
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
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Emoción: ${mood.emotion}")
                                    if (!mood.note.isNullOrBlank()) {
                                        Text("Nota: ${mood.note}")
                                    }
                                    Text("Fecha: ${mood.date}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
