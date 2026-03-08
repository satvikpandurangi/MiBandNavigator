package com.satvik.mibandnavigator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "nav_channel"
    private val NOTIFICATION_ID = 1001

    // Access our saved settings!
    private val sharedPrefs = context.getSharedPreferences("NavSettings", Context.MODE_PRIVATE)

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Mi Band Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Sends turn-by-turn alerts to Zepp" }
        notificationManager.createNotificationChannel(channel)
    }

    fun sendToBand(navData: NavData) {
        val isCompact = sharedPrefs.getBoolean("compact_mode", false)

        val bottomLine = if (navData.eta.isNotEmpty() && navData.totalDistance.isNotEmpty()) {
            "${navData.eta} • ${navData.totalDistance}"
        } else {
            navData.eta + navData.totalDistance
        }

        val text = if (isCompact) {
            // --- COMPACT MODE ---
            // Uses standard emojis to save vertical screen space
            val smallArrow = when (navData.direction) {
                NavDirection.LEFT -> "⬅️"
                NavDirection.RIGHT -> "➡️"
                NavDirection.STRAIGHT -> "⬆️"
                NavDirection.UTURN -> "↩️"
                NavDirection.SLIGHT_LEFT -> "↖️"
                NavDirection.SLIGHT_RIGHT -> "↗️"
                NavDirection.ROUNDABOUT -> "🔄"
                else -> "⏺"
            }
            "$smallArrow ${navData.distance}\n${navData.roadName}\n$bottomLine"
        } else {
            // --- DOT MATRIX MODE (Default) ---
            val arrowArt = when (navData.direction) {
                NavDirection.STRAIGHT -> "    •    \n   •••   \n    •    \n    •    "
                NavDirection.LEFT -> "      •  \n     •   \n   ••••  \n     •   \n      •  "
                NavDirection.RIGHT -> "  •      \n   •     \n  ••••   \n   •     \n  •      "
                NavDirection.UTURN -> "   •••   \n  •   •  \n  •   •  \n  •      \n  •      "
                NavDirection.SLIGHT_LEFT -> "   ••••  \n   ••    \n   • •   \n     •   "
                NavDirection.SLIGHT_RIGHT -> "  ••••   \n    ••   \n   • •   \n   •     "
                NavDirection.ROUNDABOUT -> "    ••   \n  •    • \n  •    • \n    ••   "
                else -> "   •••   \n   •••   \n   •••   "
            }
            "${navData.distance}\n$arrowArt\n${navData.roadName}\n$bottomLine"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle(" ")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun clear() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}