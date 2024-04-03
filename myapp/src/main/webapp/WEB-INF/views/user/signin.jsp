<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<jsp:include page="../layout/header.jsp">
  <jsp:param value="Sign In" name="title"/>
</jsp:include>
  
<!--  로그인 화면 구성 -->
<h1 class="title">Sign IN</h1>

<div>
  <form method="POST"
        action="${contextPath}/user/signin.do">
     <div>
        <label for="email">아이디</label>
        <input type="text" id="email" name="email" placeholder="example@example.com"> <!-- 실제 DB로 보내는 값 : eamil , pw | 데이터 전송 후 돌아올 url 필요하기 떄문에 url 값 or /user/signin.do 도 함께 넘어감 -->
     </div>
     <div>
        <label for="pw">비밀번호</label>
        <input type="password" id="pw" name="pw" placeholder="●●●●">
     </div>
     <div>
       <!-- 로그인 버튼 -->
       <input type="hidden" name="url" value="${url}"> <!-- model 로 저장한 값(url)은 view 에서 EL로 확인 가능 => 모델이 저장된 값은 일회용으로 다시 value로 저장해서 signin.do 로 전송  -->
       <button type="submit">Sign In</button> 
     </div>
     <div>
       <button type="button" id="btn-signout">Sign Out</button>
     </div>   
     <div>
      <!-- UserController의 signinPage 메소드 내 model 에 저장된 naverLoginUrl을 EL 태그로 가져옴 -->
       <a href="${naverLoginURL}">
        <img src="${contextPath}/resources/2021_Login_with_naver_guidelines_Kr/btnG_아이콘원형.png">
      </a>
     </div>
  </form>
</div>
    
<%@ include file="../layout/footer.jsp" %>