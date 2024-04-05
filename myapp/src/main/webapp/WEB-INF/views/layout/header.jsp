<!--  페이지 지시어 모든 jsp 파일에 포함 필요함 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> <!--  value 자매품: "${pageContext.request.contextPath}" -->
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- 페이지마다 다른 제목 -->
 <!-- empty : 파라미터로 받은게 없다 (EL 연산자 => jspstudy/02_jsp/src/main/webapp/pkg04_EL 참고!) -->
<title>
  <c:choose>
    <c:when test="${empty param.title}">Welcome</c:when>
    <c:otherwise>${param.title}</c:otherwise>
  </c:choose>
</title>

<!-- css파일 가져오기 : <link> , js파일 <js> -->
<!-- include libraries(jquery, bootstrap) -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<!-- include summernote css/js -->
<link rel="stylesheet" href="${contextPath}/resources/summernote-0.8.18-dist/summernote.min.css">
<script src="${contextPath}/resources/summernote-0.8.18-dist/summernote.min.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/lang/summernote-ko-KR.min.js"></script>

<!-- include custom css/js -->
<link rel="stylesheet" href="${contextPath}/resources/css/init.css?dt=${dt}">
<link rel="stylesheet" href="${contextPath}/resources/css/header.css?dt=${dt}">

</head>
<body>

  <div class="header-wrap">
    <!-- 로고 -->
    <div class="logo"></div>
    <!-- 사용자 정보 -->
    <div class="user-wrap">
      <!-- Sign In 안 된 경우 -->
      <c:if test="${sessionScope.user == null}">  
        <a href="${contextPath}/user/signin.page"><i class="fa-solid fa-arrow-right-to-bracket"></i>Sign In</a>
        <a href="${contextPath}/user/signup.page"><i class="fa-solid fa-user-plus"></i>Sign Up</a>
      </c:if>
      <!-- Sign In 된 경우 -->
      <!-- 로그인 성공하면 메시지 띄우기 -->
      <c:if test="${sessionScope.user != null}">
        ${sessionScope.user.name}님 반갑습니다
        <a href="${contextPath}/user/signout.do">로그아웃</a>
        <a href="${contextPath}/user/leave.do">회원탈퇴</a>
      </c:if>
    </div>
    <!-- 네비게이션 -->
    <div class="gnb-wrap">
      <ul class="gnb">
        <li><a href="${contextPath}/bbs/list.do">계층형게시판</a></li>
        <li><a href="${contextPath}/blog/list.do">댓글형게시판</a></li>
        <li><a href="${contextPath}/">첨부형게시판</a></li>
      </ul>
    </div>
    <!-- 
    
     JSP 의 내장객체
     - 총 9개의 내장객체가 있으며, 선언 없이 바로 사용 가능함
     
     1. SESSION
       1) session 의 시작 => 작업이 시작되었음을 의미함
       2) 페이지 열림 -> 로그인 완료 : 세션 유지          => 로그인 ~ 로그아웃 사이는 동일한 session -> 로그인 한 사용자만 사용할 수 있는 session
       3) 로그아웃 완료  : 세션 만료(sessionId 변함)  => session.invalidate() 초기화 코드로 인해 변하는 것
       4) 세션 만료  : session.invalidate(), 세션유지 시간이 지났을 때(30분) => 세션만료 시간은 spring이 알 수 있음(HttpSessionListener 인터페이스 : 세션 생성되었을 때, 세션 만료되었을 때 메소드 구현 가능)
     
     2. SESSION ID
      1) SIGNOUT_DT 는 email 기준이 아닌 SESSION_ID 를 기준으로 세션 UPDATE 를 진행해야함
         email을 기준으로 업데이트를 진행하게 되면 로그인, 로그아웃 기록이 동일 아이디로 여러 건 찍힘
        세션 ID를 부여하여 해당 ID 를 기준으로 기록이 UPDATE 되도록 함
        => 예전에는 세션 ID 를 기준으로 자동로그인 구현을 하였음, 현재는 보안상 문제로 인해 사용하지 않음 (사용자는 쿠키에 브라우저 기록을 저장하게 되는데 쿠키는 보안에 매우 취약하기 때문) 
     
         
      동일한 세션유지 : 브라우저 닫을 때 까지
     
     -->
    <div>현재 sessionId : <%=session.getId()%></div>
    
  </div>
  <!-- 닫는 태그 없이 작업 --> 
  <div class="main-wrap">