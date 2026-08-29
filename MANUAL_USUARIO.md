# Manual de Usuario Extendido - Ecosistema EcoLibertad

Este manual proporciona instrucciones detalladas para cada rol dentro de la plataforma EcoLibertad, cubriendo todas las funcionalidades desde la administración hasta el uso público.

---

## 1. Módulo Transversal: Registro e Inicio de Sesión
### **1.1 Creación de Cuenta**
1. Al abrir la app, presione **"Registrarse"**.
2. Ingrese su **Nombre Completo**, **Correo Electrónico Institucional** y una **Contraseña** segura.
3. **Selección de Rol:** Es crucial elegir el rol correcto:
   *   **Estudiante:** Debe elegir su curso de la lista desplegable para que sus puntos sumen al ranking correcto.
   *   **Docente:** Permite realizar auditorías ambientales.
   *   **Administrador:** Acceso a la configuración global (requiere validación previa).
4. Finalice pulsando **"Crear Cuenta"**.

### **1.2 Recuperación de Acceso**
*   Si olvida su contraseña, utilice la opción "Olvidé mi contraseña" en la pantalla de inicio para recibir un enlace de restablecimiento en su correo.

---

## 2. Rol: Administrador (Gestión Estratégica)
El administrador es el arquitecto del sistema ambiental.

### **2.1 Configuración de Infraestructura (Aulas y Cursos)**
1. **Crear Aula:** Vaya a **"Gestión de Aulas"** > Botón **"+"**. Ingrese nombre (ej: "Salón 101") y Bloque.
2. **Generar QR:** Cada aula genera un código único. Pulse sobre el aula en la lista para ver el QR.
   *   **Actividad Sugerida:** Imprima los códigos y colóquelos en la entrada de cada salón. Esto permite que los docentes evalúen sin buscar el salón manualmente.
3. **Gestión de Cursos:** En el módulo de cursos, cree los grupos académicos (ej: "Grado 11-02"). Estos son los que compiten en el ranking.

### **2.2 Definición de Indicadores Ambiental**
1. Ingrese a **"Indicadores"**.
2. **Agregar Nuevo:** Defina el nombre del indicador (ej: "Limpieza de pisos", "Recolección de Tapas").
3. **Tipo de Dato:**
   *   Si es una calificación, defina el **Valor Máximo** (ej: 5).
   *   Si es reciclaje, active el interruptor **"Es Contador"**. Esto hará que el sistema sume estos valores en Kilogramos (Kg) en lugar de puntos simples.

### **2.3 Auditoría de Evidencias**
1. Vaya a **"Gestión de Evidencias"**.
2. Podrá ver todas las fotos subidas por los docentes, filtradas por fecha y salón. Úselo para verificar que las puntuaciones asignadas coincidan con la realidad física.

---

## 3. Rol: Docente / Evaluador (Auditoría de Campo)
El docente garantiza la veracidad de los datos ambientales.

### **3.1 Ejecución de una Auditoría Ambiental**
1. **Inicio:** En el dashboard, pulse el icono de **Cámara/QR**.
2. **Escaneo:** Apunte al código QR del salón. La app reconocerá automáticamente el aula.
3. **Calificación:**
   *   Mueva los deslizadores o ingrese los números según el estado del salón.
   *   Sea preciso con los Kg de botellas/tapas, ya que esto impacta el contador global.
4. **Registro Fotográfico:** Pulse **"Añadir Foto"**. Tome fotos de los puntos positivos y áreas de mejora.
5. **Comentarios:** Escriba recomendaciones (ej: "Se sugiere vaciar el bote de basura").
6. **Envío:** Pulse **"Finalizar Evaluación"**.

---

## 4. Rol: Estudiante (Participación y Mejora)
El estudiante es el protagonista del cambio.

### **4.1 Monitoreo de Desempeño**
1. **Pestaña Mi Salón:** Vea el puntaje actual de su curso y su posición en el ranking.
2. **Feedback Directo:** Revise las "Evaluaciones Recientes" para leer qué dijo el docente sobre su salón.
3. **Retos y Tips:** Lea los "EcoTips" diarios para aprender nuevas formas de sumar puntos (ej: cómo clasificar mejor los residuos).

---

## 5. Tablero Público IA (Visualización Comunitaria)
Ideal para proyectar en pantallas de pasillos o cafeterías.

*   **URL:** `https://tablero-inteligente.web.app/`
*   **Contenidos Automáticos:**
    *   **Kg Totales:** Suma en tiempo real de todo el reciclaje de la institución.
    *   **Galería Dinámica:** Rotación de las mejores fotos de evidencias.
    *   **Top 15:** Los salones que lideran la semana.
    *   **Educación:** EcoTips que cambian cada 5 segundos para mantener el interés.

---

## 6. Soporte Técnico para Usuarios
*   **Problema:** "La cámara no abre".
    *   *Solución:* Vaya a los ajustes de su teléfono > Aplicaciones > EcoLibertad > Permisos y asegúrese de que "Cámara" esté permitido.
*   **Problema:** "Mis puntos no suben".
    *   *Solución:* Los puntos se actualizan tras el envío exitoso de la evaluación del docente. Asegúrese de tener conexión a internet al enviar.
