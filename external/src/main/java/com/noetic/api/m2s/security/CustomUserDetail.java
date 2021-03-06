package com.noetic.api.m2s.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.noetic.api.m2s.config.DeployProfileConfig;

import java.util.Collection;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
public class CustomUserDetail implements UserDetails{

    String password;
    String userName;

    public CustomUserDetail(DeployProfileConfig deployProfileConfig){
        this.password = new BCryptPasswordEncoder().encode(deployProfileConfig.getExternalApiPassword());
        this.userName = deployProfileConfig.getExternalApiUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
