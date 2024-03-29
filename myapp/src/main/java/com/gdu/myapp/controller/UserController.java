package com.gdu.myapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    String[] excludeUrls = {};
    
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
    
    return "user/signin";                  
    
  }
  
  @PostMapping("/signin.do")
  public void signin(HttpServletRequest request, HttpServletResponse response) {
    userService.signin(request, response);
  }
  
}
