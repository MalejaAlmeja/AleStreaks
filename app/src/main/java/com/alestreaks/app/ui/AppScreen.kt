package com.alestreaks.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task

@Composable
fun AppScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    if (uiState.userId == null) {
        AuthScreen(
            loading = uiState.loading,
            error = uiState.error,
            onSubmit = { email, password, register -> viewModel.signIn(email, password, register) },
            onGoogleSignIn = { viewModel.signInWithGoogle(it) },
        )
        return
    }

    HomeScreen(
        tasks = tasks,
        onAddTask = viewModel::addTask,
        onDone = viewModel::markDone,
        onSkip = viewModel::skip,
        onGenerateReport = viewModel::generateReport,
        reportSummary = uiState.report,
        onSignOut = viewModel::signOut,
    )
}

@Composable
private fun AuthScreen(
    loading: Boolean,
    error: String?,
    onSubmit: (String, String, Boolean) -> Unit,
    onGoogleSignIn: (android.content.Context) -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val fieldShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F7ED))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(Color(0xFF9AB17A), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "AS",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "AleStreaks",
                color = Color(0xFF243126),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Track the habits that matter today.",
                color = Color(0xFF60705C),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = { onGoogleSignIn(context) },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = fieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF243126),
                ),
                border = BorderStroke(1.dp, Color(0xFFD8E1D2)),
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF243126),
                    )
                } else {
                    Text("G", fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.size(12.dp))
                    Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8E1D2))
                Text("or", color = Color(0xFF60705C), style = MaterialTheme.typography.labelMedium)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8E1D2))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                singleLine = true,
                shape = fieldShape,
                label = { Text("Email") },
                colors = authFieldColors(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                singleLine = true,
                shape = fieldShape,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") },
                colors = authFieldColors(),
            )

            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onSubmit(email, password, false) },
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4635)),
                ) {
                    Text("Sign in", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { onSubmit(email, password, true) },
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9AB17A)),
                ) {
                    Text("Register", fontWeight = FontWeight.SemiBold)
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color(0xFFF8FAF6),
    focusedBorderColor = Color(0xFF9AB17A),
    unfocusedBorderColor = Color(0xFFD8E1D2),
    focusedLabelColor = Color(0xFF2F4635),
    cursorColor = Color(0xFF2F4635),
)

@Composable
private fun HomeScreen(
    tasks: List<Task>,
    onAddTask: (String, List<String>, LocationMode) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String, String) -> Unit,
    onGenerateReport: () -> Unit,
    reportSummary: com.alestreaks.app.model.UserReport?,
    onSignOut: () -> Unit,
) {
    var taskName by remember { mutableStateOf("") }
    var reminderInput by remember { mutableStateOf("09:00") }
    var mode by remember { mutableStateOf(LocationMode.BOTH) }
    var skipDialogTaskId by remember { mutableStateOf<String?>(null) }
    var skipReason by remember { mutableStateOf("") }
    var showOverflow by remember { mutableStateOf(false) }

    val visibleTasks = tasks.take(10)
    val hiddenTasks = tasks.drop(10)
    val showing = if (showOverflow) tasks else visibleTasks

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AleStreaks", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("New task") },
        )
        OutlinedTextField(
            value = reminderInput,
            onValueChange = { reminderInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Reminders (comma-separated, max 5)") },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { mode = LocationMode.ENTER }) { Text("Arrive") }
            Button(onClick = { mode = LocationMode.EXIT }) { Text("Leave") }
            Button(onClick = { mode = LocationMode.BOTH }) { Text("Both") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                val reminders = reminderInput.split(",").map { it.trim() }.filter { it.isNotBlank() }.take(5)
                if (taskName.isNotBlank()) {
                    onAddTask(taskName, reminders, mode)
                    taskName = ""
                }
            }) { Text("Add task") }
            Button(onClick = onGenerateReport) { Text("Generate report") }
            Button(onClick = onSignOut) { Text("Sign out") }
        }

        reportSummary?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Done: ${it.totalDone} | Skipped: ${it.totalSkipped} | Top reason: ${it.topSkippedReason}")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(if (showOverflow) "All tasks" else "Today's tasks (max 10)")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(showing, key = { it.id }) { task ->
                TaskCard(task = task, onDone = { onDone(task.id) }, onSkip = { skipDialogTaskId = task.id })
            }
        }

        if (hiddenTasks.isNotEmpty()) {
            TextButton(onClick = { showOverflow = !showOverflow }) {
                Text(if (showOverflow) "Hide overflow" else "View ${hiddenTasks.size} more tasks")
            }
        }
    }

    if (skipDialogTaskId != null) {
        AlertDialog(
            onDismissRequest = { skipDialogTaskId = null },
            title = { Text("Skip reason") },
            text = {
                OutlinedTextField(
                    value = skipReason,
                    onValueChange = { skipReason = it },
                    label = { Text("Why are you skipping?") },
                )
            },
            confirmButton = {
                Button(onClick = {
                    val taskId = skipDialogTaskId
                    if (taskId != null && skipReason.isNotBlank()) {
                        onSkip(taskId, skipReason)
                    }
                    skipReason = ""
                    skipDialogTaskId = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { skipDialogTaskId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun TaskCard(task: Task, onDone: () -> Unit, onSkip: () -> Unit) {
    Card {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(task.title, style = MaterialTheme.typography.titleMedium)
            Text("Reminders: ${task.reminders.joinToString().ifBlank { "none" }}")
            Text("Location: ${task.locationMode} @ ${task.locationRadiusMeters}m")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDone) { Text("Done") }
                Button(onClick = onSkip) { Text("Skip") }
            }
        }
    }
}
