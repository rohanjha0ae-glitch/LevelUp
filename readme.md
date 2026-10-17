# LevelUp

**LevelUp** (`com.optimalhuman.lvlupp`) is an Android health, fitness, and lifestyle optimization application built with Kotlin and Jetpack Compose, hosted at [rohanjha0ae-glitch/LevelUp](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohanjha0ae-glitch%2FLevelUp). It combines daily habit tracking, workout and sleep logging, guided meditation and stretching routines, and personalized AI wellness coaching powered by Google's Gemini AI (`gemini-2.5-flash`).

The repository also includes a companion React + Vite web module (`level-up-loading`) for the interactive loading experience.

## Download & Releases

Grab the latest Android installation package directly from GitHub:

* **[Download Latest APK Release](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohanjha0ae-glitch%2FLevelUp%2Freleases%2Flatest)**

To install, download the `.apk` file directly onto your Android device (Android 7.0+ / API 24+) and enable "Install unknown apps" in your browser or file manager settings.

## Key Features

* **Habit Tracking**: Create and track predefined or custom daily habits across categories including Workout, Sleep, Diet, Meditation, Evening Stretch, and General lifestyle goals, complete with 7-day progress analytics.
* **Workout Logging**: Record detailed exercise sessions including sets, reps, and weight lifted, persisted locally via Room Database.
* **Sleep Monitoring**: Track nightly sleep duration (hours), sleep quality ratings (1–10 scale), and custom sleep notes.
* **AI Wellness Coach**: Receive personalized fitness, recovery, and habit recommendations powered by Google's `gemini-2.5-flash` model based on your logged metrics.
* **Guided Meditation & Breathing**: Follow an interactive breathing visualizer and timer cycling through Inhale, Hold, Exhale, and Rest phases.
* **Evening Stretch Routines**: Complete guided stretching sequences (including Cobra Stretch, Super Ceiling Stretch, and Bridge Pose) with built-in exercise timers.
* **Authentication & User Profiles**: Manage user sign-in, profile settings, and dynamic light/dark theme switching.

## Tech Stack

| Layer | Technology / Library | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Kotlin | `2.2.10` | Primary Android application development |
| **UI Framework** | Jetpack Compose (BOM) | `2024.09.00` | Declarative UI, Material Design, and animations |
| **Navigation & Lifecycle** | Navigation Compose / ViewModel | `2.8.9` / `2.8.7` | Screen routing and reactive state management |
| **Local Persistence** | Room Database & KSP | `2.7.0` / `2.3.6` | Offline-first storage for habits, workouts, and sleep logs |
| **Networking** | Retrofit, Moshi, OkHttp | `2.12.0` / `4.10.0` | REST client for Google Gemini AI API calls |
| **AI Integration** | Gemini API (`gemini-2.5-flash`) | `v1beta` | Personalized wellness coaching and insights |
| **Authentication** | Firebase Auth & Credentials | BOM `34.15.0` | User authentication and Google Identity sign-in |
| **Media & Camera** | Coil Compose & CameraX | `2.7.0` / `1.5.0` | Image loading and device camera integration |
| **Build & Secrets** | Android Gradle Plugin & Secrets Plugin | `9.3.1` | Build automation and `.env` API key injection |
| **Companion Web UI** | React 19 + Vite 8 | `19.2.8` / `8.2.0` | Web-based loading interface (`level-up-loading`) |

## Project Structure

```text
LevelUp/
├── .env.example                  # Template for environment variables (GEMINI_API_KEY)
├── build.gradle.kts              # Top-level Gradle configuration
├── settings.gradle.kts           # Project settings and module declarations
├── gradle/
│   └── libs.versions.toml        # Centralized Gradle version catalog
├── app/                          # Main Android application module
│   ├── build.gradle.kts          # App module build config (SDK 24–36)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/
│           ├── MainActivity.kt               # Entry point, theme controller, and root navigation
│           ├── data/
│           │   ├── AppDatabase.kt            # Room database configuration
│           │   ├── Daos.kt                   # Data Access Objects for local queries
│           │   ├── Entities.kt               # Habit, HabitLog, WorkoutLog, and SleepLog tables
│           │   └── Repository.kt             # Data repository mediating Room and ViewModels
│           ├── network/
│           │   └── GeminiService.kt          # Retrofit service for Gemini 2.5 Flash API
│           └── ui/
│               ├── AuthViewModel.kt          # Authentication state management
│               ├── LevelUpLoadingScreen.kt   # App initialization and loading UI
│               ├── LevelUpMainScreen.kt      # Central dashboard, timers, and AI coach view
│               ├── LevelUpViewModel.kt       # Core state store for logs, stats, and AI prompts
│               ├── LoginScreen.kt            # User sign-in screen
│               ├── ProfileScreen.kt          # User profile and preferences
│               ├── SmoothScroll.kt           # Custom scroll utilities
│               └── theme/                    # Compose theme, typography, and color palettes
└── level-up-loading/             # Standalone React + Vite web loading component
    ├── package.json
    ├── vite.config.js
    └── src/
```

