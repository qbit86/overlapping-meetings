import kotlin.test.*

/**
 * Comprehensive test suite for UnionFindIntervalMerger<T>.
 *
 * Tests cover:
 * - Basic functionality with overlapping and non-overlapping intervals
 * - Edge cases (empty lists, invalid intervals, single intervals)
 * - Type safety with different Comparable types (Double, Int)
 * - Union-Find specific behavior (component formation, union operations, overlap detection)
 * - Algorithm comparison with SortingIntervalMerger to ensure consistent results
 * - Performance characteristics verification (O(n²) time complexity)
 *
 * Educational Focus:
 * This test suite demonstrates how union-find can solve interval merging problems
 * by treating overlapping intervals as connected components in a graph, providing
 * an alternative perspective to the sequential sorting approach.
 */
class UnionFindIntervalMergerTest {

    private val doubleMerger = UnionFindIntervalMerger<Double, Double>()
    private val intMerger = UnionFindIntervalMerger<Int, Int>()

    // For algorithm comparison tests
    private val sortingDoubleMerger = SortingIntervalMerger<Double, Double>()
    private val sortingIntMerger = SortingIntervalMerger<Int, Int>()

    // ========== BASIC FUNCTIONALITY TESTS ==========
    // These tests verify the same core functionality as SortingIntervalMerger

    @Test
    fun `should find longest busy period from problem statement example`() {
        // Given: Example from problem statement
        // Union-Find perspective: Intervals [0,1] will form one component, [2,3] separate components
        val intervals = listOf(
            9.0..10.0,    // Component with next interval, merged duration: 2.0
            9.5..11.0,    // Overlaps with first → union(0,1)
            13.0..14.0,   // Separate component, duration: 1.0
            15.0..17.0    // Separate component, duration: 2.0
        )

        // When: Union-find should create 3 components: {0,1}, {2}, {3}
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {0,1} has span [9.0, 11.0] with duration 2.0 (tied with {3})
        // Should return first occurrence: [9.0, 11.0]
        assertNotNull(result)
        assertEquals(9.0, result.start)
        assertEquals(11.0, result.endInclusive)
    }

    @Test
    fun `should find longest period when multiple overlapping groups exist`() {
        // Given: Two distinct groups of overlapping intervals
        // Union-Find perspective: Should form 2 components through union operations
        val intervals = listOf(
            1.0..3.0,     // Component 1: union(0,1) → span [1.0, 4.0], duration 3.0
            2.0..4.0,     // Overlaps with first
            10.0..15.0,   // Component 2: union(2,3,4) → span [10.0, 18.0], duration 8.0
            12.0..18.0,   // Overlaps with previous
            14.0..16.0    // Overlaps with both previous → all in same component
        )

        // When: Union-find should create 2 components: {0,1} and {2,3,4}
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {2,3,4} has the longest duration (8.0)
        assertNotNull(result)
        assertEquals(10.0, result.start)
        assertEquals(18.0, result.endInclusive)
    }

    @Test
    fun `should handle non-overlapping intervals correctly`() {
        // Given: Separate intervals (no union operations will occur)
        // Union-Find perspective: Each interval remains its own component
        val intervals = listOf(
            1.0..2.0,     // Component 0: duration 1.0
            5.0..9.0,     // Component 1: duration 4.0 (longest)
            12.0..13.0    // Component 2: duration 1.0
        )

        // When: No overlaps detected → 3 separate components
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component 1 has the longest duration
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(9.0, result.endInclusive)
    }

    @Test
    fun `should handle single interval`() {
        // Given: Only one interval (trivial union-find case)
        val intervals = listOf(5.0..10.0)

        // When: Single component with no union operations needed
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return the single interval
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(10.0, result.endInclusive)
    }

    @Test
    fun `should handle adjacent intervals that touch but don't overlap`() {
        // Given: Adjacent intervals (boundary case for overlap detection)
        // Union-Find perspective: max(1,3)=3 <= min(3,5)=3 → overlapping!
        val intervals = listOf(
            1.0..3.0,     // Adjacent to next → should union
            3.0..5.0,     // Touching boundary → union(0,1)
            10.0..12.0    // Separate component
        )

        // When: Adjacent intervals should be detected as overlapping
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Components {0,1} merged to [1.0, 5.0], duration 4.0 (longest)
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(5.0, result.endInclusive)
    }

    // ========== EDGE CASES & ERROR HANDLING ==========

    @Test
    fun `should return null for empty interval list`() {
        // Given: Empty list (no components to analyze)
        val intervals = emptyList<ClosedRange<Double>>()

        // When: No intervals to process
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Should return null
        assertNull(result)
    }

