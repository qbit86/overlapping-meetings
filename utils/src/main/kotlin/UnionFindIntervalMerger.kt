/**
 * Implementation of IntervalMerger using union-find (disjoint set) approach.
 *
 * Algorithm:
 * 1. Create union-find structure for intervals
 * 2. Union overlapping intervals into connected components
 * 3. Find the component with the largest span
 *
 * Time complexity: O(n² * α(n)) where α is the inverse Ackermann function
 * Space complexity: O(n) for union-find structure
 *
 * This approach demonstrates how union-find can solve interval problems by treating
 * overlapping intervals as connected components in a graph.
 */
class UnionFindIntervalMerger<TInstant : Comparable<TInstant>, TDuration> : IntervalMerger<TInstant, TDuration> {

    override fun <TPolicy> findLongestBusyPeriod(
        intervals: List<ClosedRange<TInstant>>, policy: TPolicy
    ): ClosedRange<TInstant>? where TPolicy : Comparator<TDuration>, TPolicy : Subtractor<TInstant, TDuration> {

        // Step 1: Input validation - filter out invalid intervals where start >= end
        // Same approach as SortingIntervalMerger for consistency
        val validIntervals = intervals.filter { it.start < it.endInclusive }

        // Return null if no valid intervals exist (Kotlin's null safety approach)
        if (validIntervals.isEmpty()) return null

        // Step 2: Initialize union-find structure
        val n = validIntervals.size
        val parent = IntArray(n) { it }  // Each interval is initially its own parent
        val componentStart = MutableList(n) { validIntervals[it].start }  // Track min start per component
        val componentEnd = MutableList(n) { validIntervals[it].endInclusive }  // Track max end per component

        // Step 3: Overlap detection and union phase
        // Check all pairs of intervals for overlap (O(n²) operation)
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (intervalsOverlap(validIntervals[i], validIntervals[j])) {
                    union(parent, componentStart, componentEnd, i, j)
                }
            }
        }

        // Step 4: Component analysis - find all unique component roots and their durations
        val componentDurations = mutableListOf<Pair<ClosedRange<TInstant>, TDuration>>()

        for (i in 0 until n) {
            if (parent[i] == i) {  // This is a component root
                val componentRange = componentStart[i]..componentEnd[i]
                val duration = policy.subtract(componentEnd[i], componentStart[i])
                componentDurations.add(Pair(componentRange, duration))
            }
        }

        // Step 5: Find the component with maximum duration
        return componentDurations.maxWithOrNull { a, b ->
            policy.compare(a.second, b.second)
        }?.first
    }

    /**
     * Find operation with basic path compression.
     *
     * Educational Note: We implement basic path compression by setting the parent
     * of the queried node directly to the root after finding it. This simple
     * optimization makes subsequent finds for the same node faster, while avoiding
     * the complexity of full path compression.
     */
    private fun find(parent: IntArray, x: Int): Int {
        val originalNode = x
        var current = x

        // Follow parent pointers until we reach the root (where parent[i] == i)
        while (parent[current] != current) {
            current = parent[current]
        }

        // Basic path compression: set parent of original node to root
        // This optimizes future find operations for this node
        parent[originalNode] = current
        return current
    }

    /**
     * Union operation that merges two components and updates their bounds.
     *
     * Educational Note: We use a simple union strategy (no union by rank) to keep
     * the focus on core union-find concepts. In production code, union by rank
     * would provide better performance guarantees.
     */
    private fun union(
        parent: IntArray, componentStart: MutableList<TInstant>, componentEnd: MutableList<TInstant>, x: Int, y: Int
    ) {
        val rootX = find(parent, x)
        val rootY = find(parent, y)

        // If already in the same component, no union needed
        if (rootX == rootY) return

        // Simple union: choose rootX as the new root (could randomize for educational exploration)
        val newRoot = rootX
        val oldRoot = rootY

        // Update parent pointer
        parent[oldRoot] = newRoot

        // Update component bounds to encompass both original components
        // This is key to tracking the time span of merged components
        componentStart[newRoot] = minOf(componentStart[newRoot], componentStart[oldRoot])
        componentEnd[newRoot] = maxOf(componentEnd[newRoot], componentEnd[oldRoot])
    }

    /**
     * Determines if two intervals overlap or are adjacent (touching boundaries).
     *
     * Two intervals [a,b] and [c,d] overlap if: max(a,c) <= min(b,d)
     *
     * Examples:
     * - [1,5] and [3,7]: max(1,3)=3 <= min(5,7)=5 ✓ (overlapping)
     * - [1,3] and [3,5]: max(1,3)=3 <= min(3,5)=3 ✓ (adjacent/touching)
     * - [1,2] and [4,5]: max(1,4)=4 <= min(2,5)=2 ✗ (gap between them)
     *
     * Educational Note: This is the same logic used in computational geometry
     * and is equivalent to checking !(a.end < b.start || b.end < a.start) but
     * more intuitive to understand.
     */
    private fun intervalsOverlap(a: ClosedRange<TInstant>, b: ClosedRange<TInstant>): Boolean {
        return maxOf(a.start, b.start) <= minOf(a.endInclusive, b.endInclusive)
    }
}
