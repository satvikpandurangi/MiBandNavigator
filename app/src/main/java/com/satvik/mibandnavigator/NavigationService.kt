package com.satvik.mibandnavigator

import android.app.Notification
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.content.ContextCompat

class NavigationService : NotificationListenerService() {

    private val tag = "NavService"
    private val googleMapsPackage = "com.google.android.apps.maps"

    private val parser = NavigationParser()
    private lateinit var notifier: NotificationHelper
    private lateinit var bandConnectionMonitor: BandConnectionMonitor
    private val mainHandler = Handler(Looper.getMainLooper())

    private var lastData: NavData? = null
    private var activeNavigationKey: String? = null

    private val uiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!bandConnectionMonitor.isSupportedBandConnected()) {
                clearNavigation()
                return
            }

            val dirName = intent?.getStringExtra("test_dir") ?: return
            try {
                val testDirection = NavDirection.valueOf(dirName)
                val testData = NavData("150 m", "Test Road", testDirection, "10 min", "4.5 km")
                notifier.clear()
                notifier.sendToBand(testData)
            } catch (e: IllegalArgumentException) {
                Log.e(tag, "Error parsing test direction", e)
            }
        }
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (bandConnectionMonitor.isSupportedBandConnected()) {
                syncCurrentNavigation()
            } else {
                clearNavigation()
            }

            // The ACL-connected broadcast can arrive just before the GATT connection
            // appears in BluetoothManager, so check once more after it settles.
            if (intent?.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                mainHandler.removeCallbacksAndMessages(null)
                mainHandler.postDelayed(::syncCurrentNavigation, BAND_CONNECTION_SETTLE_MS)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notifier = NotificationHelper(this)
        bandConnectionMonitor = BandConnectionMonitor(this)

        ContextCompat.registerReceiver(
            this,
            uiReceiver,
            IntentFilter("TRIGGER_TEST_NAV"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val bluetoothFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        ContextCompat.registerReceiver(
            this,
            bluetoothStateReceiver,
            bluetoothFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        unregisterReceiver(uiReceiver)
        unregisterReceiver(bluetoothStateReceiver)
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        syncCurrentNavigation()
    }

    private fun syncCurrentNavigation() {
        if (!bandConnectionMonitor.isSupportedBandConnected()) {
            clearNavigation()
            return
        }

        val currentNavigation = activeNotifications
            ?.asSequence()
            ?.filter { it.packageName == googleMapsPackage }
            ?.sortedByDescending { it.postTime }
            ?.firstOrNull(::isActiveNavigationNotification)

        if (currentNavigation == null) {
            clearNavigation()
        } else {
            handleMapsNotification(currentNavigation)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn?.packageName != googleMapsPackage) return

        handleMapsNotification(sbn)
    }

    private fun handleMapsNotification(sbn: StatusBarNotification) {
        if (!bandConnectionMonitor.isSupportedBandConnected()) {
            clearNavigation()
            return
        }

        if (!isActiveNavigationNotification(sbn)) return

        val extras = sbn.notification.extras
        val sharedPrefs = getSharedPreferences("NavSettings", Context.MODE_PRIVATE)
        val isVibrationEnabled = sharedPrefs.getBoolean("vibrate_turn", true)

        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val subText = extras.getCharSequence("android.subText")?.toString() ?: ""
        val textLines = extras.getCharSequenceArray("android.textLines")?.map { it.toString() }?.toTypedArray()

        if (sharedPrefs.getBoolean("debug_mode", false)) {
            Log.d(tag, "Maps payload: title=$title, text=$text, subText=$subText, lines=${textLines?.contentToString()}")
        }

        val cleanData = parser.parseMapsData(title, text, subText, textLines)
        if (cleanData.direction == NavDirection.UNKNOWN) return

        activeNavigationKey = sbn.key
        // Google Maps can repost an identical payload; only exact duplicates are skipped.
        // Distance, road, ETA, total-distance, and direction changes are all forwarded.
        if (cleanData == lastData) return

        val maneuverChanged = NavigationUpdatePolicy.isNewManeuver(lastData, cleanData)
        val shouldAlert = isVibrationEnabled && maneuverChanged
        if (shouldAlert && lastData != null) {
            notifier.clear()
        }

        notifier.sendToBand(cleanData, alert = shouldAlert)
        lastData = cleanData
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn?.packageName == googleMapsPackage && sbn.key == activeNavigationKey) {
            syncCurrentNavigation()
        }
    }

    private fun isActiveNavigationNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        val hasNavigationCategory = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            notification.category == Notification.CATEGORY_NAVIGATION
        val navigationLike = hasNavigationCategory || sbn.isOngoing
        return navigationLike && parseNavigation(sbn).direction != NavDirection.UNKNOWN
    }

    private fun parseNavigation(sbn: StatusBarNotification): NavData {
        val extras = sbn.notification.extras
        return parser.parseMapsData(
            title = extras.getCharSequence("android.title")?.toString().orEmpty(),
            text = extras.getCharSequence("android.text")?.toString().orEmpty(),
            subText = extras.getCharSequence("android.subText")?.toString().orEmpty(),
            textLines = extras.getCharSequenceArray("android.textLines")
                ?.map { it.toString() }
                ?.toTypedArray()
        )
    }

    private fun clearNavigation() {
        notifier.clear()
        lastData = null
        activeNavigationKey = null
    }

    companion object {
        private const val BAND_CONNECTION_SETTLE_MS = 750L
    }
}
