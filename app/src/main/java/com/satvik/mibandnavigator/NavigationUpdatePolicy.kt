package com.satvik.mibandnavigator

internal object NavigationUpdatePolicy {
    fun isNewManeuver(previous: NavData?, current: NavData): Boolean {
        if (previous == null) return true

        return previous.direction != current.direction ||
            normalizeRoad(previous.roadName) != normalizeRoad(current.roadName)
    }

    private fun normalizeRoad(roadName: String): String =
        roadName.trim().lowercase().replace(Regex("\\s+"), " ")
}
