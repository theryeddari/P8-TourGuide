package com.openclassrooms.tourguide.config;

import com.openclassrooms.tourguide.service.RewardsService;
import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/**
 * Configuration class for the Tour Guide application.
 * <p>
 * This class is responsible for defining and configuring the beans required
 * by the Tour Guide application. It provides the necessary beans for GPS
 * functionality, reward services, and reward central service.
 * </p>
 */
@Configuration
public class TourGuideModule {

    /**
     * Provides a bean for the GPS utility.
     * <p>
     * This bean allows the application to use GPS functionalities provided
     * by the {@link GpsUtil} class.
     * </p>
     *
     * @return A new instance of {@link GpsUtil}.
     */
    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

    /**
     * Provides a bean for the rewards service.
     * <p>
     * This bean provides the service to handle rewards-related operations.
     * It depends on the {@link GpsUtil} and {@link RewardCentral} beans.
     * </p>
     *
     * @return A new instance of {@link RewardsService}.
     */
    @Bean
    public RewardsService getRewardsService() {
        return new RewardsService(getGpsUtil(), getRewardCentral());
    }

    /**
     * Provides a bean for the reward central service.
     * <p>
     * This bean is used to handle reward-related operations through the
     * {@link RewardCentral} class.
     * </p>
     *
     * @return A new instance of {@link RewardCentral}.
     */
    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

}