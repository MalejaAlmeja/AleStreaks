package com.alestreaks.app.data

import com.alestreaks.app.util.AiReasonValidator
import com.alestreaks.app.model.Completion
import com.alestreaks.app.model.CompletionStatus
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task
import com.alestreaks.app.model.UserReport
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirestoreTaskRepository(
    private val db: FirebaseFirestore,
    private val aiReasonValidator: AiReasonValidator,
) : TaskRepository {

    override fun observeTasks(userId: String): Flow<List<Task>> = callbackFlow {
        val sub = db.collection("users").document(userId).collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val tasks = snapshot.documents.mapNotNull { it.toObject<Task>()?.copy(id = it.id) }
                trySend(tasks)
            }
        awaitClose { sub.remove() }
    }

    override suspend fun addTask(
        userId: String,
        title: String,
        iconKey: String,
        colorHex: String,
        reminders: List<String>,
        locationMode: LocationMode,
        locationRadiusMeters: Int,
    ) {
        val task = Task(
            title = title,
            iconKey = iconKey,
            colorHex = colorHex,
            reminders = reminders.take(5),
            locationMode = locationMode,
            locationRadiusMeters = locationRadiusMeters,
            active = true,
            createdAt = Timestamp.now(),
        )

        db.collection("users")
            .document(userId)
            .collection("tasks")
            .add(task)
            .await()
    }

    override suspend fun completeTask(userId: String, taskId: String) {
        val completion = Completion(
            taskId = taskId,
            date = LocalDate.now().toString(),
            status = CompletionStatus.DONE,
            createdAt = Timestamp.now(),
        )
        db.collection("users").document(userId).collection("taskCompletions").add(completion).await()
    }

    override suspend fun skipTask(userId: String, taskId: String, reason: String) {
        val validity = aiReasonValidator.classify(reason)
        val completion = Completion(
            taskId = taskId,
            date = LocalDate.now().toString(),
            status = CompletionStatus.SKIPPED,
            skipReason = reason,
            aiReasonValidity = validity,
            createdAt = Timestamp.now(),
        )
        db.collection("users").document(userId).collection("taskCompletions").add(completion).await()
    }

    override fun observeCompletions(userId: String): Flow<List<Completion>> = callbackFlow {
        val sub = db.collection("users").document(userId).collection("taskCompletions")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot.documents.mapNotNull { it.toObject<Completion>()?.copy(id = it.id) }
                trySend(items)
            }
        awaitClose { sub.remove() }
    }

    override suspend fun buildReport(userId: String): UserReport {
        val entries = db.collection("users").document(userId).collection("taskCompletions")
            .get().await().documents.mapNotNull { it.toObject<Completion>() }

        val done = entries.count { it.status == CompletionStatus.DONE }
        val skipped = entries.count { it.status == CompletionStatus.SKIPPED }
        val reason = entries
            .filter { it.status == CompletionStatus.SKIPPED }
            .mapNotNull { it.skipReason }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?: "No skip reasons yet"

        return UserReport(done, skipped, reason)
    }
}
