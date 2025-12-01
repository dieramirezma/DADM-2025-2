# 📱 Instrucciones Paso a Paso - Reto 12

## ✅ Checklist de Configuración

- [ ] **Paso 1**: Crear OAuth App en GitHub
- [ ] **Paso 2**: Obtener Client ID
- [ ] **Paso 3**: Generar Client Secret
- [ ] **Paso 4**: Configurar credenciales en build.gradle.kts
- [ ] **Paso 5**: Sincronizar proyecto
- [ ] **Paso 6**: Ejecutar la app
- [ ] **Paso 7**: Probar autenticación

---

## 🚀 Paso 1: Crear OAuth App en GitHub

### 1.1 Accede a GitHub Settings
1. Abre tu navegador
2. Ve a: **https://github.com/settings/developers**
3. Si no has iniciado sesión, inicia sesión con tu cuenta de GitHub

### 1.2 Navega a OAuth Apps
1. En el menú lateral izquierdo, busca **"OAuth Apps"**
2. Haz clic en **"OAuth Apps"**

### 1.3 Crea una nueva OAuth App
1. Haz clic en el botón verde **"New OAuth App"**
2. Verás un formulario con varios campos

### 1.4 Completa el formulario
Llena los campos EXACTAMENTE como se muestra:

```
┌─────────────────────────────────────────────────────────┐
│ Application name                                        │
│ ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ │
│ ┃ Reto12 Auth App                                  ┃ │
│ ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ │
│                                                         │
│ Homepage URL                                            │
│ ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ │
│ ┃ http://localhost                                 ┃ │
│ ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ │
│                                                         │
│ Application description (optional)                      │
│ ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ │
│ ┃ App de autenticación para el reto 12            ┃ │
│ ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ │
│                                                         │
│ Authorization callback URL                              │
│ ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ │
│ ┃ reto12://github-callback                        ┃ │
│ ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ │
└─────────────────────────────────────────────────────────┘
```

⚠️ **MUY IMPORTANTE**: El **Authorization callback URL** debe ser exactamente:
```
reto12://github-callback
```
(Sin espacios, sin mayúsculas, exactamente como está escrito)

### 1.5 Registra la aplicación
1. Haz clic en el botón verde **"Register application"**
2. Serás redirigido a la página de tu nueva OAuth App

---

## 🔑 Paso 2: Obtener Client ID

1. En la página de tu OAuth App, verás un campo llamado **"Client ID"**
2. Se ve algo así: `Iv1.abc123def456789`
3. Haz clic en el botón de copiar (📋) junto al Client ID
4. **Pega este valor en un archivo de texto temporal** (lo necesitarás en el Paso 4)

---

## 🔐 Paso 3: Generar Client Secret

### 3.1 Genera el secreto
1. En la misma página, busca la sección **"Client secrets"**
2. Haz clic en el botón **"Generate a new client secret"**
3. Es posible que GitHub te pida confirmar tu contraseña

### 3.2 Copia el secreto
1. Aparecerá un código largo (ejemplo: `1234567890abcdef1234567890abcdef12345678`)
2. **¡IMPORTANTE!** Este secreto **solo se muestra UNA VEZ**
3. Haz clic en el botón de copiar (📋)
4. **Pega este valor en el mismo archivo de texto temporal**

Tu archivo temporal ahora debería tener algo como:
```
Client ID: Iv1.abc123def456789
Client Secret: 1234567890abcdef1234567890abcdef12345678
```

---

## ⚙️ Paso 4: Configurar Credenciales en el Proyecto

### 4.1 Abre Android Studio
1. Abre el proyecto Reto12 en Android Studio
2. Espera a que termine de cargar e indexar

### 4.2 Ubica el archivo build.gradle.kts
1. En el panel izquierdo (Project), navega a:
   ```
   reto12 > app > build.gradle.kts
   ```
2. Haz doble clic para abrir el archivo

### 4.3 Busca las líneas de configuración
1. Busca (Ctrl+F o Cmd+F) la palabra: `GITHUB_CLIENT_ID`
2. Encontrarás estas dos líneas (alrededor de la línea 18-19):

```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"YOUR_GITHUB_CLIENT_ID\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"YOUR_GITHUB_CLIENT_SECRET\"")
```

### 4.4 Reemplaza los valores
**ANTES:**
```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"YOUR_GITHUB_CLIENT_ID\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"YOUR_GITHUB_CLIENT_SECRET\"")
```

**DESPUÉS** (usando tus valores reales):
```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"Iv1.abc123def456789\"")
buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"1234567890abcdef1234567890abcdef12345678\"")
```

⚠️ **NOTAS IMPORTANTES**:
- Mantén las comillas dobles con backslash: `\"`
- No elimines las comillas
- No agregues espacios adicionales
- Usa TUS valores reales (los que copiaste en el Paso 2 y 3)

### 4.5 Guarda el archivo
1. Presiona `Ctrl+S` (Windows/Linux) o `Cmd+S` (Mac)
2. O ve a: **File > Save All**

---

## 🔄 Paso 5: Sincronizar el Proyecto

1. Aparecerá una barra amarilla en la parte superior que dice: **"Gradle files have changed since last project sync..."**
2. Haz clic en **"Sync Now"**
3. Espera a que termine la sincronización (verás una barra de progreso abajo)
4. Si todo está bien, verás: **"Gradle sync finished"** ✅

