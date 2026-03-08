<p align="center">
  <img src="MiBand-Navigator-Logo.png" width="150" alt="MiBand Navigator Logo">
</p>
# 🧭 MiBand Navigator

> **Seamless Google Maps Turn-by-Turn Navigation for Xiaomi & Amazfit Smart Bands.**

MiBand Navigator is a lightweight, background-driven Android application designed to bridge the gap between Google Maps and legacy fitness trackers. By leveraging Android's notification listener service, this app intercepts live navigation data, parses it, and formats it into optimized visual alerts forwarded directly to your wrist via the Zepp ecosystem.
---

## 🚀 The Problem & The Solution
Most legacy smart bands lack native map integration or turn-by-turn support. This project solves that hardware limitation through software systems integration.

Instead of relying on generic text notifications, this app acts as a data parser and visual formatter. It reads the raw, high-frequency notification payloads from Google Maps, extracts the relevant distances and directions, and injects custom-built ASCII arrows or compact emojis before pushing the payload to the watch.

---

## ✨ Key Features
* **Smart Background Engine:** Utilizes a highly optimized `NotificationListenerService` that actively filters and hijacks Google Maps streams without draining battery or requiring a direct BLE connection.
* **Hardware-Optimized UI Modes:** * **Arrow Animation:** Blinks animated arrows on-screen for upcoming turns.
  * **Compact Notifications:** Shorter text formats designed specifically for small band screens.
  * **Visual Progress Bars:** Real-time distance tracking visualizer formatted for watch displays.
* **UX Throttling:** Smart vibration controls ("Vibration on turn change") to prevent the watch from buzzing excessively during rapid map updates.
* **Modern Android UI:** A fully responsive Jetpack Compose interface featuring custom Canvas-drawn Neo-Minimalist icons, dynamic live notification previews, and a built-in step-by-step setup guide.

---

## ⌚ Compatible Devices
This app is designed to work as a bridge through the Zepp / Zepp Life apps. Based on the integrated setup guide, it supports:
* Mi Band 4, 5, 6, 7, 8
* Future Mi Bands
* Amazfit Band Series

---

## 🛠️ Installation & Setup Guide
1. **Install & open Zepp:** Make sure Zepp is installed, connected to your Mi Band, and running in the background. Do not close it.
2. **Enable Notification Access:** Go to Settings -> Notifications -> MiBand Navigator, and allow all notifications so navigation alerts can be forwarded.
3. **Configure Zepp Forwarding:** In Zepp -> Notification -> App Alerts, enable "MiBand Navigator" so your band vibrates on each turn.
4. **Battery Optimization:** Disable battery optimization for this app and Zepp to prevent Android from killing them in the background.
5. **Start Navigation:** Open Google Maps, start a route, and watch your Mi Band display turn-by-turn directions with vibration alerts.

---

## 💻 Tech Stack & Architecture
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material Design 3)
* **Graphics:** Native Compose Canvas API for custom scalable vector drawing.
* **Architecture:** Event-driven background processing with real-time UI state observation.

---

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