    @Test
    fun `should filter out invalid intervals where start is greater than end`() {
        // Given: Mix of valid and invalid intervals
        // Union-Find perspective: Only valid intervals participate in union operations
        val intervals = listOf(
            1.0..5.0,     // Valid: Component 0, duration 4.0
            10.0..8.0,    // Invalid: start > end, filtered out
            15.0..20.0,   // Valid: Component 1, duration 5.0 (longest)
            25.0..20.0    // Invalid: start > end, filtered out
        )

        // When: Invalid intervals filtered before union-find processing
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Only valid intervals considered, longest is [15.0, 20.0]
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

        // When: All intervals filtered out before union-find processing
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: No valid intervals → null result
        assertNull(result)
    }

    @Test
    fun `should handle identical intervals`() {
        // Given: Multiple identical intervals
        // Union-Find perspective: All identical intervals will union into one component
        val intervals = listOf(
            5.0..10.0,    // All these will union together
            5.0..10.0,    // union(0,1), union(0,2) → all in component 0
            5.0..10.0,    // Component span remains [5.0, 10.0]
            15.0..18.0    // Separate component, duration 3.0
        )

        // When: Identical intervals detected as overlapping
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {0,1,2} has duration 5.0 (longest)
        assertNotNull(result)
        assertEquals(5.0, result.start)
        assertEquals(10.0, result.endInclusive)
    }

    @Test
    fun `should handle zero-duration intervals`() {
        // Given: Point intervals (zero duration)
        val intervals = listOf(
            5.0..5.0,     // Zero duration point
            10.0..15.0,   // Duration 5.0 (longest)
            20.0..20.0    // Zero duration point
        )

        // When: Point intervals are valid and processed
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Non-zero duration interval should be longest
        assertNotNull(result)
        assertEquals(10.0, result.start)
        assertEquals(15.0, result.endInclusive)
    }

    // ========== TYPE SAFETY TESTS ==========

    @Test
    fun `should work correctly with Integer ranges`() {
        // Given: Integer intervals to test generic type handling
        val intervals = listOf(
            1..5,         // Component with next, merged duration: 7
            3..8,         // Overlaps → union(0,1)
            15..17        // Separate component, duration: 2
        )

        // When: Union-find with integer arithmetic
        val result = intMerger.findLongestBusyPeriod(intervals, IntPolicy)

        // Then: Component {0,1} has span [1, 8], duration 7 (longest)
        assertNotNull(result)
        assertEquals(1, result.start)
        assertEquals(8, result.endInclusive)
    }

    @Test
    fun `should handle floating point precision correctly`() {
        // Given: Precise decimal intervals
        val intervals = listOf(
            0.1..0.3,           // Component with next, merged duration: 0.6
            0.25..0.7,          // Overlaps → union(0,1)
            1.1..1.15           // Separate component, duration: 0.05
        )

        // When: Union-find with floating point arithmetic
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {0,1} has the longest duration
        assertNotNull(result)
        assertEquals(0.1, result.start, 0.0001)
        assertEquals(0.7, result.endInclusive, 0.0001)
    }

    // ========== UNION-FIND SPECIFIC TESTS ==========
    // These tests verify union-find algorithm characteristics

    @Test
    fun `should handle complex component formation through multiple unions`() {
        // Given: Complex overlapping pattern that tests union operations
        // This tests the union-find's ability to merge components transitively
        val intervals = listOf(
            1.0..4.0,     // Base interval
            2.0..3.0,     // Completely contained → union(0,1)
            3.5..6.0,     // Overlaps with merged component → union(component, 2)
            5.0..7.0,     // Overlaps with extended component → union(component, 3)
            10.0..15.0    // Separate component
        )

        // When: Multiple union operations should merge intervals 0,1,2,3 into one component
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {0,1,2,3} spans [1.0, 7.0], duration 6.0 (longest)
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(7.0, result.endInclusive)
    }

    @Test
    fun `should correctly detect overlaps in all pairwise combinations`() {
        // Given: Intervals where overlap detection order matters
        // Tests the O(n²) pairwise overlap detection
        val intervals = listOf(
            10.0..15.0,   // Will be checked against all others
            5.0..12.0,    // Overlaps with first → union(0,1)
            14.0..20.0,   // Overlaps with merged component → union(component, 2)
            1.0..6.0      // Overlaps with component through interval 1 → union(component, 3)
        )

        // When: All pairs checked: (0,1), (0,2), (0,3), (1,2), (1,3), (2,3)
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: All intervals should be in one component spanning [1.0, 20.0]
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(20.0, result.endInclusive)
    }

    @Test
    fun `should handle large number of overlapping intervals efficiently`() {
        // Given: Many overlapping intervals to test union-find performance
        // This tests the O(n²) time complexity with union-find operations
        val intervals = (1..100).map { i ->
            val start = i.toDouble()
            val end = start + 50.0  // Large overlap to ensure all intervals connect
            start..end
        }.toList()

        // When: O(n²) overlap detection with O(α(n)) union operations
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: All intervals should merge into one large component
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(150.0, result.endInclusive)  // Last interval: 100..150
    }

