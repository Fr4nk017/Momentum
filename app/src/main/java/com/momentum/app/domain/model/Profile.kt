package com.momentum.app.domain.model

data class Profile(
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?
)