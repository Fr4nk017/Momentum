package com.momentum.app.model.forms

data class RegisterForm(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) : FormBase
