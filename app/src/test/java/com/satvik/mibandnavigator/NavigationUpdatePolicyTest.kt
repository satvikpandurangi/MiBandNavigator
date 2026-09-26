package com.satvik.mibandnavigator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUpdatePolicyTest {
    private val currentTurn = NavData("150 m", "Main Road", NavDirection.LEFT, "10 min", "4.5 km")

    @Test
    fun firstManeuverVibrates() {
        assertTrue(NavigationUpdatePolicy.isNewManeuver(null, currentTurn))
    }

    @Test
    fun distanceAndEtaUpdatesStaySilent() {
        val update = currentTurn.copy(distance = "100 m", eta = "9 min", totalDistance = "4.4 km")

        assertFalse(NavigationUpdatePolicy.isNewManeuver(currentTurn, update))
    }

    @Test
    fun changedDirectionVibrates() {
        val update = currentTurn.copy(direction = NavDirection.RIGHT)

        assertTrue(NavigationUpdatePolicy.isNewManeuver(currentTurn, update))
    }

    @Test
    fun consecutiveSameDirectionTurnsOnDifferentRoadsVibrate() {
        val update = currentTurn.copy(roadName = "Market Road")

        assertTrue(NavigationUpdatePolicy.isNewManeuver(currentTurn, update))
    }
}
