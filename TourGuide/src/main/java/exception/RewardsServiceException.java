package exception;

import com.openclassrooms.tourguide.constant.ConstantException;

public class RewardsServiceException extends Exception {
    public RewardsServiceException(String message, Throwable cause) {
        super(message, cause);
    }


    public static class CalulateRewardsException extends RewardsServiceException {
        public CalulateRewardsException(Exception e) {
            super(ConstantException.CALCULATE_REWARDS_EXCEPTION,e);
        }
    }
}
