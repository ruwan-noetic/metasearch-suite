package com.noetic.api.m2s.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@RestController
public class IndexResource {

    private static Logger log = LoggerFactory.getLogger(IndexResource.class);

    @RequestMapping("/m2s/info")
    public String hello() {

        log.info("Welcome to MetaSearch Suite API service-external");
        return " Welcome to MetaSearch Suite API service-external";
    }


}
