package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.remote.DriveServiceHelper
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.CourseRepositoryImpl
import com.example.myapplication.data.repository.DriveRepositoryImpl
import com.example.myapplication.data.repository.EvaluationRepositoryImpl
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.CourseRepository
import com.example.myapplication.domain.repository.DriveRepository
import com.example.myapplication.domain.repository.EvaluationRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Collections
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val WEB_CLIENT_ID = "391565457119-7aa89g65d29ddnm4o7v648q9m9aj0rqd.apps.googleusercontent.com"

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_FILE))
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideDriveServiceHelper(@ApplicationContext context: Context, auth: FirebaseAuth): DriveServiceHelper? {
        val userEmail = auth.currentUser?.email ?: return null
        return try {
            val credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = android.accounts.Account(userEmail, "com.google")
            val driveService = Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("EcoLibertad").build()
            DriveServiceHelper(driveService)
        } catch (e: Exception) { null }
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, googleSignInClient: GoogleSignInClient): AuthRepository = 
        AuthRepositoryImpl(auth, firestore, googleSignInClient)

    @Provides
    @Singleton
    fun provideCourseRepository(firestore: FirebaseFirestore): CourseRepository = CourseRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideEvaluationRepository(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): EvaluationRepository = 
        EvaluationRepositoryImpl(firestore, context)

    @Provides
    @Singleton
    fun provideDriveRepository(driveServiceHelper: DriveServiceHelper?): DriveRepository = DriveRepositoryImpl(driveServiceHelper)
}
