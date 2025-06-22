# Solution Approach: Union-Find (Disjoint Set) for Interval Merging

## Problem Overview

**Objective**: Find the longest continuous "busy" time period from a collection of potentially overlapping meeting intervals.

**Input**: List of time intervals `[[start, end], ...]` representing scheduled meetings
**Output**: Single interval `[start, end]` representing the longest continuous busy period, or `null` if no valid intervals exist

**Example**: 
- Input: `[[9, 10], [9.5, 11], [13, 14], [15, 17]]`
- Output: `[9, 11]` (duration: 2 hours)

## Algorithm Approach: Union-Find (Disjoint Set)

### High-Level Strategy

The union-find approach transforms this problem from sequential interval merging into a graph connectivity problem:

1. **Initialization**: Treat each interval as a node in a graph, initially in its own connected component
2. **Union Phase**: For every pair of overlapping intervals, union them into the same connected component
3. **Component Analysis**: Calculate the time span (min start, max end) for each connected component
4. **Selection**: Return the component with the maximum duration

### Why This Approach?

- **Alternative Perspective**: Views the problem as finding connected components in an "overlap graph"
- **Educational Value**: Demonstrates union-find applications beyond traditional graph problems
- **Conceptual Clarity**: Directly models the relationship between overlapping intervals
- **Extensibility**: Easy to extend for queries like "how many separate busy periods?" or "find all busy periods"
- **No Sorting Required**: Avoids the O(n log n) sorting step, though trades it for O(n²) overlap detection

### Union-Find Fundamentals for C# Developers

Union-Find (also called Disjoint Set Union) is similar to managing groups with a `Dictionary<T, HashSet<T>>` in C#, but more efficient for dynamic connectivity queries:

```csharp
// C# approach for grouping
var groups = new Dictionary<int, HashSet<Interval>>();

// Union-Find approach
var parent = new int[intervals.Count];  // Each element points to its parent
```

**Core Operations**:
- **Find(x)**: Determine which component/group element x belongs to
- **Union(x, y)**: Merge the components containing x and y

## Detailed Algorithm Design

### Step 1: Input Validation and Preprocessing

```kotlin
// Filter out invalid intervals where start >= end
val validIntervals = intervals.filter { it.start < it.endInclusive }

if (validIntervals.isEmpty()) return null
```

**Rationale**: Same as sorting approach - invalid intervals should be excluded rather than causing errors.

### Step 2: Initialize Union-Find Structure

```kotlin
val n = validIntervals.size
val parent = IntArray(n) { it }  // Each interval is initially its own parent
val componentStart = Array(n) { validIntervals[it].start }  // Track min start per component
val componentEnd = Array(n) { validIntervals[it].endInclusive }  // Track max end per component
```

**Data Structures**:
- **parent[]**: Parent pointer for each interval (index-based)
- **componentStart[]**: Minimum start time for each component root
- **componentEnd[]**: Maximum end time for each component root

### Step 3: Find Operation (Basic Path Compression)

```kotlin
fun find(x: Int): Int {
    val originalNode = x
    var current = x
    while (parent[current] != current) {
        current = parent[current]
    }
    // Basic path compression: set parent of original node to root
    parent[originalNode] = current
    return current
}
```

**Educational Note**: We implement basic path compression by setting the parent of the queried node directly to the root after finding it.
This simple optimization makes subsequent finds for the same node faster, while avoiding the complexity of full path compression that would update all intermediate nodes.
This approach demonstrates the core concept of path compression while keeping the implementation straightforward and focused on fundamental union-find principles.

### Step 4: Union Operation (Random Choice)

```kotlin
fun union(x: Int, y: Int) {
    val rootX = find(x)
    val rootY = find(y)
    
    if (rootX == rootY) return  // Already in same component
    
    // Random choice for union (no union by rank optimization)
    val newRoot = rootX  // Could also choose rootY randomly
    val oldRoot = rootY
    
    parent[oldRoot] = newRoot
    
    // Update component bounds
    componentStart[newRoot] = minOf(componentStart[newRoot], componentStart[oldRoot])
    componentEnd[newRoot] = maxOf(componentEnd[newRoot], componentEnd[oldRoot])
}
```

