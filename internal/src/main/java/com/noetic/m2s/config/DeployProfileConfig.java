package com.noetic.m2s.config;

/**
 * Created by hurman on 29/06/2017.
 *
 * Possible configuration options for a deployment
 */
public interface DeployProfileConfig {

    DeployProfile getDeployProfile();

    void setDeployProfile(DeployProfile deployProfile);

    String getApiUsername();

    String getApiPassword();

    String getBaseUrl();

    String getIdentityUrl();

    String getEmailBaseUrl();

    String getEmailUsername();

    String getEmailPassword();

    String getEmailLinkBaseUrl();

    String getSchedulerSecond();

    String getSchedulerMinute();

    String getSchedulerHour();

    String getSchedulerDayOfMonth();

    String getSchedulerMonth();

    String getSchedulerDayOfWeek();

    String getSchedulerRunning();

}
