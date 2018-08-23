package com.noetic.m2s.domain.external.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.noetic.m2s.domain.external.PromotionDetail;

import java.util.List;

/**
 * Created by hurman on 26/10/2017.
 */
@Repository
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, String> {

    public final String FIND_ALL_COUNT =
            "SELECT COUNT(pd.urn) FROM PromotionDetail pd ";

    @Query(FIND_ALL_COUNT)
    public Long findAllCount();

    public final String FIND_ALL = "From PromotionDetail ";

    @Query(FIND_ALL)
    public List<PromotionDetail> findAll();

}
