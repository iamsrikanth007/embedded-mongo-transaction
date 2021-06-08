package com.shareknowledge.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MongoValidationConfig {

    @Bean
    @Primary
    public ValidatingMongoEventListener validatingMongoEventListener(LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }

    @Bean
    @Primary
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
