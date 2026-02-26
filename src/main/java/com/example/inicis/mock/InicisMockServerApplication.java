package com.example.inicis.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * KG 이니시스 간편인증 Mock Server
 * 테스트용 서버로 실제 이니시스 인증 플로우를 시뮬레이션합니다.
 */
@SpringBootApplication
public class InicisMockServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InicisMockServerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
