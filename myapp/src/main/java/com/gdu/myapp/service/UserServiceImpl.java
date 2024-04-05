package com.gdu.myapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
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
    // state : 나만 만들어 낼 수 있는 값 (SecureRandom 로 랜덤으로 생성됨)
    String state = new BigInteger(130, new SecureRandom()).toString();   // 네이버 개발자센터에서 제공하는 state 생성 방법 (고정 코드)
    
    StringBuilder builder = new StringBuilder();
    builder.append("https://nid.naver.com/oauth2.0/authorize");
    builder.append("?response_type=code");
    builder.append("&client_id=6oAUlmBdK_Zz1iN3Jr8v");
    builder.append("&redirect_uri=" + redirectUri);
    builder.append("&state=" + state);  
    
    return builder.toString();

  }
  
  // 받은 Access Token 를 Controller 로 보내기
  @Override 
  public String getNaverLoginAccessToken(HttpServletRequest request) {
    /************* 네이버 로그인 2 *************/
    // 네이버로부터 Access Token 을 발급 받아 반환하는 메소드
    // 네이버 로그인 1단계에서 전달한 redirect_uri 에서 동작하는 서비스
    // code 와 state 파라미터를 받아서 Access Token 을 발급 받을 때 사용
    
    String code  = request.getParameter("code"); // 파라미터로 받아서 다시 naver로 전송
    String state = request.getParameter("state");
    
    String spec = "https://nid.naver.com/oauth2.0/token";
    String grantType = "authorization_code";
    String clientId = "6oAUlmBdK_Zz1iN3Jr8v";
    String clientSecret = "I2Fraf4CwZ";
    
    StringBuilder builder = new StringBuilder();
    builder.append(spec);
    builder.append("?grant_type=" + grantType);
    builder.append("&client_id=" + clientId);
    builder.append("&client_secret=" + clientSecret);
    builder.append("&code=" + code);
    builder.append("&state=" + state);

    // javastudy -> 13_network -> pkg02_open_api 참고
    
    // 선언부
    HttpURLConnection con = null; // try 밖으로 뺀 이유? 닫는 것을 try-catch 밖에서 진행하기 위함
    JSONObject obj = null;
    try {
      
      // 요청
      URL url = new URL(builder.toString());
      con = (HttpURLConnection) url.openConnection(); // 자식타입으로 다운 캐스팅
      con.setRequestMethod("GET"); // 반드시 대문자로 작성해야 한다.
      
      // 응답 스트림 생성
      BufferedReader reader = null;
      int responseCode = con.getResponseCode(); //  OK : 200
      if(responseCode == HttpURLConnection.HTTP_OK) {
        reader = new BufferedReader(new InputStreamReader(con.getInputStream())); // 입력스트림으로 naver로 부터 데이터 받아옴 -> 입력스트림은 byte 기반 -> 그러나 우리는 char 로 받아와야 함
      } else {                                                                    // byte(InputStream) -> char(InputStreamReader) -> buffer(BufferedReader) (버퍼를 껴서 속도 ↑)
        reader = new BufferedReader(new InputStreamReader(con.getErrorStream())); // 정상 스트림은 console 로 글자색 검정으로 출력 , 에러 스트림은 글자색 빨강으로 출력됨
      }
      
      // 응답 데이터 받기
      String line = null;
      StringBuilder responseBody = new StringBuilder();
      while((line = reader.readLine()) != null) { // readLine은 모든 값을 읽으면 null 값 반환 => null 값이 아닐 때 까지 읽어드림 (= 끝까지 읽어드림)
        responseBody.append(line);
      }

      // 응답 데이터 JSON 객체로 변환하기
      obj = new JSONObject(responseBody.toString());
      
      // 응답 스트림 닫기
      reader.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    con.disconnect();
    
    return obj.getString("access_token"); 
    
  }
  
  @Override
  public UserDto getNaverLoginProfile(String accessToken) {
    /************* 네이버 로그인 3 *************/
    // 네이버로부터 프로필 정보(이메일, [이름, 성별, 휴대전화] 을 발급 받아 반환하는 메소드
    // 네이버 로그인 1단계에서 전달한 redirect_uri 에서 동작하는 서비스
    // code 와 state 파라미터를 받아서 Access Token 을 발급 받을 때 사용
    
    /* 로그인 방법은 1) 네이버 로그인만 가능하도록 구현하는 방법, 2) 간편가입으로 추가로 비밀번호 + 회원정보를 받아서 네이버 로그인 + 직접 로그인 하는 방법 2가지로 구현 가능함 
     * => 비밀번호가 없는 상태로 구현할 경우 마이페이지에 들어갈 때 비밀번호를 요구하는데 로직이 피곤해짐..! -> 간편가입 기능을 추가하는 것을 권장
     */
    
    String spec = "https://openapi.naver.com/v1/nid/me";
    HttpURLConnection con = null;
    UserDto user = null;
    
    try {
    
      // 요청
      URL url = new URL(spec);
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      
      // 요청 헤더
      con.setRequestProperty("Authorization", "Bearer " + accessToken); // setRequestProperty(헤더이름, 값) : 요청헤더를 만들어주는 메소드 
      
      // 응답 스트림 생성
      BufferedReader reader = null;
      int responseCode = con.getResponseCode(); //  OK : 200
      if(responseCode == HttpURLConnection.HTTP_OK) {
        reader = new BufferedReader(new InputStreamReader(con.getInputStream())); // 입력스트림으로 naver로 부터 데이터 받아옴 -> 입력스트림은 byte 기반 -> 그러나 우리는 char 로 받아와야 함
      } else {                                                                    // byte(InputStream) -> char(InputStreamReader) -> buffer(BufferedReader) (버퍼를 껴서 속도 ↑)
        reader = new BufferedReader(new InputStreamReader(con.getErrorStream())); // 정상 스트림은 console 로 글자색 검정으로 출력 , 에러 스트림은 글자색 빨강으로 출력됨
      }
      
      // 응답 데이터 받기
      String line = null;
      StringBuilder responseBody = new StringBuilder();
      while((line = reader.readLine()) != null) { // readLine은 모든 값을 읽으면 null 값 반환 => null 값이 아닐 때 까지 읽어드림 (= 끝까지 읽어드림)
        responseBody.append(line);
      }

      // 응답 데이터 JSON 객체로 변환하기
      JSONObject obj = new JSONObject(responseBody.toString());
      JSONObject response = obj.getJSONObject("response");
      user = UserDto.builder()
                  .email(response.getString("email"))
                  .gender(response.has("gender") ? response.getString("gender") : null)
                  .name(response.has("name") ? response.getString("name") : null)
                  .mobile(response.has("mobile") ? response.getString("mobile") : null)
                 .build();
      // if(response.has("name")) user.setName(response.getString("name")); builder 에서 빼서 if 문에 넣는 것도 가능
      
      // 응답 스트림 닫기
      reader.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    con.disconnect();
    
    return user;
  }
  
  @Override
  public boolean hasUser(UserDto user) {
    // service는 mapper 호출 하는데 UserMapper 내에 getUserByMap 전달
    return userMapper.getUserByMap(Map.of("email", user.getEmail())) != null; // null 이 아니면 true 반환 -> 즉, 사용자 존재한다는 의미임
  }
  
  // 네이버 로그인
  @Override
  public void naverSignin(HttpServletRequest request, UserDto naverUser) {
    
    Map<String, Object> map = Map.of("email", naverUser.getEmail(),
                                      "ip", request.getRemoteAddr());
    
    UserDto user = userMapper.getUserByMap(map);
    // 회원 정보를 세션(브라우저 닫기 전까지 정보가 유지되는 공간, 기본 30분 정보 유지)에 보관하기  => 다른 페이지들을 돌아다녀도 로그인 정보를 보관
    request.getSession().setAttribute("user", user);
    userMapper.insertAccessHistory(map);  // map에 담긴 사용자 정보를 userMapper 에 AccessHistory 저장 시킴
    
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
      
      // 접속 수단 (요청 헤더의 User-Agent) 값
      String userAgent = request.getHeader("User-Agent");
      
      // DB로 보낼 정보 (email/pw: USER_T , email/ip: ACCESS_HISTORY_T) | select , insert 할 때 쓰는 map
      Map<String, Object> params = Map.of("email", email
                                        , "pw", pw
                                        , "ip", request.getRemoteAddr()
                                        , "userAgent", request.getHeader("User-Agent")  // 접속 경로 (chrome, java ...)
                                        , "sessionId", request.getSession().getId());   // SessionId
      
      // email/pw 가 일치하는 회원 정보 가져오기
      UserDto user = userMapper.getUserByMap(params);
      
      // 일치하는 회원 있음 (Sign In 성공)
      if(user != null) {  // 로그인의 기본 session(bind 영역) 에 올려야 함
        
        // 접속 기록 ACCESS_HISTORY_T 에 남기기 (로그인 성공시 히스토리 남김)
        userMapper.insertAccessHistory(params); 
        
        // 회원 정보를 세션(브라우저 닫기 전까지 정보가 유지되는 공간, 기본 30분 정보 유지)에 보관하기  => 다른 페이지들을 돌아다녀도 로그인 정보를 보관 
        HttpSession session = request.getSession();
        request.getSession().setAttribute("user", user); 
        session.setMaxInactiveInterval(10); // 세션 유지 시간 10초 설정 (디폴트 값 : 60 * 30 => 30분)
        
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
    
try {
      
      // Sign Out 기록 남기기
      HttpSession session = request.getSession();
      String sessionId = session.getId(); 
      userMapper.updateAccessHistory(sessionId);
      
      // 세션에 저장된 모든 정보 초기화  => 기록을 먼저 남긴 후 초기화 시켜야함!
      session.invalidate();
      
      // 메인 페이지로 이동
      response.sendRedirect(request.getContextPath() + "/main.page");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
  }
 
}
