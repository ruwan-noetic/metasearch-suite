package com.noetic.api.m2s.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.noetic.api.m2s.config.prelive.CommonConfig;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@Profile("PRELIVE")
@Configuration
@EnableConfigurationProperties({CommonConfig.class})
@ComponentScan("com.noetic.api.m2s")
@Component(value = "PreliveDeployConfig")
public class PreliveDeployConfig extends AbstractDeployConfig {

    @Autowired(required = true)
    @Qualifier(value = "PRELIVE")
    CommonConfig commonConfig;

    public PreliveDeployConfig() {
        super(DeployProfile.PRELIVE);
    }

    public PreliveDeployConfig(DeployProfile deployProfile) {
        super(deployProfile);
    }

    @Override
    public String getInternalApiUsername() {
        return commonConfig.getInternalApiUsername();
    }

    @Override
    public String getInternalApiPassword() {
        return commonConfig.getInternalApiPassword();
    }

    @Override
    public String getInternalBaseUrl() {
        return commonConfig.getInternalBaseUrl();
    }

    @Override
    public String getExternalApiUsername() {
        return commonConfig.getExternalApiUsername();
    }

    @Override
    public String getExternalApiPassword() {
        return commonConfig.getExternalApiPassword();
    }

}
