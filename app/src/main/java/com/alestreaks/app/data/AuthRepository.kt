package com.alestreaks.app.data

import com.google.firebase.auth.FirebaseAuth
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

    fun signOut() {
        auth.signOut()
    }
}