**Key Points**:
- **Random Union**: We arbitrarily choose rootX as the new root (could randomize for educational exploration)
- **Bound Tracking**: Update the time span of the merged component
- **Simplicity**: No rank-based optimization to keep focus on core algorithm

### Step 5: Overlap Detection and Union Phase

```kotlin
// Check all pairs of intervals for overlap
for (i in 0 until n) {
    for (j in i + 1 until n) {
        if (intervalsOverlap(validIntervals[i], validIntervals[j])) {
            union(i, j)
        }
    }
}

fun intervalsOverlap(a: ClosedRange<T>, b: ClosedRange<T>): Boolean {
    // Two intervals overlap if: max(start1, start2) <= min(end1, end2)
    return maxOf(a.start, b.start) <= minOf(a.endInclusive, b.endInclusive)
}
```

**Overlap Logic**: Two intervals `[a,b]` and `[c,d]` overlap if `max(a,c) <= min(b,d)`

**Examples**:
- `[1,5]` and `[3,7]`: `max(1,3)=3 <= min(5,7)=5` ✓ (overlapping)
- `[1,3]` and `[3,5]`: `max(1,3)=3 <= min(3,5)=3` ✓ (adjacent/touching)
- `[1,2]` and `[4,5]`: `max(1,4)=4 <= min(2,5)=2` ✗ (gap between them)

### Step 6: Find Longest Component

```kotlin
// Find all unique component roots and their durations
val componentDurations = mutableListOf<Pair<ClosedRange<T>, TDuration>>()

for (i in 0 until n) {
    if (parent[i] == i) {  // This is a root
        val componentRange = componentStart[i]..componentEnd[i]
        val duration = policy.subtract(componentEnd[i], componentStart[i])
        componentDurations.add(Pair(componentRange, duration))
    }
}

// Return the component with maximum duration
return componentDurations.maxWithOrNull { a, b -> 
    policy.compare(a.second, b.second) 
}?.first
```

**Component Identification**: Only intervals where `parent[i] == i` are component roots.

## Implementation Strategy

### Core Data Structures

1. **Input**: `List<ClosedRange<T>>` - Generic intervals supporting any comparable type
2. **Union-Find Arrays**:
    - `IntArray` for parent pointers
    - `Array<T>` for component start/end bounds
3. **Working Set**: `MutableList<Pair<ClosedRange<T>, TDuration>>` - Component durations
4. **Output**: `ClosedRange<T>?` - Single longest interval or null

### Error Handling Strategy

| Scenario | Handling | Rationale |
|----------|----------|-----------|
| Empty input | Return `null` | No intervals means no busy periods |
| All invalid intervals | Return `null` | No valid data to process |
| Single valid interval | Return that interval | Trivial case - no unions needed |
| Zero-duration intervals | Include in processing | Valid time points |

### Type Safety Considerations

The generic `<T : Comparable<T>>` constraint ensures:
- Works with `Double`, `Int`, `LocalDateTime`, etc.
- Type-safe arithmetic operations via policy pattern
- Consistent comparison semantics

## C# to Kotlin Transition Notes

### Key Differences for C# Developers

| Concept | C# Equivalent | Kotlin Implementation |
|---------|---------------|----------------------|
| **Union-Find Structure** | `Dictionary<T, T>` for parent mapping | `IntArray` with index-based parents |
| **Component Tracking** | `Dictionary<T, (min, max)>` | Separate arrays for bounds |
| **Overlap Detection** | LINQ `Any()` with predicate | Nested loops with custom function |
| **Generic Constraints** | `where T : IComparable<T>` | `<T : Comparable<T>>` |
| **Null Safety** | Nullable reference types | Built-in null safety |

### Familiar Patterns

```csharp
// C# grouping approach (less efficient)
var groups = intervals
    .Where(IsValid)
    .GroupBy(i => FindOverlapGroup(i))
    .Select(g => new { 
        Start = g.Min(i => i.Start), 
        End = g.Max(i => i.End) 
    })
    .MaxBy(g => g.End - g.Start);
```

```kotlin
// Kotlin union-find approach
val validIntervals = intervals.filter { it.start < it.endInclusive }
// ... union-find operations ...
val longestComponent = componentDurations.maxByOrNull { it.second }
```

## Performance Analysis

### Time Complexity: O(n² × α(n))

