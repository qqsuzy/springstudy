package com.gdu.prj02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller // 컨트롤러
public class MyController1 {
  
  /*
   * 메소드 (MVC 패턴)
   * 
   * 1. 반환타입
   *  1) String : 응답할 jsp 의 경로와 이름을 반환한다.
   *  2) void   
   *    (1) 요청한 주소를 jsp의 경로와 이름으로 인식하고 처리한다. 
   *        - 요청 주소 : /board/list.do
   *        - jsp 경로  : /board/list.jsp (suffix값 빼고 찾음)
   *        
   *    (2) 직접 응답(HttpServletResponse)을 만든다. => 직접 응답을 만들기 위해 void 처리하는 경우
   *        대부분 JavaScript 코드를 만든다.
   *        
   * 2. 메소드명
   *   아무 일도 안한다.
   *   
   * 3. 매개변수
   *  1) 요청과 응답을 위한 각종 변수의 선언이 가능하다.
   *  2) 주요 매개변수
   *     (1) HttpServletRequest request
   *     (2) HttpServletResponse response
   *     (3) HttpSession session                    =>  선언해서 사용하는 것은 spring 뿐 (본래 request.getSession로 가져옴)
   *     (4) 커맨드 객체 : 요청 파라미터를 받는 객체
   *     (5) 일반 변수   : 요청 파라미터를 받는 변수
   *     (6) Model mode  : forward 할 때 정보를 저장할 객체 (attribute)
   *     (7) RedirectAttributes rttr : redirect 할 때 정보를 저장할 객체 (flash attribute) => forward만 가능한 정보 전달을 Spring 에서는 redirect도 가능함   
   *    
   */

  // value="/"        : contextPath 요청을 의미함  => http://localhost:8080/prj02
  // value="/main.do" : contextPath/main.do 요청을 의미함  => http://localhost:8080/prj02/main.do
  @RequestMapping(value= {"/" , "/main.do"}, method=RequestMethod.GET) // value가 2개 이상일 경우엔 배열{ }로 전달해야 함 => 현재는 문자열 배열임
  public String welcome() {
    // 뷰 리졸버 prefix : /WEB-INF/views/
    // 뷰 리졸버 suffix : .jsp
    // 실제 리턴        : /WEB-INF/views/index.jsp
    return "index";
  }
  
}
