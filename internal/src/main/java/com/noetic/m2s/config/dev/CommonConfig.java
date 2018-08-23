package com.noetic.m2s.config.dev;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.noetic.m2s.config.AbstractCommonConfig;

/**
 * Created by hurman on 29/06/2017.
 */
@PropertySource("classpath:dev.application.properties")
@ConfigurationProperties(ignoreUnknownFields = true, prefix = "dev")
@Component(value = "DEV")
public class CommonConfig extends AbstractCommonConfig {

    String apiPassword;

    public String getApiPassword() {
        return this.apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

}
