package com.noetic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

import com.noetic.api.m2s.config.DeployProfile;
import com.noetic.api.m2s.util.MetaSearchUtil;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@Component
public class ServletInitialiser extends SpringBootServletInitializer {

    @Value("${runtime.parameter.production}")
    private String productionParameter = "/etc/m2sAPI/production";

    private static Logger log = LoggerFactory.getLogger(ServletInitialiser.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        String deployProfileValue = System.getProperty("deployProfile");

        if (deployProfileValue != null) {
            log.info("Initialising application deployProfile {}", deployProfileValue);
        } else {
            boolean isProduction = false;
            if (productionParameter != null) {
                log.info("Runtime parameter setting {}", productionParameter);
                isProduction = MetaSearchUtil.isFileExist(productionParameter);
            }

            if (isProduction) {
                deployProfileValue = DeployProfile.PRODUCTION.name();
            } else {
                deployProfileValue = DeployProfile.LOCAL.name();
            }
            log.warn("Initialising application runtime parameter for External {}",
                    deployProfileValue);
        }

        return application.sources(MetasearchServiceExternalApp.class).profiles(deployProfileValue);
    }

}
