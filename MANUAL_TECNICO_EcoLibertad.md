# Manual Técnico - EcoLibertad v1.0.0

**ESTADO:** PRIVADO / DOCUMENTACIÓN TÉCNICA  
**Versión documentada:** 1.0.0  
**Tipo de aplicación:** Aplicación Android Nativa (Kotlin/Compose) integrada con Firebase, Google Drive y Google Apps Script.  
**Zona horaria del backend:** America/Bogota

---

## 1. Alcance
**EcoLibertad** es una plataforma institucional diseñada para la gestión ambiental y el fomento del reciclaje en centros educativos.
*   **Gestión Administrativa:** Control de infraestructura (aulas y cursos) y métricas de evaluación.
*   **Operación en Campo:** Auditorías ambientales mediante códigos QR y captura de evidencias fotográficas.
*   **Visualización de Impacto:** Tablero en tiempo real que convierte auditorías en Kg de reciclaje y puntos de ranking.
*   **Gamificación:** Sistema de incentivos mediante medallas y competencia entre grados.

**Nota sobre almacenamiento de archivos:**  
La aplicación utiliza un flujo híbrido. Los metadatos residen en **Firestore**, mientras que los archivos pesados (imágenes de evidencia) se almacenan en **Google Drive** a través de un puente (Bridge) en Apps Script, optimizando costos y cuotas de Firebase Storage.

---

## 2. Arquitectura
### 2.1 Capas del Proyecto

| Capa | Ubicación | Responsabilidad |
| :--- | :--- | :--- |
| **Interfaz (UI)** | `lib/ui/` | Pantallas declarativas en Jetpack Compose y ViewModels (MVVM). |
| **Navegación** | `lib/ui/navigation/` | Definición de rutas y protección de acceso según el `UserRole`. |
| **Repositorios** | `lib/data/repository/` | Centralización de lógica de datos: Firebase Auth, Firestore y OkHttp para Drive. |
| **Modelos (Dominio)** | `lib/domain/model/` | Entidades puras de datos: `User`, `Room`, `Evaluation`, `Indicator`, etc. |
| **Utilidades** | `lib/util/` | Compresión de imágenes, generación de QR (ZXing) y formateo de datos. |
| **Backend Bridge** | `apps-script/Code.gs` | Endpoint de Google Apps Script que gestiona la carga de archivos en Drive. |

---

## 3. Inicio de la Aplicación
La aplicación utiliza **Hilt** para la inyección de dependencias. 
1.  **`EcoApplication`:** Punto de entrada que inicializa Dagger-Hilt.
2.  **`MainActivity`:** Configura el `NavHost` y observa el estado de autenticación inicial.
3.  **Inicialización de Firebase:** Se realiza automáticamente mediante el plugin de Google Services. La configuración reside en `google-services.json`.

---

## 4. Flujo de Datos y Firebase
### 4.1 Autenticación y Roles
El sistema maneja 4 niveles de acceso definidos en `UserRole.kt`:
*   **ADMIN:** Acceso a gestión de indicadores, aulas, cursos y creación de contenido.
*   **DOCENTE (COMITE_AMBIENTAL):** Permisos para realizar evaluaciones y subir evidencias.
*   **ESTUDIANTE:** Consulta de ranking, impacto de su curso y EcoTips.
*   **INVITADO:** Acceso limitado a visualización de impacto general.

### 4.2 Modelo de Datos en Firestore (Recomendado)
#### **Colección: `users`**
*   `uid`: ID de Auth.
*   `rol`: String (mapeado a enum).
*   `courseId`: ID del curso (solo estudiantes).

#### **Colección: `rooms` (Aulas)**
*   `id`: UID autogenerado.
*   `nombre`: Ej: "Aula 201".
*   `puntosTotales`: Acumulado histórico.

#### **Colección: `evaluations` (Auditorías)**
*   `roomId`: Referencia al aula.
*   `indicadores`: Map<String, Int> (Nombre del indicador : Valor).
*   `evidenciasUrls`: Lista de enlaces públicos de Drive.
*   `puntajeObtenido`: Cálculo calculado en cliente.

---

## 5. Conexión con Google Drive (Evidence Bridge)
### 5.1 Flujo de Subida
Para cada foto capturada:
1.  **Compresión:** `ImageUtils` reduce el peso de la imagen a JPEG.
2.  **Base64:** La imagen se codifica para el transporte.
3.  **POST:** Se envía a `DRIVE_BRIDGE_URL`.
4.  **Respuesta:** Apps Script guarda el archivo en la carpeta institucional, establece permisos de "Cualquiera con el enlace" y retorna la URL pública y el `fileId`.

---

## 6. Lógica de Impacto Ambiental (IA)
El sistema implementa una lógica de agregación automática para indicadores de reciclaje:
*   **Filtrado Semántico:** El `EnvironmentalDashboardViewModel` analiza los mapas de indicadores buscando palabras clave como `"botella"` o `"tapa"`.
*   **Normalización Numérica:** Al leer de Firestore, los valores se convierten explícitamente a `Int` para evitar errores de tipo entre `Long` y `Double`.
*   **Agregación:** Los resultados se suman en tiempo real para alimentar los contadores del Tablero Web.

---

## 7. Diagnóstico y Resolución de Problemas

| Síntoma | Causa Probable | Solución |
| :--- | :--- | :--- |
| **Puntajes en 0** | Error de casting en el Repositorio al recibir `Long` de Firestore. | Verificar el uso de `(it.value as? Number)?.toInt()` en `EcoRepository.kt`. |
| **Fallo en carga de fotos** | Token de Apps Script expirado o permisos de carpeta incorrectos. | Revisar el despliegue del Script como "Web App" y acceso "Anyone". |
| **QR no reconocido** | El `roomId` escaneado no existe en la colección `rooms`. | Validar que el ID del QR coincida con el documento en Firebase. |
| **Ranking desactualizado** | Falta de conectividad o reglas de Firestore bloqueando el incremento. | Comprobar `FieldValue.increment()` y las reglas de escritura del rol actual. |

---

## 8. Consideraciones de Seguridad
*   **Reglas de Firestore:** La escritura en `indicators`, `rooms` y `courses` está restringida exclusivamente a usuarios con `rol == 'ADMIN'`.
*   **Privacidad:** Las evidencias en Drive son públicas mediante enlace, por lo que no se debe subir información personal sensible de estudiantes.
*   **Mantenimiento:** Se recomienda rotar la URL del Bridge de Apps Script anualmente.
