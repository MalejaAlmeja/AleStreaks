package com.alestreaks.app.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alestreaks.app.model.Completion
import com.alestreaks.app.model.CompletionStatus
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task
import com.alestreaks.app.model.UserReport
import java.time.LocalDate

private val AppBackground = Color(0xFFF2F7ED)
private val Ink = Color(0xFF243126)
private val MutedInk = Color(0xFF60705C)
private val Leaf = Color(0xFF9AB17A)
private val DeepLeaf = Color(0xFF2F4635)
private val Mist = Color(0xFFB4D3D9)
private val Citrus = Color(0xFFE9A34A)
private val Line = Color(0xFFD8E1D2)
private val Panel = Color.White

private enum class HomeSection { Habits, New, Insights }

private data class HabitIcon(val key: String, val label: String, val icon: ImageVector)
private data class HabitStats(
    val streak: Int,
    val doneToday: Boolean,
    val doneCount: Int,
    val skippedCount: Int,
)

private val HabitIcons = listOf(
    HabitIcon("check_circle", "Core", Icons.Outlined.CheckCircle),
    HabitIcon("run", "Run", Icons.Outlined.DirectionsRun),
    HabitIcon("water", "Water", Icons.Outlined.LocalDrink),
    HabitIcon("read", "Read", Icons.Outlined.MenuBook),
    HabitIcon("fitness", "Train", Icons.Outlined.FitnessCenter),
    HabitIcon("food", "Food", Icons.Outlined.Restaurant),
    HabitIcon("mind", "Mind", Icons.Outlined.SelfImprovement),
)

private val HabitColors = listOf("#9AB17A", "#B4D3D9", "#E9A34A", "#7FA37E", "#8A6F56")

