package com.noetic.api.m2s.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noetic.api.m2s.config.DeployProfileConfig;
import com.noetic.api.m2s.config.DeployProfileConfigFactory;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

        if (deployProfileConfig == null) {
            throw new UsernameNotFoundException("User for deployment environment not found");
        }

        if (!deployProfileConfig.getExternalApiUsername().equals(s)) {
           throw new UsernameNotFoundException("User for deployment environment not found");
        }

        return new CustomUserDetail(deployProfileConfig);
    }
}