## Database Schema

The Android app uses **Room** (`AppDatabase.kt`) to persist user activity across four core entities defined in `data/Entities.kt`:

| Entity | Description | Key Fields |
| :--- | :--- | :--- |
| **`Habit`** | Stores predefined and user-created habits | Habit ID, title, category (`Workout`, `Sleep`, `Diet`, `Meditation`, `Evening Stretch`, `General`) |
| **`HabitLog`** | Tracks daily habit completion history | Log ID, habit reference, date (`yyyy-MM-dd`), completion status |
| **`WorkoutLog`** | Records individual strength and fitness sessions | Exercise name, sets, reps, weight, date |
| **`SleepLog`** | Stores nightly recovery and sleep metrics | Sleep duration (hours), quality rating (`1–10`), notes, date |

## Getting Started

### Prerequisites

* **Android Studio**: Ladybug / Koala or newer (with Android Gradle Plugin `9.3.1` support)
* **Android SDK**: `compileSdk 36` / `targetSdk 36` (`minSdk 24` — Android 7.0 Nougat or higher)
* **JDK**: Java 11 or higher
* **Google Gemini API Key**: Required for the AI coaching assistant
* **Node.js**: v18+ and `npm` (optional, for running the `level-up-loading` React web module)

### 1. Clone the Repository

```bash
git clone https://github.com/rohanjha0ae-glitch/LevelUp.git
cd LevelUp
```

### 2. Configure Environment Variables

The project uses the Gradle Secrets Plugin to inject variables from a root `.env` file into `BuildConfig`.

1. Copy the example environment file in the root directory:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and replace `MY_GEMINI_API_KEY` with your actual Google Gemini API key:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```

### 3. Build and Run the Android App

1. Open Android Studio and select **Open** to load the root `LevelUp` directory.
2. Allow Gradle to sync and download dependencies defined in `gradle/libs.versions.toml`.
3. Select an Android Emulator or physical device running Android 7.0 (API 24) or higher.
4. Click **Run** (`Shift + F10`) or build via the terminal:
   ```bash
   ./gradlew assembleDebug
   ```

### 4. Building and Publishing an APK Release

Follow these steps to produce a standalone APK and publish it as a GitHub Release:

#### Step A: Generate the APK

- **Quick Debug APK (for testing)**:
  ```bash
  ./gradlew assembleDebug
  ```
  The generated APK will be located at:
  `app/build/outputs/apk/debug/app-debug.apk`

- **Signed Release APK (recommended for distribution)**:
  1. In Android Studio, go to the top menu and click **Build** > **Generate Signed Bundle / APK...**.
  2. Select **APK** and click **Next**.
  3. Under **Key store path**, choose an existing keystore or click **Create new...** to create a signing key.
  4. Enter your keystore password, key alias, and key password, then click **Next**.
  5. Select the **release** destination folder, check both **V1 (Jar Signature)** and **V2 (Full APK Signature)** check boxes, and click **Finish**.
  6. Your signed APK will be output to:
     `app/release/app-release.apk`

#### Step B: Publish the Release on GitHub

1. Open your repository on GitHub at [LevelUp Releases](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohanjha0ae-glitch%2FLevelUp%2Freleases).
2. Click **Draft a new release** (or **Create a new release**).
3. In the **Choose a tag** field, enter a semantic version tag (e.g., `v1.0.0`) and click **Create new tag on publish**.
4. Set the **Release title** (e.g., `LevelUp v1.0.0 - Initial Release`).
5. Write release notes summarizing new features, bug fixes, and installation prerequisites.
6. In the **Attach binaries by dropping them here or selecting them** box, drag and drop your generated `.apk` file (renamed to something clean like `LevelUp-v1.0.0.apk`).
7. Click **Publish release**.

### 5. Run the Companion Web Component (Optional)

To preview or develop the standalone React + Vite loading interface located in `level-up-loading/`:

```bash
cd level-up-loading
npm install
npm run dev
```

Available scripts in `level-up-loading`:

* `npm run dev`: Starts the local Vite development server with Hot Module Replacement (HMR).
* `npm run build`: Bundles the web component for production.
* `npm run lint`: Runs `oxlint` for static code analysis.
* `npm run preview`: Previews the production build locally.

## Contributing

1. Fork the [LevelUp repository](https://www.google.com/search?q=https%3A%2F%2Fgithub.com%2Frohanjha0ae-glitch%2FLevelUp).
2. Create a feature branch (`git checkout -b feature/your-feature-name`).
3. Commit your changes (`git commit -m "Add your feature description"`).
4. Push to the branch (`git push origin feature/your-feature-name`).
5. Open a Pull Request.