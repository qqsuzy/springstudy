package com.gdu.myapp.service;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.UserMapper;
import com.gdu.myapp.utils.MySecurityUtils;

import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor : 생성자 대신에 사용가능한 생성자주입 어노테이션
@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  public UserServiceImpl(UserMapper userMapper) {
    super();
    this.userMapper = userMapper;
  }
  
  @Override
  public void signin(HttpServletRequest request, HttpServletResponse response) {

    try {
      
      String email = request.getParameter("email");
      String pw = MySecurityUtils.getSha256(request.getParameter("pw"));  // 사용자가 입력한 패스워드가 암호화되어 나옴
      String ip = request.getRemoteAddr();
      
      // select , insert 할 때 쓰는 map
      Map<String, Object> params = Map.of("email", email
                                        , "pw", pw
                                        , "ip", ip);  
      
      UserDto user = userMapper.getUserByMap(params);
      
      if(user != null) {  // 로그인의 기본 session(bind 영역) 에 올려야 함
        userMapper.insertAccesHistory(params);   // 로그인 성공시 히스토리 남김
        request.getSession().setAttribute("user", user); // session에 저장함
        response.sendRedirect(request.getParameter("url")); 
      } else {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('일치하는 회원 정보가 없습니다.')");
        out.println("location.href='" + request.getContextPath() + "/main.do'");
        out.println("</script>");
        out.flush();
        out.close();
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  @Override
  public void signout(HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub

  }

  @Override
  public void signup(HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub

  }

  @Override
  public void leave(HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub

  }



}
