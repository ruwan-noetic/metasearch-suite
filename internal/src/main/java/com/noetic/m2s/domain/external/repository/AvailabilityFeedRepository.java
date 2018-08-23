package com.noetic.m2s.domain.external.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.noetic.m2s.domain.external.AvailabilityFeed;

import java.util.List;

/**
 * Created by hurman on 28/09/2017.
 */
@Repository
public interface AvailabilityFeedRepository extends JpaRepository<AvailabilityFeed, String> {

    public final String FIND_TOTAL_COUNT =
            "SELECT COUNT(af.urn) FROM AvailabilityFeed af " +
                    "WHERE af.updated > :updatedDate ORDER BY af.updated";

    @Query(FIND_TOTAL_COUNT)
    public Long findTotalCount(@Param("updatedDate") Long updatedDate);

    //==========

    public final String RETRIEVE_AVAILABILITY_URNS =
            "SELECT af FROM AvailabilityFeed af " +
                    "WHERE af.updated >= :updatedDate ORDER BY af.updated ";

    @Query(RETRIEVE_AVAILABILITY_URNS)
    public List<AvailabilityFeed> retrieveAvailabilityData(@Param("updatedDate") Long updatedDate);

    //==========

    public final String GET_SPECIFIC_AVAILABILITY =
            "SELECT distinct af FROM AvailabilityFeed af " +
                    "LEFT JOIN af.availabilityRoomFeed arf " +
                    "WHERE af.checkIn = :checkInDate " +
                    "AND af.completed = 1 " +
                    "AND arf.hotelCode = :hotel " +
                    "AND arf.stayCount = :nights " +
                    "AND arf.totalRate > 0 ";

    @Query(GET_SPECIFIC_AVAILABILITY)
    public List<AvailabilityFeed> retrieveAvailabilityDataSpecific(@Param("checkInDate") Long checkInDate,
                                                                   @Param("hotel") String hotel,
                                                                   @Param("nights") Integer nights);

}
