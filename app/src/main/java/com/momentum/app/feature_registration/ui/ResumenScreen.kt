package com.momentum.app.feature_registration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.momentum.app.feature_registration.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    onBackClick: (() -> Unit)? = null
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Resumen del Registro", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Nombre: ${estado.nombre}")
        Text(text = "Correo: ${estado.correo}")
        Text(text = "Dirección: ${estado.direccion}")
        Text(text = "Contraseña: ${"*".repeat(n = estado.clave.length)}")
        Text(text = "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")
        
        onBackClick?.let {
            Button(
                onClick = it,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al Registro")
            }
        }
    }
}

