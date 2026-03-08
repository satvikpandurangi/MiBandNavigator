package com.satvik.mibandnavigator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "nav_channel"
    private val NOTIFICATION_ID = 1001

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

        val text = if (isCompact) {
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
            // Compact mode text
            val bottomLine = if (navData.eta.isNotEmpty()) "${navData.eta} • ${navData.totalDistance}" else navData.totalDistance
            "$smallArrow ${navData.distance}\n${navData.roadName}\n$bottomLine"
        } else {
            // --- THE FULL 5-LINE GRID WITH TAILS ---
            val arrowArt = when (navData.direction) {
                NavDirection.STRAIGHT ->
                    "    •    \n" +
                            "   • •   \n" +
                            "  •   •  \n" +
                            "    •    \n" +
                            "    •    "
                NavDirection.LEFT ->
                    "      •  \n" +
                            "    •    \n" +
                            "  • • • •\n" +
                            "    •    \n" +
                            "      •  "
                NavDirection.RIGHT ->
                    "  •      \n" +
                            "    •    \n" +
                            "• • • •  \n" +
                            "    •    \n" +
                            "  •      "
                NavDirection.UTURN ->
                    "   •••   \n" +
                            "  •   •  \n" +
                            "  •  ••  \n" +
                            "  • •    \n" +
                            "  •      "
                NavDirection.SLIGHT_LEFT ->
                    "   ••    \n" +
                            "  •  •   \n" +
                            "      •  \n" +
                            "      •  \n" +
                            "         "
                NavDirection.SLIGHT_RIGHT ->
                    "    ••   \n" +
                            "   •  •  \n" +
                            "  •      \n" +
                            "  •      \n" +
                            "         "
                NavDirection.ROUNDABOUT ->
                    "   •••   \n" +
                            "  •   •  \n" +
                            "    ••   \n" +
                            "  ••     \n" +
                            " •       "
                else ->
                    "    •    \n" +
                            "   • •   \n" +
                            "  •   •  \n" +
                            "    •    \n" +
                            "    •    "
            }

            // --- THE TEXT COMPRESSION FIX ---
            // We combine the ETA with the distance at the top (e.g., "150 m • 10 min")
            // We combine the Total Distance with the road name at the bottom (e.g., "Test Road • 4.5 km")
            // This saves a full line of vertical space, allowing the tail to stay!
            val topText = if (navData.eta.isNotEmpty()) "${navData.distance}  •  ${navData.eta}" else navData.distance
            val bottomText = if (navData.totalDistance.isNotEmpty()) "${navData.roadName}    ${navData.totalDistance}" else navData.roadName

            "$topText\n$arrowArt\n$bottomText"
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