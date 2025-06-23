@file:OptIn(ExperimentalTime::class)

package org.example.app

import InstantPolicy
import SortingIntervalMerger
import kotlin.time.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Demonstration app for SortingIntervalMerger with InstantPolicy.
 *
 * This app showcases how to find the longest continuous busy period
 * from overlapping time intervals using Kotlin's time API.
 */
@OptIn(ExperimentalTime::class)
fun main() {
    println("=".repeat(60))
    println("Overlapping Meetings - Longest Busy Period Demo")
    println("=".repeat(60))

    val now = Clock.System.now()

    // Create sample meeting intervals - mix of overlapping, adjacent, and separate
    val meetingIntervals = listOf(
        // Morning block: overlapping meetings (9:00-10:30 and 9:30-11:00)
        now..(now + 1.hours + 30.minutes),                    // 9:00-10:30
        (now + 30.minutes)..(now + 2.hours),                  // 9:30-11:00

        // Lunch meeting: separate from morning block (12:00-13:00)
        (now + 3.hours)..(now + 4.hours),                     // 12:00-13:00

        // Afternoon block: adjacent meetings (14:00-15:00 and 15:00-17:00)
        (now + 5.hours)..(now + 6.hours),                     // 14:00-15:00
        (now + 6.hours)..(now + 8.hours),                     // 15:00-17:00

        // Late afternoon: overlapping with afternoon block (16:00-18:00)
        (now + 7.hours)..(now + 9.hours),                     // 16:00-18:00

        // Evening: separate meeting (19:00-20:00)
        (now + 10.hours)..(now + 11.hours)                    // 19:00-20:00
    )

    // Display original intervals
    println("\nOriginal Meeting Intervals:")
    println("-".repeat(40))
    meetingIntervals.forEachIndexed { index, interval ->
        val duration = InstantPolicy.subtract(interval.endInclusive, interval.start)
        println("${index + 1}. ${formatTimeRange(interval, now)} (${formatDuration(duration)})")
    }

    val merger = SortingIntervalMerger<Instant, Duration>()

    // Find the longest busy period using InstantPolicy
    val longestBusyPeriod = merger.findLongestBusyPeriod(meetingIntervals, InstantPolicy)

    // Display results with null safety
    println("\nResults:")
    println("-".repeat(40))

    if (longestBusyPeriod != null) {
        val duration = InstantPolicy.subtract(longestBusyPeriod.endInclusive, longestBusyPeriod.start)

        println("✅ Longest busy period found!")
        println("   Time range: ${formatTimeRange(longestBusyPeriod, now)}")
        println("   Duration: ${formatDuration(duration)}")

        println("\nExplanation:")
        println("The algorithm merged overlapping and adjacent intervals to find")
        println("the longest continuous period where at least one meeting is active.")

    } else {
        // This shouldn't happen with our valid data, but demonstrates null safety
        println("❌ No valid intervals found (all intervals were invalid)")
    }

    println("\n" + "=".repeat(60))
    println("Demo completed successfully!")
    println("=".repeat(60))
}

/**
 * Helper function to format a time range for display.
 * Shows relative times (e.g., "Now + 1h 30m") for better readability.
 */
private fun formatTimeRange(range: ClosedRange<Instant>, baseTime: Instant): String {
    val startOffset = InstantPolicy.subtract(range.start, baseTime)
    val endOffset = InstantPolicy.subtract(range.endInclusive, baseTime)

    return "${formatOffset(startOffset)} → ${formatOffset(endOffset)}"
}

/**
 * Helper function to format a duration offset from base time.
 */
private fun formatOffset(offset: Duration): String {
    return when {
        offset == Duration.ZERO -> "Now"
        offset > Duration.ZERO -> "Now + ${formatDuration(offset)}"
        else -> "Now - ${formatDuration(-offset)}"
    }
}

/**
 * Helper function to format duration in a human-readable way.
 */
private fun formatDuration(duration: Duration): String {
    val hours = duration.inWholeHours
    val minutes = (duration - hours.hours).inWholeMinutes

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "${duration.inWholeSeconds}s"
    }
}
