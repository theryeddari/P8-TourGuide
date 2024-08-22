package exception;

import com.openclassrooms.tourguide.constant.ConstantException;

/**
 * The {@code RewardsServiceException} class represents a custom exception used
 * in the rewards service of the Tour Guide application.
 * It extends the base {@code Exception} class to handle exceptions specific to
 * the rewards service.
 */
public class RewardsServiceException extends Exception {

    /**
     * Constructs a new {@code RewardsServiceException} with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A {@code null} value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public RewardsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The {@code CalulateRewardsException} class is a nested subclass of
     * {@code RewardsServiceException}. It specifically handles exceptions that
     * occur during the calculation of rewards in the rewards service.
     */
    public static class CalulateRewardsException extends RewardsServiceException {

        /**
         * Constructs a new {@code CalulateRewardsException} with the specified exception
         * as the cause.
         *
         * @param e the exception that caused this exception to be thrown.
         */
        public CalulateRewardsException(Exception e) {
            super(ConstantException.CALCULATE_REWARDS_EXCEPTION, e);
        }
    }
}
