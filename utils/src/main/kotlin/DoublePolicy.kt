/**
 * Strategy implementation for floating-point time intervals and durations.
 *
 * This policy provides arithmetic and comparison operations for intervals where both
 * time instants and durations are represented as double-precision floating-point numbers.
 * Commonly used for:
 * - Continuous time measurements (e.g., seconds with fractional precision)
 * - Scientific simulations with precise timing requirements
 * - Financial systems requiring sub-second precision
 * - Performance benchmarking with millisecond/microsecond accuracy
 *
 * **Thread Safety**: This object is immutable and thread-safe.
 *
 * **Precision Considerations**: Uses IEEE 754 double-precision arithmetic.
 * Be aware of potential floating-point precision issues when comparing very small
 * differences or performing many accumulated operations.
 *
 * @see Subtractor for duration calculation contract
 * @see Comparator for comparison contract
 * @see IntervalMerger for usage in interval merging algorithms
 *
 * Example usage:
 * ```kotlin
 * val intervals = listOf(9.0..10.0, 9.5..11.0, 13.0..14.0)
 * val merger = SortingIntervalMerger<Double, Double>()
 * val result = merger.findLongestBusyPeriod(intervals, DoublePolicy)
 * // result: 9.0..11.0 (duration = 2.0, longest merged interval)
 * ```
 */
object DoublePolicy : Subtractor<Double, Double>, Comparator<Double> {
    /**
     * Calculates the duration between two floating-point time instants.
     *
     * @param first The later time instant (end time)
     * @param second The earlier time instant (start time) to subtract from first
     * @return The floating-point duration between the two instants
     */
    override fun subtract(first: Double, second: Double): Double = first - second

    /**
     * Compares two floating-point durations for ordering.
     *
     * Uses natural ordering of Double values, handling NaN and infinity according
     * to IEEE 754 standards (NaN is considered greater than any finite value).
     *
     * @param first The first duration to compare
     * @param second The second duration to compare
     * @return Negative if first < second, zero if equal, positive if first > second
     */
    override fun compare(first: Double, second: Double): Int = first.compareTo(second)
}
