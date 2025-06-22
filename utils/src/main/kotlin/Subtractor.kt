/**
 * An interface for calculating the duration between two time instants.
 *
 * This interface abstracts the operation of finding the time difference between two points
 * in time, allowing algorithms to work with various time representations.
 *
 * @param TInstant The type representing a point in time (e.g., Instant, LocalDateTime)
 * @param TDuration The type representing the duration between two time points (e.g., Duration)
 */
interface Subtractor<TInstant, TDuration> {
    /**
     * Calculates the duration between two time instants.
     *
     * @param first The later time instant
     * @param second The earlier time instant to be subtracted from the first
     * @return The duration between the two instants
     */
    fun subtract(first: TInstant, second: TInstant): TDuration
}
