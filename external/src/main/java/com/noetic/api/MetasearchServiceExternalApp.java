package com.noetic.api;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;

import com.noetic.api.m2s.config.SpringApplicationContext;
import com.noetic.api.m2s.resource.ResourceAdvice;
import com.noetic.api.m2s.resource.ResponseFilter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Ruwan Chathuranga on 21/08/2018.
 */
@EnableSwagger2
@SpringBootApplication
@ComponentScan(basePackageClasses = {SpringApplicationContext.class, ResponseFilter.class, ResourceAdvice.class},
        basePackages = {"com.noetic.api.m2s.resource.v1"})

public class MetasearchServiceExternalApp {

    public static void main(String[] args) {
        SpringApplication.run(MetasearchServiceExternalApp.class, args);
    }

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/v.*/m2s.*"))
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Metasearch suite API")
                .description("Metasearch documentation")
                .termsOfServiceUrl("http://www.wearenoetic.com")
                .contact("Noetic Marketing Technologies")
                .version("1.0")
                .build();
    }

}