@Composable
fun AppScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val completions by viewModel.completions.collectAsStateWithLifecycle()

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
        completions = completions,
        onAddTask = { title, reminders, locationMode, iconKey, colorHex, radius ->
            viewModel.addTask(title, reminders, locationMode, iconKey, colorHex, radius)
        },
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
    onGoogleSignIn: (Context) -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val fieldShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoMark(size = 92, textStyle = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "AleStreaks",
                color = Ink,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Track the habits that matter today.",
                color = MutedInk,
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
                    containerColor = Panel,
                    contentColor = Ink,
                ),
                border = BorderStroke(1.dp, Line),
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Ink,
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
                HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
                Text("or", color = MutedInk, style = MaterialTheme.typography.labelMedium)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
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
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
                ) {
                    Text("Sign in", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { onSubmit(email, password, true) },
                    enabled = !loading,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Leaf),
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
private fun HomeScreen(
    tasks: List<Task>,
    completions: List<Completion>,
    onAddTask: (String, List<String>, LocationMode, String, String, Int) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String, String) -> Unit,
    onGenerateReport: () -> Unit,
    reportSummary: UserReport?,
    onSignOut: () -> Unit,
) {
    var section by remember { mutableStateOf(HomeSection.Habits) }
    var selectedTaskId by remember(tasks) { mutableStateOf(tasks.firstOrNull()?.id) }
    var skipDialogTaskId by remember { mutableStateOf<String?>(null) }
    var skipReason by remember { mutableStateOf("") }

    val selectedTask = tasks.firstOrNull { it.id == selectedTaskId } ?: tasks.firstOrNull()
    val totalDoneToday = tasks.count { habitStats(it, completions).doneToday }
    val totalStreak = tasks.sumOf { habitStats(it, completions).streak }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
    ) {
        AppSidebar(
            selected = section,
            onSelected = { section = it },
            onSignOut = onSignOut,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(18.dp),
        ) {
            HeaderBar(
                tasksCount = tasks.size,
                doneToday = totalDoneToday,
                totalStreak = totalStreak,
                onNewHabit = { section = HomeSection.New },
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (section) {
                HomeSection.Habits -> HabitsSection(
                    tasks = tasks,
                    completions = completions,
                    selectedTask = selectedTask,
                    onSelectTask = { selectedTaskId = it.id },
                    onDone = onDone,
                    onSkip = { skipDialogTaskId = it },
                    onNewHabit = { section = HomeSection.New },
                )

                HomeSection.New -> NewHabitSection(
                    onAddTask = { title, reminders, mode, icon, color, radius ->
                        onAddTask(title, reminders, mode, icon, color, radius)
                        section = HomeSection.Habits
                    },
                )

                HomeSection.Insights -> InsightsSection(
                    tasks = tasks,
                    completions = completions,
                    reportSummary = reportSummary,
                    onGenerateReport = onGenerateReport,
                )
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
                    label = { Text("What got in the way?") },
                    shape = RoundedCornerShape(8.dp),
                    colors = authFieldColors(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val taskId = skipDialogTaskId
                        if (taskId != null && skipReason.isNotBlank()) {
                            onSkip(taskId, skipReason)
                        }
                        skipReason = ""
                        skipDialogTaskId = null
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { skipDialogTaskId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun AppSidebar(
    selected: HomeSection,
    onSelected: (HomeSection) -> Unit,
    onSignOut: () -> Unit,
) {
    NavigationRail(
        modifier = Modifier
            .width(86.dp)
            .fillMaxHeight(),
        containerColor = Color(0xFFE9F0E4),
        header = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(12.dp))
                LogoMark(size = 48, textStyle = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(18.dp))
            }
        },
    ) {
        SidebarItem(HomeSection.Habits, selected, Icons.Outlined.Home, "Habits", onSelected)
        SidebarItem(HomeSection.New, selected, Icons.Outlined.Add, "New", onSelected)
        SidebarItem(HomeSection.Insights, selected, Icons.Outlined.Timeline, "Stats", onSelected)
        Spacer(modifier = Modifier.weight(1f))
        NavigationRailItem(
            selected = false,
            onClick = onSignOut,
            icon = { Icon(Icons.Outlined.Close, contentDescription = null) },
            label = { Text("Exit") },
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun SidebarItem(
    section: HomeSection,
    selected: HomeSection,
    icon: ImageVector,
    label: String,
    onSelected: (HomeSection) -> Unit,
) {
    NavigationRailItem(
        selected = selected == section,
        onClick = { onSelected(section) },
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
    )
}

@Composable
private fun HeaderBar(
    tasksCount: Int,
    doneToday: Int,
    totalStreak: Int,
    onNewHabit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "AleStreaks",
                color = Ink,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "$doneToday of $tasksCount complete today",
                color = MutedInk,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        StatPill(label = "Total streak", value = "$totalStreak")
        Button(
            onClick = onNewHabit,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
            modifier = Modifier.height(46.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Habit")
        }
    }
}

@Composable
private fun HabitsSection(
    tasks: List<Task>,
    completions: List<Completion>,
    selectedTask: Task?,
    onSelectTask: (Task) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String) -> Unit,
    onNewHabit: () -> Unit,
) {
    if (tasks.isEmpty()) {
        EmptyHabits(onNewHabit = onNewHabit)
        return
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(tasks, key = { it.id }) { task ->
                HabitCard(
                    task = task,
                    stats = habitStats(task, completions),
                    selected = selectedTask?.id == task.id,
                    onClick = { onSelectTask(task) },
                    onDone = { onDone(task.id) },
                    onSkip = { onSkip(task.id) },
                )
            }
        }

        if (selectedTask != null) {
            HabitDetailPanel(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight(),
                task = selectedTask,
                stats = habitStats(selectedTask, completions),
                completions = completions.filter { it.taskId == selectedTask.id },
                onDone = { onDone(selectedTask.id) },
                onSkip = { onSkip(selectedTask.id) },
            )
        }
    }
}

@Composable
private fun EmptyHabits(onNewHabit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LogoMark(size = 72, textStyle = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No habits yet", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Create your first streak and keep the chain alive.",
                color = MutedInk,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNewHabit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
            ) {
                Text("Create habit")
            }
        }
    }
}

@Composable
private fun HabitCard(
    task: Task,
    stats: HabitStats,
    selected: Boolean,
    onClick: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
) {
    val accent = parseColor(task.colorHex)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFFBFDF8) else Panel),
        border = BorderStroke(1.dp, if (selected) accent else Line),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HabitIconBadge(iconKey = task.iconKey, color = accent, size = 52)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = task.title,
                    color = Ink,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    MiniMetric("${stats.streak}d", "streak", Citrus)
                    MiniMetric("${stats.doneCount}", "done", Leaf)
                    if (task.reminders.isNotEmpty()) {
                        Text(task.reminders.joinToString("  "), color = MutedInk, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDone,
                    enabled = !stats.doneToday,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (stats.doneToday) Leaf else DeepLeaf),
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (stats.doneToday) "Done" else "Mark")
                }
                TextButton(onClick = onSkip) {
                    Text("Skip", color = MutedInk)
                }
            }
        }
    }
}

