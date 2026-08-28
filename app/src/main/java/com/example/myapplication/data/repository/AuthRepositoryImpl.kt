package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        try {
                            if (snapshot.exists()) {
                                trySend(mapSnapshotToUser(snapshot.id, snapshot.data))
                            } else {
                                trySend(null)
                            }
                        } catch (e: Exception) {
                            trySend(null)
                        }
                    }
                    .addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    override suspend fun login(email: String, pass: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
        val firebaseUser = result.user ?: throw Exception("Error de autenticación")
        
        // Intentar obtener el documento. Si falla por red, Firestore reintentará automáticamente
        // pero aquí forzamos una espera si es necesario.
        val userDoc = try {
            firestore.collection("users").document(firebaseUser.uid).get().await()
        } catch (e: Exception) {
            // Si falla el primer intento, esperamos un segundo y reintentamos 
            // (a veces la conexión de red tarda en activarse después de Auth)
            kotlinx.coroutines.delay(1000)
            firestore.collection("users").document(firebaseUser.uid).get().await()
        }

        if (!userDoc.exists()) throw Exception("El perfil de usuario no existe en la base de datos")
        
        val user = mapSnapshotToUser(firebaseUser.uid, userDoc.data)
        Result.success(user)
    } catch (e: Exception) {
        val message = when {
            e.message?.contains("database (default) does not exist", ignoreCase = true) == true -> 
                "Error: La base de datos Firestore no ha sido creada en la consola de Firebase."
            e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                "Error de Permisos: Revisa las 'Reglas' en la consola de Firebase Firestore."
            e.message?.contains("offline") == true -> "Sin conexión al servidor. Verifica tu internet."
            e.message?.contains("password") == true -> "Contraseña incorrecta."
            e.message?.contains("user-not-found") == true -> "Usuario no encontrado."
            else -> e.localizedMessage ?: "Error al iniciar sesión"
        }
        Result.failure(Exception(message))
    }

    private fun mapSnapshotToUser(uid: String, data: Map<String, Any>?): User {
        val roleStr = (data?.get("rol") ?: data?.get("role"))?.toString() ?: ""
        val rol = when (roleStr.uppercase().trim()) {
            "ADMIN", "ADMINISTRADOR", "DIRECTIVO", "COMITE_AMBIENTAL", "COMITE AMBIENTAL", "COMITÉ AMBIENTAL" -> UserRole.ADMIN
            "DOCENTE", "PROFESOR" -> UserRole.DOCENTE
            "ESTUDIANTE", "ALUMNO" -> UserRole.ESTUDIANTE
            "INVITADO" -> UserRole.INVITADO
            else -> UserRole.ESTUDIANTE
        }
        
        return User(
            uid = uid,
            nombre = (data?.get("nombre") ?: data?.get("name") ?: "Usuario").toString(),
            email = (data?.get("email") ?: "").toString(),
            rol = rol,
            courseId = data?.get("courseId")?.toString()
        )
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun register(user: User, pass: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(user.email.trim(), pass).await()
        val firebaseUser = result.user ?: throw Exception("Fallo al crear usuario")
        
        val userMap = hashMapOf(
            "uid" to firebaseUser.uid,
            "nombre" to user.nombre.trim(),
            "email" to user.email.trim(),
            "rol" to user.rol.name,
            "courseId" to user.courseId
        )
        
        firestore.collection("users").document(firebaseUser.uid).set(userMap).await()
        Result.success(user.copy(uid = firebaseUser.uid))
    } catch (e: Exception) {
        val message = when {
            e.message?.contains("database (default) does not exist", ignoreCase = true) == true -> 
                "Error: La base de datos Firestore no ha sido creada."
            e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                "Error de Permisos: Revisa las 'Reglas' en la consola de Firebase."
            e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                "Este correo ya está registrado."
            else -> e.localizedMessage ?: "Error al registrar usuario"
        }
        Result.failure(Exception(message))
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw Exception("Google Sign-In fallido")
        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()

        if (userDoc.exists()) {
            Result.success(mapSnapshotToUser(firebaseUser.uid, userDoc.data))
        } else {
            val newUser = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nombre = firebaseUser.displayName ?: "Usuario Google",
                rol = UserRole.ESTUDIANTE
            )
            firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            Result.success(newUser)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
