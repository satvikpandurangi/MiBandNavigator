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
        val combinedText = "$title $text".lowercase()

        // Scan for keywords to determine the arrow direction
        val dir = when {
            combinedText.contains("roundabout") -> NavDirection.ROUNDABOUT
            combinedText.contains("u-turn") || combinedText.contains("u turn") -> NavDirection.UTURN
            combinedText.contains("slight left") -> NavDirection.SLIGHT_LEFT
            combinedText.contains("slight right") -> NavDirection.SLIGHT_RIGHT
            combinedText.contains("left") -> NavDirection.LEFT
            combinedText.contains("right") -> NavDirection.RIGHT
            combinedText.contains("straight") || combinedText.contains("towards") -> NavDirection.STRAIGHT
            else -> NavDirection.UNKNOWN
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
}