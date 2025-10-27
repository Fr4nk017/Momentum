package com.momentum.app.model.forms

import com.momentum.app.model.validation.FormError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoginValidationTest {

    @Test
    fun `blank email and password returns required errors`() {
        val form = LoginForm(email = "", password = "")
        val errors = form.validate()
        assertEquals(FormError.Required.message, errors.email?.message)
        assertEquals(FormError.Required.message, errors.password?.message)
    }

    @Test
    fun `short password returns ShortPassword`() {
        val form = LoginForm(email = "", password = "123")
        val errors = form.validate()
        // Email blank triggers Required, password too short triggers ShortPassword
        assertEquals(FormError.Required.message, errors.email?.message)
        assertEquals(FormError.ShortPassword.message, errors.password?.message)
    }

    @Test
    fun `only email blank shows email Required and no password error when long enough`() {
        val form = LoginForm(email = "", password = "123456")
        val errors = form.validate()
        assertEquals(FormError.Required.message, errors.email?.message)
        assertNull(errors.password)
    }
}
