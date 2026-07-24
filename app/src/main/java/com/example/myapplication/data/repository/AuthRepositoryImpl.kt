package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, _ ->
                        if (snapshot != null && snapshot.exists()) {
                            trySend(mapSnapshotToUser(snapshot.id, snapshot.data))
                        } else {
                            trySend(null)
                        }
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, pass: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, pass).await()
        val firebaseUser = result.user ?: throw Exception("User not found")
        
        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
        if (!userDoc.exists()) throw Exception("User profile not found")
        
        val user = mapSnapshotToUser(firebaseUser.uid, userDoc.data)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun mapSnapshotToUser(uid: String, data: Map<String, Any>?): User {
        val nombre = data?.get("nombre") as? String ?: data?.get("name") as? String ?: ""
        val email = data?.get("email") as? String ?: ""
        val roleStr = data?.get("rol") as? String ?: data?.get("role") as? String
        
        val rol = when (roleStr?.uppercase()) {
            "ADMIN", "ADMINISTRADOR" -> UserRole.ADMIN
            "DIRECTIVO" -> UserRole.DIRECTIVO
            "DOCENTE", "PROFESOR" -> UserRole.DOCENTE
            "COMITE_AMBIENTAL", "COMITE_AMBIENTAL" -> UserRole.COMITE_AMBIENTAL
            "ESTUDIANTE", "ALUMNO" -> UserRole.ESTUDIANTE
            else -> UserRole.ESTUDIANTE
        }
        
        return User(
            uid = uid,
            nombre = nombre,
            email = email,
            rol = rol,
            courseId = data?.get("courseId") as? String
        )
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun register(user: User, pass: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(user.email, pass).await()
        val firebaseUser = result.user ?: throw Exception("Registration failed")
        
        val newUser = user.copy(uid = firebaseUser.uid)
        firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
        
        Result.success(newUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw Exception("Google Sign-In failed")

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
