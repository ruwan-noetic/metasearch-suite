package com.noetic.m2s.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noetic.m2s.domain.external.repository.AvailabilityFeedRepository;

/**
 * Created by hurman on 13/07/2017.
 */
@Service
public class MetaSearchServiceImpl implements MetaSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaSearchServiceImpl.class);

    @Autowired
    AvailabilityFeedRepository availabilityFeedRepository;

    @Autowired
    ImperialService imperialService;







}
