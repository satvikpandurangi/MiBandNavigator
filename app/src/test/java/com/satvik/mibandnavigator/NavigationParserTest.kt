package com.satvik.mibandnavigator

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationParserTest {
    private val parser = NavigationParser()

    @Test
    fun keepsExistingDotMatrixPayloadFields() {
        val result = parser.parseMapsData(
            title = "150 m",
            text = "Turn left onto Main Road",
            subText = "10 min • 4.5 km",
            textLines = null
        )

        assertEquals(NavData("150 m", "Main Road", NavDirection.LEFT, "10 min", "4.5 km"), result)
    }

    @Test
    fun recognizesCommonMapsManeuverPhrases() {
        val cases = mapOf(
            "Keep left at the fork" to NavDirection.SLIGHT_LEFT,
            "Bear right" to NavDirection.SLIGHT_RIGHT,
            "Head north on Main Road" to NavDirection.STRAIGHT,
            "Continue on Ring Road" to NavDirection.STRAIGHT,
            "Make a U-turn" to NavDirection.UTURN,
            "Take the second exit at the roundabout" to NavDirection.ROUNDABOUT
        )

        cases.forEach { (instruction, expected) ->
            val result = parser.parseMapsData("150 m", instruction, "", null)
            assertEquals(instruction, expected, result.direction)
        }
    }

    @Test
    fun fallsBackToExpandedNotificationLines() {
        val result = parser.parseMapsData(
            title = "Navigation",
            text = "",
            subText = "",
            textLines = arrayOf("In 300 m", "Turn right onto Market Road")
        )

        assertEquals(NavDirection.RIGHT, result.direction)
    }
}
