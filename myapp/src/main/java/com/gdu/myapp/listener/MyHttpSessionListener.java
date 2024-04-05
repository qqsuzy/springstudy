package com.gdu.myapp.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gdu.myapp.mapper.UserMapper;

public class MyHttpSessionListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    // TODO Auto-generated method stub
    HttpSessionListener.super.sessionCreated(se);
  }
 
  //세션 만료 시 자동으로 동작
  @Override
  public void sessionDestroyed(HttpSessionEvent se) {   
    
    // HttpSession
    HttpSession session = se.getSession(); // 세션 구하기
  
    /*
     * 01_IoC 처럼 메인메소드 기준으로 동작하는 것이 아니라 Web 기반으로 동작하기 때문에 WebApplicationContextUtils 사용
     * Application 시작 ~ 끝 까지 저장하는 Bind 영역 : getServletContext (가장 넓은 lifecycle 을 가짐)
     * 
     * ApplicationContext 
     * 1) Spring 의 IoC 컨테이너 
     * 
     */
    
    // ApplicationContext
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()); //  getServletContext 을 꺼낸 이유? -> bean 생성을 위함
    
    // session_id
    String sessionId = session.getId();
    
    // getBean()
    // Service 를 추천하나, Mapper 도 가능 
    UserMapper userMapper = ctx.getBean("userMapper", UserMapper.class);
    
    // updateAccessHistory()
    userMapper.updateAccessHistory(sessionId);
    
    // 확인 메시지
    System.out.println(sessionId + " 세션 정보가 소멸되었습니다.");
    
    
  }
  
}
