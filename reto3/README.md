# Reto 3 - DADM-2025-2

Este proyecto corresponde al Reto 3 de la materia DADM (Desarrollo de Aplicaciones Móviles) del semestre 2025-2 en la Universidad Nacional. Es una aplicación Android desarrollada en Kotlin utilizando Gradle como sistema de construcción.

## Estructura del Proyecto

- **app/**: Contiene el módulo principal de la aplicación Android.
  - `src/main/java/`: Código fuente principal de la aplicación.
  - `src/main/res/`: Recursos gráficos, layouts, strings, etc.
  - `src/main/AndroidManifest.xml`: Archivo de manifiesto de la aplicación.
  - `build.gradle.kts`: Configuración de Gradle para el módulo app.
- **build.gradle.kts**: Configuración de Gradle a nivel de proyecto.
- **settings.gradle.kts**: Configuración de los módulos incluidos en el proyecto.
- **gradle/** y **gradle-wrapper/**: Archivos de configuración y utilidades para la gestión de dependencias y versiones de Gradle.

## Funcionalidades

Las funcionalidades específicas de la aplicación pueden variar según los requerimientos del reto, pero típicamente incluyen:

- **Interfaz de usuario moderna**: Utiliza layouts y componentes nativos de Android para una experiencia intuitiva.
- **Gestión de datos**: Puede incluir almacenamiento local (SQLite, Room) o acceso a servicios web.
- **Navegación**: Implementación de navegación entre pantallas usando el sistema de navegación de Android.
- **Pruebas**: Incluye carpetas para pruebas unitarias y de instrumentación (`src/test/java` y `src/androidTest/java`).

## Cómo ejecutar el proyecto

1. Clona el repositorio:
   ```sh
   git clone <URL-del-repositorio>
   ```
2. Abre el proyecto en Android Studio.
3. Sincroniza el proyecto con Gradle.
4. Ejecuta la aplicación en un emulador o dispositivo físico.

## Requisitos

- Android Studio (recomendado)
- JDK 17+
- Gradle 8+

## Créditos

Desarrollado por Diego Ramirez para la materia DADM-2025-2.

---

Si tienes dudas sobre la funcionalidad específica implementada en este reto, revisa la documentación interna del código fuente en `app/src/main/java/` y los comentarios en los archivos principales.
