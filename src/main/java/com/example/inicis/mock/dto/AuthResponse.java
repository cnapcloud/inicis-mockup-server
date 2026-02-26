package com.example.inicis.mock.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 이니시스 인증 응답 DTO
 */
@Data
@Builder
public class AuthResponse {
    
    /**
     * 결과 코드
     * 0000: 성공
     * 9999: 실패/취소
     */
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    private String resultMsg;
    
    /**
     * CI (Connecting Information) - 사용자 고유 식별값
     */
    private String CI;
    
    /**
     * 이름
     */
    private String name;
    
    /**
     * 생년월일 (YYYYMMDD)
     */
    private String birthDate;
    
    /**
     * 성별 (1: 남자, 2: 여자)
     */
    private String gender;
    
    /**
     * 통신사 코드
     * SKT, KT, LGU 등
     */
    private String mobileCo;
    
    /**
     * 휴대폰 번호
     */
    private String mobileNo;
    
    /**
     * 거래 고유번호 (요청시 전달된 값 그대로 반환)
     */
    private String moid;
    
    /**
     * State 파라미터 (요청시 전달된 값 그대로 반환)
     */
    private String state;
}
