<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp">
  <jsp:param value="${upload.uploadNo}번 블로그" name="title"/>
</jsp:include>

<link href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<h1 class="title">업로드 상세화면</h1>

<div>
  <span>작성자</span> 
  <span>${upload.user.email}</span> 
</div>

<div>
  <span>제목</span> 
  <span>${upload.title}</span> 
</div>

<div>
  <span>내용</span> 
  <span>${upload.contents}</span> 
</div>

<div>
  <span>작성일자</span>
  <span>${upload.createDt}</span>
</div>

<div>
  <span>최종수정일</span>
  <span>${upload.modifyDt}</span>
</div>

<div>
  <c:if test="${not empty sessionScope.user}">  
    <c:if test="${sessionScope.user.userNo == upload.user.userNo}">
      <form id="frm-btn" method="POST">  
        <input type="hidden" name="uploadNo" value="${upload.uploadNo}">
        <button type="button" id="btn-edit" class="btn btn-warning btn-sm">편집</button> <!-- btn-edit 를 누르면 form-btn 에 action을 만들고 submit 처리함 -->
        <button type="button" id="btn-remove" class="btn btn-danger btn-sm">삭제</button>
      </form>
    </c:if>
  </c:if>
</div>

<hr>

<!-- 첨부 목록 -->
<h3>첨부 파일 다운로드</h3>

<div>
  <!-- attachList : model에 실어서 service에서 전달됨 -> EL로 꺼냄 -->
  <c:if test="${empty attachList}">
    <div>첨부 없음</div>
  </c:if>
  <c:if test="${not empty attachList}">
    <c:forEach items="${attachList}" var="attach" >             <!-- var: 변수명 -->
       <div class="attach" data-attach-no="${attach.attachNo}">
         <!-- 썸네일 있을 때 -->
         <c:if test="${attach.hasThumbnail == 1 }">
           <img src="${contextPath}${attach.uploadPath}/s_${attach.filesystemName}"> 
         </c:if>
         <!-- 썸네일이 없을 때 (기본이미지 첨부 됨) -->
         <c:if test="${attach.hasThumbnail == 0 }">
           <img src="${contextPath}/resources/images/attach.png" width="96px"> <!-- contextPath = webapp 를 의미함 -->
         </c:if>
         <!-- 클릭 시 개별 다운로드 -->
         <a ${attach.originalFilename}></a>       
       </div>
    </c:forEach>
    <div>
      <!-- 클릭 시 전체 다운로드 -->
      <a id="download-all" href="${contextPath}/upload/downloadAll.do?uploadNo=${upload.uploadNo}">모두 다운로드</a> <!-- 업로드 상세보기 -> uploadNo는 upload에 들어있음 -->
    </div>
  </c:if>
</div>

<script>
  
  /* 
      개별 다운로드 : 각 파일의 경로만 알면 됨
      전체 다운로드 : 게시판의 번호 (uploadNo) 가 필요함
  */

const fnDownload = () => {
  $('.attach').on('click', (evt) => {
	  if(confirm('해당 첨부 파일을 다운로드 할까요?')) {
		  location.href = '${contextPath}/upload/download.do?attachNo=' + evt.currentTarget.dataset.attachNo; // evt.target : <div class="attach"> , attachNo 는 data속성에 저장해둠
	  }
  })
}
  
const fnDownloadAll = () => {
	document.getElementById('download-all').addEventListener('click', (evt) => {
		if(!confirm('모두 다운로드 할까요?')) { //  다운로드 취소했을 경우 이벤트 막기
		  evt.preventDefault();	
		  // return;            => 다른 코드 실행을 막기 위함인데 현재 함수 내 다른 코드가 없기 떄문에 있어도되고 없어도 됨
		}
	})
}  

//전역 객체
var frmBtn = document.getElementById('frm-btn');

<!-- btn-edit 를 누르면 form-btn 에 action을 만들고 submit 처리함 -->
const fnEditUpload = () => {
  document.getElementById('btn-edit').addEventListener('click', (evt) => {
    frmBtn.action = '${contextPath}/upload/edit.do';
    frmBtn.submit();
  })
}

const fnAfterModifyUpdate = () => {
  const updateCount = '${updateCount}';
  if(updateCount !== '') {
    if(updateCount === '1') {
      alert('게시글이 수정되었습니다.');
    } else {
      alert('게시글이 수정되지 않았습니다.');
    }
  }
}

fnDownload();
fnDownloadAll();
fnEditUpload();
fnAfterModifyUpdate();
	
</script>

<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



