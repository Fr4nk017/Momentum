

### 🎯 Objetivo general

Crear una **pantalla de inicio (Login o Registro extendido)** en Jetpack Compose para que el cliente ingrese o confirme sus datos personales antes de entrar al Home.
Esta pantalla debe almacenar la información y permitir que la app lo salude por su nombre y analice su perfil.

---

### 📁 Estructura de archivos esperada

```
ui/
 ├─ screens/
 │   ├─ LoginScreen.kt
 │   ├─ ClientProfileScreen.kt  ← NUEVA pantalla con formulario extendido
 │
 ├─ components/
 │   ├─ FormTextField.kt        ← Reutilizable
 │
model/
 ├─ forms/
 │   ├─ ClientProfileForm.kt    ← data class con los campos del formulario
 │
viewmodel/
 ├─ ClientProfileViewModel.kt   ← manejo de estado del formulario
```

---

### 👤 Nueva pantalla: `ClientProfileScreen.kt`

Crear un Composable llamado `ClientProfileScreen()` que muestre los siguientes campos en un formulario:

* Nombre completo (String)
* Edad (Int)
* Sexo (Selector: Masculino / Femenino / Otro)
* Estado civil (Soltero, Casado, Viudo, etc.)
* Ocupación o profesión (String)
* Email (String)
* Teléfono (opcional)
* Botón “Guardar y continuar”

**Requisitos funcionales:**

* Al presionar “Guardar y continuar”:

  * Validar campos requeridos.
  * Guardar los datos en un `ViewModel` (estado).
  * Navegar al Home (`Routes.HOME`).
* Mostrar errores si falta un campo obligatorio.
* Usar `OutlinedTextField` con íconos contextuales (`Icons.Filled.Person`, etc.).
* Diseñar adaptativamente para pantallas chicas y medianas (responsive con `Modifier.padding`, `verticalScroll`, etc.).

---

### 🧩 Modelo de datos: `ClientProfileForm.kt`

```kotlin
package com.example.unavegacion.model.forms

data class ClientProfileForm(
    val nombre: String = "",
    val edad: Int? = null,
    val sexo: String = "",
    val estadoCivil: String = "",
    val ocupacion: String = "",
    val email: String = "",
    val telefono: String = ""
)
```

---

### ⚙️ ViewModel: `ClientProfileViewModel.kt`

```kotlin
class ClientProfileViewModel : ViewModel() {

    private val _form = MutableStateFlow(ClientProfileForm())
    val form: StateFlow<ClientProfileForm> = _form

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors: StateFlow<Map<String, String>> = _errors

    fun onChange(field: String, value: String) {
        when (field) {
            "nombre" -> _form.update { it.copy(nombre = value) }
            "edad" -> _form.update { it.copy(edad = value.toIntOrNull()) }
            "sexo" -> _form.update { it.copy(sexo = value) }
            "estadoCivil" -> _form.update { it.copy(estadoCivil = value) }
            "ocupacion" -> _form.update { it.copy(ocupacion = value) }
            "email" -> _form.update { it.copy(email = value) }
            "telefono" -> _form.update { it.copy(telefono = value) }
        }
    }

    fun validate(): Boolean {
        val errs = mutableMapOf<String, String>()
        if (form.value.nombre.isBlank()) errs["nombre"] = "Campo obligatorio"
        if (form.value.edad == null) errs["edad"] = "Debe ingresar una edad válida"
        if (form.value.email.isBlank()) errs["email"] = "Campo obligatorio"
        _errors.value = errs
        return errs.isEmpty()
    }
}
```

---

### 🚦 Navegación (`NavGraph.kt`)

Agregar la ruta:

```kotlin
object Routes {
    const val LOGIN = "login"
    const val CLIENT_PROFILE = "client_profile"
    const val HOME = "home"
}
```

Y en el `NavHost`:

```kotlin
composable(Routes.CLIENT_PROFILE) {
    ClientProfileScreen(navController = navController)
}
```

Desde `LoginScreen`, si el login es exitoso:

```kotlin
navController.navigate(Routes.CLIENT_PROFILE)
```

---

### ✨ Extras para Copilot

* Usar **`rememberScrollState()`** para scroll en pantallas pequeñas.
* Incluir un `GreetingText` que diga “Hola, [nombre] 👋” cuando los datos estén guardados.
* Sugerir persistencia futura con `DataStore` para guardar perfil localmente.

---

### 🧠 Instrucción directa para Copilot:

> “Crea una pantalla llamada `ClientProfileScreen` con Jetpack Compose, que incluya un formulario de datos personales del cliente (nombre, edad, sexo, estado civil, ocupación, email, teléfono). Usa un ViewModel con `StateFlow` para manejar el estado, valida los campos requeridos y navega al Home al guardar correctamente.”

---

¿Quieres que te genere también la **versión visual completa del formulario (UI Compose)** lista para copiar a tu proyecto (con `OutlinedTextField`, `DropdownMenu` y `Button`)?
Puedo escribirte ese código optimizado con Material3 y validación visual.
