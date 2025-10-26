package com.momentum.app.model.forms

data class ClientProfileForm(
    val nombre: String = "",
    val edad: Int? = null,
    val sexo: String = "",
    val estadoCivil: String = "",
    val ocupacion: String = "",
    val email: String = "",
    val telefono: String = ""
)
