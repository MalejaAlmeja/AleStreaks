package com.alestreaks.app.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alestreaks.app.model.Completion
import com.alestreaks.app.model.CompletionStatus
import com.alestreaks.app.model.LocationMode
import com.alestreaks.app.model.Task
import com.alestreaks.app.model.UserReport
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    HabitIcon("work", "Work", Icons.Outlined.Work),
    HabitIcon("study", "Study", Icons.Outlined.School),
    HabitIcon("sleep", "Sleep", Icons.Outlined.WbSunny),
    HabitIcon("music", "Music", Icons.Outlined.MusicNote),
    HabitIcon("health", "Health", Icons.Outlined.FavoriteBorder),
    HabitIcon("goal", "Goal", Icons.Outlined.EmojiEvents),
)

private val HabitColors = listOf("#9AB17A", "#B4D3D9", "#E9A34A", "#7FA37E", "#8A6F56")
private val WeekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val ReminderTimes = listOf("06:00", "07:30", "09:00", "12:00", "15:00", "18:00", "20:30", "22:00")

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
        onAddTask = { title, reminders, locationMode, iconKey, colorHex, radius, latitude, longitude ->
            viewModel.addTask(title, reminders, locationMode, iconKey, colorHex, radius, latitude, longitude)
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
            .statusBarsPadding()
            .navigationBarsPadding()
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
    onAddTask: (String, List<String>, LocationMode, String, String, Int, Double?, Double?) -> Unit,
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
    var sidebarExpanded by remember { mutableStateOf(false) }

    val selectedTask = tasks.firstOrNull { it.id == selectedTaskId } ?: tasks.firstOrNull()
    val totalDoneToday = tasks.count { habitStats(it, completions).doneToday }
    val totalStreak = tasks.sumOf { habitStats(it, completions).streak }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(AppBackground),
    ) {
        val compact = maxWidth < 760.dp
        LaunchedEffect(compact) {
            sidebarExpanded = false
        }
        val contentPadding = if (compact) 12.dp else 18.dp
        val sidebarWidth = if (sidebarExpanded) 236.dp else 74.dp

        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sidebarWidth),
                color = Color(0xFFE9F0E4),
                shadowElevation = if (sidebarExpanded) 4.dp else 0.dp,
            ) {
                AppSidebar(
                    selected = section,
                    expanded = sidebarExpanded,
                    onToggleExpanded = { sidebarExpanded = !sidebarExpanded },
                    onSelected = {
                        section = it
                    },
                    onSignOut = onSignOut,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(contentPadding),
            ) {
                HeaderBar(
                    tasksCount = tasks.size,
                    doneToday = totalDoneToday,
                    totalStreak = totalStreak,
                    compact = compact,
                    onNewHabit = { section = HomeSection.New },
                )

                Spacer(modifier = Modifier.height(12.dp))

                when (section) {
                    HomeSection.Habits -> HabitsSection(
                        tasks = tasks,
                        completions = completions,
                        selectedTask = selectedTask,
                        compact = compact || sidebarExpanded,
                        onSelectTask = { selectedTaskId = it.id },
                        onDone = onDone,
                        onSkip = { skipDialogTaskId = it },
                        onNewHabit = { section = HomeSection.New },
                    )

                    HomeSection.New -> NewHabitSection(
                        onAddTask = { title, reminders, mode, icon, color, radius, latitude, longitude ->
                            onAddTask(title, reminders, mode, icon, color, radius, latitude, longitude)
                            section = HomeSection.Habits
                        },
                    )

                    HomeSection.Insights -> InsightsSection(
                        tasks = tasks,
                        completions = completions,
                        compact = compact || sidebarExpanded,
                        reportSummary = reportSummary,
                        onGenerateReport = onGenerateReport,
                    )
                }
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
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelected: (HomeSection) -> Unit,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (expanded) 14.dp else 8.dp, vertical = 14.dp),
        horizontalAlignment = if (expanded) Alignment.Start else Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpanded),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (expanded) Arrangement.spacedBy(10.dp) else Arrangement.Center,
        ) {
            LogoMark(size = 44, textStyle = MaterialTheme.typography.labelLarge)
            if (expanded) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("AleStreaks", color = Ink, fontWeight = FontWeight.Black, maxLines = 1)
                    Text("Daily habits", color = MutedInk, style = MaterialTheme.typography.labelMedium)
                }
                IconButton(onClick = onToggleExpanded) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = Ink)
                }
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        SidebarItem(HomeSection.Habits, selected, Icons.Outlined.Home, "Habits", expanded, onSelected)
        SidebarItem(HomeSection.New, selected, Icons.Outlined.Add, "New", expanded, onSelected)
        SidebarItem(HomeSection.Insights, selected, Icons.Outlined.Timeline, "Stats", expanded, onSelected)
        Spacer(modifier = Modifier.weight(1f))
        AccountBlock(expanded = expanded, onExpand = onToggleExpanded, onSignOut = onSignOut)
    }
}

