¡Listo! Aquí tienes un **prompt listo para pegar en Copilot (VS Code)** que le da contexto, tareas y resultados esperados. Funciona bien si lo pegas en un archivo `TODO.md` o en la parte superior de `NavGraph.kt` / `HomeScreen.kt` para que Copilot “vea” el proyecto.

---

# Prompt para Copilot — ordenar proyecto y crear carpeta `model` de formularios

Eres un *senior Android dev* ayudándome a **ordenar un proyecto Jetpack Compose (Kotlin)** como en la referencia: tengo `screens/ (HomeScreen.kt, LoginScreen.kt, RegisterScreen.kt)`, `components/ (AppTopBar.kt)`, `navigation/ (NavGraph.kt, Routes.kt)`, y `domain/validation/`.
Quiero:

1. **Estructura de paquetes**

* Crear los paquetes:

```
com.example.<app>/
  model/
    forms/
    validation/
  ui/
    screens/
    components/
  navigation/
  data/
  di/
```

* Mover o referenciar lo actual: `HomeScreen/LoginScreen/RegisterScreen -> ui/screens`, `AppTopBar -> ui/components`, `NavGraph/Routes -> navigation`. Mantener imports actualizados.

2. **Carpeta `model/forms`**

* Crear *data classes* para formularios iniciales de la app:

  * `LoginForm.kt`
  * `RegisterForm.kt`
  * (dejar plantilla `FormBase.kt` para futuros formularios)
* Requisitos de cada form:

```kotlin
// model/forms/LoginForm.kt
package com.example.<app>.model.forms

data class LoginForm(
    val email: String = "",
    val password: String = ""
)
```

```kotlin
// model/forms/RegisterForm.kt
package com.example.<app>.model.forms

data class RegisterForm(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
```

3. **Validación reutilizable**

* En `model/validation/` crear:

  * `ValidationRule.kt` (interface)
  * `FormError.kt` (sealed class)
  * Extensiones `validate()` por formulario.
    Ejemplo:

```kotlin
// model/validation/ValidationRule.kt
package com.example.<app>.model.validation

fun interface ValidationRule<T> { fun isValid(value: T): Boolean }
```

```kotlin
// model/validation/FormError.kt
package com.example.<app>.model.validation

sealed class FormError(val message: String) {
    data object Required : FormError("Campo requerido")
    data object InvalidEmail : FormError("Email inválido")
    data object ShortPassword : FormError("La contraseña es muy corta")
    data object PasswordsDontMatch : FormError("Las contraseñas no coinciden")
}
```

```kotlin
// model/forms/LoginValidation.kt
package com.example.<app>.model.forms

import com.example.<app>.model.validation.FormError

data class LoginErrors(
    val email: FormError? = null,
    val password: FormError? = null
)

fun LoginForm.validate(): LoginErrors {
    val emailErr =
        if (email.isBlank()) FormError.Required
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            FormError.InvalidEmail else null

    val passErr =
        if (password.isBlank()) FormError.Required
        else if (password.length < 6) FormError.ShortPassword else null

    return LoginErrors(emailErr, passErr)
}

fun LoginErrors.isValid() = email == null && password == null
```

Hacer lo mismo para `RegisterForm` incluyendo `PasswordsDontMatch`.

4. **UI State + ViewModel**

* Para `LoginScreen` y `RegisterScreen`, crear `LoginViewModel.kt` y `RegisterViewModel.kt` en `ui/screens/<screen>/`.
* Requisitos `ViewModel`:

```kotlin
class LoginViewModel : ViewModel() {
    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errors = MutableStateFlow(LoginErrors())
    val errors: StateFlow<LoginErrors> = _errors

    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _form.update { it.copy(password = v) } }

    fun submit(onSuccess: () -> Unit, onFailure: (LoginErrors) -> Unit) {
        val errs = _form.value.validate()
        _errors.value = errs
        if (errs.isValid()) onSuccess() else onFailure(errs)
    }
}
```

* En los composables, leer `form` y `errors`, mostrar `supportingText`/`isError` en `TextField`, y llamar `submit`.

5. **Integración en `NavGraph`**

* Asegurar rutas:

```kotlin
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}
```

* `NavGraph` debe usar estas rutas y navegar a `HOME` al éxito del login/registro.

6. **Tareas automáticas que quiero que generes:**

* Crear los archivos y pegar los *snippets* base.
* Actualizar imports y paquetes.
* Añadir comentarios `// TODO:` donde falten detalles de negocio.
* Proponer pruebas unitarias rápidas para validación en `model/validation/__tests__/`.

7. **Estilo**

* Kotlin idiomático, nombres en inglés para código y mensajes de error en español.
* KDoc breve en cada clase/función pública.

> Entrega el diff sugerido (o bloques de código completos) para **cada archivo nuevo o modificado**, empezando por `model/forms/*`, luego `model/validation/*`, `ui/screens/*ViewModel.kt`, y por último `navigation/NavGraph.kt`.

---

### Prompts cortos de apoyo (úsalos después si necesito archivos concretos)

* “Genera `RegisterValidation.kt` en `model/forms/` con reglas de email, contraseña >= 6 y confirmación de contraseña.”
* “Actualiza `LoginScreen.kt` para usar `LoginViewModel`, mostrando errores debajo de cada `TextField`.”
* “Crea pruebas `LoginValidationTest.kt` con casos: campos vacíos, email inválido, pass corta, válido.”

¿Quieres que lo adapte a tus nombres de paquete exactos (`com.example.unavegacion` u otro) y a los archivos que ya tienes? Puedo devolverte los diffs listos con esos paths.
