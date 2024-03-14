package com.gdu.prj02.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.gdu.prj02.dto.UserDto;

@SessionAttributes(names="user") // Model에 user가 저장되면 session에 같은 값을 저장한다. (해당 클래스에 모두 적용됨)
@Controller
public class MyController6 {

  @GetMapping("/user/login1.do")
  public String login1(HttpServletRequest request) {
    
    // HttpSession 구하기
    HttpSession session = request.getSession();
    
    // session 에 저장할 객체
    UserDto user = new UserDto(1, "min@naver.com");
    
    // session 에 객체 저장하기
    session.setAttribute("user", user);
    
    // 메인 페이지 이동
    return "redirect:/main.do"; /* => redirect => 권장(로그인 후 새로운 페이지로 넘어가는 것이 일반적이기 때문) */
 // return "index"; // forward (둘 다 사용가능)
    
  }
  
  @GetMapping("/user/logout1.do")
  public String logout1(HttpServletRequest request) {

    // HttpSession 구하기
    HttpSession session = request.getSession();
    
    // session 의 모든 정보 지우기 (invalidate() : session 의 초기화)
    session.invalidate();
    
    // 메인 페이지 이동
    return "redirect:/main.do";
    
  }
  
  @GetMapping("/user/login2.do")
  public String login2(Model model) {
    
    // model 에 저장할 객체
    UserDto user = new UserDto(1, "min@naver.com");
    
    // model 에 객체 저장하기 (@SessionAttributes 에 의해서 session 에도 저장된다.)
    model.addAttribute("user", user);
   
    // 메인 페이지 이동
    return "redirect:/main.do";
    
  }
  
  @GetMapping("/user/logout2.do")
  public String logout2(SessionStatus sessionStatus) {
    
    // session attribute 삭제를 위해 세션 완료 처리
    sessionStatus.setComplete();
    
    // 메인 페이지 이동
    return "redirect:/main.do";
    
  }
  
  // HttpSession 구하기 => 꼭 request 로 부터 받을(getSession) 필요 없음 => 파라미터로 받기
  // @GetMapping("/user/mypage.do")
  public String mypage1(HttpSession session, Model model) { 
    
    // session 에 저장된 user 정보
    UserDto user = (UserDto) session.getAttribute("user");
   
    // model 에 user 정보 저장
    model.addAttribute("user", user);
    
    // user/mypage.jsp 로 forward
    return "user/mypage";
  }
  
  @GetMapping("/user/mypage.do") 
  public String mypage2(@SessionAttribute(name="user") UserDto user) { // session attribute 중 user를 UserDto user 에 저장하시오.
  
    
    // user/mypage.jsp 로 forward
    return "user/mypage";  
    
  }
}
