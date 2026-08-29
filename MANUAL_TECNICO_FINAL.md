# Manual Técnico - EcoLibertad v1.0.0

**Estado:** Privado  
**Versión documentada:** 1.0.0  
**Tipo de aplicación:** Android Nativa (Kotlin/Compose) con Firebase, Firestore, Google Drive y Google Apps Script.  
**Zona horaria del backend:** America/Bogota (Servidores de Firebase).

---

## 1. Alcance
**EcoLibertad** es la plataforma tecnológica central del proyecto ambiental institucional.
*   **Público/Tablero IA:** Visualización en tiempo real de impacto (Kg) y ranking.
*   **Administración:** Gestión de infraestructura (Aulas, Cursos), métricas (Indicadores) y usuarios.
*   **Operación:** Auditorías físicas mediante escaneo de códigos QR y carga de evidencias fotográficas.
*   **Gamificación:** Sistema de medallas y ranking dinámico basado en puntos acumulados.

**Nota sobre almacenamiento:**  
Para optimizar costos, la app **no utiliza Firebase Storage**. Todas las evidencias se procesan mediante un Bridge de Google Apps Script que almacena las imágenes en **Google Drive** y devuelve la URL pública a Firestore.

---

## 2. Arquitectura
### 2.1 Capas del Proyecto (MVVM + Clean Architecture)

| Capa | Ubicación | Responsabilidad |
| :--- | :--- | :--- |
| **UI (Presentación)** | `app/src/main/java/com/example/myapplication/ui/` | Pantallas en Compose y ViewModels que gestionan el estado mediante `StateFlow`. |
| **Dominio (Modelos)** | `app/src/main/java/com/example/myapplication/domain/model/` | Definición de entidades de negocio (`User`, `Room`, `Evaluation`, `Indicator`). |
| **Datos (Repositorios)** | `app/src/main/java/com/example/myapplication/data/repository/` | Implementación de acceso a Firestore, Auth y el Bridge de Drive. |
| **Navegación** | `app/src/main/java/com/example/myapplication/ui/navigation/` | Control de rutas y protección de pantallas por Rol. |
| **Backend Bridge** | `apps-script/Code.gs` | Endpoint POST que recibe Base64, guarda en Drive y retorna URLs. |

---

## 3. Inicio y Configuración
### 3.1 Inyección de Dependencias
Se utiliza **Dagger-Hilt**. El punto de entrada es `EcoApplication` (anotada con `@HiltAndroidApp`).
*   Los repositorios (`EcoRepository`, `AuthRepositoryImpl`) están anotados con `@Singleton`.

### 3.2 Navegación Basada en Roles
La aplicación redirige al usuario tras el login según su `UserRole`:
*   `ADMIN` -> `AdminDashboard`
*   `DOCENTE` -> `TeacherDashboard` (Históricamente llamado `COMITE_AMBIENTAL`)
*   `ESTUDIANTE` -> `StudentDashboard`

---

## 4. Ecosistema de Datos (Firestore)
### 4.1 Colecciones Principales
*   **`users`**: Documento ID = `uid` de Firebase Auth. Contiene `rol` y `courseId`.
*   **`rooms`**: Unidades físicas evaluables. Tienen `puntosTotales` y `embajadorAmbiental`.
*   **`evaluations`**: El corazón del sistema. Guarda el mapa de `indicadores` y la lista de `evidenciasUrls`.
*   **`courses`**: Grupos académicos que compiten en el ranking.
*   **`indicators`**: Define si una métrica es un puntaje (1-5) o un contador de reciclaje (`esContador: true`).

### 4.2 Lógica de "Kilogramos" (Impacto Ambiental)
El sistema realiza un filtrado inteligente en el `EnvironmentalDashboardViewModel`:
1. Consulta el flujo de evaluaciones.
2. Escanea las llaves del mapa `indicadores`.
3. Si el nombre del indicador contiene **"Botella"** o **"Tapa"**, el valor se agrega al acumulado de impacto institucional.

---

## 5. Conexión con Google Drive (Evidence Bridge)
### 5.1 Flujo de Subida
1. `EcoRepository.uploadToDriveBridge(uri)` captura la URI de la imagen.
2. `ImageUtils` comprime la imagen a JPEG para reducir consumo de ancho de banda.
3. Se convierte a **Base64** y se envía vía POST al endpoint de Apps Script.
4. El Script de Google:
    *   Decodifica el Base64.
    *   Crea el archivo en la carpeta configurada (`folderId`).
    *   Establece permisos de visualización pública.
    *   Retorna un JSON con `status: "success"` y la `url`.

---

## 6. Modelo de Datos Detallado
### 6.1 Usuario (`User`)
```kotlin
data class User(
    val uid: String,
    val nombre: String,
    val email: String,
    val rol: UserRole, // ADMIN, COMITE_AMBIENTAL, ESTUDIANTE, INVITADO
    val courseId: String?
)
```

### 6.2 Evaluación (`Evaluation`)
```kotlin
data class Evaluation(
    val id: String,
    val roomId: String,
    val fecha: Long,
    val puntajeObtenido: Int,
    val evidenciasUrls: List<String>,
    val indicadores: Map<String, Int> // Ej: {"Botellas": 10, "Limpieza": 5}
)
```

---

## 7. Diagnóstico y Resolución de Problemas
| Síntoma | Causa Probable | Solución |
| :--- | :--- | :--- |
| **Error "database does not exist"** | Proyecto Firebase sin base de datos Firestore creada. | Crear la DB en la consola de Firebase en modo producción/prueba. |
| **Contadores en 0 (Kg)** | Inconsistencia de tipos (Long vs Int) en Firestore. | El repositorio ya incluye normalización `(it.value as? Number)?.toInt()`. |
| **QR no reconocido** | El `roomId` en el QR no coincide con ningún documento en `rooms`. | Regenerar e imprimir el QR desde el panel de Admin. |
| **Fallo al subir fotos** | `DRIVE_BRIDGE_URL` no tiene permisos de ejecución "Anyone". | Revisar el despliegue del Apps Script y configurar ejecución como "Me" y acceso "Anyone". |

---

## 8. Consideraciones de Seguridad
1.  **Reglas de Firestore:** Solo los administradores tienen permiso de escritura (`write`) en las colecciones `indicators`, `rooms` y `courses`.
2.  **Auth:** Se requiere correo institucional verificado para el rol `ADMIN`.
3.  **Drive:** La carpeta de Drive debe ser gestionada por una cuenta institucional para evitar la pérdida de evidencias si un desarrollador abandona el proyecto.
