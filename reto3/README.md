# Tic Tac Toe - Reto 3 Enhanced

## 📱 Descripción del Proyecto

Este proyecto implementa un juego de Tic Tac Toe (Tres en Raya) para Android con una interfaz gráfica personalizada, efectos de sonido y múltiples niveles de dificultad. La aplicación ha sido mejorada siguiendo las especificaciones del "Reto 5 - Graphics and Sound".

## ✨ Características Principales

### 🎮 Funcionalidades del Juego
- **Tres niveles de dificultad:**
  - Easy: Movimientos aleatorios de la IA
  - Harder: La IA bloquea al jugador y hace movimientos aleatorios
  - Expert: La IA trata de ganar, bloquea al jugador o hace movimientos aleatorios
- **Estadísticas de juego:** Contador de victorias para humano, IA y empates
- **Alternancia de turnos:** El primer jugador cambia después de cada partida
- **Menú de opciones** con diálogos para configuración y información

### 🎨 Mejoras Gráficas Implementadas
- **Vista personalizada (BoardView):** Reemplaza los botones tradicionales con una vista personalizada que dibuja el tablero
- **Gráficos vectoriales:** Piezas X y O dibujadas como recursos vectoriales escalables
- **Interfaz táctil:** Detección de toques convertida a coordenadas del tablero
- **Animaciones suaves:** Redibujado eficiente del tablero tras cada movimiento

### 🔊 Sistema de Sonido
- **Sonidos diferenciados:** 
  - `human_sound.mp3` para movimientos del jugador
  - `ai_sound.mp3` para movimientos de la IA
- **Gestión de recursos:** Carga y liberación apropiada de MediaPlayer según el ciclo de vida de la actividad
- **Reproducción automática:** Los sonidos se reproducen automáticamente en cada movimiento

### ⏱️ Mejoras de UX
- **Retraso de 1 segundo:** La IA espera 1 segundo antes de hacer su movimiento usando `Handler.postDelayed()`
- **Retroalimentación visual:** Actualización inmediata del tablero tras cada movimiento
- **Prevención de errores:** Validación de movimientos y estados del juego

## 🏗️ Arquitectura Técnica

### Componentes Principales

#### 1. **BoardView.kt** - Vista Personalizada
```kotlin
class BoardView : View {
    // Constantes para el diseño del tablero
    private const val GRID_WIDTH = 6f
    
    // Componentes gráficos
    private lateinit var paint: Paint
    private var xImage: Bitmap?
    private var oImage: Bitmap?
    
    // Métodos principales
    - initialize(): Configura Paint y carga imágenes
    - onDraw(): Dibuja el tablero y las piezas
    - getBoardCellFromCoordinates(): Convierte coordenadas táctiles a posiciones del tablero
}
```

#### 2. **MainActivity.kt** - Actividad Principal
```kotlin
class MainActivity : AppCompatActivity {
    // Componentes de sonido
    private var humanSoundPlayer: MediaPlayer?
    private var computerSoundPlayer: MediaPlayer?
    
    // Handler para delays
    private val handler = Handler(Looper.getMainLooper())
    
    // Métodos de gestión de sonido
    - loadSounds(): Carga archivos MP3 desde res/raw
    - releaseSounds(): Libera recursos de MediaPlayer
    - onBoardTouch(): Maneja eventos táctiles del tablero
}
```

#### 3. **TicTacToeGame.kt** - Lógica del Juego
```kotlin
class TicTacToeGame {
    // Niveles de dificultad
    enum class DifficultyLevel { EASY, HARDER, EXPERT }
    
    // Algoritmos de IA
    - getWinningMove(): Encuentra movimiento ganador
    - getBlockingMove(): Encuentra movimiento de bloqueo
    - getRandomMove(): Selecciona movimiento aleatorio
}
```

### Recursos Gráficos

#### Drawables Vectoriales
- **x_piece.xml**: Representa la pieza X con líneas cruzadas verdes
- **o_piece.xml**: Representa la pieza O con un círculo rojo

#### Recursos de Audio
- **res/raw/human_sound.mp3**: Sonido para movimientos del jugador
- **res/raw/ai_sound.mp3**: Sonido para movimientos de la IA

## 🔧 Cambios Técnicos Implementados

### 1. **Migración de Botones a Vista Personalizada**
- **Antes:** TableLayout con 9 Button widgets
- **Después:** BoardView personalizada con detección táctil

### 2. **Sistema de Dibujo Personalizado**
- **Canvas y Paint:** Dibujo manual de líneas del tablero
- **Bitmap rendering:** Conversión de vectores a bitmaps para renderizado eficiente
- **Gestión de coordenadas:** Cálculo preciso de posiciones de celdas

### 3. **Integración de Audio**
- **Lifecycle management:** Carga en `onResume()`, liberación en `onPause()`
- **Error handling:** Manejo de errores en carga de archivos de audio
- **Sincronización:** Reproducción coordinada con movimientos del juego

### 4. **Optimización de Performance**
- **Handler vs Coroutines:** Uso de Handler para delays de UI
- **View invalidation:** Redibujado eficiente solo cuando es necesario
- **Resource management:** Liberación apropiada de recursos gráficos y de audio

## 🚀 Compilación e Instalación

### Requisitos Previos
- Android Studio (versión recomendada: Arctic Fox o superior)
- SDK de Android (API nivel 21 o superior)
- Dispositivo físico con depuración USB habilitada

### Pasos de Instalación

1. **Abrir el proyecto en Android Studio**
2. **Sincronizar dependencias de Gradle**
3. **Conectar dispositivo físico**
4. **Habilitar depuración USB en el dispositivo**
5. **Ejecutar la aplicación**

## 📱 Compatibilidad
- **API mínima:** Android 5.0 (API 21)
- **API objetivo:** Android 14 (API 34)
- **Arquitecturas:** ARM, x86, x86_64

## 🎯 Características de Accesibilidad
- **Elementos táctiles grandes:** Celdas del tablero optimizadas para dedos
- **Retroalimentación audiovisual:** Sonidos y animaciones claras
- **Contrastes adecuados:** Colores diferenciados para piezas X y O

## 🐛 Manejo de Errores
- **Validación de movimientos:** Prevención de movimientos inválidos
- **Gestión de recursos:** Manejo seguro de archivos multimedia
- **Estados del juego:** Prevención de condiciones de carrera

---

**Desarrollado para:** DADM-2025-2  
**Fecha:** Septiembre 2025  
**Versión:** 2.0 Enhanced