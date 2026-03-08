package com.satvik.mibandnavigator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NavigationService : NotificationListenerService() {

    private val TAG = "NavService"
    private val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"

    private val parser = NavigationParser()
    private lateinit var notifier: NotificationHelper

    private var lastDirection: NavDirection = NavDirection.UNKNOWN
    private var lastDistance: String = ""
    private var lastUpdateTime: Long = 0

    private val uiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val dirName = intent?.getStringExtra("test_dir") ?: return
            try {
                val testDirection = NavDirection.valueOf(dirName)
                val testData = NavData("150 m", "Test Road", testDirection, "10 min", "4.5 km")

                lastDirection = NavDirection.UNKNOWN
                lastDistance = ""
                lastUpdateTime = 0

                notifier.sendToBand(testData)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing test direction", e)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notifier = NotificationHelper(this)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RECEIVER_EXPORTED else 0
        registerReceiver(uiReceiver, IntentFilter("TRIGGER_TEST_NAV"), flags)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(uiReceiver)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn?.packageName != GOOGLE_MAPS_PACKAGE) return

        val extras = sbn.notification.extras

        // --- SETTINGS INTEGRATION ---
        val sharedPrefs = getSharedPreferences("NavSettings", Context.MODE_PRIVATE)
        val isDebugEnabled = sharedPrefs.getBoolean("debug_mode", false)
        val isVibrationEnabled = sharedPrefs.getBoolean("vibrate_turn", true)

        // Only dump logs if Developer Debug Mode is ON
        if (isDebugEnabled) {
            Log.w(TAG, "🐛 DEBUG MODE ON: Dumping Extras")
            for (key in extras.keySet()) {
                Log.w(TAG, "🐛 KEY: '$key' | VALUE: '${extras.get(key)}'")
            }
        }

        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val subText = extras.getCharSequence("android.subText")?.toString() ?: ""
        val textLines = extras.getCharSequenceArray("android.textLines")?.map { it.toString() }?.toTypedArray()

        val cleanData = parser.parseMapsData(title, text, subText, textLines)

        if (cleanData.direction == lastDirection && cleanData.distance == lastDistance) return

        val currentTime = System.currentTimeMillis()
        if (cleanData.direction == lastDirection) {
            if (currentTime - lastUpdateTime < 10000) return
        }

        // Only force heavy vibration if the user wants it!
        if (cleanData.direction != lastDirection && cleanData.direction != NavDirection.UNKNOWN) {
            if (isVibrationEnabled) {
                Log.d(TAG, "📳 TURN CHANGED! Forcing new alert.")
                notifier.clear()
            }
        }

        lastDirection = cleanData.direction
        lastDistance = cleanData.distance
        lastUpdateTime = currentTime

        notifier.sendToBand(cleanData)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn?.packageName == GOOGLE_MAPS_PACKAGE) {
            notifier.clear()
            lastDirection = NavDirection.UNKNOWN
            lastDistance = ""
            lastUpdateTime = 0
        }
    }
}