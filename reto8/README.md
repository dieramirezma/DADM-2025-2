# Directorio de Empresas de Software

Esta aplicación móvil Android permite gestionar un directorio de empresas de desarrollo de software utilizando SQLite como base de datos local.

## Características

### Información de Empresas
Cada empresa en el directorio contiene la siguiente información:
- **Nombre de la empresa**: Nombre comercial de la empresa
- **URL de la página web**: Sitio web oficial de la empresa
- **Teléfono de contacto**: Número telefónico para contacto
- **Email de contacto**: Dirección de correo electrónico
- **Productos y servicios**: Descripción de los productos y servicios que ofrece
- **Clasificación**: Tipo de empresa según su especialización:
  - Consultoría
  - Desarrollo a la medida
  - Fábrica de software
  - Combinaciones de las anteriores

### Funcionalidades CRUD
La aplicación permite realizar todas las operaciones CRUD (Create, Read, Update, Delete):

- **Crear**: Agregar nuevas empresas al directorio
- **Leer**: Visualizar la lista de empresas con toda su información
- **Actualizar**: Modificar la información de empresas existentes
- **Eliminar**: Remover empresas del directorio (con confirmación)

### Funcionalidades de Búsqueda y Filtrado
- **Búsqueda por nombre**: Filtrar empresas por nombre (búsqueda parcial)
- **Filtrado por clasificación**: Mostrar solo empresas de un tipo específico
- **Búsqueda en tiempo real**: Los resultados se actualizan automáticamente mientras se escribe
- **Limpiar filtros**: Restablecer la vista para mostrar todas las empresas

### Características de la Interfaz
- **Diseño moderno**: Utiliza Material Design 3 para una experiencia visual atractiva
- **Lista responsive**: RecyclerView optimizado para mostrar las empresas
- **Estado vacío**: Mensaje informativo cuando no hay empresas o no se encuentran resultados
- **Confirmación de eliminación**: Diálogo de confirmación antes de eliminar una empresa
- **Validación de formularios**: Validación completa de todos los campos requeridos

## Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación principal
- **SQLite**: Base de datos local para almacenamiento persistente
- **Material Design 3**: Framework de diseño para la interfaz de usuario
- **View Binding**: Para acceso seguro a las vistas
- **RecyclerView**: Para mostrar listas eficientes de empresas
- **Activity Result API**: Para manejo moderno de resultados entre actividades

## Estructura del Proyecto

```
app/src/main/java/com/example/reto8/
├── MainActivity.kt                 # Actividad principal con lista y filtros
├── CompanyFormActivity.kt          # Formulario para agregar/editar empresas
├── model/
│   └── Company.kt                  # Modelo de datos de la empresa
├── database/
│   ├── CompanyDatabaseHelper.kt    # Helper para operaciones SQLite
│   └── CompanyDAO.kt               # Data Access Object
└── adapter/
    └── CompanyAdapter.kt           # Adaptador para RecyclerView
```

## Instalación y Uso

1. **Compilar la aplicación**:
   ```bash
   ./gradlew build
   ```

2. **Instalar en dispositivo/emulador**:
   ```bash
   ./gradlew installDebug
   ```

3. **Usar la aplicación**:
   - Al abrir la app, verás la lista de empresas (vacía inicialmente)
   - Usa el botón flotante (+) para agregar una nueva empresa
   - Completa el formulario con toda la información requerida
   - Usa los filtros de búsqueda para encontrar empresas específicas
   - Toca "Editar" en cualquier empresa para modificar su información
   - Toca "Eliminar" para remover una empresa (se pedirá confirmación)

## Base de Datos

La aplicación utiliza SQLite con la siguiente estructura de tabla:

```sql
CREATE TABLE companies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    website TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL,
    products_services TEXT NOT NULL,
    classification TEXT NOT NULL
);
```

## Validaciones

- **Nombre**: Campo obligatorio
- **Website**: URL válida (con o sin protocolo)
- **Teléfono**: Campo obligatorio
- **Email**: Formato de email válido
- **Productos y servicios**: Campo obligatorio
- **Clasificación**: Debe seleccionar una opción válida del dropdown

## Características Técnicas

- **Mínimo SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 36)
- **Arquitectura**: Patrón DAO para separación de responsabilidades
- **Persistencia**: Base de datos SQLite local
- **UI**: Material Design 3 con View Binding
- **Navegación**: Activity Result API para comunicación entre actividades
