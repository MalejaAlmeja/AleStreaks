# AleStreaks (Android + Firebase)

AleStreaks is a native Android starter app for tracking personal streak-style tasks with Firebase-backed authentication and storage.

## Implemented features

- Firebase Auth login/register with email and password
- Firestore-backed tasks and completion tracking
- Task creation with custom reminder times, capped at 5 per task
- Location mode per task: `ENTER`, `EXIT`, `BOTH`, or `NONE`
- Main screen capped to 10 visible tasks with an overflow toggle
- Skip flow that requires a reason
- Local AI-style skip reason classification: `valid`, `weak`, or `unknown`
- Basic report summary for done and skipped task entries
- Jetpack Compose UI with Material 3 theme foundation

## Firebase package id

Use this exact Android package name in Firebase:

```txt
com.alestreaks.app
```

The app is configured with:

- `applicationId = "com.alestreaks.app"`
- `namespace = "com.alestreaks.app"`

## Firebase status

The Firebase Android config file is present at:

- `app/google-services.json`

Configured for:

- `project_id`: `alestreak`
- `package_name`: `com.alestreaks.app`

## Project structure

- `app/src/main/java/com/alestreaks/app/MainActivity.kt`: app entry point
- `app/src/main/java/com/alestreaks/app/ui/`: Compose screens and `MainViewModel`
- `app/src/main/java/com/alestreaks/app/data/`: Firebase repositories
- `app/src/main/java/com/alestreaks/app/model/`: domain models
- `app/src/main/java/com/alestreaks/app/theme/`: theme and color scheme
- `app/src/main/java/com/alestreaks/app/util/`: service locator and skip reason helper

## Firebase console setup

1. Create or open the Firebase project.
2. Register an Android app with package name `com.alestreaks.app`.
3. Download `google-services.json` and place it in `app/google-services.json`.
4. Enable Authentication -> Sign-in method -> Email/Password.
5. Create a Cloud Firestore database.
6. For personal development, use rules scoped to the signed-in user:

```txt
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Build from terminal

Requirements:

- Android SDK 35
- JDK 17

From the repo root:

```bash
./gradlew assembleDebug
```

Optional checks:

```bash
./gradlew test
```

Debug APK output:

- `app/build/outputs/apk/debug/app-debug.apk`

## Run from Android Studio

1. Open this repository folder in Android Studio.
2. Let Gradle sync finish.
3. Select the `app` run configuration.
4. Start an emulator or connect a device.
5. Click Run.

## Manual test flow

1. Register or sign in with email/password.
2. Add a task with one or more reminder times.
3. Mark a task as Done.
4. Skip a task and provide a reason.
5. Generate a report.
6. Confirm Firestore documents appear under:
   - `users/{uid}/tasks`
   - `users/{uid}/taskCompletions`

## Notes on AI integration

The current implementation uses an on-device heuristic classifier for skip reasons.

A later version can call a Cloud Function, Gemini API, or another backend endpoint from the repository layer for real AI summaries and suggestions.
