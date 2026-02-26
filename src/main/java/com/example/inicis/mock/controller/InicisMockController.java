package com.example.inicis.mock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.inicis.mock.config.MockProperties;
import com.example.inicis.mock.dto.AuthRequest;
import com.example.inicis.mock.dto.AuthResponse;
import com.example.inicis.mock.service.MockAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이니시스 Mock 인증 Controller
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class InicisMockController {
    
    private final MockProperties mockProperties;
    private final MockAuthService mockAuthService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 인증 요청 엔드포인트 (Keycloak에서 리다이렉트되는 곳)
     */
    @GetMapping("/auth")
    public String authPage(AuthRequest request, Model model) {
        log.info("========================================");
        log.info("Inicis Mock Auth Request Received");
        log.info("MID: {}", request.getMid());
        log.info("returnUrl: {}", request.getReturnUrl());
        log.info("reqSvcCd: {}", request.getReqSvcCd());
        log.info("moid: {}", request.getMoid());
        log.info("state: {}", request.getState());
        log.info("phoneNo: {}", request.getPhoneNo());
        log.info("birthDate: {}", request.getBirthDate());
        log.info("========================================");
        
        // MID 검증 (선택적)
        if (mockProperties.getValidMids() != null && 
            !mockProperties.getValidMids().isEmpty() &&
            !mockProperties.getValidMids().contains(request.getMid())) {
            log.warn("Invalid MID: {}", request.getMid());
            model.addAttribute("error", "유효하지 않은 상점 ID입니다: " + request.getMid());
            return "error";
        }
        
        // 자동 성공 모드인 경우 바로 성공 응답
        if (mockProperties.isAutoSuccess()) {
            log.info("Auto-success mode enabled, redirecting to success");
            String[] testUser = MockAuthService.TestUsers.getRandomUser();
            return postToCallback(request, testUser[0], testUser[1], testUser[2], true, null);
        }
        
        // 수동 모드: 인증 화면 표시
        model.addAttribute("request", request);
        model.addAttribute("testUsers", MockAuthService.TestUsers.USERS);
        
        return "auth";
    }
    
    /**
     * 인증 수행 (사용자가 성공/실패 선택)
     */
    @PostMapping("/auth/process")
    public String processAuth(
            @RequestParam String mid,
            @RequestParam String returnUrl,
            @RequestParam(required = false) String reqSvcCd,
            @RequestParam(required = false) String moid,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String phoneNo,
            @RequestParam(required = false) String birthDate,
            @RequestParam String action,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String selectedBirthDate,
            @RequestParam(required = false) String selectedPhoneNo,
            @RequestParam(required = false) String failureReason) {
        
        log.info("Processing auth - action: {}, name: {}", action, name);
        
        AuthRequest request = new AuthRequest();
        request.setMid(mid);
        request.setReturnUrl(returnUrl);
        request.setReqSvcCd(reqSvcCd);
        request.setMoid(moid);
        request.setState(state);
        request.setPhoneNo(phoneNo);
        request.setBirthDate(birthDate);
        
        // 인증 지연 시뮬레이션
        if (mockProperties.getAuthDelayMs() > 0) {
            try {
                Thread.sleep(mockProperties.getAuthDelayMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if ("success".equals(action)) {
            // 성공 처리
            String finalName = name != null ? name : "홍길동";
            String finalBirthDate = selectedBirthDate != null ? selectedBirthDate : "19900101";
            String finalPhoneNo = selectedPhoneNo != null ? selectedPhoneNo : "01012345678";
            
            return postToCallback(request, finalName, finalBirthDate, finalPhoneNo, true, null);
        } else {
            // 실패 처리
            String reason = failureReason != null ? failureReason : "사용자 인증 취소";
            return postToCallback(request, null, null, null, false, reason);
        }
    }
    
    /**
     * 콜백 URL로 POST 요청
     */
    private String postToCallback(AuthRequest request, String name, String birthDate, 
                                     String phoneNo, boolean success, String failureReason) {
        AuthResponse response;
        
        if (success) {
            response = mockAuthService.generateSuccessResponse(
                name, birthDate, phoneNo, request.getMoid(), request.getState());
            log.info("Generating SUCCESS response - CI: {}, name: {}", response.getCI(), response.getName());
        } else {
            response = mockAuthService.generateFailureResponse(
                request.getMoid(), request.getState(), failureReason);
            log.info("Generating FAILURE response - reason: {}", response.getResultMsg());
        }
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("resultCode", response.getResultCode());
        params.add("resultMsg", response.getResultMsg());
        
        if (response.getCI() != null) {
            params.add("CI", response.getCI());
            params.add("name", response.getName());
            params.add("birthDate", response.getBirthDate());
            params.add("gender", response.getGender());
            params.add("mobileCo", response.getMobileCo());
            params.add("mobileNo", response.getMobileNo());
        }
        
        if (response.getMoid() != null) {
            params.add("moid", response.getMoid());
        }
        
        if (response.getState() != null) {
            params.add("state", response.getState());
        }
        
        log.info("Posting to callback URL: {}", request.getReturnUrl());
        
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(request.getReturnUrl(), params, String.class);
        
        if (responseEntity.getStatusCode().is3xxRedirection()) {
            String location = responseEntity.getHeaders().getLocation().toString();
            log.info("Redirecting to location: {}", location);
            return "redirect:" + location;
        } else {
            // 만약 리다이렉트가 아니면, body를 반환하거나 다른 처리
            return responseEntity.getBody();
        } 
    }
    
    
    /**
     * 홈 페이지
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("serverPort", mockProperties);
        return "index";
    }
}
