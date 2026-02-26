package com.example.inicis.mock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock Server 설정
 */
@Data
@Component
@ConfigurationProperties(prefix = "inicis.mock")
public class MockProperties {
    
    /**
     * 유효한 MID 목록
     */
    private List<String> validMids;
    
    /**
     * 자동 성공 모드
     * true: 항상 성공
     * false: 사용자가 UI에서 성공/실패 선택
     */
    private boolean autoSuccess = false;
    
    /**
     * 인증 지연 시간 (밀리초)
     */
    private long authDelayMs = 2000;
}
