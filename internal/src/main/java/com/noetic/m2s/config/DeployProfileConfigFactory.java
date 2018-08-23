package com.noetic.m2s.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.noetic.m2s.util.MetaSerachUtil;

/**
 * Runtime determination of the config for the deployment based on system property.
 * <p>
 * E.g. supply JVM arg -DdeployProfile=DEV to run with DEV settings.
 */

@Component
public class DeployProfileConfigFactory {

    private static Logger log = LoggerFactory.getLogger(DeployProfileConfigFactory.class);

    @Value("${runtime.parameter.production}")
    private static String productionParameter = "/etc/m2sAPI/production";

    public static DeployProfileConfig forEnvironment() {

        String deployProfileValue = System.getProperty("deployProfile");

        if (deployProfileValue != null) {
            log.info("Attempting to run deployProfile on " + deployProfileValue);
        } else {

            boolean isProduction = false;
            if (productionParameter != null) {
                log.debug("Runtime parameter setting {}", productionParameter);
                isProduction = MetaSerachUtil.isFileExist(productionParameter);
            }

            if (isProduction) {
                deployProfileValue = DeployProfile.PRODUCTION.name();
            } else {
                deployProfileValue = DeployProfile.LOCAL.name();
            }
            log.debug("Initialising application running on internal {}", deployProfileValue);
        }

        DeployProfile deployProfile = DeployProfile.getDeployProfile(deployProfileValue);

        DeployProfileConfig deployProfileConfig = null;

        switch (deployProfile) {

            case LOCAL:
                log.debug("Matched to LocalDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean("LocalDeployConfig", LocalDeployConfig.class);
                break;
            case DEV:
                log.debug("Matched to DevDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean("DevDeployConfig", DevDeployConfig.class);
                break;
            case STAGING:
                log.debug("Matched to StagingDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean(StagingDeployConfig.class);
                break;
            case PRELIVE:
                log.debug("Matched to PreliveDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean(PreliveDeployConfig.class);
                break;
            case PRODUCTION:
                log.debug("Matched to ProductionDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean(ProductionDeployConfig.class);
                break;
            default:
                log.warn("Runtime deployProfile not set defaulting to LocalDeployConfig");
                deployProfileConfig = SpringApplicationContext.getBean("LocalDeployConfig", LocalDeployConfig.class);
                break;
        }

        deployProfileConfig.setDeployProfile(deployProfile);

        return deployProfileConfig;
    }

}