    // ========== ALGORITHM COMPARISON TESTS ==========
    // These tests verify that union-find produces identical results to sorting approach

    @Test
    fun `should produce same results as sorting approach for problem statement example`() {
        // Given: Problem statement example
        val intervals = listOf(
            9.0..10.0,
            9.5..11.0,
            13.0..14.0,
            15.0..17.0
        )

        // When: Both algorithms process the same input
        val unionFindResult = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
        val sortingResult = sortingDoubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Results should be identical
        assertEquals(sortingResult, unionFindResult)
    }

    @Test
    fun `should produce same results as sorting approach for complex overlapping case`() {
        // Given: Complex overlapping scenario
        val intervals = listOf(
            1.0..4.0,
            2.0..3.0,
            3.5..6.0,
            5.0..7.0,
            10.0..15.0,
            12.0..18.0,
            20.0..22.0
        )

        // When: Both algorithms process the same input
        val unionFindResult = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
        val sortingResult = sortingDoubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Results should be identical
        assertEquals(sortingResult, unionFindResult)
    }

    @Test
    fun `should produce same results as sorting approach for non-overlapping intervals`() {
        // Given: Non-overlapping intervals
        val intervals = listOf(
            1.0..2.0,
            5.0..9.0,
            12.0..13.0,
            20.0..25.0
        )

        // When: Both algorithms process the same input
        val unionFindResult = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
        val sortingResult = sortingDoubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Results should be identical
        assertEquals(sortingResult?.start, unionFindResult?.start)
        assertEquals(sortingResult?.endInclusive, unionFindResult?.endInclusive)
    }

    @Test
    fun `should produce same results as sorting approach for edge cases`() {
        // Given: Various edge cases
        val testCases = listOf(
            emptyList<ClosedRange<Double>>(),
            listOf(5.0..10.0),
            listOf(1.0..1.0, 2.0..2.0),
            listOf(10.0..5.0, 20.0..15.0),  // Invalid intervals
            listOf(1.0..3.0, 3.0..5.0, 5.0..7.0)  // Adjacent intervals
        )

        // When & Then: All test cases should produce identical results
        testCases.forEach { intervals ->
            val unionFindResult = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
            val sortingResult = sortingDoubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
            assertEquals(sortingResult?.start, unionFindResult?.start, "Failed for intervals: $intervals")
            assertEquals(sortingResult?.endInclusive, unionFindResult?.endInclusive, "Failed for intervals: $intervals")
        }
    }

    @Test
    fun `should produce same results as sorting approach with integer types`() {
        // Given: Integer intervals
        val intervals = listOf(
            1..5,
            3..8,
            10..12,
            15..20
        )

        // When: Both algorithms process integer intervals
        val unionFindResult = intMerger.findLongestBusyPeriod(intervals, IntPolicy)
        val sortingResult = sortingIntMerger.findLongestBusyPeriod(intervals, IntPolicy)

        // Then: Results should be identical
        assertEquals(sortingResult, unionFindResult)
    }

    // ========== PERFORMANCE CHARACTERISTIC TESTS ==========

    @Test
    fun `should handle moderate dataset size efficiently`() {
        // Given: Moderate size dataset to verify O(n²) performance is acceptable
        val intervals = (1..500).map { i ->
            val start = i.toDouble()
            val end = start + 2.0
            start..end
        }.toList()

        // When: Process with union-find (O(n²) time complexity)
        val startTime = System.currentTimeMillis()
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)
        val endTime = System.currentTimeMillis()

        // Then: Should complete in reasonable time and produce correct result
        assertNotNull(result)
        assertTrue(endTime - startTime < 5000, "Should complete within 5 seconds")
        assertEquals(1.0, result.start)
        assertEquals(502.0, result.endInclusive)
    }

    // ========== EDUCATIONAL DEMONSTRATION TESTS ==========

    @Test
    fun `should demonstrate union-find component formation step by step`() {
        // Given: Simple case that clearly shows union-find behavior
        // This test serves as educational documentation
        val intervals = listOf(
            1.0..3.0,     // Index 0
            2.0..4.0,     // Index 1, overlaps with 0 → union(0,1)
            5.0..7.0,     // Index 2, separate component
            6.0..8.0      // Index 3, overlaps with 2 → union(2,3)
        )

        // When: Union-find processes overlaps
        // Expected union operations: union(0,1), union(2,3)
        // Final components: {0,1} with span [1.0,4.0], {2,3} with span [5.0,8.0]
        val result = doubleMerger.findLongestBusyPeriod(intervals, DoublePolicy)

        // Then: Component {2,3} has duration 3.0, component {0,1} has duration 3.0
        // Tie-breaking should return first component {0,1}
        assertNotNull(result)
        assertEquals(1.0, result.start)
        assertEquals(4.0, result.endInclusive)
    }
}
