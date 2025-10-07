# 🔥 Guía Rápida: Obtener google-services.json

## ⚡ Tiempo Estimado: 5 minutos

---

## 📋 Paso a Paso

### 1️⃣ Abrir Firebase Console

```
🌐 URL: https://console.firebase.google.com/
```

**Login con tu cuenta de Google**

---

### 2️⃣ Crear Proyecto

1. Click en **"Add project"** o **"Agregar proyecto"**
2. **Nombre del proyecto:** `TicTacToeOnline` (o el que prefieras)
3. Click **"Continue"**
4. **Google Analytics:** Puedes deshabilitarlo (no es necesario)
5. Click **"Create project"**
6. Espera ~30 segundos
7. Click **"Continue"**

---

### 3️⃣ Agregar App Android

1. En el Dashboard, click en el ícono **Android** (robot verde)
2. **Completar formulario:**

```
Android package name: com.example.reto3
                      ^^^^^^^^^^^^^^^^^^^
                      (¡EXACTAMENTE esto!)

App nickname (opcional): Triqui Online

Debug signing certificate SHA-1 (opcional): [Dejar vacío]
```

3. Click **"Register app"**

---

### 4️⃣ Descargar google-services.json

1. Click en **"Download google-services.json"**
2. **Guardar el archivo** en tu computadora
3. Click **"Next"** (los siguientes pasos los haremos nosotros)
4. Click **"Next"** de nuevo
5. Click **"Continue to console"**

---

### 5️⃣ Habilitar Authentication

1. En el menú izquierdo, click en **"Build"** → **"Authentication"**
2. Click **"Get started"**
3. Tab **"Sign-in method"**
4. Click en **"Anonymous"**
5. **Enable** el toggle
6. Click **"Save"**

---

### 6️⃣ Crear Realtime Database

1. En el menú izquierdo, **"Build"** → **"Realtime Database"**
2. Click **"Create Database"**
3. **Location:** Elige el más cercano (ej: `us-central1`)
4. Click **"Next"**
5. **Security rules:** Selecciona **"Start in test mode"**
   ```
   ⚠️ IMPORTANTE: Esto es solo para desarrollo
   En producción deberías usar reglas más estrictas
   ```
6. Click **"Enable"**

---

### 7️⃣ Colocar el Archivo

**Windows:**
```powershell
# Copiar el archivo descargado a:
C:\Users\Diego Ramirez\Documents\dev\personal\un\DADM-2025-2\reto3\app\google-services.json
```

**Estructura final:**
```
reto3/
└── app/
    ├── build.gradle.kts
    ├── src/
    └── google-services.json  ← ¡Aquí!
```

---

### 8️⃣ Verificar el Archivo

El archivo `google-services.json` debe verse así:

```json
{
  "project_info": {
    "project_number": "123456789...",
    "project_id": "tictactoeonline-xxxxx",
    "storage_bucket": "tictactoeonline-xxxxx.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123...",
        "android_client_info": {
          "package_name": "com.example.reto3"
        }
      },
      ...
    }
  ],
  ...
}
```

**Verifica que `package_name` sea exactamente:** `com.example.reto3`

---

## ✅ Checklist

Antes de avisarme, verifica:

- [ ] Proyecto Firebase creado
- [ ] App Android agregada con package `com.example.reto3`
- [ ] Archivo `google-services.json` descargado
- [ ] **Anonymous Authentication habilitado**
- [ ] **Realtime Database creada en modo test**
- [ ] Archivo colocado en `reto3/app/google-services.json`

---

## 🎯 Cuando Termines

**Avísame con:**
```
"Tengo el archivo google-services.json listo"
```

Y yo ejecutaré automáticamente:
1. ✅ Modificar archivos Gradle
2. ✅ Agregar dependencias Firebase
3. ✅ Actualizar AndroidManifest
4. ✅ Renombrar archivos MainActivity
5. ✅ Sincronizar Gradle
6. ✅ Compilar proyecto
7. ✅ Ejecutar en emulador
8. ✅ Hacer pruebas

**Tiempo estimado: 15 minutos**

---

## 🆘 Problemas Comunes

### Error: "Package name mismatch"
**Solución:** El package debe ser exactamente `com.example.reto3`

### Error: "google-services.json not found"
**Solución:** Verifica que esté en `app/google-services.json` (no en `reto3/google-services.json`)

### No veo "Anonymous" en Authentication
**Solución:** Primero debes hacer click en "Get started" en la página de Authentication

### Realtime Database no aparece
**Solución:** Asegúrate de estar en "Build" → "Realtime Database" (no "Firestore Database")

---

## 📞 Estoy Esperando

Una vez tengas el archivo, continuaremos con:

```
📦 Fase 2: Configuración (15 min)
   └── Automático, solo observa

📦 Fase 3: Pruebas (10 min)
   └── Jugaremos una partida juntos para verificar

🎯 Total: 25 minutos hasta tener el juego online funcionando
```

---

**¡Éxito con la configuración! 🚀**
