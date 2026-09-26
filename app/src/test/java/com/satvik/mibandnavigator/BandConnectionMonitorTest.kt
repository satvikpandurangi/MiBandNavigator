package com.satvik.mibandnavigator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BandConnectionMonitorTest {
    @Test
    fun recognizesSupportedBandFamilies() {
        listOf(
            "Mi Band 4",
            "MI Smart Band 5",
            "Mi Smart Band 6",
            "Xiaomi Smart Band 7",
            "Xiaomi Smart Band 8",
            "Amazfit Band 5",
            "Amazfit Band 7"
        ).forEach { name ->
            assertTrue("Expected $name to be supported", BandConnectionMonitor.isSupportedBandName(name))
        }
    }

    @Test
    fun rejectsUnrelatedBluetoothDevices() {
        listOf("Car Audio", "Wireless Earbuds", "Pixel Watch", "Unknown").forEach { name ->
            assertFalse("Expected $name to be rejected", BandConnectionMonitor.isSupportedBandName(name))
        }
    }
}
