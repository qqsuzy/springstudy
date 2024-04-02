package com.gdu.myapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.myapp.service.UserService;

/*
 *  순서를 지켜야 하는 "동기처리"
 *  
 *  이메일중복체크    -> 요청 (비동기 요청) => promise 가 내장된 fetch 사용하여 응답을 순차적으로 처리함(요청/응답 순서를 지켜야 할 경우 반드시 promise가 필요) 
 *  가능여부          <- 응답
 *  해당이메일로전송  -> 요청 (비동기 요청)
 *  보낸코드          <- 응답
 *  일치여부 : 허용 여부 판단
 * 
 */


@RequestMapping("/user")  // user 관련된 메소드는 /user로 시작되도록 mapping 함
@Controller
public class UserController {
  
  private final UserService userService;

  public UserController(UserService userService) {
    super();
    this.userService = userService;
  }
  
  // signin.jsp 로 이동하는 controller
  @GetMapping("/signin.page")
  public String signinPage(HttpServletRequest request
                         , Model model) {  // request 로 부터 필요한 정보를 빼서 model에 저장한 후 페이지를 이동함 (페이지 이동하고자 하는 url을 request로 부터 꺼냄) => model 로 저장한 값은 view 에서 EL로 확인 가능
                                           
    
    // Sign In 페이지로 url 넘겨 주기 (로그인 후 이동할 경로를 의미함)
    model.addAttribute("url",  userService.getRedirectURLAfterSignin(request));
    
    // Sign In 페이지로 naverLoginURL 넘겨 주기 (네이버 로그인 요청 주소를 의미함)
    model.addAttribute("naverLoginURL", userService.getNaverLoginURL(request));
    
    return "user/signin";
    
  }
  
  // Service로 로그인에 필요한 값을 넘김
  @PostMapping("/signin.do") 
  public void signin(HttpServletRequest request, HttpServletResponse response) {
    userService.signin(request, response);
  }
  
  // 회원가입 페이지 이동
  @GetMapping("/signup.page")
  public String signupPage() {
    return "user/signup";
  }
  
  // 이메일 중복 체크 (Controller 는 서빙만 하고 실제 일 처리는 Service 가 함 => return 만 진행)
  @PostMapping(value="/checkEmail.do", produces="application/json") // 응답데이터 타입 : json (전송타입과 동일하게) 
  public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, Object> params){ // jackson 라이브러리가 JSON 데이터를 받아서 Map으로 저장시킴 | 비동기 작업 => 응답 받은 JSON 데이터는 Map으로 저장! 
    return userService.checkEmail(params);
  }

  // 이메일 인증 코드 전송
 @PostMapping(value="/sendCode.do", produces="application/json")
  public ResponseEntity<Map<String, Object>> sendCode(@RequestBody Map<String, Object> params) {
    return userService.sendCode(params);
 }
 
 // 회원가입
 @PostMapping("/signup.do")
 public void signup(HttpServletRequest request, HttpServletResponse response) {
   userService.signup(request, response);
 }
 
 // 회원탈퇴 (session에 저장된 데이터를 가져오는 방법 3가지)
 @GetMapping("/leave.do")
 public void leave(HttpServletRequest request, HttpServletResponse response) {
   userService.leave(request, response);
 }
 
 /*
 @GetMapping("/leave.do") => 파라미터로 Session 받아옴
 public void leave(HttpSession session, HttpServletResponse response) {
   UserDto user = (UserDto)session.getAttribute("user");
 }

 @GetMapping("/leave.do") => @SessionAttribute 사용
 public void leave(@SessionAttribute(name="user") UserDto user, HttpServletResponse response) { // session에 저장되어 있는 것들 중 user라는 정보를 UserDto로 user라는 이름으로 저장함
 }
*/
 
 @PostMapping("/signout.do")
 public void signout(HttpServletRequest request, HttpServletResponse response) {
   userService.signout(request, response);
 }
 
}
