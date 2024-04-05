<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<link href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<h1 class="title">블로그 작성화면</h1>

<form id="frm-blog-register"
      method="POST" 
      action="${contextPath}/blog/register.do">

  <div>
    <!-- session 정보가 없으면 동작하지 않는 페이지 -->
    <span>작성자</span> 
    <span>${sessionScope.user.email}</span> <!-- 작성자는 email(아이디) : session 에 정보 저장되어 있어 session 에서 꺼냄 -->
  </div>
  
   <div>
    <label for="title">제목</label>
    <input type="text" name="title" id="title">
  </div>

  <div>
    <!-- submit 할 때 전달될 정보 1  : contents -->
    <textarea id="contents" name="contents"></textarea> 
  </div>
  
  <div>
    <!-- submit 할 때 전달될 정보 2 : userNo --> 
    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
    <button type="submit">작성완료</button>
    <a href="${contextPath}/blog/list.do"><button type="button">작성취소</button></a>
  </div>
</form>

<script>

const fnSummernoteEditor = () => {  // ready: 로드(load) 후에 함수를 동작시켜서 <script> 가 맨 하단부에 위치하지 않고 어디에 위치하여도 됨
	   $('#contents').summernote({
	        width: 1024,
	        height: 500,
	        lang: 'ko-KR',
	        callbacks: {
	            onImageUpload: (images)=>{// onImageUpload 라는 이벤트가 발생되었을 때 수행할 함수 | images: 첨부된 이미지가 넘어오는 곳 (첨부된 이미지수 만큼 함수 수행됨)
	               // 비동기 방식을 이용한 이미지 업로드
	               // formData 생성
	               for(let i = 0; i < images.length; i++) {
                  let formData = new FormData();                            // <form> 
                  formData.append('image', images[i]);                      // <input type="file" name="image"> 와 같음 => 즉 form submit 과 동일하게 동작 (file 타입은 Controller 에서 MultipartFile로 받아야 함)
                  fetch('${contextPath}/blog/summernote/imageUpload.do', {  // 첨부 요청 (fetch가 server로 보내서 하나의 하나씩 저장) , /blog => BlogController 주소 
                    method: 'POST',
                    body: formData
                    /*  submit 상황에서는 <form enctype="multipart/form-data"> 필요하지만 fetch 에서는 사용하면 안 된다. 
                    headers: {
                      'Content-Type': 'multipart/form-data'
                    }
                    */
                    })
                    .then(response=>response.json())                                             // 응답을 받아서 json 데이터만 꺼냄 , serviceImpl의 summernoteImageUpload 에서 반환해서 response.json() 에서 받음 => Map.of("src", image url") 형태로 Map 에 저장되어 넘어옴
                    .then(resData=>{                                                             // 여기서 무엇을 할지는 service 측에서 결정되어야 함
                      $('#contents').summernote('insertImage', '${contextPath}' + resData.src);  // 받아온 경로를 insertImage(이미지를 만들어 줌)라는 속성으로 적용해서 등록한 이미지를 textarea 에 보여줌
                                                                                                 // insertImage : summernote 자체에서 제공하는 내장 속성 => 이미지를 삽입해주는 기능을 제공함
                                                                                                 // resData.src : 응답 주소 (image url) 을 insertImage 에 저장함 => /myapp/upload/2024/04/05/file.jpg
                                                                                                 // 최종적으로 summernote 메소드가 제작해주는 것은 <img src="image url"> 이미지 텍스트(태그) 임
        	               
        	               
	                 })
	               }
	             }
	           }
	         })
	       }
        
const fnRegisterBlog = (evt) => {
    if(document.getElementById('title').value === '') {
      alert('제목 입력은 필수입니다.');
      evt.preventDefault();
      return;
    }
  }

  document.getElementById('frm-blog-register').addEventListener('submit', (evt) => {
    fnRegisterBlog(evt);
  })
  fnSummernoteEditor();

</script>


<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



