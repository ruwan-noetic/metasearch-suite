package com.noetic.m2s.transformer;


import java.util.Comparator;

import com.noetic.m2s.domain.external.AvailabilityRoomFeed;

/**
 * Created by hurman on 18/12/2017.
 */
public class CustomComparator implements Comparator<AvailabilityRoomFeed> {
    @Override
    public int compare(AvailabilityRoomFeed o1, AvailabilityRoomFeed o2) {
        return o1.getTotalRate().compareTo(o2.getTotalRate());
    }
}