- **Input Validation**: O(n) - Single pass through input
- **Initialization**: O(n) - Initialize union-find arrays
- **Overlap Detection**: O(n²) - Check all pairs of intervals
- **Union Operations**: O(α(n)) per union, up to O(n) unions
- **Component Analysis**: O(n) - Find all roots and calculate durations

**Overall**: O(n²) due to pairwise overlap detection (α(n) is effectively constant)

### Space Complexity: O(n)

- **Union-Find Arrays**: O(n) - Parent array and component bounds
- **Component Results**: O(k) where k ≤ n - Number of components
- **Auxiliary Space**: O(1) - Constant additional variables

**Overall**: O(n) auxiliary space

### Comparison with Sorting Approach

| Aspect | Sorting Approach | Union-Find Approach |
|--------|------------------|---------------------|
| **Time Complexity** | O(n log n) | O(n²) |
| **Space Complexity** | O(n) | O(n) |
| **Best Case** | O(n log n) | O(n²) |
| **Worst Case** | O(n log n) | O(n²) |
| **Conceptual Model** | Sequential merging | Graph connectivity |
| **Extensibility** | Limited | High (component queries) |

### When to Use Union-Find Approach

**Advantages**:
- **Educational Value**: Demonstrates alternative algorithmic thinking
- **Component Queries**: Easy to extend for multiple component analysis
- **No Sorting**: Avoids comparison-based sorting limitations
- **Natural Grouping**: Directly models interval relationships

**Disadvantages**:
- **Higher Time Complexity**: O(n²) vs O(n log n) for large datasets
- **Implementation Complexity**: More complex than straightforward sorting
- **Memory Overhead**: Additional arrays for component tracking

**Recommendation**: Use for educational purposes, small datasets (n < 1000), or when component analysis is needed.

## Walkthrough with Problem Statement Example

### Input Analysis

```
Input: [[9, 10], [9.5, 11], [13, 14], [15, 17]]
Indices: [0, 1, 2, 3]
```

### Step-by-Step Execution

**Step 1: Validation**
```
All intervals valid: [9..10, 9.5..11, 13..14, 15..17]
```

**Step 2: Initialize Union-Find**
```
parent = [0, 1, 2, 3]  // Each interval is its own parent
componentStart = [9.0, 9.5, 13.0, 15.0]
componentEnd = [10.0, 11.0, 14.0, 17.0]
```

**Step 3: Overlap Detection and Union Operations**

*Check (0,1): [9,10] vs [9.5,11]*
```
max(9, 9.5) = 9.5 <= min(10, 11) = 10 ✓ → Union(0,1)
After union(0,1):
  parent = [0, 0, 2, 3]  // 1 now points to 0
  componentStart[0] = min(9.0, 9.5) = 9.0
  componentEnd[0] = max(10.0, 11.0) = 11.0
```

*Check (0,2): [9,10] vs [13,14]*
```
max(9, 13) = 13 <= min(10, 14) = 10 ✗ → No union
```

*Check (0,3): [9,10] vs [15,17]*
```
max(9, 15) = 15 <= min(10, 17) = 10 ✗ → No union
```

*Check (1,2): [9.5,11] vs [13,14]*
```
max(9.5, 13) = 13 <= min(11, 14) = 11 ✗ → No union
```

*Check (1,3): [9.5,11] vs [15,17]*
```
max(9.5, 15) = 15 <= min(11, 17) = 11 ✗ → No union
```

*Check (2,3): [13,14] vs [15,17]*
```
max(13, 15) = 15 <= min(14, 17) = 14 ✗ → No union
```

**Step 4: Final Union-Find State**
```
parent = [0, 0, 2, 3]
componentStart = [9.0, 9.5, 13.0, 15.0]  // Only [0] and [2], [3] are used
componentEnd = [11.0, 11.0, 14.0, 17.0]
```

**Step 5: Component Analysis**
```
Roots (where parent[i] == i): 0, 2, 3
Component 0: [9.0, 11.0] → duration = 2.0
Component 2: [13.0, 14.0] → duration = 1.0  
Component 3: [15.0, 17.0] → duration = 2.0
```

**Step 6: Find Longest**
```
Max duration = 2.0 (tie between components 0 and 3)
Return first occurrence: [9.0, 11.0]
```

### Union-Find Tree Visualization

