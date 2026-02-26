package com.example.inicis.mock.dto;

import lombok.Data;

/**
 * 이니시스 인증 요청 DTO
 */
@Data
public class AuthRequest {
    
    /**
     * 상점 ID (필수)
     */
    private String mid;
    
    /**
     * 콜백 URL (필수)
     */
    private String returnUrl;
    
    /**
     * 서비스 코드 (필수)
     * 01: 간편인증
     */
    private String reqSvcCd;
    
    /**
     * 거래 고유번호 (선택)
     */
    private String moid;
    
    /**
     * State 파라미터 (CSRF 방지용, 선택)
     */
    private String state;
    
    /**
     * 휴대폰 번호 (선택, 재인증시)
     */
    private String phoneNo;
    
    /**
     * 생년월일 (선택, 재인증시)
     */
    private String birthDate;
}
