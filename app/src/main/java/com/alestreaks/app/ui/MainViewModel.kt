package com.alestreaks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alestreaks.app.data.AuthRepository
import com.alestreaks.app.data.TaskRepository
import com.alestreaks.app.model.Completion
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task
import com.alestreaks.app.model.UserReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val userId: String? = null,
    val loading: Boolean = false,
    val report: UserReport? = null,
    val error: String? = null,
)

class MainViewModel(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(userId = authRepository.currentUserId))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _completions = MutableStateFlow<List<Completion>>(emptyList())
    val completions: StateFlow<List<Completion>> = _completions.asStateFlow()

    init {
        refreshStreams()
    }

    private fun refreshStreams() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            taskRepository.observeTasks(uid).collect { _tasks.value = it }
        }
        viewModelScope.launch {
            taskRepository.observeCompletions(uid).collect { _completions.value = it }
        }
    }

    fun signIn(email: String, password: String, register: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val result = if (register) authRepository.register(email, password)
            else authRepository.signIn(email, password)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(userId = it, error = null)
                refreshStreams()
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
            _uiState.value = _uiState.value.copy(loading = false)
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = UiState(userId = null)
        _tasks.value = emptyList()
        _completions.value = emptyList()
    }

    fun addTask(title: String, reminders: List<String>, locationMode: LocationMode) {
        val uid = _uiState.value.userId ?: return
        viewModelScope.launch {
            runCatching {
                taskRepository.addTask(
                    userId = uid,
                    title = title,
                    iconKey = "check_circle",
                    colorHex = "#9AB17A",
                    reminders = reminders.take(5),
                    locationMode = locationMode,
                    locationRadiusMeters = 50,
                )
            }.onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun markDone(taskId: String) {
        val uid = _uiState.value.userId ?: return
        viewModelScope.launch { taskRepository.completeTask(uid, taskId) }
    }

    fun skip(taskId: String, reason: String) {
        val uid = _uiState.value.userId ?: return
        viewModelScope.launch { taskRepository.skipTask(uid, taskId, reason) }
    }

    fun generateReport() {
        val uid = _uiState.value.userId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val report = taskRepository.buildReport(uid)
            _uiState.value = _uiState.value.copy(report = report, loading = false)
        }
    }
}
