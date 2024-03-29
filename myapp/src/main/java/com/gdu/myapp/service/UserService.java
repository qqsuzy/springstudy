package com.gdu.myapp.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
  void signin(HttpServletRequest request, HttpServletResponse response);  // 로그인    필요 사항 : email + pw + 로그인 후 이동경로 | 로그인 성공여부에 따라 로직이 필요
  void signout(HttpServletRequest request, HttpServletResponse response); // 로그아웃
  void signup(HttpServletRequest request, HttpServletResponse response);  // 회원가입 
  void leave(HttpServletRequest request, HttpServletResponse response);   // 회원탈퇴 
  
}
