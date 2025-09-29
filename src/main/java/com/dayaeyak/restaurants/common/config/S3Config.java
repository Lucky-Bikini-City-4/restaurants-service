package com.dayaeyak.restaurants.common.config;

import com.dayaeyak.restaurants.s3.AwsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(AwsProperties awsProperties) {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider( // s3 접속에 필요한 인증 정보 설정
                        StaticCredentialsProvider.create(     // 인증 정보를 고정적으로 제공하는 역할
                                AwsBasicCredentials.create(   // aws 기본 인증 객체 생성
                                        awsProperties.getAccessKey(),
                                        awsProperties.getSecretKey()
                                )
                        )
                )
                .build();
    }

}
