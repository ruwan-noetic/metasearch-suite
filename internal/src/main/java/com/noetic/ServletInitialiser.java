package com.noetic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.noetic.m2s.config.DeployProfile;
import com.noetic.m2s.util.MetaSerachUtil;

/**
 *
 * Created by hurman on 29/06/2017.
 *
 * Application initialise, where Deployed environment get passed in to as runtime parameter
 */
public class ServletInitialiser extends SpringBootServletInitializer {

    private static Logger log = LoggerFactory.getLogger(ServletInitialiser.class);

    @Value("${runtime.parameter.production}")
    private String productionParameter = "/etc/m2sAPI/production";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        String deployProfileValue = System.getProperty("deployProfile");

        if (deployProfileValue != null) {
            log.info("Initialising application deployProfile {}", deployProfileValue);
        } else {
            boolean isProduction = false;
            if (productionParameter != null) {
                log.info("Runtime parameter setting {}", productionParameter);
                isProduction = MetaSerachUtil.isFileExist(productionParameter);
            }

            if (isProduction) {
                deployProfileValue = DeployProfile.PRODUCTION.name();
            } else {
                deployProfileValue = DeployProfile.LOCAL.name();
            }
            log.warn("Initialising application runtime parameter for Internal  {}",
                    deployProfileValue);
        }

        return application.sources(MetaSearchServiceInternalApp.class).profiles(deployProfileValue);
    }

}
