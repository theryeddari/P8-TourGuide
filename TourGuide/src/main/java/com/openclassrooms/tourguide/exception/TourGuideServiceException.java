package com.openclassrooms.tourguide.exception;

import com.openclassrooms.tourguide.constant.ConstantException;

/**
 * The {@code TourGuideServiceException} class represents a custom exception used
 * in the Tour Guide service. It extends the base {@code Exception} class to handle
 * exceptions specific to the Tour Guide service.
 */
public class TourGuideServiceException extends Exception {

    /**
     * Constructs a new {@code TourGuideServiceException} with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A {@code null} value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public TourGuideServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The {@code TrackUserLocationException} class is a nested subclass of
     * {@code TourGuideServiceException}. It specifically handles exceptions that
     * occur during the tracking of a user's location within the Tour Guide service.
     */
    public static class TrackUserLocationException extends TourGuideServiceException {

        /**
         * Constructs a new {@code TrackUserLocationException} with the specified exception
         * as the cause.
         *
         * @param e the exception that caused this exception to be thrown.
         */
        public TrackUserLocationException(Exception e) {
            super(ConstantException.TRACK_USER_LOCATION_EXCEPTION, e);
        }
    }
}
