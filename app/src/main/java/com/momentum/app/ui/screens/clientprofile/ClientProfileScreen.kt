package com.momentum.app.ui.screens.clientprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.momentum.app.viewmodel.ClientProfileViewModel
import com.momentum.app.navigation.AuthRoutes

/**
 * Pantalla para capturar el perfil extendido del cliente.
 * Al guardar valida y navega a Home (Routes.PuenteEmocional / HOME según NavGraph).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(navController: NavController, viewModel: ClientProfileViewModel = viewModel()) {
    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val scroll = rememberScrollState()

    // Dropdown states
    var sexoExpanded by remember { mutableStateOf(false) }
    var estadoExpanded by remember { mutableStateOf(false) }
    val sexoOptions = listOf("Masculino", "Femenino", "Otro")
    val estadoOptions = listOf("Soltero", "Casado", "Viudo", "Divorciado")

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(scroll)) {

        Text(text = "Datos personales", modifier = Modifier.padding(bottom = 8.dp))

        OutlinedTextField(
            value = form.nombre,
            onValueChange = { viewModel.onChange("nombre", it) },
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Nombre") },
            isError = errors.containsKey("nombre"),
            modifier = Modifier.fillMaxWidth()
        )
        if (errors.containsKey("nombre")) Text(errors["nombre"]!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)

        OutlinedTextField(
            value = form.edad?.toString() ?: "",
            onValueChange = { viewModel.onChange("edad", it) },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        if (errors.containsKey("edad")) Text(errors["edad"]!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)

        // Sexo dropdown (read-only TextField + menu)
        OutlinedTextField(
            value = form.sexo,
            onValueChange = { /* no-op, use menu */ },
            readOnly = true,
            label = { Text("Sexo") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = { sexoExpanded = !sexoExpanded }) {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar sexo")
                }
            }
        )
        DropdownMenu(expanded = sexoExpanded, onDismissRequest = { sexoExpanded = false }) {
            sexoOptions.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = {
                    viewModel.onChange("sexo", opt)
                    sexoExpanded = false
                })
            }
        }

        // Estado civil dropdown (read-only + menu)
        OutlinedTextField(
            value = form.estadoCivil,
            onValueChange = { /* no-op */ },
            readOnly = true,
            label = { Text("Estado civil") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = { estadoExpanded = !estadoExpanded }) {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar estado civil")
                }
            }
        )
        DropdownMenu(expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }) {
            estadoOptions.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = {
                    viewModel.onChange("estadoCivil", opt)
                    estadoExpanded = false
                })
            }
        }

        OutlinedTextField(
            value = form.ocupacion,
            onValueChange = { viewModel.onChange("ocupacion", it) },
            label = { Text("Ocupación / Profesión") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = form.email,
            onValueChange = { viewModel.onChange("email", it) },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        if (errors.containsKey("email")) Text(errors["email"]!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)

        OutlinedTextField(
            value = form.telefono,
            onValueChange = { viewModel.onChange("telefono", it) },
            label = { Text("Teléfono (opcional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(onClick = {
            if (viewModel.validate()) {
                // Guardar (en el ViewModel ya está) y navegar al home
                navController.navigate(AuthRoutes.HOME)
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Guardar y continuar")
        }
    }
}
