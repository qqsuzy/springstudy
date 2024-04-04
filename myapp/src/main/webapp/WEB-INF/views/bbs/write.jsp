<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<h1 class="title">BBS 작성화면</h1>

<form id="frm-bbs-register"
      method="POST" 
      action="${contextPath}/bbs/register.do">

  <div>
    <span>작성자</span> 
    <span>${sessionScope.user.email}</span> <!-- 작성자는 email(아이디) : session 에 정보 저장되어 있어 session 에서 꺼냄 -->
  </div>

  <div>
    <!-- submit 할 때 전달될 정보 1  : contents -->
    <textarea id="contents" name="contents" placeholder="내용을 입력하세요"></textarea> 
  </div>
  
  <div>
    <!-- submit 할 때 전달될 정보 2 : userNo --> 
    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
    <button type="submit">작성완료</button>
    <a href="${contextPath}/bbs/list.do"><button type="button">작성취소</button></a>
  </div>
</form>

<script>


const fnRegisterBbs = (evt) => {
		if(document.getElementsById('contents').value === '') {
			alert('내용을 입력해주세요.')
			evt.preventDefault();
			return;
		}		
	}

  document.getElementsById('frm-bbs-register').addEventListener('submit', (evt)) => {
	fnRegisterBbs(evt);
}

fnRegisterBbs();



</script>


<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



