package exception;

import com.openclassrooms.tourguide.constant.ConstantException;

public class TourGuideServiceException extends Exception {
    public TourGuideServiceException(String message, Throwable cause) {
        super(message, cause);
    }


    public static class TrackUserLocationException extends TourGuideServiceException {
        public TrackUserLocationException(Exception e) {
            super(ConstantException.TRACK_USER_LOCATION_EXCEPTION,e);
        }
    }
}
