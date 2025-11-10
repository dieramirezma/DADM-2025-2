# Gemini Chatbot - Android App

Aplicación móvil Android que integra un chatbot con el modelo de inteligencia artificial generativa **Google Gemini**. La aplicación permite tener conversaciones naturales con la IA usando Jetpack Compose para una interfaz moderna y fluida.

## 🚀 Características

- **Chatbot interactivo**: Conversaciones en tiempo real con Gemini AI
- **Interfaz moderna**: UI construida con Jetpack Compose y Material Design 3
- **Gestión de API Key**: Configuración segura de la clave API desde la aplicación
- **Historial de conversación**: Mantiene el contexto de la conversación
- **Indicadores visuales**: Muestra cuando la IA está escribiendo
- **Manejo de errores**: Mensajes de error claros y útiles

## 📋 Requisitos Previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 11 o superior
- Android SDK con API Level 24 (Android 7.0) o superior
- Una cuenta de Google
- API Key de Google Gemini (gratuita)

## 🔑 Cómo Obtener tu API Key de Gemini

### Paso 1: Acceder a Google AI Studio

1. Abre tu navegador y ve a: **https://aistudio.google.com/app/apikey**
2. Inicia sesión con tu cuenta de Google

### Paso 2: Crear una API Key

1. Una vez dentro de Google AI Studio, verás un botón que dice **"Create API Key"** o **"Get API Key"**
2. Si es la primera vez, te pedirá crear un proyecto de Google Cloud (o usar uno existente)
3. Selecciona o crea un proyecto
4. Se generará automáticamente una API Key

### Paso 3: Copiar la API Key

1. La API Key se mostrará en formato: `AIza...` (comienza con "AIza")
2. **IMPORTANTE**: Copia la clave inmediatamente, ya que solo se muestra una vez
3. Si la pierdes, puedes crear una nueva desde el panel

### Paso 4: Configurar en la App

1. Al abrir la aplicación por primera vez, verás una pantalla para configurar la API Key
2. Pega la API Key que copiaste
3. Haz clic en "Guardar y Continuar"
4. La clave se guardará de forma segura en el dispositivo

## 📱 Instalación y Uso

### Compilar el Proyecto

1. Clona o descarga este repositorio
2. Abre el proyecto en Android Studio
3. Espera a que Gradle sincronice las dependencias
4. Conecta un dispositivo Android o inicia un emulador
5. Haz clic en "Run" (▶️) o presiona `Shift + F10`

### Primera Ejecución

1. La aplicación mostrará una pantalla para configurar la API Key
2. Ingresa tu API Key de Gemini (ver instrucciones arriba)
3. Una vez configurada, podrás comenzar a chatear

### Usar el Chatbot

1. Escribe tu mensaje en el campo de texto inferior
2. Presiona el botón de enviar (▶️) o presiona Enter
3. Espera la respuesta de Gemini
4. Continúa la conversación normalmente

## 🏗️ Estructura del Proyecto

```
app/src/main/java/com/example/reto11/
├── data/
│   └── ChatMessage.kt          # Modelo de datos para mensajes
├── service/
│   └── GeminiService.kt        # Servicio para interactuar con Gemini API
├── ui/
│   ├── ChatScreen.kt           # Pantalla principal del chat
│   └── ApiKeyScreen.kt         # Pantalla de configuración de API Key
├── util/
│   └── ApiKeyManager.kt        # Gestor para almacenar/recuperar API Key
├── viewmodel/
│   └── ChatViewModel.kt        # ViewModel para la lógica del chat
└── MainActivity.kt             # Actividad principal
```

## 🛠️ Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación
- **Jetpack Compose**: Framework de UI declarativa
- **Material Design 3**: Sistema de diseño
- **Google Gemini SDK**: SDK oficial de Google para Gemini AI
- **Coroutines**: Para operaciones asíncronas
- **ViewModel**: Para manejo de estado y ciclo de vida
- **SharedPreferences**: Para almacenamiento local de la API Key

## 📦 Dependencias Principales

- `com.google.ai.client.generativeai:generativeai:0.2.2` - SDK de Gemini
- `androidx.compose.*` - Jetpack Compose
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel para Compose
- `org.jetbrains.kotlinx:kotlinx-coroutines-*` - Coroutines

## 🔒 Seguridad

- La API Key se almacena localmente en el dispositivo usando SharedPreferences
- La API Key nunca se envía a servidores externos (solo a los servidores de Google Gemini)
- Se recomienda no compartir tu API Key públicamente
- Si comprometes tu API Key, puedes revocarla y crear una nueva en Google AI Studio

## ⚠️ Limitaciones y Consideraciones

- **Límites de API**: Google Gemini tiene límites de uso gratuitos. Consulta la documentación oficial para más detalles
- **Conexión a Internet**: La aplicación requiere conexión a Internet para funcionar
- **Modelo utilizado**: La app usa `gemini-pro` por defecto

## 🐛 Solución de Problemas

### Error: "API Key no configurada"
- Asegúrate de haber ingresado la API Key correctamente
- Verifica que la API Key comience con "AIza"
- Intenta eliminar los datos de la app y configurar nuevamente

### Error: "Error al procesar la solicitud"
- Verifica tu conexión a Internet
- Asegúrate de que tu API Key sea válida y no haya expirado
- Revisa si has excedido los límites de uso de la API

### La app no compila
- Asegúrate de tener la última versión de Android Studio
- Sincroniza el proyecto: `File > Sync Project with Gradle Files`
- Limpia y reconstruye: `Build > Clean Project` y luego `Build > Rebuild Project`

## 📚 Recursos Adicionales

- [Documentación de Google Gemini](https://ai.google.dev/docs)
- [Google AI Studio](https://aistudio.google.com/)
- [Documentación de Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Documentación de Material Design 3](https://m3.material.io/)

## 📄 Licencia

Este proyecto es parte de un reto académico. Úsalo como referencia para tus propios proyectos.

## 👨‍💻 Autor

Desarrollado como parte del reto 11 del curso DADM-2025-2

---

**Nota**: Esta aplicación es un ejemplo educativo. Para uso en producción, considera implementar medidas de seguridad adicionales y manejo de errores más robusto.

