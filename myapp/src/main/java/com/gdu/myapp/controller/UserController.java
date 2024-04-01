package com.gdu.myapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.myapp.service.UserService;

@RequestMapping("/user")  // user 관련된 메소드는 /user로 시작되도록 mapping 함
@Controller
public class UserController {
  
  private final UserService userService;

  public UserController(UserService userService) {
    super();
    this.userService = userService;
  }
  
  @GetMapping("/signin.page")
  public String signinPage(HttpServletRequest request
                         , Model model) {  // request 로 부터 필요한 정보를 빼서 model에 저장한 후 페이지를 이동함 (페이지 이동하고자 하는 url을 request로 부터 꺼냄) => model 로 저장한 값은 view 에서 EL로 확인 가능
                                           // => request 에 HEADER 값이 있는데 "referer" (현재 페이지가 아닌 이전 페이지의 정보를 가지고 있음)
    
    // Sign In 페이지 이전의 주소가 저장되어 있는 Request Header 의 referer
    String referer = request.getHeader("referer");
    
    // referer 로 돌아가면 안 되는 예외 상황 (아이디/비밀번호 찾기 화면, 가입 화면 등) => 쓰면 안되는 URL은 배열로 만들어 둠 
    String[] excludeUrls = {"/findId.page", "/findPw.page", "/signup.page"}; 
    
    // Sign In 이후 이동할 url
    String url = referer;                   // 초기값 referer (아래 코드 : null 여부에 따라 referer 덮어쓰기 됨)
    if(referer != null) {                   
      for(String excludeUrl : excludeUrls) {
        if(referer.contains(excludeUrl)) {  // referer 값이 예외해야 하는 주소와 같다면 메인페이지로 이동
          url = request.getContextPath() + "/main.page";  // url 관련된 것은 모두 contextPath 앞에 붙여 주어야 함
          break;
        }
      }
    } else {
      url = request.getContextPath() + "/main.page";  // referer 값이 없는(null) 경우 : 사이트 접속 후 바로 로그인한 경우 -> 로그인 후 main 페이지로 이동
    }
    
    // Sign In 페이지로 url 넘겨 주기
    model.addAttribute("url", url);  
    
    return "user/signin";    // forward 진행              
    
  }
  
  // Service로 로그인에 필요한 값을 넘김
  @PostMapping("/signin.do") 
  public void signin(HttpServletRequest request, HttpServletResponse response) {
    userService.signin(request, response);
  }
  
  // 회원가입
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
    System.out.println(params);
    return new ResponseEntity<Map<String,Object>>(HttpStatus.OK);
 }
  
}
