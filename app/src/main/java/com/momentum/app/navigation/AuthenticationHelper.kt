package com.momentum.app.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class AuthenticationViewModel : ViewModel() {
    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: State<Boolean> = _isAuthenticated
    
    fun login() {
        _isAuthenticated.value = true
    }
    
    fun logout() {
        _isAuthenticated.value = false
    }
}
