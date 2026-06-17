# 🔥 Flare
[![JitPack](https://jitpack.io/v/RoxyBasicNeedBot/Flare.svg)](https://jitpack.io/#RoxyBasicNeedBot/Flare)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-21-blue.svg)](https://developer.android.com/about/dashboards)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3%2B-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

> "Beautiful, expressive alerts for Android — by Roxy"

Flare is a production-grade, highly customizable Android notification and toast library designed for modern Kotlin-first applications. It offers complete, separate integrations for both Jetpack Compose and the traditional Android View (XML) system.

---

## 🌟 Key Features

* **100% Kotlin & Modern Architecture** — Modular design ready to scale to multiplatform (KMP desktop/iOS) in the future.
* **Dual-System Support** — Dedicated Compose and View-system modules (zero hacky bridges).
* **6 Custom Alert Types** — SUCCESS (✓), ERROR (✗), WARNING (⚠), INFO (ℹ), LOADING (rotating spinner), and CUSTOM.
* **Physics-Based Transitions** — Driven by `SpringAnimation` for natural, buttery-smooth entries, exits, and bounces.
* **Touch gestures** — Drag and fling to dismiss with organic deceleration.
* **Queue System** — Intelligent task-runner supporting sequential alerts or instant replacement overrides.
* **Elevation & Customization** — Rounded corners, custom action buttons with callbacks, global configurations, and thin countdown progress bars.
* **Auto Theme Detection** — Follows system dark/light configuration, with manual override support.
* **Tactile Dimensions** — Custom haptic feedback wrapper utilizing `VibrationEffect` with legacy SDK fallbacks.

---

## 📦 Installation

Add the JitPack repository to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then, add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Pure Kotlin core models & queue (usually transitively included)
    implementation("com.github.RoxyBasicNeedBot.Flare:flare-core:1.0.0")

    // For Android View (XML) system support
    implementation("com.github.RoxyBasicNeedBot.Flare:flare-android:1.0.0")

    // For Jetpack Compose support
    implementation("com.github.RoxyBasicNeedBot.Flare:flare-compose:1.0.0")
}
```

---

## 🚀 Quick Start

### 1. Global Setup (Optional)
Initialize settings globally in your custom `Application` class:

```kotlin
import android.app.Application
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlarePosition
import com.roxy.flare.FlareTheme
import com.roxy.flare.android.Flare

class FlareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Flare.configure {
            defaultPosition = FlarePosition.BOTTOM
            defaultDuration = FlareDuration.SHORT
            hapticEnabled = true
            theme = FlareTheme.AUTO // Follows light/dark system theme
            cornerRadiusDp = 12f
        }
    }
}
```

---

### 2. Jetpack Compose Integration

Wrap your screen content in a `FlareHost` and show alerts using the suspend function `show`:

```kotlin
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlareType
import com.roxy.flare.compose.FlareHost
import com.roxy.flare.compose.rememberFlareHostState
import kotlinx.coroutines.launch

@Composable
fun MainComposeScreen() {
    val flareHostState = rememberFlareHostState()
    val scope = rememberCoroutineScope()

    FlareHost(state = flareHostState) {
        Scaffold { paddingValues ->
            // Your screen layout
            Button(onClick = {
                scope.launch {
                    flareHostState.show {
                        type = FlareType.SUCCESS
                        message = "Saved!"
                        duration = FlareDuration.SHORT
                        action("Undo") { 
                            // Run undo action logic
                        }
                    }
                }
            }) {
                Text("Show Alert")
            }
        }
    }
}
```

---

### 3. Android View (XML) System Integration

Trigger custom overlay banners programmatically using a fluent builder:

```kotlin
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlarePosition
import com.roxy.flare.FlareType
import com.roxy.flare.android.Flare

// From any Activity context
Flare.with(activity)
    .type(FlareType.ERROR)
    .message("Something went wrong")
    .position(FlarePosition.TOP)
    .duration(FlareDuration.LONG)
    .action("Retry") {
        runRetryLogic()
    }
    .showProgressBar(true)
    .show()
```

---

## 🛠️ API & Parameters Reference

| DSL Property | Type | Default Value | Description |
|:---|:---|:---|:---|
| `type` | `FlareType` | `FlareType.INFO` | Success, Error, Warning, Info, Loading, or Custom color setup |
| `message` | `String` | `""` | Text label display content |
| `position` | `FlarePosition` | `FlarePosition.BOTTOM` | TOP, BOTTOM, or CENTER alignment |
| `duration` | `FlareDuration` | `FlareDuration.SHORT` | SHORT (2s), LONG (3.5s), INDEFINITE, or CUSTOM in ms |
| `showProgressBar` | `Boolean` | `false` | Visual countdown timer bar |
| `haptic` | `Boolean` | `true` | Device tactile vibration trigger on show |
| `icon` | `FlareIconType` | `FlareIconType.Default` | Override default with custom Bitmap/Drawable/ImageVector |
| `animationType` | `FlareAnimationType`| `FlareAnimationType.SLIDE`| SLIDE, FADE, or BOUNCE springs |
| `customColor` | `Long?` | `null` | Hex ARGB override (e.g. `0xFF7B1FA2`) |

---

## 🏛️ Module Architecture

```
flare/
├── flare-core/     ← Pure Kotlin (Enums, Queue, Models)
├── flare-android/  ← View System (DecorView overlay, SpringAnimation)
└── flare-compose/  ← Jetpack Compose (FlareHost, FlareAlert, gestures)
```

This clean separation keeps the **Core** module lightweight and ready for multiplatform targets (like KMP iOS/Desktop) in subsequent versions without modifying Android UI layers.

---

## 📝 License

This project is licensed under the BSD 3-Clause License - see the [LICENSE](LICENSE) file for details.
