# Solution Approach: Sorting and Merging Intervals

## Problem Overview

**Objective**: Find the longest continuous "busy" time period from a collection of potentially overlapping meeting intervals.

**Input**: List of time intervals `[[start, end], ...]` representing scheduled meetings
**Output**: Single interval `[start, end]` representing the longest continuous busy period, or `null` if no valid intervals exist

**Example**: 
- Input: `[[9, 10], [9.5, 11], [13, 14], [15, 17]]`
- Output: `[9, 11]` (duration: 2 hours)

## Algorithm Approach: Sort and Merge

### High-Level Strategy

The sorting approach solves this problem in three main phases:

1. **Preprocessing**: Filter out invalid intervals and sort by start time
2. **Merging**: Combine overlapping and adjacent intervals into continuous periods
3. **Selection**: Return the merged interval with the maximum duration

### Why This Approach?

- **Intuitive**: Mirrors how humans would solve this manually
- **Efficient**: O(n log n) time complexity, optimal for comparison-based solutions
- **Robust**: Handles all edge cases naturally through the sorting and merging process
- **Maintainable**: Clear separation of concerns makes debugging and testing straightforward

## Detailed Algorithm Design

### Step 1: Input Validation and Preprocessing

```kotlin
// Filter out invalid intervals where start >= end
val validIntervals = intervals.filter { it.start < it.endInclusive }

if (validIntervals.isEmpty()) return null
```

**Rationale**: Invalid intervals (where start >= end) are meaningless in the context of time periods and should be excluded rather than causing errors.

### Step 2: Sorting by Start Time

```kotlin
// Sort intervals by start time to enable sequential merging
val sortedIntervals = validIntervals.sortedBy { it.start }
```

**Why sort by start time?**
- Enables sequential processing for merging
- Ensures we encounter overlapping intervals in chronological order
- Simplifies the merging logic significantly

### Step 3: Merge Overlapping and Adjacent Intervals

```kotlin
val mergedIntervals = mutableListOf<ClosedRange<T>>()
var currentInterval = sortedIntervals.first()

for (interval in sortedIntervals.drop(1)) {
    if (interval.start <= currentInterval.endInclusive) {
        // Overlapping or adjacent - merge them
        currentInterval = currentInterval.start..maxOf(currentInterval.endInclusive, interval.endInclusive)
    } else {
        // Gap found - save current and start new interval
        mergedIntervals.add(currentInterval)
        currentInterval = interval
    }
}
mergedIntervals.add(currentInterval) // Don't forget the last interval
```

**Key Merging Rules**:
- **Overlapping**: `[1,5]` and `[3,7]` → `[1,7]`
- **Adjacent**: `[1,3]` and `[3,5]` → `[1,5]` (touching intervals are continuous)
- **Contained**: `[1,7]` and `[2,4]` → `[1,7]` (smaller interval absorbed)

### Step 4: Find Longest Merged Interval

```kotlin
return mergedIntervals.maxByOrNull { it.endInclusive - it.start }
```

**Duration Calculation**: For `ClosedRange<T>`, duration is `endInclusive - start`

## Implementation Strategy

### Core Data Structures

1. **Input**: `List<ClosedRange<T>>` - Generic intervals supporting any comparable type
2. **Working Set**: `MutableList<ClosedRange<T>>` - Accumulates merged intervals
3. **Output**: `ClosedRange<T>?` - Single longest interval or null

### Error Handling Strategy

| Scenario | Handling | Rationale |
|----------|----------|-----------|
| Empty input | Return `null` | No intervals means no busy periods |
| All invalid intervals | Return `null` | No valid data to process |
| Single valid interval | Return that interval | Trivial case |
| Zero-duration intervals | Include in processing | Valid time points |

### Type Safety Considerations

The generic `<T : Comparable<T>>` constraint ensures:
- Works with `Double`, `Int`, `LocalDateTime`, etc.
- Type-safe arithmetic operations
- Consistent comparison semantics

## C# to Kotlin Transition Notes

### Key Differences for C# Developers

| Concept | C# Equivalent | Kotlin Implementation |
|---------|---------------|----------------------|
| **Intervals** | Custom `Interval<T>` class | Built-in `ClosedRange<T>` |
| **Nullable Returns** | `Interval<T>?` or `null` | `ClosedRange<T>?` |
| **Generic Constraints** | `where T : IComparable<T>` | `<T : Comparable<T>>` |
| **Collection Operations** | LINQ methods | Extension functions |
| **Null Safety** | Nullable reference types | Built-in null safety |

### Familiar Patterns

