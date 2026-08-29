# Guía Técnica de Módulos - Proyecto EcoLibertad

Este documento detalla los módulos principales del sistema para facilitar la continuidad del desarrollo.

## 1. Módulo de Autenticación y Roles
*   **Componentes:** `AuthViewModel`, `AuthRepository`, `RoleSelectionScreen`.
*   **Lógica:** Gestiona el acceso mediante Firebase Auth. Al registrarse, el usuario elige un rol (`UserRole`). El acceso a las pantallas está restringido en el `NavHost` basado en este rol.
*   **Extensibilidad:** Para añadir un nuevo rol, modificar `UserRole.kt` y actualizar las condiciones de navegación en `MainActivity`.

## 2. Gestión de Aulas y QR (`RoomManagement`)
*   **Componentes:** `RoomManagementScreen`, `QRGenerator`.
*   **Lógica:** Cada aula creada genera un código QR único basado en su `roomId`. Este código es la llave para las evaluaciones.
*   **Detalle Técnico:** El QR contiene un String con la ID del salón. La App usa la librería ZXing para el escaneo.

## 3. Sistema de Evaluación Semanal
*   **Componentes:** `EvaluationViewModel`, `EvaluationFormScreen`, `IndicatorRepository`.
*   **Lógica:** Dinámica. Los indicadores se cargan desde Firestore (`indicators`). El formulario genera un mapa de resultados.
*   **Puntaje:** Se calcula sumando los valores de los indicadores. El total actualiza el `puntosTotales` del curso asociado en una transacción de Firestore.

## 4. Repositorio de Evidencias (Google Drive Bridge)
*   **Componentes:** `EcoRepository`, `DriveBridge` (Google Apps Script).
*   **Lógica:** Las fotos no se guardan en Firestore ni en Firebase Storage para ahorrar cuota. Se envían en Base64 a un Script de Google que las aloja en una carpeta de Drive y devuelve el link público.
*   **Rendimiento:** Las imágenes se comprimen localmente antes del envío usando `ImageUtils`.

## 5. Tablero de Impacto Ambiental (IA)
*   **Componentes:** `EnvironmentalDashboardViewModel`, `EnvironmentalImpact`.
*   **Lógica:** Procesa todas las evaluaciones en tiempo real.
    *   **Filtros Inteligentes:** Usa `.contains("Botella", ignoreCase = true)` para detectar métricas de reciclaje.
    *   **Consumo:** Provee datos tanto a la App Android como al Tablero Web (Hosting).

## 6. Módulo de Gamificación (Ranking y Medallas)
*   **Componentes:** `RankingViewModel`, `BadgeManagement`.
*   **Lógica:** Compara los `puntosTotales` de los `courses`. Las medallas se asignan automáticamente si el puntaje supera el umbral definido en la colección `badges`.

## 7. Tablero Web (Firebase Hosting)
*   **Tecnología:** HTML5/JS puro con Firebase SDK 10.
*   **Lógica:** Escucha cambios en Firestore (`onSnapshot`). Implementa una cola de visualización para rotar evidencias y tips sin recargar la página.