### Si hay errores:
- Revisa que copiaste bien las credenciales
- Verifica que no falten las comillas o backslashes
- Intenta: **File > Invalidate Caches / Restart**

---

## ▶️ Paso 6: Ejecutar la App

### 6.1 Prepara tu dispositivo

**Opción A - Dispositivo físico:**
1. Conecta tu teléfono Android por USB
2. Activa las **Opciones de desarrollador**
3. Activa la **Depuración USB**
4. Acepta la ventana de autorización en el teléfono

**Opción B - Emulador:**
1. Ve a: **Tools > Device Manager**
2. Crea o inicia un dispositivo virtual
3. Espera a que el emulador inicie completamente

### 6.2 Ejecuta la app
1. Selecciona tu dispositivo en el dropdown (arriba en la toolbar)
2. Haz clic en el botón de **Run** ▶️ (triángulo verde)
3. O presiona: `Shift + F10` (Windows/Linux) o `Control + R` (Mac)

### 6.3 Espera la instalación
1. Android Studio compilará el proyecto
2. Instalará la APK en tu dispositivo
3. La app se abrirá automáticamente

---

## 🎉 Paso 7: Probar la Autenticación

### 7.1 Pantalla inicial
1. Verás una pantalla oscura con el logo de GitHub
2. Un botón verde que dice **"Iniciar sesión con GitHub"**

### 7.2 Inicia el flujo OAuth
1. Toca el botón **"Iniciar sesión con GitHub"**
2. Se abrirá Chrome Custom Tabs (navegador integrado)
3. Verás la página de GitHub para autorizar la app

### 7.3 Autoriza la aplicación
1. Si no has iniciado sesión en GitHub:
   - Ingresa tu usuario y contraseña de GitHub
   - (Opcionalmente, código 2FA si lo tienes activado)

2. Verás una pantalla que dice:
   ```
   Authorize Reto12 Auth App
   
   This application will be able to:
   - Read your user profile data
   - Read your email addresses
   ```

3. Haz clic en el botón verde **"Authorize"**

### 7.4 ¡Éxito!
1. Serás redirigido automáticamente a la app
2. Verás una pantalla de **"¡Autenticación Exitosa!"** con:
   - ✅ Un ícono verde de check
   - 🖼️ Tu avatar de GitHub
   - 📋 Tu información de perfil:
     - Nombre de usuario
     - Nombre completo (si es público)
     - Email (si es público)
     - Ubicación (si está configurada)
   - 📊 Estadísticas:
     - Número de repositorios públicos
     - Cantidad de seguidores
     - Cantidad de personas que sigues

### 7.5 Prueba cerrar sesión
1. Toca el botón **"Cerrar sesión"**
2. Volverás a la pantalla inicial
3. Puedes volver a iniciar sesión cuando quieras

---

## ❌ Solución de Problemas

### "YOUR_GITHUB_CLIENT_ID is invalid"
**Causa**: No reemplazaste las credenciales en build.gradle.kts
**Solución**: Vuelve al Paso 4 y reemplaza los valores

### "redirect_uri_mismatch"
**Causa**: El callback URL en GitHub no coincide
**Solución**: 
1. Ve a tu OAuth App en GitHub
2. Edita el "Authorization callback URL"
3. Asegúrate de que sea exactamente: `reto12://github-callback`

### "Bad credentials" o "Unauthorized"
**Causa**: Client ID o Secret incorrectos
**Solución**:
1. Revisa que copiaste bien los valores (sin espacios extra)
2. Verifica que las comillas estén correctas en build.gradle.kts
3. Genera un nuevo Client Secret si es necesario

### "Error al abrir el navegador"
**Causa**: Chrome no está instalado o hay problemas de conexión
**Solución**:
1. Instala Google Chrome en tu dispositivo
2. Verifica tu conexión a Internet
3. Revisa los permisos de la app

### La app se cierra después de autorizar
**Causa**: Problema con el intent-filter del deep link
**Solución**:
1. Desinstala completamente la app del dispositivo
2. Limpia el proyecto: **Build > Clean Project**
3. Reconstruye: **Build > Rebuild Project**
4. Vuelve a instalar

---

## 📞 ¿Necesitas Ayuda?

1. **Lee el README.md** para más detalles técnicos
2. **Revisa los logs** en Android Studio:
   - Ve a: **View > Tool Windows > Logcat**
   - Filtra por: `AuthViewModel` o `MainActivity`
3. **Revisa tu OAuth App en GitHub**:
   - https://github.com/settings/developers
   - Verifica que esté activa y bien configurada

---

## ✅ Checklist Final

- [ ] OAuth App creada en GitHub
- [ ] Client ID copiado y configurado
- [ ] Client Secret generado y configurado
- [ ] build.gradle.kts modificado correctamente
- [ ] Proyecto sincronizado sin errores
- [ ] App ejecutándose en el dispositivo
- [ ] Autenticación probada con éxito
- [ ] Información de perfil mostrada correctamente

---

## 🎊 ¡Felicidades!

Has completado exitosamente la configuración del Reto 12. La app ahora:
- ✅ Usa autenticación delegada (OAuth 2.0)
- ✅ No almacena contraseñas
- ✅ Redirige a GitHub para autenticar
- ✅ Muestra mensajes de éxito/error apropiados

**¡Excelente trabajo! 🚀**

