package com.momentum.app.model.validation

fun interface ValidationRule<T> {
    fun isValid(value: T): Boolean
}
