# Aplicación de Puntos de Interés

Esta aplicación Android muestra puntos de interés cercanos en un mapa usando coordenadas GPS.

## Características

- ✅ Obtener ubicación actual del usuario usando GPS
- ✅ Mostrar mapa interactivo con Google Maps
- ✅ Buscar puntos de interés cercanos (hospitales, restaurantes, gasolineras, etc.)
- ✅ Configurar radio de búsqueda en kilómetros
- ✅ Mostrar marcadores en el mapa con nombres y distancias
- ✅ Interfaz de configuración para personalizar búsquedas

## Configuración Requerida

### 1. API Key de Google Maps

Para que la aplicación funcione, necesitas obtener una API key de Google Maps:

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Habilita las siguientes APIs:
   - Maps SDK for Android
   - Places API
4. Ve a "Credenciales" y crea una nueva API key
5. Restringe la API key para Android (opcional pero recomendado)

### 2. Configurar la API Key

✅ **API Key ya configurada** en `local.properties`:
```
GOOGLE_MAPS_API_KEY=AIzaSyAYvN2wbg_jh-7VIwDqp3T4vV9_Jz4lN0k
```

La aplicación está configurada para usar automáticamente esta API key desde `local.properties`.

## Permisos

La aplicación requiere los siguientes permisos:
- `ACCESS_FINE_LOCATION` - Para obtener ubicación precisa
- `ACCESS_COARSE_LOCATION` - Para obtener ubicación aproximada
- `INTERNET` - Para conectarse a los servicios de Google

## Uso

1. **Obtener Ubicación**: Presiona el botón "Obtener Ubicación" para obtener tu posición actual
2. **Buscar Lugares**: Presiona "Buscar Lugares" para encontrar puntos de interés cercanos
3. **Configurar**: Presiona el botón ⚙ para ajustar el radio de búsqueda y tipo de lugares

## Tipos de Lugares Disponibles

- Hospitales
- Restaurantes  
- Gasolineras
- Farmacias
- Bancos
- Lugares Turísticos
- Tiendas
- Hoteles

## Tecnologías Utilizadas

- **Google Maps SDK for Android** - Para mostrar mapas
- **Google Places API** - Para buscar puntos de interés
- **Fused Location Provider** - Para obtener ubicación GPS
- **Android Preferences** - Para configuración de usuario
- **Kotlin** - Lenguaje de programación

## Estructura del Proyecto

```
app/src/main/
├── java/com/example/reto9/
│   ├── MainActivity.kt          # Actividad principal con mapa
│   └── SettingsActivity.kt     # Actividad de configuración
├── res/
│   ├── layout/
│   │   ├── activity_main.xml    # Layout principal
│   │   └── activity_settings.xml
│   ├── xml/
│   │   └── preferences.xml     # Configuración de preferencias
│   └── values/
│       └── strings.xml          # Strings y arrays
└── AndroidManifest.xml         # Permisos y configuración
```

## Notas Importantes

- La aplicación solicita permisos de ubicación al usuario
- El radio de búsqueda es configurable de 1 a 50 km
- Los resultados se filtran por distancia y tipo de lugar
- La cámara del mapa se ajusta automáticamente para mostrar todos los resultados
