# Manual Técnico - EcoLibertad v1.0.0

**ESTADO:** PRIVADO / DOCUMENTACIÓN TÉCNICA  
**Versión documentada:** 1.0.0  
**Tipo de aplicación:** Aplicación Android Nativa (Kotlin/Jetpack Compose) con Firebase, Firestore, Google Drive y Google Apps Script.  
**Zona horaria del backend:** America/Bogota (Servidores de Firebase).

---

## 1. Alcance
**EcoLibertad** es una plataforma institucional diseñada para la gestión ambiental y el fomento del reciclaje en centros educativos (Proyecto PRAE).
*   **Gestión Administrativa:** Control de infraestructura (aulas y cursos) y métricas de evaluación.
*   **Operación en Campo:** Auditorías ambientales mediante códigos QR y captura de evidencias fotográficas.
*   **Visualización de Impacto:** Tablero en tiempo real que convierte auditorías en Kg de reciclaje y puntos de ranking.

**Nota Crítica sobre Archivos:**  
La aplicación **no utiliza Firebase Storage**. Todas las evidencias multimedia se procesan mediante un Bridge de Google Apps Script que almacena los archivos en **Google Drive**, conservando solo los identificadores y URLs públicas en Firestore.

---

## 2. Arquitectura del Sistema
### 2.1 Capas del Proyecto (MVVM + Clean Architecture)

| Capa | Ubicación | Responsabilidad |
| :--- | :--- | :--- |
| **Presentación (UI)** | `app/src/main/java/com/example/myapplication/ui/` | Pantallas declarativas en Compose y ViewModels que gestionan el estado mediante `StateFlow`. |
| **Dominio (Modelos)** | `app/src/main/java/com/example/myapplication/domain/model/` | Definición de entidades de negocio (`User`, `Room`, `Evaluation`, `Indicator`, `Badge`). |
| **Datos (Repositorios)** | `app/src/main/java/com/example/myapplication/data/repository/` | Implementación de acceso a Firestore, Auth y el Bridge de Drive. |
| **Navegación** | `app/src/main/java/com/example/myapplication/ui/navigation/` | Control de rutas y protección de pantallas por Rol. |
| **Puente Backend** | `apps-script/Code.gs` | Endpoint POST que recibe Base64, guarda en Drive y retorna URLs. |

---

## 3. Inicio y Configuración
### 3.1 Inyección de Dependencias
Se utiliza **Dagger-Hilt**. El punto de entrada es `EcoApplication`. 
*   **Módulos:** `AppModule` provee las instancias de `FirebaseFirestore`, `FirebaseAuth` y `GoogleSignInClient`.

### 3.2 Navegación Basada en Roles (`MainActivity.kt`)
La aplicación redirige al usuario tras el login según su `UserRole`:
*   `ADMIN` -> `AdminDashboard`
*   `DOCENTE` / `COMITE_AMBIENTAL` -> `TeacherDashboard`
*   `ESTUDIANTE` -> `StudentDashboard`
*   `INVITADO` -> `InvitadoDashboard`

---

## 4. Flujo de Firebase y Base de Datos
### 4.1 Colecciones en Firestore
1.  **`users`**: Documento ID = `uid` de Auth. Contiene `rol`, `nombre`, `email` y `courseId`.
2.  **`rooms`**: Representa las aulas físicas. Campos: `nombre`, `bloque`, `puntosTotales`, `active`.
3.  **`courses`**: Representa los grados/grupos. Campos: `nombre`, `puntosTotales`, `embajadorAmbiental`.
4.  **`evaluations`**: Registros de auditoría. Contiene `roomId`, `docenteId`, `fecha`, `puntajeObtenido`, `evidenciasUrls` y el mapa de `indicadores`.
5.  **`indicators`**: Definición de métricas. Campo clave: `esContador` (Booleano).
6.  **`tips` / `events`**: Contenido dinámico para los dashboards.

### 4.2 Lógica de Kilogramos (Impacto Ambiental)
El sistema realiza una agregación semántica en tiempo real en `EnvironmentalDashboardViewModel`:
1.  Observa el flujo de `evaluations`.
2.  Filtra las claves del mapa `indicadores`.
3.  Si la clave contiene **"Botella"** o **"Tapa"** (insensible a mayúsculas), el valor se suma al acumulado institucional.
4.  **Conversión Segura:** Se utiliza `(it.value as? Number)?.toInt()` para manejar la discrepancia entre `Long` y `Double` que Firestore genera al almacenar números.

---

## 5. Conexión con Google Drive (Evidence Bridge)
### 5.1 Flujo de Subida de Evidencias
1.  **Captura:** La App toma la URI de la imagen.
2.  **Compresión:** `ImageUtils` comprime a JPEG para optimizar el ancho de banda.
3.  **Encoding:** Se convierte a **Base64**.
4.  **Endpoint:** Se envía un POST a la URL de Apps Script.
5.  **Apps Script:**
    *   Decodifica y guarda en la carpeta definida por `folderId`.
    *   Establece permisos `ANYONE_WITH_LINK` (VIEW).
    *   Retorna JSON con la `url` pública y el `fileId`.

---

## 6. Procedimiento: Crear el Primer Administrador
La aplicación no permite registrar administradores desde la UI. Se debe hacer manualmente:
1.  Cree el usuario en **Firebase Console > Authentication**.
2.  Copie el **UID** del usuario.
3.  En **Firestore**, cree un documento en la colección `users` con ese **UID** como ID.
4.  Agregue los campos: `nombre`, `email` y `rol` con valor `"ADMIN"`.

---

## 7. Diagnóstico y Resolución de Problemas

| Síntoma | Causa Probable | Solución |
| :--- | :--- | :--- |
| **Error "PERMISSION_DENIED"** | Reglas de Firestore mal configuradas. | Revisar `firestore.rules`. El rol `ADMIN` debe estar presente en el token. |
| **Contadores en 0** | El indicador no contiene las palabras "Botella" o "Tapa". | Renombrar el indicador en la colección `indicators`. |
| **Fallo al subir fotos** | Apps Script no desplegado como Web App con acceso "Anyone". | Redesplegar el Script y actualizar `DRIVE_BRIDGE_URL` en `EcoRepository.kt`. |
| **QR no reconoce el aula** | El código QR no corresponde a ningún `roomId` activo. | Validar la existencia del documento en la colección `rooms`. |

---

## 8. Seguridad y Mantenimiento
*   **Reglas de Firestore:** La escritura en `indicators` y `rooms` solo es permitida si `get(/databases/$(database)/documents/users/$(request.auth.uid)).data.rol == 'ADMIN'`.
*   **Google Apps Script:** La cuenta propietaria del script debe tener acceso a la carpeta de Drive institucional.
*   **Despliegue Web:** El tablero se actualiza mediante `firebase deploy --only hosting`.
