package com.alestreaks.app.util

import com.alestreaks.app.data.AuthRepository
import com.alestreaks.app.data.FirestoreTaskRepository
import com.alestreaks.app.data.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object ServiceLocator {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    val authRepository: AuthRepository by lazy { AuthRepository(auth) }

    val taskRepository: TaskRepository by lazy {
        FirestoreTaskRepository(
            db = firestore,
            aiReasonValidator = AiReasonValidator(),
        )
    }
}
