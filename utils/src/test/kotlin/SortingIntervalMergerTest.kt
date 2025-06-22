import kotlin.test.*

/**
 * Comprehensive test suite for SortingIntervalMerger<T>.
 *
 * Tests cover:
 * - Basic functionality with overlapping and non-overlapping intervals
 * - Edge cases (empty lists, invalid intervals, single intervals)
 * - Type safety with different Comparable types (Double, Int)
 * - Algorithm-specific behavior (sorting, merging, longest selection)
 */
class SortingIntervalMergerTest {

    private val doubleMerger = SortingIntervalMerger<Double, Double>()
    private val intMerger = SortingIntervalMerger<Int, Int>()

    // ========== BASIC FUNCTIONALITY TESTS ==========

    @Test
    fun `should find longest busy period from problem statement example`() {
        // Given: Example from problem statement
        val intervals = listOf(
            9.0..10.0,    // Duration: 1.0
            9.5..11.0,    // Overlaps with first, merged duration: 2.0
            13.0..14.0,   // Duration: 1.0
            15.0..17.0    // Duration: 2.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return the merged interval [9.0, 11.0] with duration 2.0
        assertNotNull(result)
        assertEquals(9.0, result.start)
        assertEquals(11.0, result.endInclusive)
    }

    @Test
    fun `should find longest period when multiple overlapping groups exist`() {
        // Given: Two groups of overlapping intervals
        val intervals = listOf(
            1.0..3.0,     // Group 1: merges to [1.0, 4.0], duration 3.0
            2.0..4.0,
            10.0..15.0,   // Group 2: merges to [10.0, 18.0], duration 8.0 (longest)
            12.0..18.0,
            14.0..16.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return the longer merged interval [10.0, 18.0]
        assertNotNull(result)
        assertEquals(10.0, result.start)
        assertEquals(18.0, result.endInclusive)
    }

    @Test
    fun `should handle non-overlapping intervals correctly`() {
        // Given: Separate intervals with different durations
        val intervals = listOf(
            1.0..2.0,     // Duration: 1.0
            5.0..9.0,     // Duration: 4.0 (longest)
            12.0..13.0    // Duration: 1.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return the longest individual interval
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(9.0, result.endInclusive)
    }

    @Test
    fun `should handle single interval`() {
        // Given: Only one interval
        val intervals = listOf(5.0..10.0)

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return that single interval
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(10.0, result.endInclusive)
    }

    @Test
    fun `should handle adjacent intervals that touch but don't overlap`() {
        // Given: Adjacent intervals (end of one equals start of next)
        val intervals = listOf(
            1.0..3.0,     // Adjacent to next
            3.0..5.0,     // Should merge with previous: [1.0, 5.0], duration 4.0
            10.0..12.0    // Separate, duration 2.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Adjacent intervals should be treated as continuous
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(5.0, result.endInclusive)
    }

    // ========== EDGE CASES & ERROR HANDLING ==========

    @Test
    fun `should return null for empty interval list`() {
        // Given: Empty list
        val intervals = emptyList<ClosedRange<Double>>()

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return null
        assertNull(result)
    }

    @Test
    fun `should filter out invalid intervals where start is greater than end`() {
        // Given: Mix of valid and invalid intervals
        val intervals = listOf(
            1.0..5.0,     // Valid, duration 4.0
            10.0..8.0,    // Invalid: start > end, should be filtered
            15.0..20.0,   // Valid, duration 5.0 (longest)
            25.0..20.0    // Invalid: start > end, should be filtered
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return longest valid interval, ignoring invalid ones
        assertNotNull(result)
        assertEquals(15.0, result.start)
        assertEquals(20.0, result.endInclusive)
    }

    @Test
    fun `should return null when all intervals are invalid`() {
        // Given: Only invalid intervals
        val intervals = listOf(
            10.0..5.0,    // Invalid
            20.0..15.0,   // Invalid
            30.0..25.0    // Invalid
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return null since no valid intervals exist
        assertNull(result)
    }

    @Test
    fun `should handle identical intervals`() {
        // Given: Multiple identical intervals
        val intervals = listOf(
            5.0..10.0,
            5.0..10.0,
            5.0..10.0,
            15.0..18.0    // Different interval, duration 3.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Identical intervals should merge to single interval, duration 5.0 (longest)
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(10.0, result.endInclusive)
    }

    @Test
    fun `should handle zero-duration intervals`() {
        // Given: Intervals with zero duration (point intervals)
        val intervals = listOf(
            5.0..5.0,     // Zero duration
            10.0..15.0,   // Duration 5.0 (longest)
            20.0..20.0    // Zero duration
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return the interval with actual duration
        assertNotNull(result)
        assertEquals(10.0, result.start)
        assertEquals(15.0, result.endInclusive)
    }

    // ========== TYPE SAFETY TESTS ==========

    @Test
    fun `should work correctly with Integer ranges`() {
        // Given: Integer time slots
        val intervals = listOf(
            1..5,         // Duration: 4
            3..8,         // Overlaps, merged duration: 7 (longest)
            15..17        // Duration: 2
        )

        // When
        val result = intMerger.findLongestBusyPeriod(intervals, IntPolicy)

        // Then: Should handle integer arithmetic correctly
        assertNotNull(result)
        assertEquals(1, result.start)
        assertEquals(8, result.endInclusive)
    }

    @Test
    fun `should handle floating point precision correctly`() {
        // Given: Intervals with precise decimal values
        val intervals = listOf(
            0.1..0.3,           // Duration: 0.2
            0.25..0.7,          // Overlaps, merged duration: 0.6 (longest)
            1.1..1.15           // Duration: 0.05
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should handle floating point arithmetic correctly
        assertNotNull(result)
        assertEquals(0.1, result.start, 0.0001)
        assertEquals(0.7, result.endInclusive, 0.0001)
    }

    // ========== ALGORITHM-SPECIFIC TESTS ==========

    @Test
    fun `should handle unsorted input correctly`() {
        // Given: Intervals in random order (tests sorting requirement)
        val intervals = listOf(
            15.0..17.0,   // Should be processed after sorting
            5.0..8.0,     // Should merge with next after sorting
            7.0..12.0,    // Overlaps with previous, merged duration: 7.0 (longest)
            1.0..2.0      // Smallest start time
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should correctly sort and merge
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(12.0, result.endInclusive)
    }

    @Test
    fun `should handle complex overlapping scenario`() {
        // Given: Complex overlapping pattern
        val intervals = listOf(
            1.0..4.0,     // Base interval
            2.0..3.0,     // Completely contained within first
            3.5..6.0,     // Extends the merged interval
            5.0..7.0,     // Further extends: final merge [1.0, 7.0], duration 6.0
            10.0..15.0    // Separate interval, duration 5.0
        )

        // When
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should correctly merge all overlapping intervals
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(7.0, result.endInclusive)
    }

    @Test
    fun `should handle large number of intervals efficiently`() {
        // Given: Large dataset to test O(n log n) performance characteristics
        val intervals = (1..1000).map { i ->
            val start = i.toDouble()
            val end = start + 1.5
            start..end
        }.toList()

        // When: Should complete in reasonable time due to O(n log n) complexity
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: All intervals should merge into one continuous period
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(1001.5, result.endInclusive)
    }
}
