package com.momentum.app.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.momentum.app.ui.theme.MomentumTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MomentumTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Ejemplo: volver a MainActivity o abrir la pantalla principal
                        // val intent = Intent(this, MainActivity::class.java)
                        // startActivity(intent)
                        finish()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}