@Composable
private fun SidebarItem(
    section: HomeSection,
    selected: HomeSection,
    icon: ImageVector,
    label: String,
    expanded: Boolean,
    onSelected: (HomeSection) -> Unit,
) {
    val active = selected == section
    val background = if (active) Panel else Color.Transparent
    val tint = if (active) DeepLeaf else MutedInk
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable { onSelected(section) }
            .padding(horizontal = if (expanded) 12.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (expanded) Arrangement.spacedBy(12.dp) else Arrangement.Center,
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        if (expanded) {
            Text(label, color = tint, fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun AccountBlock(expanded: Boolean, onExpand: () -> Unit, onSignOut: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (expanded) Panel else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = if (expanded) BorderStroke(1.dp, Line) else null,
    ) {
        if (expanded) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = DeepLeaf)
                    Column {
                        Text("Account", color = Ink, fontWeight = FontWeight.Bold)
                        Text("Signed in", color = MutedInk, style = MaterialTheme.typography.labelMedium)
                    }
                }
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line),
                ) {
                    Text("Sign out", color = Ink)
                }
            }
        } else {
            IconButton(onClick = onExpand, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = MutedInk)
            }
        }
    }
}

@Composable
private fun HeaderBar(
    tasksCount: Int,
    doneToday: Int,
    totalStreak: Int,
    compact: Boolean,
    onNewHabit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TodayProgressPill(
            doneToday = doneToday,
            tasksCount = tasksCount,
            totalStreak = totalStreak,
            compact = compact,
            modifier = Modifier.weight(1f),
        )
        Button(
            onClick = onNewHabit,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepLeaf),
            modifier = Modifier.height(42.dp),
        ) {
            if (!compact) {
                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Habit")
            } else {
                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun TodayProgressPill(
    doneToday: Int,
    tasksCount: Int,
    totalStreak: Int,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val progressText = if (tasksCount == 0) "0/0" else "$doneToday/$tasksCount"
    Surface(
        modifier = modifier.height(42.dp),
        color = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Leaf.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = DeepLeaf, modifier = Modifier.size(20.dp))
            }
            Text(progressText, color = Ink, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            if (!compact) {
                Text("today", color = MutedInk, style = MaterialTheme.typography.bodyMedium)
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(Line, CircleShape),
                )
                Icon(Icons.Outlined.Flag, contentDescription = null, tint = Citrus, modifier = Modifier.size(18.dp))
                Text("$totalStreak", color = Ink, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HabitsSection(
    tasks: List<Task>,
    completions: List<Completion>,
    selectedTask: Task?,
    compact: Boolean,
    onSelectTask: (Task) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String) -> Unit,
    onNewHabit: () -> Unit,
) {
    if (tasks.isEmpty()) {
        EmptyHabits(onNewHabit = onNewHabit)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        selectedTask?.let { task ->
            item {
                HabitDetailPanel(
                    modifier = Modifier.fillMaxWidth(),
                    task = task,
                    stats = habitStats(task, completions),
                    completions = completions.filter { it.taskId == task.id },
                    onDone = { onDone(task.id) },
                    onSkip = { onSkip(task.id) },
                )
            }
        }
        items(tasks, key = { it.id }) { task ->
            HabitCard(
                task = task,
                stats = habitStats(task, completions),
                selected = selectedTask?.id == task.id,
                compact = compact,
                onClick = { onSelectTask(task) },
                onDone = { onDone(task.id) },
                onSkip = { onSkip(task.id) },
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
    compact: Boolean,
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
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
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
                    }
                }
            }

            if (task.reminders.isNotEmpty()) {
                Text(
                    task.reminders.joinToString("  "),
                    color = MutedInk,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onDone,
                    enabled = !stats.doneToday,
                    modifier = Modifier.weight(1f).height(if (compact) 46.dp else 42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (stats.doneToday) Leaf else DeepLeaf),
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (stats.doneToday) "Done" else "Mark done")
                }
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f).height(if (compact) 46.dp else 42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line),
                ) {
                    Text("Skip", color = Ink)
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
        LazyColumn(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HabitIconBadge(iconKey = task.iconKey, color = accent, size = 58)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text("Today ${if (stats.doneToday) "complete" else "pending"}", color = MutedInk)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigMetric(modifier = Modifier.weight(1f), value = "${stats.streak}", label = "day streak", color = Citrus)
                    BigMetric(modifier = Modifier.weight(1f), value = "${stats.doneCount}", label = "done", color = Leaf)
                }
            }

            item {
                Surface(color = Color(0xFFF8FAF6), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailLine(Icons.Outlined.Timeline, "Reminders", task.reminders.joinToString(", ").ifBlank { "None" })
                        DetailLine(Icons.Outlined.LocationOn, "Location", task.locationSummary())
                        DetailLine(Icons.Outlined.Flag, "Skipped", "${stats.skippedCount} times")
                    }
                }
            }

            item {
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
            }

            item { HorizontalDivider(color = Line) }
            item { Text("Recent days", color = Ink, fontWeight = FontWeight.Bold) }
            item { CompletionTimeline(completions = completions) }
        }
    }
}

