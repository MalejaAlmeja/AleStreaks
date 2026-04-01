# AleStreaks Product Specification (v1)

## 1) Scope

Android-only personal streak tracker with Firebase backend, reminders, geofencing, and light AI assistance.

## 2) Core UX rules

1. Main screen shows up to 10 tasks.
2. Overflow button opens hidden tasks list.
3. Tasks can be marked Done / Skip.
4. Skip requires a typed reason.
5. AI can classify skip reason validity and suggest improvements.
6. Task reminders support 1–5 reminder times/day.
7. Location reminders can trigger on enter, exit, or both.

## 3) Theme and visual system

### Light palette (provided)

- `#9AB17A`
- `#C3CC9B`
- `#E4DFB5`
- `#B4D3D9`

### Dark palette (derived to match green/blue/brown request)

- Background: `#1F2A2E`
- Surface: `#2A3A33`
- Primary: `#7FA37E`
- Secondary: `#7DA3AE`
- Accent: `#8A6F56`

Design notes:
- Light theme default on first launch.
- Dark theme optional via settings toggle.
- Minimalistic Material iconography.

## 4) Firebase architecture

### Firebase services

- Firebase Authentication (email/password or Google)
- Cloud Firestore
- Cloud Functions (optional, for report generation / AI orchestration)

### Firestore model

- `users/{uid}`
  - `profile`
    - `displayName`
    - `theme`
    - `language` (`en`)
  - `tasks/{taskId}`
    - `title`
    - `iconKey`
    - `colorHex`
    - `active`
    - `schedule` (custom rule)
    - `maxRemindersPerDay` (1..5)
    - `locationMode` (`enter` | `exit` | `both`)
    - `locationRadiusMeters` (default 50)
    - `createdAt`
    - `updatedAt`
  - `taskCompletions/{entryId}`
    - `taskId`
    - `date` (YYYY-MM-DD)
    - `status` (`done` | `skipped`)
    - `skipReason` (required if skipped)
    - `aiReasonValidity` (`valid` | `weak` | `unknown`)
  - `reports/{reportId}`
    - `period`
    - `summary`
    - `generatedAt`

## 5) AI features (limited scope)

AI is used for:

1. Suggesting reminder times / habit setup hints.
2. Evaluating skip reason quality (lightweight classifier).
3. Generating weekly or monthly personal reports.

AI is **not** used for autonomous actions or high-risk decisions.

## 6) Android implementation plan

### Phase 1 — Foundation

- Kotlin + Jetpack Compose app skeleton
- Firebase Auth + Firestore integration
- Core task CRUD

### Phase 2 — Streak engine + reminders

- Daily streak calculations
- Time reminders (1–5/day/task)
- Notification actions (Done, Skip, Snooze)

### Phase 3 — Geofencing

- Geofence add/edit per task
- Enter/Exit trigger support
- 50m default radius + manual override

### Phase 4 — AI suggestions/reports

- Skip reason validity scoring
- Habit suggestion prompts
- Periodic summary reports

### Phase 5 — Packaging

- APK generation for sideload
- Basic backup/export guidance

## 7) Non-functional constraints

- English localization only in v1.
- User account required (Firebase Auth).
- Offline-first behavior can be added later; v1 prioritizes functional online sync.
- Keep permission prompts contextual and minimal.
