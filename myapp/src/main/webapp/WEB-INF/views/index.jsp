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

<style>
  @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css');
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap')

  * {
   font-family: "Noto Sans KR", sans-serif;
   font-weight: 400;
  }
  
</style>

</head>
<body>
  
  <!-- Sign In 안 된 경우 -->
  <c:if test="${sessionScope.user == null}"> <!-- session에 있는 user(UserServiceImpl의 signin) -->
    <a href="${contextPath}/user/signin.page"><i class="fa-solid fa-arrow-right-to-bracket"></i>Sign In</a> <!-- page : 단순페이지 이동 , do : DB,Service 처리가 있는 페이지-->
    <a href="${contextPath}/user/signup.page"><i class="fa-solid fa-user-plus"></i>Sign Up</a>
  </c:if>
  
  <!-- Sign In 된 경우 -->
  <!-- 로그인 성공하면 메시지 띄우기 -->
  <c:if test="${sessionScope.user != null}">
    ${sessionScope.user.name}님 반갑습니다
    <a href="${contextPath}/user/signout.do">로그아웃</a>
    <a href="${contextPath}/user/leave.do">회원탈퇴</a>
  </c:if>
  
</body>
</html>