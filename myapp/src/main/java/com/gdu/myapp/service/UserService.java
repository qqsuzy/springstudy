package com.gdu.myapp.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

// 구현 순서대로 메소드 작성됨
public interface UserService {
  
  // 가입 및 탈퇴
  ResponseEntity<Map<String, Object>> checkEmail(Map<String, Object> params); // controller 로 부터 받아오는 건 Map으로 파라미터는 Map
  ResponseEntity<Map<String, Object>> sendCode(Map<String, Object> params);   // 인증코드 전송
  void signup(HttpServletRequest request, HttpServletResponse response);      // 회원가입 
  void leave(HttpServletRequest request, HttpServletResponse response);       

  // 로그인 및 로그아웃
  String getRedirectURLAfterSignin(HttpServletRequest request);
  String getNaverLoginURL(HttpServletRequest request);
  void signin(HttpServletRequest request, HttpServletResponse response);     // 로그인
  void signout(HttpServletRequest request, HttpServletResponse response);    // 로그아웃
     
}
