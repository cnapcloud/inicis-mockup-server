package com.example.inicis.mock.service;

import com.example.inicis.mock.dto.AuthResponse;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

/**
 * Mock 인증 데이터 생성 서비스
 */
@Service
public class MockAuthService {
    
    private static final Random random = new Random();
    
    /**
     * 테스트용 사용자 데이터 생성
     */
    public AuthResponse generateSuccessResponse(String name, String birthDate, 
                                                String phoneNo, String moid, String state) {
        // 이름과 생년월일로 CI 생성 (실제와 유사하게)
        String ci = generateCI(name, birthDate);
        
        return AuthResponse.builder()
                .resultCode("0000")
                .resultMsg("인증 성공")
                .CI(ci)
                .name(name)
                .birthDate(birthDate)
                .gender(random.nextBoolean() ? "1" : "2") // 랜덤 성별
                .mobileCo(getRandomMobileCo())
                .mobileNo(phoneNo != null ? phoneNo : generateRandomPhoneNo())
                .moid(moid)
                .state(state)
                .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public AuthResponse generateFailureResponse(String moid, String state, String reason) {
        return AuthResponse.builder()
                .resultCode("9999")
                .resultMsg(reason != null ? reason : "사용자 인증 취소")
                .moid(moid)
                .state(state)
                .build();
    }
    
    /**
     * CI 생성 (해시 기반)
     */
    private String generateCI(String name, String birthDate) {
        try {
            String input = name + birthDate + System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return Base64.getEncoder().encodeToString((name + birthDate).getBytes());
        }
    }
    
    /**
     * 랜덤 통신사 선택
     */
    private String getRandomMobileCo() {
        String[] carriers = {"SKT", "KT", "LGU"};
        return carriers[random.nextInt(carriers.length)];
    }
    
    /**
     * 랜덤 휴대폰 번호 생성
     */
    private String generateRandomPhoneNo() {
        int middle = 1000 + random.nextInt(9000);
        int last = 1000 + random.nextInt(9000);
        return "010" + middle + last;
    }
    
    /**
     * 미리 정의된 테스트 사용자 목록
     */
    public static class TestUsers {
        public static final String[][] USERS = {
            {"홍길동", "19900101", "01012345678"},
            {"김철수", "19850515", "01087654321"},
            {"이영희", "19950303", "01055556666"},
            {"박민수", "19881212", "01099998888"},
            {"정수진", "19920707", "01011112222"}
        };
        
        public static String[] getRandomUser() {
            return USERS[new Random().nextInt(USERS.length)];
        }
    }
}
