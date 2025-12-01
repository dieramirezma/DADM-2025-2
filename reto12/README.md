# Reto 12 - Autenticación con GitHub OAuth

Esta aplicación Android implementa autenticación delegada usando GitHub como proveedor OAuth 2.0.

## 🎯 Características

- ✅ Autenticación OAuth 2.0 con GitHub
- ✅ No almacena contraseñas localmente
- ✅ Redirección segura mediante Chrome Custom Tabs
- ✅ UI moderna con Jetpack Compose y Material Design 3
- ✅ Muestra información del perfil de GitHub del usuario
- ✅ Manejo de estados (éxito/error/cargando)

## 📋 Requisitos Previos

- Android Studio (versión reciente)
- Cuenta de GitHub
- Dispositivo Android o emulador con API 24+

## 🔧 Configuración

### Paso 1: Crear OAuth App en GitHub

1. Ve a **GitHub Settings**: https://github.com/settings/developers
2. Haz clic en **"OAuth Apps"** en el menú lateral
3. Haz clic en **"New OAuth App"**
4. Completa el formulario:
   - **Application name**: `Reto12 Auth App` (o el nombre que prefieras)
   - **Homepage URL**: `http://localhost`
   - **Application description**: (opcional) "App de prueba OAuth"
   - **Authorization callback URL**: `reto12://github-callback`
5. Haz clic en **"Register application"**
6. **Guarda el `Client ID`** que aparece en la pantalla
7. Haz clic en **"Generate a new client secret"**
8. **Guarda el `Client Secret`** (solo se muestra una vez)

### Paso 2: Configurar las Credenciales en la App

Abre el archivo `app/build.gradle.kts` y reemplaza los valores placeholder:

```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"TU_CLIENT_ID_AQUI\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"TU_CLIENT_SECRET_AQUI\"")
```

**Ejemplo:**
```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"Iv1.abc123def456\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"1234567890abcdef1234567890abcdef12345678\"")
```

### Paso 3: Sincronizar y Compilar

1. Sincroniza el proyecto con Gradle (botón "Sync Now" en Android Studio)
2. Compila y ejecuta la aplicación en tu dispositivo o emulador

## 🚀 Uso

1. **Inicia la app**: Verás la pantalla de bienvenida
2. **Toca "Iniciar sesión con GitHub"**: Se abrirá Chrome Custom Tabs
3. **Autoriza la aplicación**: Ingresa tus credenciales de GitHub si no has iniciado sesión
4. **¡Listo!**: La app mostrará tu información de perfil

## 📱 Capturas del Flujo

### Estados de la Aplicación:

1. **Idle**: Pantalla inicial con botón de login
2. **Loading**: Mientras se procesa la autenticación
3. **Success**: Muestra el perfil del usuario con:
   - Avatar
   - Nombre de usuario
   - Email (si es público)
   - Ubicación (si está configurada)
   - Estadísticas (repos, seguidores, siguiendo)
4. **Error**: Mensaje de error con opción de reintentar

## 🏗️ Arquitectura

```
app/
├── auth/
│   └── GitHubAuthManager.kt        # Manejo del flujo OAuth
├── data/
│   ├── GitHubUser.kt               # Modelo de datos del usuario
│   └── AccessTokenResponse.kt      # Modelo de respuesta de token
├── network/
│   ├── GitHubApiService.kt         # Definición de API con Retrofit
│   └── RetrofitClient.kt           # Cliente HTTP configurado
├── viewmodel/
│   └── AuthViewModel.kt            # ViewModel con estados de autenticación
└── ui/
    └── screens/
        └── AuthScreen.kt           # UI con Jetpack Compose
```

## 🔒 Seguridad

- ✅ No se almacenan credenciales en la aplicación
- ✅ La autenticación se realiza en el navegador del usuario
- ✅ Se usa HTTPS para todas las comunicaciones
- ✅ Los tokens de acceso se manejan en memoria (no se persisten)
- ✅ Estado CSRF generado aleatoriamente para cada request

## 📦 Dependencias Principales

- **Jetpack Compose**: UI moderna y declarativa
- **Retrofit**: Cliente HTTP para APIs REST
- **Chrome Custom Tabs**: Navegador integrado para OAuth
- **Coil**: Carga de imágenes
- **Material Design 3**: Componentes de UI

## 🐛 Troubleshooting

### Error: "Error al autenticar: Unauthorized"
- Verifica que el Client ID y Client Secret sean correctos
- Asegúrate de que no haya espacios extra en las credenciales

### Error: "Error al abrir el navegador"
- Verifica que tu dispositivo tenga Chrome instalado
- Revisa que tengas conexión a Internet

### La app no recibe el callback
- Verifica que el callback URL en GitHub sea exactamente: `reto12://github-callback`
- Revisa que el AndroidManifest.xml tenga configurado el intent-filter correctamente

## 📝 Notas de Desarrollo

### ¿Por qué Chrome Custom Tabs?
Chrome Custom Tabs proporciona una experiencia de navegación integrada y segura, permitiendo que el usuario vea que está realmente en github.com, lo que aumenta la confianza en el proceso de autenticación.

### ¿Por qué no usar Firebase Auth?
Este proyecto implementa OAuth directamente para demostrar cómo funciona el flujo de autenticación delegada sin abstracciones adicionales.

## 👨‍💻 Autor

Desarrollado como parte del curso DADM 2025-2 - Reto 12

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

