package com.noetic.api.m2s.resource.v1;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.noetic.api.m2s.config.DeployProfileConfig;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@RestController(value = "M2SResourceImplV1")
public class MetaResourceImpl implements MetaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaResourceImpl.class);
    private HttpClient httpClient = HttpClientBuilder.create().build();
    private JacksonXmlModule module = new JacksonXmlModule();
    private XmlMapper mapper = new XmlMapper();

    @Value("${default.base.endpoint}")
    private String baseURL;


   
    /**
     * Update Request Header with Basic authorisation
     *
     * @param deployProfileConfig
     * @param httpRequest
     */
    private void updateAuthorization(String xAuthorisation, DeployProfileConfig deployProfileConfig,
            HttpRequestBase httpRequest) {

        byte[] credentials = Base64.encodeBase64(
                (deployProfileConfig.getInternalApiUsername() + ":" + deployProfileConfig.getInternalApiPassword())
                        .getBytes(StandardCharsets.UTF_8));

        httpRequest.setHeader("Authorization", "Basic " + new String(credentials, StandardCharsets.UTF_8));
        if (xAuthorisation != null) {
            httpRequest.setHeader("X-Authorisation", xAuthorisation);

        }
    }

}
