package com.noetic.m2s.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hurman on 29/06/2017.
 */
@RestController
public class IndexResource {

    private static Logger log = LoggerFactory.getLogger(IndexResource.class);

    @RequestMapping("/gha/info")
    public String hello() {
        log.info("Welcome to Google Hotels service-internal");
        return " Welcome to Google Hotels service-internal";
    }

}
