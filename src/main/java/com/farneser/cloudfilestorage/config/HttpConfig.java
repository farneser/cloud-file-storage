package com.farneser.cloudfilestorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class HttpConfig {
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        var filter = new CharacterEncodingFilter();

        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        return filter;
    }
}
