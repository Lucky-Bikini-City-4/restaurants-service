package com.dayaeyak.restaurants.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Getter
@Setter
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;  // 버킷값 관리
}
