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

 <form method="POST"
       action="${contextPath}/board/register.do"> 
    <div>
      <textarea id="contents" name="contents"></textarea>
    </div>
    <div>
      <button type="submit">전송</button> <!-- type은 submit 디폴트(생략 가능) -->
    </div>      
 </form>  
 
 <script>
 
 $(document).ready(function(){   // ready: 로드(load) 후에 함수를 동작시켜서 <script> 가 맨 하단부에 위치하지 않고 어디에 위치하여도 됨
	 $('#contents').summernote({
        width: 1024,
        height: 500,
        lang: 'ko-KR',
        callbacks: {
        	 onImageUpload: (images)=>{// onImageUpload 라는 이벤트가 발생되었을 때 수행할 함수 | images: 첨부된 이미지들
          		// 비동기 방식을 이용한 이미지 업로드
          		// formData 생성
          	  for(let i = 0; i < images.length; i++) {
          	  	let formData = new FormData();
          		  formData.append('image', images[i]);
      	    	  fetch('${contextPath}/summernote/imageUpload.do', {
      	    		  method: 'POST',
      	    		  body: formData
      	    	  }).then(response=>response.json()) // 응답을 받아서 json 데이터만 꺼냄
      	    			.then(resData=>{          // 여기서 무엇을 할지는 service 측에서 결정되어야 함
      	    				 $('#contents').summernote('insertImage', resData.src); // 받아온 경로를 insertImage(이미지를 만들어 줌)라는 속성으로 적용해서 등록한 이미지를 textarea 에 보여줌
      	    				                                                        // insertImage : summernote 자체에서 제공하는 내장 속성 => 이미지를 삽입해주는 기능을 제공함
      	    			});
      	    	  }
	    	  
	    	  /*
	    	  $ajax({
	    		  type: 'POST',
	    		  url: ${contextPath}/summbernot/imageUpload.do,
	    		  data: formData
	    		  contentType: false,
	    		  processData: false
	    	  })
	    	  */
	    	}
	    }
   });
 })
 </script>  
  
</body>
</html>