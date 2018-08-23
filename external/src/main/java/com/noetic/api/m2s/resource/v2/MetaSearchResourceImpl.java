package com.noetic.api.m2s.resource.v2;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.api.m2s.config.DeployProfileConfig;
import com.noetic.api.m2s.config.DeployProfileConfigFactory;
import com.noetic.api.m2s.util.MetaSearchUtil;
import com.noetic.common.v1.dto.MessageDTO;
import com.noetic.common.v1.dto.ResponseDTO;
import com.noetic.common.v1.enums.ResultStatus;
import com.noetic.dto.AccountDTO;
import com.noetic.dto.GenericListDTO;
import com.noetic.dto.RunTimeConfigDTO;
import com.noetic.dto.SystemDTO;

/**
 * Created by Ruwan Chathuranga on 23/05/2018.
 */
@RestController(value = "M2SResourceImplV2")
public class MetaSearchResourceImpl implements MetaSearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaSearchResourceImpl.class);
    private HttpClient httpClient = HttpClientBuilder.create().build();
    private ObjectMapper objMapper = new ObjectMapper();
    private ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);

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
    
    
    //==============================================================================================

/*    CRUD OPERATIONS*/
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> addAccount(@RequestBody AccountDTO accountDto,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isEmptyParameter(accountDto.getName(), "name", response)) {
                return response;
            }

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/addAccount");

            HttpPost httpRequest = new HttpPost(stringRequest.toString());
            LOGGER.info("RequestURL to m2s add account : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(accountDto);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, accountDto.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> editAccount(@RequestBody AccountDTO accountDto, HttpServletResponse httpServletResponse) {
        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isEmptyParameter(accountDto.getId(), "loggedInAccountId", response)
                    || isEmptyParameter(accountDto.getName(), "name", response)) {
                return response;
            }

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/editAccount");

            HttpPut httpRequest = new HttpPut(stringRequest.toString());
            LOGGER.info("RequestURL to m2s add account : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(accountDto);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, accountDto.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> deleteAccount(@RequestParam(required = true) String accountId,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/deleteAccount/?accountId=" + accountId);
            HttpDelete httpRequest = new HttpDelete(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findAccount(@RequestParam(required = true) String accountId,
            HttpServletResponse httpServletResponse) {
        
        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findAccount/?accountId=" + accountId);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findAllAccounts(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse) {
        
        ResponseDTO<GenericListDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findAllAccounts/?page="+page +"&size="+size);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    
    //==============================================================================================
    
    //SYSTEM
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> addSystem(@RequestBody SystemDTO systemDto,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<SystemDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isEmptyParameter(systemDto.getName(), "name", response)) {
                return response;
            }

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/addSystem");

            HttpPost httpRequest = new HttpPost(stringRequest.toString());
            LOGGER.info("RequestURL to M2S add system : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(systemDto);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, systemDto.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> editSystem(@RequestBody SystemDTO systemDto, HttpServletResponse httpServletResponse) {
        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isEmptyParameter(systemDto.getId(), "loggedInAccountId", response)
                    || isEmptyParameter(systemDto.getName(), "name", response)) {
                return response;
            }

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/editSystem");

            HttpPut httpRequest = new HttpPut(stringRequest.toString());
            LOGGER.info("RequestURL to M2S edit system : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(systemDto);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, systemDto.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> deleteSystem(@RequestParam(required = true) String systemId,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/deleteSystem/?systemId=" + systemId);
            HttpDelete httpRequest = new HttpDelete(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findSystem(@RequestParam(required = true) String systemId,
            HttpServletResponse httpServletResponse) {
        
        ResponseDTO<AccountDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findSystem/?systemId=" + systemId);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findAllSystems(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse) {
        
        ResponseDTO<GenericListDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findAllSystems/?page="+page +"&size="+size);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    //RunTime Configs
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> addRunConf(@RequestBody RunTimeConfigDTO runTimeConfigDTO,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<RunTimeConfigDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isEmptyParameter(runTimeConfigDTO.getConfigKey(), "name", response)) {
                return response;
            }

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/addRunConf");

            HttpPost httpRequest = new HttpPost(stringRequest.toString());
            LOGGER.info("RequestURL to M2S add system : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(runTimeConfigDTO);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, runTimeConfigDTO.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> editRunConf(@RequestBody RunTimeConfigDTO runTimeConfigDTO,
            HttpServletResponse httpServletResponse) {
        ResponseDTO<RunTimeConfigDTO> response = new ResponseDTO<>();
        LOGGER.info("{}", "--<< Inside M2S API controller >>--");
        response.setResultStatus(ResultStatus.AWATING);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {

            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();

            baseURL = deployProfileConfig.getInternalBaseUrl();

            StringBuilder stringRequest = new StringBuilder(baseURL + "/internal/m2s/editRunConf");

            HttpPut httpRequest = new HttpPut(stringRequest.toString());
            LOGGER.info("RequestURL to M2S edit system : {}", stringRequest);

            setupRequestHeader(httpRequest);

            updateAuthorization(null, deployProfileConfig, httpRequest);

            String jsonString = mapper.writeValueAsString(runTimeConfigDTO);

            httpRequest.setEntity(new StringEntity(jsonString, StandardCharsets.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpRequest);

            jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());

            LOGGER.info("HttpJSONResponse for fetching records : {}", jsonString);

            response = mapper.readValue(jsonString, ResponseDTO.class);

        } catch (Exception e) {
            updateResponse(response, runTimeConfigDTO.toString(), e);

            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(response.getMessage().getMessage()));
        } finally {
            httpServletResponse.setStatus(response.getHttpStatus().value());
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> deleteRunConf(@RequestParam(required = true) String runConfId,
            HttpServletResponse httpServletResponse) {

        ResponseDTO<RunTimeConfigDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/deleteRunConf/?runConfId=" + runConfId);
            HttpDelete httpRequest = new HttpDelete(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findRunConf(@RequestParam(required = true) String runConfId,
            HttpServletResponse httpServletResponse) {
        
        ResponseDTO<RunTimeConfigDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findRunConf/?runConfId=" + runConfId);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("unchecked")
    @Override
    public ResponseDTO<?> findAllRunConfs(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size, HttpServletResponse httpServletResponse) {
        
        ResponseDTO<GenericListDTO> response = new ResponseDTO<>();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
            baseURL = deployProfileConfig.getInternalBaseUrl();
            StringBuilder stringRequest = new StringBuilder(
                    baseURL + "/internal/m2s/findAllRunConfs/?page="+page +"&size="+size);
            HttpGet httpRequest = new HttpGet(stringRequest.toString());
            LOGGER.info("HTTP Request : {}", stringRequest);
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Connection", "keep-alive");
            updateAuthorization(null, deployProfileConfig, httpRequest);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            String jsonString = MetaSearchUtil.convertStreamToString(httpResponse.getEntity().getContent());
            LOGGER.debug("Response JSON: {}", jsonString);
            response = objMapper.readValue(jsonString, ResponseDTO.class);
        } catch (Exception e) {
            response.setResultStatus(ResultStatus.FAILED);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Error  ,  {}", e);
            response.setMessage(new MessageDTO(e.getMessage()));
            response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                    : HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage(new MessageDTO(e.getMessage()));
        }
        return response;
    }
    
    //==============================================================================================
    
    @SuppressWarnings("rawtypes")
    public boolean isEmptyParameter(String paramVal, String paramName, ResponseDTO response) {

        if ((paramVal != null) && (!paramVal.isEmpty()) && !"string".equals(paramVal)) {
            return false;
        }
        response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                : HttpStatus.BAD_REQUEST.toString());
        response.setMessage(new MessageDTO("Valid value is required for request parameter: "+paramName));

        response.setHttpStatus(HttpStatus.BAD_REQUEST);

        return true;
    }
    
    
    @SuppressWarnings("rawtypes")
    public boolean isEmptyParameter(int paramVal, String paramName, ResponseDTO response) {

        if (paramVal != 0) {
            return false;
        }
        response.setHttpCode(response.getHttpStatus() != null ? response.getHttpStatus().toString()
                : HttpStatus.BAD_REQUEST.toString());
        response.setMessage(new MessageDTO("Valid value is required for request parameter: "+paramName));

        response.setHttpStatus(HttpStatus.BAD_REQUEST);

        return true;
    }
    
    private void setupRequestHeader(HttpPost httpRequest) {
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setHeader("Connection", "keep-alive");
    }
    
    private void setupRequestHeader(HttpPut httpRequest) {
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setHeader("Connection", "keep-alive");
    }
    
    /**
     * 
     * @param response
     * @param message
     * @param e
     */
    private void updateResponse(ResponseDTO<?> response, String message, Exception e) {
        response.setResultStatus(ResultStatus.FAILED);
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        LOGGER.error("Error  {} ,  {}", message, e);
        response.setMessage(new MessageDTO(e.getMessage()));
    }


 

}
