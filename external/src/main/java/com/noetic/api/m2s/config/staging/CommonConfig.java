package com.noetic.api.m2s.config.staging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.noetic.api.m2s.config.AbstractCommonConfig;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@PropertySource("classpath:staging.application.properties")
@ConfigurationProperties(ignoreUnknownFields = true, prefix = "staging")
@Component(value = "STAGING")
public class CommonConfig extends AbstractCommonConfig {

    String internalApiPassword;
    String externalApiPassword;

    @Override
    public String getInternalApiPassword() {
        return this.internalApiPassword;
    }

    @Override
    public void setInternalApiPassword(String internalApiPassword) {
        this.internalApiPassword = internalApiPassword;
    }

    @Override
    public String getExternalApiPassword() {
        return this.externalApiPassword;
    }

    @Override
    public void setExternalApiPassword(String externalApiPassword) {
        this.externalApiPassword = externalApiPassword;
    }
}
