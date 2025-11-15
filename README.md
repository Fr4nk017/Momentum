# Momentum App

Aplicación móvil de bienestar mental y seguimiento de actividades físicas desarrollada con Kotlin y Jetpack Compose.

## 📚 Documentación

### Base de Datos

La aplicación utiliza Room Database para persistencia local. La documentación completa está disponible en:

- **[DATABASE.md](DATABASE.md)** - Documentación completa de la base de datos
  - Arquitectura y patrones utilizados
  - Descripción detallada de todas las entidades (tablas)
  - DAOs y operaciones disponibles
  - Patrón Repository
  - Flujo de datos
  - Ejemplos de uso completos

- **[DATABASE_DIAGRAM.md](DATABASE_DIAGRAM.md)** - Diagramas visuales
  - Diagrama de componentes
  - Diagrama de entidades (ERD)
  - Flujo de datos
  - Estructura de archivos

- **[DATABASE_QUICK_GUIDE.md](DATABASE_QUICK_GUIDE.md)** - Guía rápida
  - Operaciones CRUD comunes
  - Casos de uso específicos
  - Ejemplos de código
  - Tips y mejores prácticas

## 🗄️ Estructura de la Base de Datos

La base de datos `momentum.db` incluye las siguientes tablas:

- **clients** - Información de clientes registrados
- **users** - Perfiles de usuario
- **friends** - Relaciones de amistad
- **community_posts** - Publicaciones de la comunidad
- **diary_entries** - Entradas del diario emocional
- **hiking_sessions** - Sesiones de caminata/ejercicio
- **location_points** - Puntos GPS de las rutas
- **chat_messages** - Mensajes del chat con IA
- **chat_sessions** - Sesiones de conversación
- **recent_places** - Lugares buscados recientemente

Para más detalles, consulta la [documentación completa de la base de datos](DATABASE.md).

## 🚀 Inicio Rápido

### Configuración de la Base de Datos

```kotlin
// Obtener instancia de la base de datos
val db = DatabaseProvider.getDatabase(context)

// Obtener repositorio
val repository = DatabaseProvider.clientRepository(context)

// Insertar datos
viewModelScope.launch {
    val clientId = repository.insert(
        name = "Juan Pérez",
        email = "juan@example.com",
        age = 30
    )
}

// Observar datos reactivamente
val clients = repository.observeAll()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
```

Consulta la [guía rápida](DATABASE_QUICK_GUIDE.md) para más ejemplos.

## 📱 Características

- **Registro e inicio de sesión** de usuarios
- **Diario emocional** con tracking de estados de ánimo
- **Seguimiento de caminatas** con GPS
- **Chat con IA** para apoyo emocional
- **Red social** con amigos y publicaciones
- **Estadísticas** y análisis de bienestar

## 🛠️ Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Room Database** - Persistencia de datos
- **Coroutines & Flow** - Programación asíncrona y reactiva
- **ViewModel** - Gestión de estado
- **Repository Pattern** - Arquitectura de datos

## 📄 Licencia

Este proyecto está bajo licencia [especificar licencia].
