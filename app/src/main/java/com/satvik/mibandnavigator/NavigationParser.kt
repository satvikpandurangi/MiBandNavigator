package com.satvik.mibandnavigator

// 1. We define a strict set of allowed directions
enum class NavDirection {
    LEFT, RIGHT, STRAIGHT, SLIGHT_LEFT, SLIGHT_RIGHT, UTURN, ROUNDABOUT, UNKNOWN
}

// 2. Updated data packet to hold our new ETA and Total Distance variables
data class NavData(
    val distance: String,
    val roadName: String,
    val direction: NavDirection,
    val eta: String = "",
    val totalDistance: String = ""
)

class NavigationParser {

    // Updated to accept subText and textLines from the notification
    fun parseMapsData(title: String, text: String, subText: String, textLines: Array<String>?): NavData {
        // Combine both strings and make them lowercase to make searching easier
        val primaryText = "$title $text".lowercase()
        val supplementalText = textLines.orEmpty().joinToString(" ").lowercase()

        // Scan for keywords to determine the arrow direction
        val primaryDirection = detectDirection(primaryText)
        val dir = if (primaryDirection != NavDirection.UNKNOWN) {
            primaryDirection
        } else {
            detectDirection(supplementalText)
        }

        // Clean up the road name to save screen space on the Mi Band
        val cleanRoad = text.replace(Regex("(?i)turn.*onto |(?i)towards |(?i)continue.*onto "), "").trim()

        // --- EXTRACTION LOGIC FOR ETA & DISTANCE ---
        // Google Maps usually puts ETA and distance in subText like this: "10 min • 4 km"
        var parsedEta = ""
        var parsedTotalDist = ""

        if (subText.isNotEmpty()) {
            // Split the string at the bullet point character
            val parts = subText.split("·", "•", "-").map { it.trim() }
            if (parts.size >= 2) {
                parsedEta = parts[0]       // "10 min"
                parsedTotalDist = parts[1] // "4 km"
            } else {
                // If it doesn't split perfectly, just show whatever is there
                parsedEta = subText
            }
        }

        // Return our neat, structured data packet
        return NavData(
            distance = title,
            roadName = cleanRoad,
            direction = dir,
            eta = parsedEta,
            totalDistance = parsedTotalDist
        )
    }

    private fun detectDirection(value: String): NavDirection = when {
        value.contains("roundabout") -> NavDirection.ROUNDABOUT
        value.contains("u-turn") || value.contains("u turn") -> NavDirection.UTURN
        value.contains("slight left") || value.contains("keep left") || value.contains("bear left") -> NavDirection.SLIGHT_LEFT
        value.contains("slight right") || value.contains("keep right") || value.contains("bear right") -> NavDirection.SLIGHT_RIGHT
        value.contains("left") -> NavDirection.LEFT
        value.contains("right") -> NavDirection.RIGHT
        value.contains("straight") || value.contains("toward") || value.contains("continue") ||
            value.contains("head ") || value.contains("merge") -> NavDirection.STRAIGHT
        else -> NavDirection.UNKNOWN
    }

}
