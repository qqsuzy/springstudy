<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<h1 class="title">업로드 편집화면</h1>

<form id="frm-upload-modify"
      method="POST" 
      action="${contextPath}/upload/modify.do">

  <div>
    <span>작성자</span> 
    <span>${sessionScope.user.email}</span> <!-- 작성자는 email(아이디) : session 에 정보 저장되어 있어 session 에서 꺼냄 -->
  </div>
  <div>
    <span>작성일자</span> 
    <span>${upload.createDt}</span> 
  </div>
  <div>
    <span>최종수정일</span> 
    <span>${upload.modifyDt}</span>
  </div>
  <!-- 수정가능한 text 정보 : title, contents -->
  <!-- <form>에서 submit 되면  전달되는 값 : title , contents, uploadNo -->
  <div>
    <label for="title">제목</label>
    <input type="text" name="title" id="title" value="${upload.title}"> <!-- 기존에 입력한 제목이 나와있어서(value 값으로 넣음 => controller에서 "upload" 라는 이름으로 model에 저장되어 넘어옴) 그것을 수정하도록 구현 -->
  </div>

  <div>
    <textarea id="contents" name="contents" >${upload.contents}</textarea> 
  </div>

  <div>
    <input type="hidden" name="uploadNo" value="${upload.uploadNo}">
    <button type="submit">수정완료</button>
    <a href="${contextPath}/bbs /list.do"><button type="button">수정취소</button></a>
  </div>
</form>

 <!-- files 는 다른 정보들(name)과 다르게 파라미터 받는 방법이 다름 -->
  <div>
    <label for="files">첨부</label>
    <input type="file" name="files" id="files" multiple> <!-- 다중첨부를 위해 multiple 속성 추가 -->
  </div>

<!-- 첨부된 파일명 -->
<div id="attach-list"></div>

<script>

// 첨부 목록 가져와서 <div id="attach-list"></div> 에 표시하기
const fnAttachList = () => {
	fetch('${contextPath}/upload/attachList.do?uploadNo=${upload.uploadNo}', {   // (주소, {옵션})  => GET 방식은 주소창에 데이터를 전송하므로 주소값에 uploadNo 포함시켜 전달
		method: 'GET'
	})
	.then(response => response.json())  // .then(response => return response.json()) 와 같음 -> 생략 ver
	.then(resData => {                  // 받아온 데이터도 promise 로 받아오기 때문에 다시 then 으로 resData로부터 데이터 꺼냄 , resData = {"attachList": []} => 배열로 꺼내짐
		
		// 상세보기(detail.jsp) 와 동일하게 파일 첨부 목록 뿌리기    =>  detail.jsp 에서는 mvc 로 edit.jsp 에서는 ajax 로 처리함
		let divAttachList = document.getElementById('attach-list');
		divAttachList.innerHTML = '';        // 초기화
		const attachList = resData.attachList;
	  for(let i = 0; i < attachList.length; i++) {
		  const attach = attachList[i];
		  let str = '<div class="attach">';
		  if(attachList[i].hasThumbnail === 0) {  // 썸네일 없을 때 (기본이미지 보여줌)
			  str += '<img src="${contextPath}/resources/images/attach.png" width="96px">';
		  } else {                                // 썸네일 있을 때
			  str += '<img src="${contextPath}'+ attach.uploadPath + '/s_' + attach.filesystemName + '">';
		  } 
		  str += '<span>' + attach.originalFilename + '</span>';  // ajax 으로 <span> 으로 태그 만들어 줌 (<a> 태그 x)
		  str += '</div>';
		  divAttachList.innerHTML += str;
	  }
	})
}

//제목 필수 입력 스크립트'attach-list-list'pload = () => {
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
fnAttachList();

</script>


<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



