package com.dayaeyak.restaurants.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "headerAuditorAware") // ★ 중요
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> headerAuditorAware() {
        return new HeaderAuditorAware();
    }
}