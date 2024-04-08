<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp">
  <jsp:param value="${blog.blogNo}번 블로그" name="title"/>
</jsp:include>

<link href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<h1 class="title">블로그 상세화면</h1>

<div>
  <span>작성자</span> 
  <span>${blog.user.email}</span> 
</div>

<div>
  <span>제목</span> 
  <span>${blog.title}</span> 
</div>

<div>
  <span>내용</span> 
  <span>${blog.contents}</span> 
</div>

  
 <hr>
<form id="frm-comment">
  <textarea id="contents" name="contents"></textarea>
  <input type="hidden" name="blogNo" value="${blog.blogNo}">
   <!-- 상세보기는 비로그인일 때도 접근 가능 -> session에 로그인 정보가 없으므로 null값 전달됨 -> 오류 메시지 뜸(500), 화면에 전달되어야 하는 값이 넘어오지 않을 경우 => 이러한 null 값이나 값이 넘어오지 않는 예외의 상황에 대해 처리가 반드시 필요함! -->
  <c:if test="${not empty sessionScope.user}">
    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
  </c:if>
  <button type="button" id="btn-comment-register">댓글등록</button>
</form>  

<hr>

<div id="comment-list"></div>

<script>

const fnRegisterComment = () => {
	$('#btn-comment-register').on('click', (evt) => {
		if('${sessionScope.user}' === '') {
			if(confirm('Sign In 이 필요한 기능입니다. Sign In 할까요?')) {
		    location.href = '${contextPath}/user/signin.page';
		} else {
			 return;  // 로그인 안됨
		 } 
		} else {
			 $.ajax({    // 로그인 됨 (DB로 데이터 저장 -> ajax 처리 필요)
				 // 요청
				 type: 'POST',
				 url: '${contextPath}/blog/registerComment.do', 
				 data: $('#frm-comment').serialize()  // <form> 내부의 모든 입력을 파라마터 형식으로 보낼 때 사용, 입력 요소들은 name 속성을 가지고 있어야 함(본래 파라미터는 name 속성 => 같은 것!)
			 })
		}
	})
}

fnRegisterComment();	
	
</script>


<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



