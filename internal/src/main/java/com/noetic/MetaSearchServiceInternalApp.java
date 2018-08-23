package com.noetic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by hurman on 29/06/2017.
 */
@SpringBootApplication
@EnableScheduling
public class MetaSearchServiceInternalApp {

    public static void main(String[] args) {
        SpringApplication.run(MetaSearchServiceInternalApp.class, args);
    }
}
