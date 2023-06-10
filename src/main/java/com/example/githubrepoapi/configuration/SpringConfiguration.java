package com.example.githubrepoapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

@Configuration
public class SpringConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}