@Composable
private fun NewHabitSection(
    onAddTask: (String, List<String>, LocationMode, String, String, Int, Double?, Double?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri")) }
    var selectedTimes by remember { mutableStateOf(setOf("09:00")) }
    var selectedIcon by remember { mutableStateOf(HabitIcons.first().key) }
    var selectedColor by remember { mutableStateOf(HabitColors.first()) }
    var locationMode by remember { mutableStateOf(LocationMode.NONE) }
    var radius by remember { mutableStateOf("50") }
    var locationLatitude by remember { mutableStateOf<Double?>(null) }
    var locationLongitude by remember { mutableStateOf<Double?>(null) }
    var locationStatus by remember { mutableStateOf<String?>(null) }
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            scope.launch {
                val location = currentLocationOrNull(context)
                locationLatitude = location?.latitude
                locationLongitude = location?.longitude
                locationStatus = if (location != null) "Location saved" else "Could not read location"
            }
        } else {
            locationStatus = "Location permission denied"
        }
    }

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
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { selectedIcon = suggestIconKey(title) },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line),
                ) {
                    Icon(Icons.Outlined.SelfImprovement, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Suggest icon")
                }
            }

            item {
                Text("Icon", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    items(HabitIcons, key = { it.key }) { item ->
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
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    items(HabitColors, key = { it }) { color ->
                        ColorChoice(
                            colorHex = color,
                            selected = selectedColor == color,
                            onClick = { selectedColor = color },
                        )
                    }
                }
            }

            item {
                Text("Days", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(WeekDays, key = { it }) { day ->
                        DayChip(
                            label = day,
                            selected = selectedDays.contains(day),
                            onClick = {
                                selectedDays = selectedDays.toggleValue(day).ifEmpty { setOf(day) }
                            },
                        )
                    }
                }
            }

            item {
                Text("Reminder times", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(ReminderTimes, key = { it }) { time ->
                        TimeChip(
                            label = time,
                            selected = selectedTimes.contains(time),
                            onClick = {
                                selectedTimes = selectedTimes.toggleValue(time).ifEmpty { setOf(time) }.takeMax(5)
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Up to 5 reminders. Saved as day/time schedule.", color = MutedInk, style = MaterialTheme.typography.labelMedium)
            }

            item {
                Text("Location", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(listOf(LocationMode.NONE, LocationMode.ENTER, LocationMode.EXIT, LocationMode.BOTH), key = { it.name }) { mode ->
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
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        ) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasFine || hasCoarse) {
                            scope.launch {
                                val location = currentLocationOrNull(context)
                                locationLatitude = location?.latitude
                                locationLongitude = location?.longitude
                                locationStatus = if (location != null) "Location saved" else "Could not read location"
                            }
                        } else {
                            locationLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Line),
                ) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (locationLatitude != null) "Update current location" else "Use current location")
                }
                locationStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MutedInk, style = MaterialTheme.typography.labelMedium)
                }
            }

            item {
                Button(
                    onClick = {
                        val cleanReminders = buildReminderLabels(selectedDays, selectedTimes)
                        if (title.isNotBlank()) {
                            onAddTask(
                                title.trim(),
                                cleanReminders,
                                locationMode,
                                selectedIcon,
                                selectedColor,
                                radius.toIntOrNull() ?: 50,
                                locationLatitude,
                                locationLongitude,
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
    compact: Boolean,
    reportSummary: UserReport?,
    onGenerateReport: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        LazyColumn(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
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
            }

            item {
                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        BigMetric(Modifier.fillMaxWidth(), "${tasks.size}", "habits", Leaf)
                        BigMetric(Modifier.fillMaxWidth(), "${completions.count { it.status == CompletionStatus.DONE }}", "done", Mist)
                        BigMetric(Modifier.fillMaxWidth(), "${completions.count { it.status == CompletionStatus.SKIPPED }}", "skipped", Citrus)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        BigMetric(Modifier.weight(1f), "${tasks.size}", "habits", Leaf)
                        BigMetric(Modifier.weight(1f), "${completions.count { it.status == CompletionStatus.DONE }}", "done", Mist)
                        BigMetric(Modifier.weight(1f), "${completions.count { it.status == CompletionStatus.SKIPPED }}", "skipped", Citrus)
                    }
                }
            }

            item {
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
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(50),
        leadingIcon = if (selected) {
            { Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else {
            null
        },
    )
}

@Composable
private fun TimeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        color = if (selected) DeepLeaf else Color(0xFFF8FAF6),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, if (selected) DeepLeaf else Line),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = if (selected) Color.White else MutedInk, modifier = Modifier.size(17.dp))
            Text(label, color = if (selected) Color.White else Ink, fontWeight = FontWeight.Bold)
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
    "work" -> Icons.Outlined.Work
    "study" -> Icons.Outlined.School
    "sleep" -> Icons.Outlined.WbSunny
    "music" -> Icons.Outlined.MusicNote
    "health" -> Icons.Outlined.FavoriteBorder
    "goal" -> Icons.Outlined.EmojiEvents
    else -> Icons.Outlined.CheckCircle
}

private fun suggestIconKey(title: String): String {
    val text = title.lowercase()
    return when {
        listOf("run", "walk", "cardio", "steps", "correr", "caminar", "pasos").any { text.contains(it) } -> "run"
        listOf("water", "drink", "agua", "hidratar").any { text.contains(it) } -> "water"
        listOf("read", "book", "study", "learn", "leer", "libro", "estudiar").any { text.contains(it) } -> "read"
        listOf("gym", "lift", "train", "workout", "entrenar", "ejercicio").any { text.contains(it) } -> "fitness"
        listOf("food", "eat", "diet", "meal", "comer", "comida").any { text.contains(it) } -> "food"
        listOf("meditate", "mind", "breath", "journal", "meditar", "respirar").any { text.contains(it) } -> "mind"
        listOf("work", "focus", "deep", "trabajo", "foco").any { text.contains(it) } -> "work"
        listOf("sleep", "bed", "rest", "dormir", "descansar").any { text.contains(it) } -> "sleep"
        listOf("music", "guitar", "piano", "música", "musica").any { text.contains(it) } -> "music"
        listOf("health", "doctor", "medicine", "salud").any { text.contains(it) } -> "health"
        listOf("goal", "win", "challenge", "meta", "logro").any { text.contains(it) } -> "goal"
        else -> "check_circle"
    }
}

private fun Set<String>.toggleValue(value: String): Set<String> {
    return if (contains(value)) this - value else this + value
}

private fun Set<String>.takeMax(max: Int): Set<String> {
    return asSequence().take(max).toSet()
}

private fun buildReminderLabels(days: Set<String>, times: Set<String>): List<String> {
    val orderedDays = WeekDays.filter { days.contains(it) }
    val orderedTimes = ReminderTimes.filter { times.contains(it) }
    return orderedTimes
        .map { time -> "${orderedDays.joinToString("/")} @$time" }
        .take(5)
}

private fun Task.locationSummary(): String {
    if (locationMode == LocationMode.NONE) return "Off"
    val coordinates = if (locationLatitude != null && locationLongitude != null) {
        "saved point"
    } else {
        "no point yet"
    }
    return "${locationMode.name.lowercase().replaceFirstChar { it.uppercase() }} / ${locationRadiusMeters}m / $coordinates"
}

private suspend fun currentLocationOrNull(context: Context): android.location.Location? {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!fine && !coarse) return null
    return runCatching {
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .await()
    }.getOrNull()
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
