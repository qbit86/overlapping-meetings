# Overlapping Meetings

## Problem Statement

Given a list of time intervals representing scheduled meetings (which may overlap), find the longest continuous "busy" time period where at least one meeting is happening.

**Example:**
- Input: `[[9, 10], [9.5, 11], [13, 14], [15, 17]]`
- Output: `[9, 11]` (duration: 2 hours)

## Implementation

This project implements two algorithmic approaches to solve the overlapping meetings problem:

### 1. Sorting Approach (`SortingIntervalMerger`)

- **Algorithm:** Sort intervals by start time, merge overlapping/adjacent intervals, find longest merged interval
- **Time Complexity:** O(n log n)
- **Space Complexity:** O(n)

### 2. Union-Find Approach (`UnionFindIntervalMerger`)

- **Algorithm:** Model overlapping intervals as connected components in a graph, find largest component
- **Time Complexity:** O(n² × α(n)) where α is the inverse Ackermann function
- **Space Complexity:** O(n)

Both implementations support generic time types (Double, Int, Instant) through a policy pattern for type-safe arithmetic operations.

## Build and Run

This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app` and `utils` subprojects.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).
