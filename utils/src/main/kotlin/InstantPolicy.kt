import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Strategy implementation for Kotlin's time API with Instant and Duration types.
 *
 * This policy provides arithmetic and comparison operations for intervals using
 * Kotlin's experimental time API, where time instants are represented as [Instant]
 * and durations as [Duration]. Ideal for:
 * - Modern time-based applications requiring type safety
 * - Cross-platform time handling (Kotlin Multiplatform)
 * - Integration with Kotlin's coroutines and time-based operations
 * - Applications requiring nanosecond precision
 *
 * **Experimental API**: This class uses Kotlin's experimental time API.
 * The API may change in future versions. Consider this when using in production code.
 *
 * **Thread Safety**: Instances of this class are immutable and thread-safe.
 * [Instant] and [Duration] are also immutable value types.
 *
 * @see Subtractor for duration calculation contract
 * @see Comparator for comparison contract
 * @see IntervalMerger for usage in interval merging algorithms
 * @see kotlin.time.Instant for time instant representation
 * @see kotlin.time.Duration for duration representation
 *
 * Example usage:
 * ```kotlin
 * val now = Clock.System.now()
 * val intervals = listOf(
 *     now..(now + 5.minutes),
 *     (now + 3.minutes)..(now + 8.minutes)
 * )
 * val merger = SortingIntervalMerger<Instant, Duration>()
 * val policy = InstantPolicy()
 * val result = merger.findLongestBusyPeriod(intervals, policy)
 * // result: merged interval with 8-minute duration
 * ```
 */
@OptIn(ExperimentalTime::class)
object InstantPolicy : Subtractor<Instant, Duration>, Comparator<Duration> {
    /**
     * Calculates the duration between two time instants.
     *
     * @param first The later time instant (end time)
     * @param second The earlier time instant (start time) to subtract from first
     * @return The [Duration] between the two instants (positive if first > second)
     */
    override fun subtract(first: Instant, second: Instant): Duration = first - second

    /**
     * Compares two durations for ordering.
     *
     * Uses the natural ordering of [Duration] values, supporting nanosecond precision
     * and handling both positive and negative durations correctly.
     *
     * @param first The first duration to compare
     * @param second The second duration to compare
     * @return Negative if first < second, zero if equal, positive if first > second
     */
    override fun compare(first: Duration, second: Duration): Int = first.compareTo(second)
}
