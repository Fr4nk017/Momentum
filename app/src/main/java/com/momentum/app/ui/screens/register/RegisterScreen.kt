package com.momentum.app.ui.screens.register

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
fun RegisterScreen(onSuccess: () -> Unit, viewModel: RegisterViewModel = viewModel()) {
    val form = viewModel.form.collectAsState()
    val errors = viewModel.errors.collectAsState()

    Column {
        OutlinedTextField(
            value = form.value.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.value.name != null
        )
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
        OutlinedTextField(
            value = form.value.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.value.confirmPassword != null
        )

        Button(onClick = { viewModel.submit(onSuccess = onSuccess, onFailure = {}) }) {
            Text("Register")
        }
    }
}
