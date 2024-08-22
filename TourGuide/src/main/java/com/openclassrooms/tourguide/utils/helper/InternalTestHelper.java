package com.openclassrooms.tourguide.utils.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@code InternalTestHelper} class provides utility methods for internal tests
 * by managing a simulated number of users. The static methods allow reading and modifying
 * this value for testing purposes.
 */
public class InternalTestHelper {

    private static final Logger logger = LogManager.getLogger(InternalTestHelper.class);

    /**
     * Default value for the number of users in testing.
     */
    private static int internalUserNumber = 100;

    /**
     * Gets the number of simulated users.
     *
     * @return The number of simulated users.
     */
    public static int getInternalUserNumber() {
        logger.debug("Getting internal user number: {}", internalUserNumber);
        return internalUserNumber;
    }

    /**
     * Sets the number of simulated users.
     *
     * @param internalUserNumber The new number of simulated users.
     */
    public static void setInternalUserNumber(int internalUserNumber) {
        logger.info("Setting internal user number to {}", internalUserNumber);
        InternalTestHelper.internalUserNumber = internalUserNumber;
        logger.debug("Updated internal user number to: {}", internalUserNumber);
    }
}
