# AleStreaks (Android + Firebase)

This repository now contains a complete Android starter implementation for your personal Streaks-style app.

## Implemented features

- Firebase Auth login/register (email/password)
- Firestore-backed tasks and completion tracking
- Task creation with custom reminder times (max 5/day)
- Location mode per task (`ENTER`, `EXIT`, `BOTH`) with 50m default radius
- Main screen capped to 10 visible tasks + overflow toggle
- Skip flow requires a reason
- AI-style skip reason classification (`valid`, `weak`, `unknown`) and summary reports
- Theme foundation using your requested palette

## Required package id

### Exact value to paste in Firebase

`com.alestreaks.app`

The app is already configured with:

- `applicationId = "com.alestreaks.app"`
- `namespace = "com.alestreaks.app"`

in `app/build.gradle.kts`.

## Project structure

- `app/src/main/java/com/alestreaks/app/MainActivity.kt` – app entry
- `app/src/main/java/com/alestreaks/app/ui/` – Compose screens + `MainViewModel`
- `app/src/main/java/com/alestreaks/app/data/` – Firebase repositories
- `app/src/main/java/com/alestreaks/app/model/` – domain models
- `app/src/main/java/com/alestreaks/app/theme/` – theme + color scheme
- `app/src/main/java/com/alestreaks/app/util/` – service locator + AI reason helper

---

<<<<<<< ours
=======

## Firebase status in repo

The Firebase Android config file is now present at:

- `app/google-services.json`

Configured for:
- `project_id`: `alestreak`
- `package_name`: `com.alestreaks.app`

>>>>>>> theirs
## Step-by-step: connect this app to Firebase

### 1) Create Firebase project

1. Open Firebase Console.
2. Click **Add project**.
3. Name it (for example `AleStreaks`).
4. Continue and finish project creation.

### 2) Register Android app

1. In Firebase project, click **Add app** → **Android**.
2. Set Android package name to exactly: `com.alestreaks.app`
3. (Optional) App nickname: `AleStreaks`.
4. Download `google-services.json`.
5. Place `google-services.json` in:
   - `app/google-services.json`

### 3) Enable Firebase Authentication

1. Firebase Console → **Authentication** → **Get started**.
2. Enable **Email/Password** provider.

### 4) Enable Cloud Firestore

1. Firebase Console → **Firestore Database** → **Create database**.
2. Start in **test mode** first (personal development).
3. Choose closest region.

### 5) Firestore rules for development (temporary)

Use these permissive rules while testing your own app account:

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

### 6) Build and run

From project root:

```bash
./gradlew assembleDebug
```

APK output:

- `app/build/outputs/apk/debug/app-debug.apk`

Install on phone (USB/file transfer) and allow unknown-source install if prompted.

### 7) Verify app flow

1. Register/sign in with email/password.
2. Add a task with reminders.
3. Mark Done and Skip (with reason).
4. Generate report.
5. Check Firestore paths:
   - `users/{uid}/tasks`
   - `users/{uid}/taskCompletions`

---

## Notes on AI integration

Current implementation uses an on-device heuristic classifier for skip reasons.

If you want real AI suggestions/reports next, you can wire a Cloud Function endpoint (or Gemini API) and call it from the repository layer.
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======


## Next Firebase console steps (do these now)

1. **Authentication**
   - Firebase Console → Authentication → Sign-in method
   - Enable **Email/Password**

2. **Firestore Database**
   - Create Firestore DB (production or test mode)
   - Use the rules in this README (or tighter rules if needed)

3. **(Optional) Storage**
   - Enable Cloud Storage only if you plan image/icon uploads

4. **SHA certificates (recommended)**
   - In Project settings → Your apps (Android), add SHA-1 and SHA-256
   - Useful for Google Sign-In and other auth providers later

5. **Run app and verify writes**
   - Register user in app
   - Add task and skip/done events
   - Confirm documents appear in:
     - `users/{uid}/tasks`
     - `users/{uid}/taskCompletions`

>>>>>>> theirs