```csharp
// C# LINQ approach
var result = intervals
    .Where(i => i.Start <= i.End)           // Filter invalid
    .OrderBy(i => i.Start)                  // Sort by start
    .Aggregate(...)                         // Merge logic
    .MaxBy(i => i.Duration);                // Find longest
```

```kotlin
// Kotlin equivalent
val result = intervals
    .filter { it.start <= it.endInclusive } // Filter invalid
    .sortedBy { it.start }                  // Sort by start
    .fold(...)                              // Merge logic
    .maxByOrNull { it.endInclusive - it.start } // Find longest
```

### Extension Function Opportunity

```kotlin
// Kotlin extension function (similar to C# extension methods)
fun <T : Comparable<T>> ClosedRange<T>.duration(): T = endInclusive - start
```

## Performance Analysis

### Time Complexity: O(n log n)

- **Filtering**: O(n) - Single pass through input
- **Sorting**: O(n log n) - Dominant operation
- **Merging**: O(n) - Single pass through sorted intervals
- **Selection**: O(m) where m ≤ n - Pass through merged intervals

**Overall**: O(n log n) due to sorting requirement

### Space Complexity: O(n)

- **Sorted intervals**: O(n) - Copy of input for sorting
- **Merged intervals**: O(n) worst case - When no intervals overlap
- **Auxiliary space**: O(1) - Constant additional variables

**Overall**: O(n) auxiliary space

### Scalability Considerations

- **Small datasets (n < 100)**: Overhead minimal, very fast
- **Medium datasets (n < 10,000)**: Excellent performance
- **Large datasets (n > 100,000)**: Still efficient, sorting dominates
- **Memory usage**: Linear growth, suitable for most applications

## Walkthrough with Problem Statement Example

### Input Analysis

```
Input: [[9, 10], [9.5, 11], [13, 14], [15, 17]]
```

### Step-by-Step Execution

**Step 1: Validation**
```
All intervals valid: [9..10, 9.5..11, 13..14, 15..17]
```

**Step 2: Sorting by Start Time**
```
Already sorted: [9..10, 9.5..11, 13..14, 15..17]
```

**Step 3: Merging Process**
```
Initialize: currentInterval = 9..10
Process 9.5..11: 9.5 <= 10 → Merge to 9..11
Process 13..14: 13 > 11 → Save 9..11, start new with 13..14
Process 15..17: 15 > 14 → Save 13..14, start new with 15..17
Final merged: [9..11, 13..14, 15..17]
```

**Step 4: Find Longest**
```
Durations: [9..11] = 2, [13..14] = 1, [15..17] = 2
Result: 9..11 (first occurrence of maximum duration)
```

### Edge Case Examples

**Adjacent Intervals**:
```
Input: [[1, 3], [3, 5], [10, 12]]
Merged: [[1, 5], [10, 12]]  // 3 is both end and start
Result: [1, 5] (duration 4 > duration 2)
```

**Completely Overlapping**:
```
Input: [[1, 10], [2, 5], [3, 7]]
Merged: [[1, 10]]  // Smaller intervals absorbed
Result: [1, 10]
```

**No Overlaps**:
```
Input: [[1, 2], [5, 9], [12, 13]]
Merged: [[1, 2], [5, 9], [12, 13]]  // No merging
Result: [5, 9] (duration 4 is longest)
```

## Implementation Checklist

### Core Algorithm

- [ ] Input validation (filter invalid intervals)
- [ ] Sort intervals by start time
- [ ] Merge overlapping/adjacent intervals
- [ ] Find interval with maximum duration
- [ ] Handle null return for empty/invalid input

### Edge Cases

- [ ] Empty input list
- [ ] Single interval
- [ ] All invalid intervals
- [ ] Zero-duration intervals
- [ ] Identical intervals
- [ ] Adjacent intervals (touching boundaries)

### Type Safety

- [ ] Generic `<T : Comparable<T>>` constraint
- [ ] Proper arithmetic operations
- [ ] Null safety for return type

### Performance

- [ ] O(n log n) time complexity
- [ ] O(n) space complexity
- [ ] Efficient for large datasets

## Testing Strategy

The comprehensive test suite should verify:

1. **Basic functionality** with the problem statement example
2. **Multiple overlapping groups** to test longest selection
3. **Non-overlapping intervals** to verify individual interval handling
4. **Edge cases** including empty lists and invalid intervals
5. **Type safety** with different comparable types (Double, Int)
6. **Algorithm-specific behavior** like handling unsorted input
7. **Performance characteristics** with large datasets

This approach provides a robust, efficient, and maintainable solution to the overlapping intervals problem, with clear parallels to familiar C#/.NET patterns while leveraging Kotlin's strengths in null safety and functional programming.
