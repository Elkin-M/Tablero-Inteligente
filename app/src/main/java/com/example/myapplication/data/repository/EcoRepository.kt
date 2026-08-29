package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.*
import com.example.myapplication.util.ImageUtils
import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class EcoRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {
    private val DRIVE_BRIDGE_URL = "https://script.google.com/macros/s/AKfycbxyv3-zF5VjKEHkTAvGFypa5ISLBdOhX-4EECQVrFjRoSHk9W5zi3yU2bhAU7vFmbT2jg/exec"

    fun getRankingFlow(): Flow<List<Course>> = kotlinx.coroutines.flow.combine(
        getCoursesFlow(),
        getRoomsFlow()
    ) { courses: List<Course>, rooms: List<Room> ->
        val roomsAsCourses = rooms.map { room ->
            Course(
                id = room.id,
                nombre = room.nombre,
                puntosTotales = room.puntosTotales,
                embajadorAmbiental = room.embajadorAmbiental
            )
        }
        (courses + roomsAsCourses).sortedByDescending { it.puntosTotales }
    }

    fun getUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        User(
                            uid = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            email = doc.getString("email") ?: "",
                            rol = UserRole.valueOf(doc.getString("rol") ?: UserRole.ESTUDIANTE.name),
                            courseId = doc.getString("courseId")
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    fun getCoursesFlow(): Flow<List<Course>> = callbackFlow {
        val listener = firestore.collection("courses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Course(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            puntosTotales = (doc.getLong("puntosTotales") ?: 0L).toInt(),
                            embajadorAmbiental = doc.getString("embajadorAmbiental") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { listener.remove() }
    }

    fun getRoomsFlow(): Flow<List<Room>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Room(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            bloque = doc.getString("bloque") ?: "",
                            active = doc.getBoolean("active") ?: true,
                            fechaRegistro = doc.getLong("fechaRegistro") ?: System.currentTimeMillis(),
                            docenteId = doc.getString("docenteId") ?: "",
                            puntosTotales = (doc.getLong("puntosTotales") ?: 0L).toInt(),
                            embajadorAmbiental = doc.getString("embajadorAmbiental") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(rooms)
            }
        awaitClose { listener.remove() }
    }

    fun getChallengesFlow(): Flow<List<Challenge>> = callbackFlow {
        val listener = firestore.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Challenge(
                            id = doc.id,
                            titulo = doc.getString("titulo") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            puntos = (doc.getLong("puntos") ?: 0L).toInt(),
                            activo = doc.getBoolean("activo") ?: true,
                            fechaExpiracion = doc.getLong("fechaExpiracion")
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    fun getCampaignsFlow(): Flow<List<Campaign>> = callbackFlow {
        val listener = firestore.collection("campaigns")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val campaigns = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Campaign(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            fechaInicio = doc.getLong("fechaInicio") ?: 0L,
                            fechaFin = doc.getLong("fechaFin") ?: 0L,
                            activa = doc.getBoolean("activa") ?: true
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(campaigns)
            }
        awaitClose { listener.remove() }
    }

    fun getBadgesFlow(): Flow<List<Badge>> = callbackFlow {
        val listener = firestore.collection("badges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val badges = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Badge(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            icono = doc.getString("icono") ?: "military_tech",
                            puntosRequeridos = (doc.getLong("puntosRequeridos") ?: 0L).toInt()
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(badges)
            }
        awaitClose { listener.remove() }
    }

    fun getIndicatorsFlow(): Flow<List<Indicator>> = callbackFlow {
        val listener = firestore.collection("indicators")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val indicators = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Indicator(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            categoria = doc.getString("categoria") ?: "",
                            valorMaximo = (doc.getLong("valorMaximo") ?: 5L).toInt(),
                            activo = doc.getBoolean("activo") ?: true,
                            esContador = doc.getBoolean("esContador") ?: false
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(indicators)
            }
        awaitClose { listener.remove() }
    }

    fun getEventsFlow(): Flow<List<EcoEvent>> = callbackFlow {
        val listener = firestore.collection("events")
            .whereEqualTo("activa", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.toObjects(EcoEvent::class.java) ?: emptyList()
                trySend(events.sortedBy { it.fecha })
            }
        awaitClose { listener.remove() }
    }

    fun getTipsFlow(): Flow<List<EcoTip>> = callbackFlow {
        val listener = firestore.collection("tips")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val tips = snapshot?.toObjects(EcoTip::class.java) ?: emptyList()
                val filteredAndSorted = tips.filter { it.activa }
                    .sortedByDescending { it.fecha }
                trySend(filteredAndSorted)
            }
        awaitClose { listener.remove() }
    }

    fun getEvaluationsFlow(): Flow<List<Evaluation>> = callbackFlow {
        val listener = firestore.collection("evaluations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Evaluation(
                            id = doc.id,
                            roomId = doc.getString("roomId") ?: "",
                            docenteId = doc.getString("docenteId") ?: "",
                            courseId = doc.getString("courseId") ?: "",
                            fecha = doc.getLong("fecha") ?: 0L,
                            puntajeObtenido = (doc.getLong("puntajeObtenido") ?: 0L).toInt(),
                            evidenciasUrls = (doc.get("evidenciasUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            indicadores = (doc.get("indicadores") as? Map<*, *>)?.entries?.associate {
                                it.key.toString() to ((it.value as? Long)?.toInt() ?: 0)
                            } ?: emptyMap(),
                            observaciones = doc.getString("observaciones") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                
                // Ordenamos localmente para evitar requerir un índice en Firestore
                val sortedEvaluations = evaluations.sortedByDescending { it.fecha }
                trySend(sortedEvaluations)
            }
        awaitClose { listener.remove() }
    }

    fun getBaselinesFlow(): Flow<List<BaselineDiagnostic>> = callbackFlow {
        val listener = firestore.collection("baseline_diagnostics")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val baselines = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        BaselineDiagnostic(
                            roomId = doc.getString("roomId") ?: "",
                            estadoLimpieza = (doc.getLong("estadoLimpieza") ?: 0L).toInt(),
                            clasificacionResiduos = (doc.getLong("clasificacionResiduos") ?: 0L).toInt(),
                            ahorroEnergia = (doc.getLong("ahorroEnergia") ?: 0L).toInt(),
                            cuidadoMobiliario = (doc.getLong("cuidadoMobiliario") ?: 0L).toInt(),
                            participacionAmbiental = (doc.getLong("participacionAmbiental") ?: 0L).toInt(),
                            fecha = doc.getLong("fecha") ?: 0L
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(baselines)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createRoom(nombre: String, bloque: String, docenteId: String): Result<Unit> = try {
        val room = hashMapOf(
            "nombre" to nombre,
            "bloque" to bloque,
            "active" to true,
            "fechaRegistro" to System.currentTimeMillis(),
            "docenteId" to docenteId,
            "puntosTotales" to 0,
            "embajadorAmbiental" to ""
        )
        firestore.collection("rooms").add(room).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveBaseline(diagnostic: BaselineDiagnostic): Result<Unit> = try {
        firestore.collection("baseline_diagnostics").add(diagnostic).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun toggleRoomStatus(roomId: String, currentStatus: Boolean): Result<Unit> = try {
        firestore.collection("rooms").document(roomId).update("active", !currentStatus).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveEvaluation(evaluation: Evaluation, imageUris: List<String>): Result<Unit> = try {
        val uploadedUrls = imageUris.mapNotNull { uriString ->
            uploadToDriveBridge(uriString)
        }
        
        val finalEvaluation = evaluation.copy(evidenciasUrls = uploadedUrls)
        firestore.collection("evaluations").add(finalEvaluation).await()
        
        if (evaluation.courseId.isNotEmpty()) {
            firestore.collection("courses").document(evaluation.courseId)
                .update("puntosTotales", com.google.firebase.firestore.FieldValue.increment(evaluation.puntajeObtenido.toLong()))
                .await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadToDriveBridge(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val bytes = ImageUtils.compressImage(context, uri) ?: return null
            val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            
            val json = org.json.JSONObject().apply {
                put("base64Image", base64Image)
                put("mimeType", "image/jpeg")
                put("fileName", "evidencia_${System.currentTimeMillis()}.jpg")
            }

            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(DRIVE_BRIDGE_URL)
                .post(json.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val resJson = org.json.JSONObject(response.body?.string() ?: "")
                if (resJson.getString("status") == "success") {
                    resJson.getString("url")
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserRole(uid: String, newRole: UserRole, courseId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>(
            "rol" to newRole.name
        )
        if (courseId != null) {
            updates["courseId"] = courseId
        } else if (newRole != UserRole.ESTUDIANTE) {
            updates["courseId"] = com.google.firebase.firestore.FieldValue.delete()
        }
        
        firestore.collection("users").document(uid).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createCourse(nombre: String): Result<Unit> = try {
        val course = hashMapOf(
            "nombre" to nombre,
            "puntosTotales" to 0,
            "embajadorAmbiental" to ""
        )
        firestore.collection("courses").add(course).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteCourse(courseId: String): Result<Unit> = try {
        firestore.collection("courses").document(courseId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRoom(room: Room): Result<Unit> = try {
        firestore.collection("rooms").document(room.id).set(room).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> = try {
        firestore.collection("rooms").document(roomId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createChallenge(challenge: Challenge): Result<Unit> = try {
        firestore.collection("challenges").add(challenge).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateChallenge(challenge: Challenge): Result<Unit> = try {
        firestore.collection("challenges").document(challenge.id).set(challenge).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createCampaign(campaign: Campaign): Result<Unit> = try {
        firestore.collection("campaigns").add(campaign).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createBadge(badge: Badge): Result<Unit> = try {
        firestore.collection("badges").add(badge).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createIndicator(indicator: Indicator): Result<Unit> = try {
        firestore.collection("indicators").add(indicator).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateIndicator(indicator: Indicator): Result<Unit> = try {
        firestore.collection("indicators").document(indicator.id).set(indicator).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteIndicator(indicatorId: String): Result<Unit> = try {
        firestore.collection("indicators").document(indicatorId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateEvaluationScore(evaluationId: String, score: Int): Result<Unit> = try {
        firestore.runTransaction { transaction ->
            val evalRef = firestore.collection("evaluations").document(evaluationId)
            val evaluation = transaction.get(evalRef)
            
            if (evaluation.exists()) {
                val roomId = evaluation.getString("roomId") ?: ""
                val courseId = evaluation.getString("courseId") ?: ""
                
                transaction.update(evalRef, "puntajeObtenido", score)
                
                // Actualizar puntos totales en Room o Course
                if (courseId.isNotEmpty()) {
                    val courseRef = firestore.collection("courses").document(courseId)
                    val currentPoints = transaction.get(courseRef).getLong("puntosTotales") ?: 0L
                    transaction.update(courseRef, "puntosTotales", currentPoints + (score).toLong())
                } else if (roomId.isNotEmpty()) {
                    val roomRef = firestore.collection("rooms").document(roomId)
                    val currentPoints = transaction.get(roomRef).getLong("puntosTotales") ?: 0L
                    transaction.update(roomRef, "puntosTotales", currentPoints + (score).toLong())
                }
            }
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createEvent(event: EcoEvent): Result<Unit> = try {
        val doc = firestore.collection("events").document()
        firestore.collection("events").document(doc.id).set(event.copy(id = doc.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createTip(tip: EcoTip): Result<Unit> = try {
        // Obtenemos todos los tips para filtrar y ordenar localmente, evitando la necesidad de índices compuestos
        val tipsSnapshot = firestore.collection("tips")
            .get()
            .await()

        val activeTips = tipsSnapshot.documents.mapNotNull { doc ->
            try {
                val t = doc.toObject(EcoTip::class.java)
                t?.copy(id = doc.id)
            } catch (e: Exception) { null }
        }.filter { it.activa }.sortedBy { it.fecha }

        if (activeTips.size >= 3) {
            val oldestTip = activeTips.first()
            firestore.collection("tips").document(oldestTip.id).delete().await()
        }

        val doc = firestore.collection("tips").document()
        firestore.collection("tips").document(doc.id).set(tip.copy(id = doc.id, fecha = System.currentTimeMillis())).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
