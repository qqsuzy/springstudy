package com.gdu.myapp.service;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.UserMapper;
import com.gdu.myapp.utils.MyJavaMailUtils;
import com.gdu.myapp.utils.MySecurityUtils;

import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor : 생성자 대신에 사용가능한 생성자주입 어노테이션
@Service
public class UserServiceImpl implements UserService {
  
  private final UserMapper userMapper;
  private final MyJavaMailUtils myJavaMailUtils;
  
  public UserServiceImpl(UserMapper userMapper, MyJavaMailUtils myJavaMailUtils) {
    super();
    this.userMapper = userMapper;
    this.myJavaMailUtils = myJavaMailUtils;
  }

  @Override
  public void signin(HttpServletRequest request, HttpServletResponse response) {

    try {
      
      // 입력한 아이디
      String email = request.getParameter("email");
      
      // 입력한 비밀번호 + SHA-256 방식의 암호화
      String pw = MySecurityUtils.getSha256(request.getParameter("pw"));  // 사용자가 입력한 패스워드가 암호화되어 나옴
      
      // 접속 IP (접속 기록을 남길 때 필요한 정보)
      String ip = request.getRemoteAddr();
      
      // DB로 보낼 정보 (email/pw: USER_T , email/ip: ACCESS_HISTORY_T) | select , insert 할 때 쓰는 map
      Map<String, Object> params = Map.of("email", email
                                        , "pw", pw
                                        , "ip", ip);  
      
      // email/pw 가 일치하는 회원 정보 가져오기
      UserDto user = userMapper.getUserByMap(params);
      
      // 일치하는 회원 있음 (Sign In 성공)
      if(user != null) {  // 로그인의 기본 session(bind 영역) 에 올려야 함
        // 접속 기록 ACCESS_HISTORY_T 에 남기기 (로그인 성공시 히스토리 남김)
        userMapper.insertAccesHistory(params); 
        // 회원 정보를 세션(브라우저 닫기 전까지 정보가 유지되는 공간, 기본 30분 정보 유지)에 보관하기  => 다른 페이지들을 돌아다녀도 로그인 정보를 보관 
        request.getSession().setAttribute("user", user); 
        // Sign In 후 페이지 이동 (url로 이동)
        response.sendRedirect(request.getParameter("url"));
      // 일치하는 회원 없음 (Sign In 실패)  
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
  public ResponseEntity<Map<String, Object>> checkEmail(Map<String, Object> params) {
    boolean enableEmail = userMapper.getUserByMap(params) == null  // 이메일 사용 가능 여부(값이 없어야 사용 가능하기에 null)를 boolean 타입으로 저장
                        && userMapper.getLeaveUserByMap(params) == null;
    return new ResponseEntity<>(Map.of("enableEmail", enableEmail)  // enableEmail 이 signup.jsp의 fetch 로 넘어감 
                              , HttpStatus.OK);                     // ResponseEntity는 생성이후 타입(String,Object) 생략 가능
  }

  @Override
  public ResponseEntity<Map<String, Object>> sendCode(Map<String, Object> params) { // Map에 email : 받는 사람 있음
    
    // 인증코드 생성
    String code = MySecurityUtils.getRandomString(6, true, true);   // MySecurityUtils 는 static 처리되어 있어 바로 불러서 사용 가능
    
    // 메일 보내기
    myJavaMailUtils.sendMail((String)params.get("email")
                           , "myapp 인증요청"
                           , "<div>인증코드는 <strong>" + code + "</strong>입니다.</div>"); 
    
    // 인증코드 입력화면으로 보내주는 값
    return new ResponseEntity<>(Map.of("code", code)
                              , HttpStatus.OK);
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
