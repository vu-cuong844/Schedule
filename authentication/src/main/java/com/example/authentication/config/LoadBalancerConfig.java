// filepath: d:\Hust\Project II\Schedule\authentication\src\main\java\com\example\authentication\config\LoadBalancerConfig.java
package com.example.authentication.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
@LoadBalancerClient(name = "authentication-service")
public class LoadBalancerConfig {
    @Bean
    @Primary
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}