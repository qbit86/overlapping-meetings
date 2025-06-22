/**
 * Strategy implementation for integer-based time intervals and durations.
 *
 * This policy provides arithmetic and comparison operations for intervals where both
 * time instants and durations are represented as integers. Commonly used for:
 * - Discrete time slots (e.g., minute-based scheduling)
 * - Integer timestamps (e.g., Unix timestamps in seconds)
 * - Slot-based time systems (e.g., 15-minute appointment blocks)
 *
 * **Thread Safety**: This object is immutable and thread-safe.
 *
 * @see Subtractor for duration calculation contract
 * @see Comparator for comparison contract
 * @see IntervalMerger for usage in interval merging algorithms
 *
 * Example usage:
 * ```kotlin
 * val intervals = listOf(1..5, 3..8, 10..12)
 * val merger = SortingIntervalMerger<Int, Int>()
 * val result = merger.findLongestBusyPeriod(intervals, IntPolicy)
 * // result: 3..8 (duration = 5, longest merged interval)
 * ```
 */
object IntPolicy : Subtractor<Int, Int>, Comparator<Int> {
    /**
     * Calculates the duration between two integer time instants.
     *
     * @param first The later time instant (end time)
     * @param second The earlier time instant (start time) to subtract from first
     * @return The integer duration between the two instants
     */
    override fun subtract(first: Int, second: Int): Int = first - second

    /**
     * Compares two integer durations for ordering.
     *
     * @param first The first duration to compare
     * @param second The second duration to compare
     * @return Negative if first < second, zero if equal, positive if first > second
     */
    override fun compare(first: Int, second: Int): Int = first.compareTo(second)
}
