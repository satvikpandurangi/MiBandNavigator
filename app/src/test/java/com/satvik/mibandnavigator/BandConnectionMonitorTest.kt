package com.satvik.mibandnavigator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BandConnectionMonitorTest {
    @Test
    fun uiListsEveryDeclaredSupportedDeviceFamily() {
        assertEquals(
            listOf(
                "Mi Band 4",
                "Mi Band 5",
                "Mi Band 6",
                "Mi Band 7",
                "Mi Band 8",
                "Amazfit Band Series"
            ),
            BandConnectionMonitor.supportedDeviceDisplayNames
        )
        BandConnectionMonitor.supportedDeviceDisplayNames.forEach { name ->
            assertTrue("Expected UI device $name to be accepted", BandConnectionMonitor.isSupportedBandName(name))
        }
    }

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
        listOf("Car Audio", "Wireless Earbuds", "Pixel Watch", "Mi Watch", "Amazfit GTR", "Unknown").forEach { name ->
            assertFalse("Expected $name to be rejected", BandConnectionMonitor.isSupportedBandName(name))
        }
    }
}