@Composable
private fun HabitDetailPanel(
    modifier: Modifier,
    task: Task,
    stats: HabitStats,
    completions: List<Completion>,
    onDone: () -> Unit,
    onSkip: () -> Unit,
) {
    val accent = parseColor(task.colorHex)
    Surface(
        modifier = modifier,
        color = Panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HabitIconBadge(iconKey = task.iconKey, color = accent, size = 58)
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Today ${if (stats.doneToday) "complete" else "pending"}", color = MutedInk)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                BigMetric(modifier = Modifier.weight(1f), value = "${stats.streak}", label = "day streak", color = Citrus)
                BigMetric(modifier = Modifier.weight(1f), value = "${stats.doneCount}", label = "done", color = Leaf)
            }

            Surface(color = Color(0xFFF8FAF6), shape = RoundedCornerShape(8.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailLine(Icons.Outlined.Timeline, "Reminders", task.reminders.joinToString(", ").ifBlank { "None" })
                    DetailLine(Icons.Outlined.LocationOn, "Location", "${task.locationMode} / ${task.locationRadiusMeters}m")
                    DetailLine(Icons.Outlined.Flag, "Skipped", "${stats.skippedCount} times")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onDone,
                    enabled = !stats.doneToday,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
                ) {
                    Text(if (stats.doneToday) "Complete" else "Complete today")
                }
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line),
                ) {
                    Text("Skip", color = Ink)
                }
            }

            HorizontalDivider(color = Line)
            Text("Recent days", color = Ink, fontWeight = FontWeight.Bold)
            CompletionTimeline(completions = completions)
        }
    }
}

