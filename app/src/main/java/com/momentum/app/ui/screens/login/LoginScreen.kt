package com.momentum.app.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(onSuccess: () -> Unit, onNavigateRegister: () -> Unit, viewModel: LoginViewModel = viewModel()) {
    val form = viewModel.form.collectAsState()
    val errors = viewModel.errors.collectAsState()

    Column {
        OutlinedTextField(
            value = form.value.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.value.email != null
        )
        OutlinedTextField(
            value = form.value.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.value.password != null
        )

        Button(onClick = { viewModel.submit(onSuccess = onSuccess, onFailure = {}) }) {
            Text("Login")
        }

        Button(onClick = onNavigateRegister) {
            Text("Register")
        }
    }
}
