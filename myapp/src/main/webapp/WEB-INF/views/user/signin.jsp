<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<!-- css파일 가져오기 : <link> , js파일 <js> -->

<!-- include libraries(jquery, bootstrap) -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<link href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<!-- include summernote css/js -->
<link rel="stylesheet" href="${contextPath}/resources/summernote-0.8.18-dist/summernote.min.css">
<script src="${contextPath}/resources/summernote-0.8.18-dist/summernote.min.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/lang/summernote-ko-KR.min.js"></script>

</head>
<body>
  
  <!--  로그인 화면 구성 -->
  <h1>Sign IN</h1>
  
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
    
</body>
</html>