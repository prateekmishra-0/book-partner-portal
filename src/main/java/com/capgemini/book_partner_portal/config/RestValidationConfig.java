package com.capgemini.book_partner_portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class RestValidationConfig implements RepositoryRestConfigurer {

    // 1. Create the Spring version of the ValidatorFactory you used in your tests
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    // 2. Hook it directly into Spring Data REST's lifecycle events
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", validator()); // Run on POST
        validatingListener.addValidator("beforeSave", validator());   // Run on PUT/PATCH
    }
}