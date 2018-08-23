package com.noetic.m2s.domain.internal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.noetic.m2s.domain.internal.Account;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, String>{

}
