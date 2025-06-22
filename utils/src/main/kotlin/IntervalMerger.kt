/**
 * Interface for finding the longest continuous busy period from overlapping intervals.
 * Handles invalid intervals gracefully by filtering them out.
 */
interface IntervalMerger<TInstant : Comparable<TInstant>, TDuration> {
    /**
     * Finds the longest continuous period where at least one interval is active.
     *
     * @param intervals Collection of time intervals (may overlap)
     * @return The longest continuous busy period, or null if no valid intervals exist
     *
     * Example:
     * Input: [9.0..10.0, 9.5..11.0, 13.0..14.0, 15.0..17.0]
     * Output: 9.0..11.0 (duration: 2.0)
     */
    fun <TPolicy> findLongestBusyPeriod(intervals: List<ClosedRange<TInstant>>, policy: TPolicy): ClosedRange<TInstant>?
    where TPolicy : Comparator<TDuration>,
          TPolicy : Subtractor<TInstant, TDuration>
}
