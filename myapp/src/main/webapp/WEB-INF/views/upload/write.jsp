<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<h1 class="title">업로드 작성화면</h1>

<!--  파일 첨부 content 타입 : multipart/form-data => 작성해주어야 파일 첨부 가능 -->
<form id="frm-upload-register"
      method="POST" 
      enctype="multipart/form-data"   
      action="${contextPath}/upload/register.do">

  <div>
    <span>작성자</span> 
    <span>${sessionScope.user.email}</span> <!-- 작성자는 email(아이디) : session 에 정보 저장되어 있어 session 에서 꺼냄 -->
  </div>

  <!-- submit 할 때 전달될 정보 1  : title -->
  <div>
    <label for="title">제목</label>
    <input type="text" name="title" id="title">
  </div>

  <!-- submit 할 때 전달될 정보 2  : contents -->
  <div>
    <textarea id="contents" name="contents" placeholder="내용을 입력하세요"></textarea> 
  </div>
  
  <!-- files 는 다른 정보들(name)과 다르게 파라미터 받는 방법이 다름 -->
  <div>
    <label for="files">첨부</label>
    <input type="file" name="files" id="files" multiple> <!-- 다중첨부를 위해 multiple 속성 추가 -->
  </div>
  
  <!-- 첨부된 파일명 -->
  <div id="attach-list"></div> 

  <!-- submit 할 때 전달될 정보 3 : userNo -->  
  <div>
    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
    <button type="submit">작성완료</button>
    <a href="${contextPath}/bbs /list.do"><button type="button">작성취소</button></a>
  </div>
</form>

<script>

//제목 필수 입력 스크립트
const fnRegisterUpload = () => {
  document.getElementById('frm-upload-register').addEventListener('submit', (evt) => {
    if(document.getElementById('title').value === '') {
      alert('제목은 필수입니다.');
      evt.preventDefault();
      return;
    }
  })
}
 
// 크기 제한 스크립트 + 첨부 목록 출력 스크립트
const fnAttachCheck = () => {
  document.getElementById('files').addEventListener('change', (evt) => {
    const limitPerSize = 1024 * 1024 * 10;
    const limitTotalSize = 1024 * 1024 * 100;
    let totalSize = 0;
    const files = evt.target.files;
    const attachList = document.getElementById('attach-list');
    attachList.innerHTML = '';
    for(let i = 0; i < files.length; i++){
      if(files[i].size > limitPerSize){
        alert('각 첨부 파일의 최대 크기는 10MB입니다.');
        evt.target.value = '';
        attachList.innerHTML = '';
        return;
      }
      totalSize += files[i].size;
      if(totalSize > limitTotalSize){
        alert('전체 첨부 파일의 최대 크기는 100MB입니다.');
        evt.target.value = '';
        attachList.innerHTML = '';
        return;
      }
      attachList.innerHTML += '<div>' + files[i].name + '</div>';
    }
  })
}

fnRegisterUpload();
fnAttachCheck();

</script>


<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



