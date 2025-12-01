# 🔐 Guía Rápida de Configuración

## Pasos para Configurar la App

### 1️⃣ Crear OAuth App en GitHub

**URL directa**: https://github.com/settings/developers

1. Click en **"OAuth Apps"**
2. Click en **"New OAuth App"**
3. Llenar el formulario:

```
Application name: Reto12 Auth App
Homepage URL: http://localhost
Authorization callback URL: reto12://github-callback
```

4. Click en **"Register application"**
5. Copiar el **Client ID**
6. Click en **"Generate a new client secret"**
7. Copiar el **Client Secret** (¡solo se muestra una vez!)

### 2️⃣ Configurar Credenciales

Editar `app/build.gradle.kts` líneas 18-19:

**ANTES:**
```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"YOUR_GITHUB_CLIENT_ID\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"YOUR_GITHUB_CLIENT_SECRET\"")
```

**DESPUÉS:**
```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"Iv1.tu_client_id_real\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"tu_client_secret_real_aqui\"")
```

⚠️ **IMPORTANTE**: 
- Mantén las comillas dobles escapadas `\"`
- No compartas estas credenciales públicamente
- No las subas a GitHub (considera usar local.properties)

### 3️⃣ Ejecutar

1. **Sync del proyecto**: Click en "Sync Now" en Android Studio
2. **Build**: Menu → Build → Rebuild Project
3. **Run**: Click en el botón ▶️ o Shift+F10

## 🎯 Verificar Configuración

Si todo está bien configurado:
- ✅ La app compila sin errores
- ✅ Al hacer click en "Iniciar sesión con GitHub" se abre el navegador
- ✅ Después de autorizar, vuelves a la app
- ✅ Se muestra tu información de perfil

## ❌ Problemas Comunes

### "YOUR_GITHUB_CLIENT_ID no es válido"
→ No reemplazaste los valores en `build.gradle.kts`

### "redirect_uri_mismatch"
→ El callback URL en GitHub debe ser exactamente: `reto12://github-callback`

### "Bad credentials"
→ Revisa que copiaste bien el Client ID y Secret (sin espacios extra)

## 🔐 Seguridad

Para producción, considera:
1. Usar `local.properties` para las credenciales:

```properties
# local.properties
github.client.id=tu_client_id
github.client.secret=tu_client_secret
```

2. Modificar `build.gradle.kts`:

```kotlin
val githubClientId = project.findProperty("github.client.id") ?: "default"
val githubClientSecret = project.findProperty("github.client.secret") ?: "default"

buildConfigField("String", "GITHUB_CLIENT_ID", "\"$githubClientId\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"$githubClientSecret\"")
```

3. Agregar `local.properties` al `.gitignore`

## 📞 Soporte

Si tienes problemas, revisa:
1. README.md - Documentación completa
2. Logs de Android Studio (Logcat)
3. Estado de la OAuth App en GitHub

