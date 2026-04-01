package com.alestreaks.app.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

enum class CompletionStatus { DONE, SKIPPED }
enum class LocationMode { ENTER, EXIT, BOTH, NONE }

data class Task(
    @DocumentId val id: String = "",
    val title: String = "",
    val iconKey: String = "check_circle",
    val colorHex: String = "#9AB17A",
    val reminders: List<String> = emptyList(),
    val locationMode: LocationMode = LocationMode.NONE,
    val locationRadiusMeters: Int = 50,
    val active: Boolean = true,
    val createdAt: Timestamp? = null,
)

data class Completion(
    @DocumentId val id: String = "",
    val taskId: String = "",
    val date: String = "",
    val status: CompletionStatus = CompletionStatus.DONE,
    val skipReason: String? = null,
    val aiReasonValidity: String = "unknown",
    val createdAt: Timestamp? = null,
)

data class UserReport(
    val totalDone: Int,
    val totalSkipped: Int,
    val topSkippedReason: String,
)