@Composable
private fun NewHabitSection(
    onAddTask: (String, List<String>, LocationMode, String, String, Int) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var reminders by remember { mutableStateOf("09:00") }
    var selectedIcon by remember { mutableStateOf(HabitIcons.first().key) }
    var selectedColor by remember { mutableStateOf(HabitColors.first()) }
    var locationMode by remember { mutableStateOf(LocationMode.NONE) }
    var radius by remember { mutableStateOf("50") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        LazyColumn(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text("New habit", color = Ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Choose a visual identity, reminders and location behavior.", color = MutedInk)
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Habit name") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = authFieldColors(),
                )
            }

            item {
                Text("Icon", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HabitIcons.forEach { item ->
                        IconChoice(
                            item = item,
                            selected = selectedIcon == item.key,
                            color = parseColor(selectedColor),
                            onClick = { selectedIcon = item.key },
                        )
                    }
                }
            }

            item {
                Text("Color", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HabitColors.forEach { color ->
                        ColorChoice(
                            colorHex = color,
                            selected = selectedColor == color,
                            onClick = { selectedColor = color },
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = reminders,
                    onValueChange = { reminders = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reminders, max 5") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = authFieldColors(),
                )
            }

            item {
                Text("Location", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(LocationMode.NONE, LocationMode.ENTER, LocationMode.EXIT, LocationMode.BOTH).forEach { mode ->
                        FilterChip(
                            selected = locationMode == mode,
                            onClick = { locationMode = mode },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingIcon = if (locationMode == mode) {
                                { Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Location radius in meters") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = authFieldColors(),
                )
            }

            item {
                Button(
                    onClick = {
                        val cleanReminders = reminders.split(",").map { it.trim() }.filter { it.isNotBlank() }.take(5)
                        if (title.isNotBlank()) {
                            onAddTask(
                                title.trim(),
                                cleanReminders,
                                locationMode,
                                selectedIcon,
                                selectedColor,
                                radius.toIntOrNull() ?: 50,
                            )
                            title = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create habit", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InsightsSection(
    tasks: List<Task>,
    completions: List<Completion>,
    reportSummary: UserReport?,
    onGenerateReport: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Insights", color = Ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text("A quick read on your current habit system.", color = MutedInk)
                }
                Button(
                    onClick = onGenerateReport,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
                ) {
                    Text("Generate")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                BigMetric(Modifier.weight(1f), "${tasks.size}", "habits", Leaf)
                BigMetric(Modifier.weight(1f), "${completions.count { it.status == CompletionStatus.DONE }}", "done", Mist)
                BigMetric(Modifier.weight(1f), "${completions.count { it.status == CompletionStatus.SKIPPED }}", "skipped", Citrus)
            }

            reportSummary?.let {
                Surface(color = Color(0xFFF8FAF6), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Report", color = Ink, fontWeight = FontWeight.Bold)
                        Text("Done: ${it.totalDone}", color = MutedInk)
                        Text("Skipped: ${it.totalSkipped}", color = MutedInk)
                        Text("Top reason: ${it.topSkippedReason}", color = MutedInk)
                    }
                }
            }
        }
    }
}

@Composable
private fun IconChoice(item: HabitIcon, selected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) color.copy(alpha = 0.18f) else Color(0xFFF8FAF6))
            .border(1.dp, if (selected) color else Line, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(item.icon, contentDescription = item.label, tint = if (selected) color else MutedInk)
    }
}

@Composable
private fun ColorChoice(colorHex: String, selected: Boolean, onClick: () -> Unit) {
    val color = parseColor(colorHex)
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color)
            .border(3.dp, if (selected) Ink else Color.Transparent, CircleShape)
            .clickable(onClick = onClick),
    )
}

@Composable
private fun CompletionTimeline(completions: List<Completion>) {
    val byDate = completions.associateBy { it.date }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0..6).forEach { index ->
            val date = LocalDate.now().minusDays((6 - index).toLong())
            val status = byDate[date.toString()]?.status
            val color = when (status) {
                CompletionStatus.DONE -> Leaf
                CompletionStatus.SKIPPED -> Citrus
                null -> Line
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = if (status == null) 0.5f else 1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(date.dayOfWeek.name.take(1), color = if (status == null) MutedInk else Color.White)
            }
        }
    }
}

@Composable
private fun HabitIconBadge(iconKey: String, color: Color, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(iconFor(iconKey), contentDescription = null, tint = color, modifier = Modifier.size((size * 0.48f).dp))
    }
}

@Composable
private fun LogoMark(size: Int, textStyle: androidx.compose.ui.text.TextStyle) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(Leaf, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "AS",
            color = Color.White,
            style = textStyle,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Surface(color = Panel, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Line)) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Flag, contentDescription = null, tint = Citrus, modifier = Modifier.size(18.dp))
            Column {
                Text(value, color = Ink, fontWeight = FontWeight.Black)
                Text(label, color = MutedInk, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MiniMetric(value: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(7.dp).background(color, CircleShape))
        Text("$value $label", color = MutedInk, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun BigMetric(modifier: Modifier, value: String, label: String, color: Color) {
    Surface(modifier = modifier, color = color.copy(alpha = 0.13f), shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, color = color, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(label, color = MutedInk, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun DetailLine(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = MutedInk, modifier = Modifier.size(18.dp))
        Text(label, color = Ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(88.dp))
        Text(value, color = MutedInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Panel,
    unfocusedContainerColor = Panel,
    disabledContainerColor = Color(0xFFF8FAF6),
    focusedBorderColor = Leaf,
    unfocusedBorderColor = Line,
    focusedLabelColor = DeepLeaf,
    cursorColor = DeepLeaf,
)

private fun habitStats(task: Task, completions: List<Completion>): HabitStats {
    val taskCompletions = completions.filter { it.taskId == task.id }
    val doneDates = taskCompletions
        .filter { it.status == CompletionStatus.DONE }
        .mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
        .toSet()
    var cursor = LocalDate.now()
    var streak = 0
    while (doneDates.contains(cursor)) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return HabitStats(
        streak = streak,
        doneToday = doneDates.contains(LocalDate.now()),
        doneCount = taskCompletions.count { it.status == CompletionStatus.DONE },
        skippedCount = taskCompletions.count { it.status == CompletionStatus.SKIPPED },
    )
}

private fun iconFor(key: String): ImageVector = when (key) {
    "run" -> Icons.Outlined.DirectionsRun
    "water" -> Icons.Outlined.LocalDrink
    "read" -> Icons.Outlined.MenuBook
    "fitness" -> Icons.Outlined.FitnessCenter
    "food" -> Icons.Outlined.Restaurant
    "mind" -> Icons.Outlined.SelfImprovement
    else -> Icons.Outlined.CheckCircle
}

private fun parseColor(hex: String): Color {
    return runCatching {
        val normalized = hex.removePrefix("#")
        val value = normalized.toLong(16)
        Color(
            red = ((value shr 16) and 0xFF) / 255f,
            green = ((value shr 8) and 0xFF) / 255f,
            blue = (value and 0xFF) / 255f,
        )
    }.getOrDefault(Leaf)
}
