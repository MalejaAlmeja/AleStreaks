package com.alestreaks.app.data

import com.alestreaks.app.model.Completion
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task
import com.alestreaks.app.model.UserReport
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(userId: String): Flow<List<Task>>
    suspend fun addTask(
        userId: String,
        title: String,
        iconKey: String,
        colorHex: String,
        reminders: List<String>,
        locationMode: LocationMode,
        locationRadiusMeters: Int,
    )

    suspend fun completeTask(userId: String, taskId: String)
    suspend fun skipTask(userId: String, taskId: String, reason: String)
    fun observeCompletions(userId: String): Flow<List<Completion>>
    suspend fun buildReport(userId: String): UserReport
}
