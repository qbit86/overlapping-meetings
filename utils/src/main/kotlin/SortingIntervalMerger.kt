/**
 * Implementation of IntervalMerger using sorting and merging approach.
 *
 * Algorithm:
 * 1. Sort intervals by start time
 * 2. Merge overlapping intervals
 * 3. Find the longest merged interval
 *
 * Time complexity: O(n log n) due to sorting
 * Space complexity: O(n) for merged intervals storage
 */
class SortingIntervalMerger<TInstant : Comparable<TInstant>, TDuration> : IntervalMerger<TInstant, TDuration> {
    override fun <TPolicy> findLongestBusyPeriod(
        intervals: List<ClosedRange<TInstant>>, policy: TPolicy
    ): ClosedRange<TInstant>? where TPolicy : Comparator<TDuration>, TPolicy : Subtractor<TInstant, TDuration> {
        // Step 1: Input validation - filter out invalid intervals where start >= end
        val validIntervals = intervals.filter { it.start < it.endInclusive }

        // Return null if no valid intervals exist (Kotlin's null safety approach)
        if (validIntervals.isEmpty()) return null

        // Step 2: Sort intervals by start time to enable sequential merging
        val sortedIntervals = validIntervals.sortedBy { it.start }

        // Step 3: Merge overlapping and adjacent intervals
        val mergedIntervals = mutableListOf<ClosedRange<TInstant>>()
        var currentMergedInterval = sortedIntervals.first()

        // Process remaining intervals sequentially
        for (interval in sortedIntervals.drop(1)) {
            // Check if intervals overlap or are adjacent (touching boundaries)
            // Adjacent check: interval.start <= currentInterval.endInclusive
            // This handles both overlapping ([1,5] + [3,7]) and adjacent ([1,3] + [3,5]) cases
            if (interval.start <= currentMergedInterval.endInclusive) {
                // Merge intervals by extending the end to the maximum of both ends
                currentMergedInterval =
                    currentMergedInterval.start..maxOf(currentMergedInterval.endInclusive, interval.endInclusive)
            } else {
                // Gap found - save current merged interval and start new one
                mergedIntervals.add(currentMergedInterval)
                currentMergedInterval = interval
            }
        }

        // Don't forget to add the last interval!
        mergedIntervals.add(currentMergedInterval)

        // Step 4: Find the interval with maximum duration
        // return mergedIntervals.maxByOrNull { it.endInclusive - it.start }
        val intervalDurationPairs = mergedIntervals.map { Pair(it, policy.subtract(it.endInclusive, it.start)) }
        val maxIntervalDurationPair = intervalDurationPairs.maxWithOrNull { a, b -> policy.compare(a.second, b.second) }
        return maxIntervalDurationPair?.first
    }
}
