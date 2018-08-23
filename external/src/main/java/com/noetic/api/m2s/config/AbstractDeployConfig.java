package com.noetic.api.m2s.config;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
public abstract class AbstractDeployConfig implements DeployProfileConfig {

    protected DeployProfile deployProfile;

    public AbstractDeployConfig(DeployProfile deployProfile) {
        this.deployProfile = deployProfile;
    }

    public DeployProfile getDeployProfile() {
        return deployProfile;
    }

    public void setDeployProfile(DeployProfile deployProfile){
        this.deployProfile = deployProfile;
    }

    abstract public String getInternalApiUsername();

    abstract public  String getInternalApiPassword();

    abstract public  String getInternalBaseUrl();

    abstract public  String getExternalApiUsername();

    abstract public  String getExternalApiPassword();

}
