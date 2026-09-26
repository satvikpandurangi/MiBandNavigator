package com.satvik.mibandnavigator

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class BandConnectionMonitor(context: Context) {
    private val appContext = context.applicationContext
    private val bluetoothManager = appContext.getSystemService(BluetoothManager::class.java)

    @SuppressLint("MissingPermission")
    fun isSupportedBandConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        val adapter = bluetoothManager?.adapter ?: return false
        if (!adapter.isEnabled) return false

        return try {
            val bondedNamesByAddress = adapter.bondedDevices.associate { device ->
                device.address to device.name.orEmpty()
            }

            bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).any { device ->
                val currentName = device.name.orEmpty()
                val bondedName = bondedNamesByAddress[device.address].orEmpty()
                isSupportedBandName(currentName) || isSupportedBandName(bondedName)
            }
        } catch (_: SecurityException) {
            false
        }
    }

    companion object {
        internal fun isSupportedBandName(name: String): Boolean {
            val normalized = name.lowercase().replace(Regex("[^a-z0-9]+"), " ").trim()
            return normalized.contains("mi band") ||
                normalized.contains("mi smart band") ||
                normalized.contains("xiaomi smart band") ||
                (normalized.contains("amazfit") && normalized.contains("band"))
        }
    }
}
