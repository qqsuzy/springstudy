package com.gdu.prj02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller // base-package 에 작성된 위치에서 컴포넌트를 찾음
public class MyController2 {

  // value="/board/list.do"  : http://localhost:8080/prj02/board/list.do
  // 응답 jsp                : /WEB-INF/views/board/list.jsp
  @RequestMapping(value="/board/list.do", method=RequestMethod.GET)
  public void method() {
    
  }
  
}
