package com.obs.friendmgmt;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static springfox.documentation.service.ApiInfo.DEFAULT_CONTACT;

@Profile(value = {"default", "swagger"})
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfo("Friend Management API", "REST endpoint for client to invoke.",
                        "1.0", "urn:tos",
                        DEFAULT_CONTACT, "", "", Collections.EMPTY_LIST))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build().directModelSubstitute(LocalDateTime.class, String.class)
                .genericModelSubstitutes(Optional.class);
    }
}
