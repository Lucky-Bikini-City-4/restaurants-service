package com.dayaeyak.restaurants.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getBucket()) // 업로드할 s3 버킷 이름 설정
                .key(key) //s3 내 파일 경로/이름 지정
                .contentType(file.getContentType()) //파일 타입 설정
                .build();
        s3Client.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return "https://" + awsProperties.getBucket() + ".s3." + awsProperties.getRegion() + ".amazonaws.com/" + key;
    }                                     // file.getInputStream(): MultipartFile을 바이트 스트림으로 변환
            // MultipartFile의 스트림과 크기를 RequestBody로 감싸서 S3에 업로드 실행
}
