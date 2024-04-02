package com.gdu.myapp.service;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.UserMapper;
import com.gdu.myapp.utils.MyJavaMailUtils;
import com.gdu.myapp.utils.MySecurityUtils;

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
  public ResponseEntity<Map<String, Object>> checkEmail(Map<String, Object> params) {
    boolean enableEmail = userMapper.getUserByMap(params) == null  // 이메일 사용 가능 여부(값이 없어야 사용 가능하기에 null)를 boolean 타입으로 저장
                        && userMapper.getLeaveUserByMap(params) == null;
    return new ResponseEntity<>(Map.of("enableEmail", enableEmail)  // enableEmail 이 signup.jsp의 fetch 로 넘어감 
                              , HttpStatus.OK);                     // ResponseEntity는 생성이후 타입(String,Object) 생략 가능
  }

  @Override
  public ResponseEntity<Map<String, Object>> sendCode(Map<String, Object> params) { // Map에 email : 받는 사람 있음
    
    /*
     * 구글 앱 비밀번호 설정 방법
     * 1. 구글에 로그인한다.
     * 2. [계정] - [보안]
     * 3. [Google에 로그인하는 방법] - [2단계 인증]을 사용 설정한다.
     * 4. 검색란에 "앱 비밀번호"를 검색한다.
     * 5. 앱 이름을 "myapp"으로 작성하고 [만들기] 버튼을 클릭한다.
     * 6. 16자리 비밀번호가 나타나면 복사해서 사용한다. (비밀번호 사이 공백은 모두 제거한다.)
     */
    
    
    // 인증코드 생성
    String code = MySecurityUtils.getRandomString(6, true, true);   // MySecurityUtils 는 static 처리되어 있어 바로 불러서 사용 가능
    
    // 개발할 때 인증코드 찍어보기
    System.out.println("인증코드 : " + code);
    
    // 메일 보내기
    myJavaMailUtils.sendMail((String)params.get("email")
                           , "myapp 인증요청"
                           , "<div>인증코드는 <strong>" + code + "</strong>입니다."); 
    
    // 인증코드 입력화면으로 보내주는 값
    return new ResponseEntity<>(Map.of("code", code)
                              , HttpStatus.OK);
  }
  
  @Override
  public void signup(HttpServletRequest request, HttpServletResponse response) {
    
    // 전달된 파라미터
    String email = request.getParameter("email");
    String pw = MySecurityUtils.getSha256(request.getParameter("pw"));         // SHA-256 암호화 작업 후 pw 저장
    String name = MySecurityUtils.getPreventXss(request.getParameter("name")); // 크로스 사이트 스크립트 작업 후 name 저장
    String mobile = request.getParameter("mobile");
    String gender = request.getParameter("gender");
    String event = request.getParameter("event");
    
    // Mapper 로 보낼 UserDto 만들기
    UserDto user = UserDto.builder()
                          .email(email)
                          .pw(pw)
                          .name(name)
                          .mobile(mobile)
                          .gender(gender)
                          .eventAgree(event == null ? 0 : 1)   // DB에서 0 또는 1로 저장하기로 함 => checkbox 는 체크하지 않으면 null 값 전달 됨
                        .build();
    
    // 회원 가입 (응답 카운트) 
    int insertCount = userMapper.insertUser(user);
    
    // 응답 만들기 (성공하면 sing in 처리하고 /main.page 이동, 실패하면 뒤로 가기)
    try {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      
      // 가입 성공
      if(insertCount == 1) {
       
        // Sign In 및 접속 기록을 위한 Map
        Map<String, Object> map = Map.of("email", email
                                       , "pw", pw
                                       , "ip", request.getRemoteAddr());
       
        // Sign In (세션에 user 저장하기)
        request.getSession().setAttribute("user", userMapper.getUserByMap(map));
       
        // 접속 기록 남기기
        userMapper.insertAccessHistory(map);
       
        out.println("alert('회원 가입되었습니다.');");
        out.println("location.href='" + request.getContextPath() + "/main.page';");
        
      // 가입 실패
      } else {
        out.println("alert('회원 가입이 실패했습니다.');");
        out.println("history.back();");  //회원가입 화면으로 다시 돌아감
      }
      out.println("</script>");
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  @Override
  public void leave(HttpServletRequest request, HttpServletResponse response) {
    
    try {
      // 세션에 저장된 user 값 확인
      HttpSession session = request.getSession();
      UserDto user = (UserDto) session.getAttribute("user");
      
      // 세션 만료로 user 정보가 세션에 없을 수 있음
      if(user == null) {
        response.sendRedirect(request.getContextPath() + "/main.page"); // 세션 정보가 없으므로 redirect 하여 메인페이지로 이동 => 로그인 유도
      }
      
      // 탈퇴 처리
      int deleteCount = userMapper.deleteUser(user.getUserNo()); // userNo를 빼서 넘기던 user 자체를 넘겨도 됨
      
      // 탈퇴 이후 응답 만들기
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      // 탈퇴 성공
      if(deleteCount == 1) {
        
        // 세션에 저장된 모든 정보 초기화 => 해당 작업을 하지 않으면 회원 탈퇴는 되었지만 세션에 정보가 남아 있음 
        session.invalidate(); // SessionStatus 객체의 setComplete() 메소드 호출  => SessionStatus 사용하려면 controller 에서 선언 후 넘겨주어야 사용 가능 | Session에 대한 내용은 02_spring_mvc 의 MyController6 참고하기!
                
        out.println("alert('회원 탈퇴되었습니다. 이용해 주셔서 감사합니다.');");
        out.println("location.href='" + request.getContextPath() + "/main.page';");
       
        // 탈퇴 실패 
      } else {
        out.println("alert('회원 탈퇴되지 않았습니다.');");
        out.println("history.back();"); 
      }
      out.println("</script>");
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  @Override
  public String getRedirectURLAfterSignin(HttpServletRequest request) { // => request 에 HEADER 값이 있는데 "referer" (현재 페이지가 아닌 이전 페이지의 정보를 가지고 있음)
  
    // Sign In 페이지 이전의 주소가 저장되어 있는 Request Header 의 referer 값 확인
    String referer = request.getHeader("referer");
    
    // referer 로 돌아가면 안 되는 예외 상황 (아이디/비밀번호 찾기 화면, 가입 화면 등)  => 쓰면 안되는 URL은 배열로 만들어 둠 
    String[] excludeUrls = {"/findId.page", "/findPw.page", "/signup.page"};
    
    // Sign In 이후 이동할 url
    String url = referer;                                // 초기값 referer (아래 코드 : null 여부에 따라 referer 덮어쓰기 됨)
    if(referer != null) {
      for(String excludeUrl : excludeUrls) {
        if(referer.contains(excludeUrl)) {               // referer 값이 예외해야 하는 주소와 같다면 메인페이지로 이동
          url = request.getContextPath() + "/main.page"; // url 관련된 것은 모두 contextPath 앞에 붙여 주어야 함
          break;
        }
      }
    } else {
      url = request.getContextPath() + "/main.page";     // referer 값이 없는(null) 경우 : 사이트 접속 후 바로 로그인한 경우 -> 로그인 후 main 페이지로 이동
    }
    
    return url;
    
  }

  @Override
  public String getNaverLoginURL(HttpServletRequest request) {
   
    /************* 네이버 로그인 1 *************/
    // 네이버 로그인 요청 주소를 만들어서 반환하는 메소드
    String redirectUri = "http://localhost:8080" + request.getContextPath() + "/user/naver/getAccessToken.do";
    String state = new BigInteger(130, new SecureRandom()).toString();   // 네이버 개발자센터에서 제공하는 state 생성 방법 (고정 코드)
    
    StringBuilder builder = new StringBuilder();
    builder.append("https://nid.naver.com/oauth2.0/authorize");
    builder.append("?response_type=code");
    builder.append("&client_id=6oAUlmBdK_Zz1iN3Jr8v");
    builder.append("&redirect_uri=" + redirectUri);
    builder.append("&state=" + state);
    
    return builder.toString();

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
        userMapper.insertAccessHistory(params); 
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
  public void signout(HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub

  }
 
}
