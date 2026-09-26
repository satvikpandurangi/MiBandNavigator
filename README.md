<div align="center">

<img src="MiBand-Navigator-Logo.png" alt="MiBand Navigator Logo" width="120"/>

# 🧭 MiBand Navigator

### Seamless Google Maps turn-by-turn navigation for Xiaomi & Amazfit smart bands

[![GitHub](https://img.shields.io/badge/GitHub-Repository-181717?style=flat&logo=github)](https://github.com/satvikpandurangi/MiBandNavigator)
[![Release](https://img.shields.io/badge/Release-v1.1.0-success?style=flat)](https://github.com/satvikpandurangi/MiBandNavigator/releases/tag/v1.1.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://github.com/satvikpandurangi/MiBandNavigator)
[![Stars](https://img.shields.io/github/stars/satvikpandurangi/MiBandNavigator?style=flat)](https://github.com/satvikpandurangi/MiBandNavigator/stargazers)
[![Open Source](https://img.shields.io/badge/Open%20Source-100%25-blue?style=flat)](https://github.com/satvikpandurangi/MiBandNavigator)

</div>

---

## 📖 Overview

**MiBand Navigator** is a lightweight, background-driven Android application that bridges Google Maps and legacy fitness trackers. Using Android's notification listener service, it intercepts live navigation data from Google Maps, parses it, and reformats it into optimized visual alerts forwarded directly to a Xiaomi Mi Band or Amazfit band through Zepp Life or Zepp.

## ❓ Problem Statement

Most legacy smart bands lack native map integration or turn-by-turn navigation support. This is a hardware limitation that MiBand Navigator solves entirely through software: instead of relying on generic, unreadable text notifications, the app parses raw, high-frequency Google Maps notification payloads, extracts distances and directions, and injects custom ASCII arrows or compact emojis before pushing the result to the watch.

## ✨ Features

- **Smart Background Engine** — An optimized `NotificationListenerService` that filters and hijacks Google Maps notification streams without a direct BLE connection or excess battery drain
- **Hardware-Optimized UI Modes**
  - Arrow Animation — animated on-screen arrows for upcoming turns
  - Compact Notifications — shorter text formats for small band screens
  - Visual Progress Bars — real-time distance tracking visualizer
- **UX Throttling** — smart vibration controls to avoid excessive buzzing during rapid map updates
- **Connection-Aware Delivery** — navigation alerts are emitted only while a supported Mi Band or Amazfit Band is actively connected over Bluetooth LE
- **Real-Time Navigation Sync** — every changed Maps payload is forwarded immediately, while distance and ETA refreshes stay silent and only new maneuvers vibrate
- **Modern Android UI** — Jetpack Compose interface with custom Canvas-drawn icons, live notification previews, and a built-in setup guide

## ⚙️ System Architecture / Workflow

```
Google Maps Notification
        │
        ▼
NotificationListenerService (intercepts & filters)
        │
        ▼
Parser (extracts distance & direction data)
        │
        ▼
Formatter (ASCII arrows / compact text / progress bar)
        │
        ▼
Zepp Life / Zepp (forwards alert via BLE)
        │
        ▼
Mi Band / Amazfit Display
```

**Compatible Devices:** Mi Band 4, 5, 6, 7, 8; future Mi Bands; Amazfit Band Series — all bridged through the Zepp Life / Zepp app.

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material Design 3) |
| Graphics | Native Compose Canvas API |
| Architecture | Event-driven background processing with real-time UI state observation |
| Build | Gradle (Kotlin DSL) |

## 📂 Project Structure

```
MiBandNavigator/
├── app/                     # Main Android application module
├── gradle/                  # Gradle wrapper files
├── build.gradle.kts         # Project build configuration
├── settings.gradle.kts      # Gradle settings
├── gradlew / gradlew.bat    # Gradle wrapper scripts
└── MiBand-Navigator-Logo.png
```

## 🚀 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/satvikpandurangi/MiBandNavigator.git
   ```
2. Open the project in Android Studio.
3. Build and install the app on your Android device via Gradle or Android Studio's Run button.

## 📱 Usage

1. **Install & open Zepp Life or Zepp** — ensure it's connected to your band and running in the background.
2. **Allow Nearby Devices and Notifications** — accept the Android permission prompts when the app first opens.
3. **Enable Notification Access** — tap **Grant Notification Access** in MiBand Navigator and enable the service.
4. **Configure notification forwarding** — in Zepp Life / Zepp → Notification → App Alerts, enable "MiBand Navigator."
5. **Disable Battery Optimization** — for both this app and Zepp Life / Zepp, to keep them alive in the background.
6. **Connect your band and start navigation** — alerts are sent only while a supported band is connected and Google Maps has an active route. Distance and ETA updates refresh silently; a new maneuver triggers vibration.

## 🔐 Permissions & Privacy

- **Notification access** is used only to read active Google Maps navigation notifications.
- **Nearby devices** is used only to verify that a supported band is currently connected over Bluetooth LE.
- **Post notifications** is used to provide the formatted navigation alert that Zepp forwards to the band.
- Navigation data stays on the phone; the app has no internet permission and does not upload route information.

## 📸 Screenshots

<table align="center">
  <tr>
    <td align="center">
      <img src="App_Home_Screen.png" width="220"><br>
      <b>App Home Screen</b>
    </td>
    <td align="center">
      <img src="Setup_Guide.png" width="220"><br>
      <b>Setup Guide</b>
    </td>
    <td align="center">
      <img src="Band_Notification_Preview.jpeg" width="220"><br>
      <b>Notification Preview</b>
    </td>
  </tr>
</table>

## 🔮 Future Improvements

- Further battery optimization for the `NotificationListenerService`
- Expanded device support, including custom ASCII parsers for newer Mi Band screen sizes
- Code and Canvas-math review/refinement for the Compose UI

## 🤝 Contributing & Forking
This project is 100% open-source. I built this to solve a personal hardware limitation, but there is always room for optimization! 

Whether you want to use this code as a base for your own wearable projects, or you want to help make MiBand Navigator better, you are highly encouraged to fork this repository. 

**Areas where I'd love some help:**
* **Battery Optimization:** Ideas to make the `NotificationListenerService` even more lightweight.
* **Device Support:** Expanding the custom ASCII parsers for different screen sizes (like the newer Mi Band 8/9 standard).
* **Code Review:** If you are an experienced Android dev and see a way to make the Compose UI or Canvas math cleaner, open a Pull Request!

Feel free to open an Issue, submit a Pull Request, or just fork the repo to experiment.

---
> *Engineered by Satvik — Built for the Xiaomi wearable ecosystem.*