```
Initial State:    After Union(0,1):
0   1   2   3     0       2   3
                  |
                  1

Final Components:
Component 0: intervals [0,1] → merged range [9,11]
Component 2: interval [2] → range [13,14]  
Component 3: interval [3] → range [15,17]
```

### Edge Case Examples

**All Overlapping Intervals**:
```
Input: [[1, 5], [3, 7], [6, 10]]
Union operations: (0,1), (1,2) → All in component 0
Result: [1, 10] (single component)
```

**No Overlapping Intervals**:
```
Input: [[1, 2], [5, 6], [10, 11]]
No union operations → 3 separate components
Result: Any interval (all have duration 1)
```

**Adjacent Intervals**:
```
Input: [[1, 3], [3, 5]]
Union(0,1) because max(1,3)=3 <= min(3,5)=3
Result: [1, 5] (merged component)
```

## Implementation Checklist

### Core Algorithm

- [ ] Input validation (filter invalid intervals)
- [ ] Initialize union-find structure (parent array, component bounds)
- [ ] Implement find operation (with basic path compression)
- [ ] Implement union operation (random choice, update bounds)
- [ ] Pairwise overlap detection (O(n²) comparisons)
- [ ] Component analysis (find roots, calculate durations)
- [ ] Select longest component
- [ ] Handle null return for empty/invalid input

### Union-Find Operations

- [ ] Correct parent array initialization
- [ ] Proper find traversal (follow parent pointers)
- [ ] Union with bound updates (min start, max end)
- [ ] Component root identification
- [ ] Avoid infinite loops in find operation

### Edge Cases

- [ ] Empty input list
- [ ] Single interval
- [ ] All invalid intervals
- [ ] Zero-duration intervals
- [ ] Identical intervals
- [ ] Adjacent intervals (touching boundaries)
- [ ] All intervals in one component
- [ ] No overlapping intervals

### Type Safety

- [ ] Generic `<T : Comparable<T>>` constraint
- [ ] Proper arithmetic operations via policy
- [ ] Null safety for return type
- [ ] Index bounds checking

### Performance

- [ ] O(n²) time complexity for overlap detection
- [ ] O(n) space complexity for union-find structure
- [ ] Efficient component analysis

## Testing Strategy

The comprehensive test suite should verify:

1. **Basic Functionality**
    - Problem statement example with expected result
    - Verify union-find correctly groups overlapping intervals

2. **Union-Find Specific Behavior**
    - Component formation with multiple overlapping groups
    - Correct parent pointer management
    - Proper bound tracking during unions

3. **Overlap Detection**
    - Adjacent intervals (touching boundaries)
    - Completely overlapping intervals
    - Partially overlapping intervals
    - Non-overlapping intervals

4. **Edge Cases**
    - Empty lists and invalid intervals
    - Single interval handling
    - All intervals in one component
    - No overlapping intervals

5. **Type Safety**
    - Different comparable types (Double, Int)
    - Policy-based duration calculation

6. **Algorithm Comparison**
    - Same results as sorting approach
    - Performance characteristics with different dataset sizes

7. **Component Analysis**
    - Correct identification of component roots
    - Accurate duration calculations
    - Proper handling of ties (first occurrence)

## Educational Insights

### Union-Find Applications Beyond Graphs

This problem demonstrates how union-find can solve problems that don't initially appear to be graph-related:

- **Interval Merging**: Treating overlaps as edges in a graph
- **Connected Components**: Each component represents a continuous time period
- **Dynamic Connectivity**: Efficiently track which intervals belong together

### Alternative Problem Modeling

**Sequential Thinking** (Sorting Approach):
"Process intervals in time order and merge adjacent ones"

**Connectivity Thinking** (Union-Find Approach):  
"Find groups of intervals that are connected through overlaps"

Both approaches solve the same problem but represent different algorithmic paradigms.

### When Union-Find Shines

While less efficient for this specific problem, union-find becomes valuable when:
- **Multiple Queries**: "How many separate busy periods exist?"
- **Dynamic Updates**: "Add/remove intervals and maintain components"
- **Component Analysis**: "List all intervals in the longest busy period"

This approach provides a foundation for more complex interval relationship queries and demonstrates the power of viewing problems through different algorithmic lenses.

The union-find solution offers valuable educational insights into alternative problem-solving approaches, even when it's not the most efficient solution for the specific use case.
