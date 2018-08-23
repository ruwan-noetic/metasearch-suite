package com.noetic.m2s.config.local;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.noetic.m2s.config.AbstractCommonConfig;

/**
 * Created by hurman on 29/06/2017.
 */
@PropertySource("classpath:local.application.properties")
@ConfigurationProperties(ignoreUnknownFields = true, prefix = "local")
@Component(value = "LOCAL")
public class CommonConfig extends AbstractCommonConfig {

    String apiPassword;

    public String getApiPassword() {
        return this.apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

}
