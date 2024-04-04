package com.gdu.myapp.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

public class RequiredSigninInterceptor implements HandlerInterceptor {

  
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // preHandle() 메소드 반환값
    // 1. true  : 요청을 처리한다.
    // 2. false : 요청을 처리하지 않는다.
    
    // 작성페이지로 갈 때 인터셉터 동작 => 작성하러가기 버튼 눌렀을 때, 로그인되어 있지 않으면 작성페이지 이동 X
    HttpSession session = request.getSession();
    if(session.getAttribute("user") == null) {
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("if(confirm('Sign In 이 필요한 기능입니다. Sign In 할까요?')){");
      out.println("  location.href='" + request.getContextPath() + "/user/signin.page';");
      out.println("} else {");
      out.println("  history.back();");
      out.println("}");
      out.println("</script>");
      out.flush();
      out.close();
      return false; // 컨트롤러로 요청이 전달되지 않는다.
    }
   
   return true;
  
  }
  
}
