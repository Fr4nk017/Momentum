package com.momentum.app.model.forms

data class LoginForm(
    val email: String = "",
    val password: String = ""
) : FormBase
