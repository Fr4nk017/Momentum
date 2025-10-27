package com.momentum.app.model.forms

import com.momentum.app.model.validation.FormError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegisterValidationTest {

    @Test
    fun `blank fields return required errors`() {
        val form = RegisterForm(name = "", email = "", password = "", confirmPassword = "")
        val errors = form.validate()
        assertEquals(FormError.Required.message, errors.name?.message)
        assertEquals(FormError.Required.message, errors.email?.message)
        assertEquals(FormError.Required.message, errors.password?.message)
        assertEquals(FormError.Required.message, errors.confirmPassword?.message)
    }

    @Test
    fun `password mismatch returns PasswordsDontMatch`() {
        val form = RegisterForm(name = "Juan", email = "", password = "123456", confirmPassword = "abcdef")
        val errors = form.validate()
        assertEquals(FormError.PasswordsDontMatch.message, errors.confirmPassword?.message)
    }

    @Test
    fun `short password returns ShortPassword`() {
        val form = RegisterForm(name = "Juan", email = "", password = "123", confirmPassword = "123")
        val errors = form.validate()
        assertEquals(FormError.ShortPassword.message, errors.password?.message)
    }

    @Test
    fun `invalid name characters returns custom error`() {
        val form = RegisterForm(name = "Juan123", email = "", password = "123456", confirmPassword = "123456")
        val errors = form.validate()
        // Custom message set in validation when name has non-letter characters
        assertEquals("El nombre solo puede contener letras", errors.name?.message)
        // Email is blank -> Required
        assertEquals(FormError.Required.message, errors.email?.message)
        // Password must contain at least one letter per validation rules
        assertEquals("La contraseña debe contener al menos una letra", errors.password?.message)
        // Confirm password matches -> no error for confirmPassword
        assertNull(errors.confirmPassword)
    }
}
