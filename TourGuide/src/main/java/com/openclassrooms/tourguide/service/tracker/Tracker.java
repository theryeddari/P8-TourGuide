package com.openclassrooms.tourguide.service.tracker;

import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.exception.TourGuideServiceException;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The {@code Tracker} class is responsible for regularly tracking users.
 * It runs in a separate thread and uses an execution service to manage tracking tasks.
 * <p>
 * The class is designed to regularly poll users using a defined polling interval
 * and track their location using the {@link TourGuideService}.
 * </p>
 */
@Service
public class Tracker extends Thread {

    private static final Logger logger = LogManager.getLogger(Tracker.class);
    private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TourGuideService tourGuideService;
    private boolean stop = false;

    /**
     * Constructor to initialize the tracking service.
     *
     * @param tourGuideService The service responsible for tracking users.
     */
    public Tracker(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
        executorService.submit(this);
    }

    /**
     * Stops tracking by interrupting the tracking thread and shutting down the execution service.
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
        logger.info("Tracker has been requested to stop.");
    }

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                logger.debug("Tracker stopping due to interruption or stop flag.");
                break;
            }

            List<User> users = tourGuideService.getAllUsers();
            logger.debug("Begin Tracker. Tracking {} users.", users.size());
            stopWatch.start();
            users.forEach(user -> {
                try {
                    tourGuideService.trackUserLocation(user);
                } catch (TourGuideServiceException.TrackUserLocationException e) {
                    logger.error("Error tracking user location for user: {}", user, e);
                    throw new RuntimeException(e);
                }
            });
            stopWatch.stop();
            logger.debug("Tracker Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
            stopWatch.reset();
            try {
                logger.debug("Tracker sleeping for {} seconds.", TRACKING_POLLING_INTERVAL);
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                logger.debug("Tracker interrupted during sleep.", e);
                break;
            }
        }
        logger.info("Tracker has stopped.");
    }
}
