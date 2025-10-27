package com.momentum.app.ui.screens.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.ui.animations.*

@Composable
fun PasswordRecoveryScreen(
    onBackToLogin: () -> Unit,
    viewModel: PasswordRecoveryViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val isEmailSent by viewModel.isEmailSent.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logo/Título
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .animatedFadeIn(delay = 0)
                .animatedScale(delay = 0)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (!isEmailSent) {
            // Formulario para enviar email
            Text(
                text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedSlideUp(delay = 120)
                    .animatedFadeIn(delay = 120),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = form.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        placeholder = { Text("tu@email.com") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Button(
                        onClick = { viewModel.sendRecoveryEmail() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .bounceClick(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = form.email.isNotBlank()
                    ) {
                        Text(
                            text = "Enviar Enlace",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Confirmación de email enviado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedSlideUp(delay = 120)
                    .animatedFadeIn(delay = 120),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("✉️", style = MaterialTheme.typography.displayMedium)
                    
                    Text(
                        text = "Correo Enviado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Text(
                        text = "Hemos enviado un enlace de recuperación a ${form.email}. Revisa tu bandeja de entrada y spam.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Button(
                        onClick = onBackToLogin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick()
                    ) {
                        Text("Volver al Login", color = Color.White)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Información adicional
        if (!isEmailSent) {
            Text(
                text = "¿Recordaste tu contraseña?",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "Volver al Login",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
