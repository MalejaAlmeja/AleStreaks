package com.alestreaks.app.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user?.uid
            ?: error("No Firebase user found")
    }

    suspend fun register(email: String, password: String): Result<String> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            ?: error("No Firebase user found")
    }

    suspend fun signInWithGoogle(context: Context): Result<String> = runCatching {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.defaultWebClientId())
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)

        auth.signInWithCredential(firebaseCredential).await().user?.uid
            ?: error("No Firebase user found")
    }

    fun signOut() {
        auth.signOut()
    }

    private fun Context.defaultWebClientId(): String {
        val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (resourceId == 0) {
            error("Google Sign-In needs an updated app/google-services.json with the OAuth web client.")
        }
        return getString(resourceId)
    }
}
