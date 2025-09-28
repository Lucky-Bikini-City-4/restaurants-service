package com.dayaeyak.restaurants.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());

        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://localhost:6379")   // 호스트와 포트 바로 지정
                .setConnectionPoolSize(64)              // 최대 연결 수
                .setConnectionMinimumIdleSize(24)       // 최소 유휴 연결
                .setTimeout(3000)                       // 연결 타임아웃(ms), TTL 설정
                .setRetryAttempts(3)                     // 재시도 횟수
                .setRetryInterval(1500);                 // 재시도 간격(ms)

        log.info("Redisson client initialized: localhost:6379");
        return Redisson.create(config);
    }

}