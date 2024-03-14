package com.gdu.prj02.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MyController5 {

   /*
    * redirect 방법 (요청 2번)
    * 
    * 1. return "redirect:요청주소"; => redirect 할 요청주소
    * 2. HttpServletResponse response 를 이용한 응답 만들기
    *   1) response 로 첫 번째 요청 처리
    *   2) PrintWriter 사용하여 JavaScript로 location 처리하여 두 번째 요청을 처리
    *   => 위와 같은 방식으로 진행하면 redirect 와 동일하게 작동함
    *   => 반환타입은 void 로 해야 직접응답(HttpServletResponse)을 만들 수 있음
    *   => 메세지(insert,update,delete) 만들기 수월하며, 동작 후 어디로 가는지 확인하기 편함
    * 
    *  ** request 는 1번 요청, 1번 응답 하면 유효하지 않음
    */
 
    /*
     * 5. RedirectAttributes 객체를 이용한 파라미터 처리 
     */
  
  // Spring 4 이후 @GetMapping / @PostMapping / @PutMapping / @DeleteMapping 등 사용가능
  
  // @GetMapping : GET 방식의 경우 사용
  @GetMapping("/faq/add.do") // 요청 1
  public String add(RedirectAttributes redirectAttributes) {    
    
    // add 결과 => 게시글 추가(add) 후에
    int addResult = Math.random() < 0.5 ? 1 : 0;        
    
    // add 결과를 flash attribute 로 저장하면 redirect 경로에서 확인이 가능하다.
    // 성공 : "/faq/list.do" 요청으로 이동하는 faq/list.jsp 에서 addResult 값을 확인할 수 있다.
    // 실패 : "/main.do" 요청으로 이동하는 index.jsp에서 addResult 값을 확인할 수 있다.
    redirectAttributes.addFlashAttribute("addResult", addResult); // addAttribute 와 구분하기! => model에서 사용됨
    
    // 요청 2
    // add 결과에 따른 이동 =>  성공(목록 보기로 이동) : 실패(메인으로 이동)
    String path = addResult == 1 ? "/faq/list.do" : "/main.do"; // redirect 의 이동경로는 항상 URLMapping (.jsp 허용 X => /faq/list.jsp)
    
    // 이동
    return "redirect:" + path;
        
  }
  
  @GetMapping("/faq/list.do")
  public String list() {
    
    return "faq/list";
  }
  
  /*
   * 6. HttpServletResponse 객체를 이용한 파라미터 처리
   */
  
  @GetMapping("/faq/modify.do")
  public void modify(HttpServletRequest request, HttpServletResponse response) { // contextPath 가져오기 위해 HttpServletRequest 로 받아옴 (location에서 사용) 
    
    // modify 결과
    int modifyResult = Math.random() < 0.5 ? 1 : 0;
    
    // 응답 만들기
    response.setContentType("text/html; charset=UTF-8");
    try {
      PrintWriter out = response.getWriter();
      out.println("<script>");
      if(modifyResult == 1) {
        out.println("alert('수정되었습니다.');");
        out.println("location.href='" + request.getContextPath() + "/faq/list.do'");
      } else {
        out.println("alert('실패했습니다.');");
        out.println("history.back();");
      }
      out.println("</script>");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
}
