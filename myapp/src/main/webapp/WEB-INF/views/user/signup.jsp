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
   
   <h1>Sign Up</h1>
   <!-- BootStrap 으로 스타일 적용 (class) --> 
   <form method="POST"
         action="${contextPath}/user/signup.do"
         id="frm-signup">
     <div class="mb-3"> <!-- margin bottom 3 => 숫자값으로 크기 변경 가능 -->
       <label for="email">아이디</label>
       <input type="text" id="email" name="email" placeholder="example@example.com">
       <!-- 인증코드 버튼 -->
       <button type="button" id="btn-code" class="btn btn-success">인증코드받기</button>
       <div id="msg-email"></div>
       <div class="mb-3">
         <!-- 이메일로 보낸 인증코드를 입력하는 텍스트창 (controller로 보내서 service 처리할 필요 x, front 단에서 javascript로 처리) -->
         <input type="text" id="code" placeholder="인증코드입력" disabled> <!-- 입력란 막아두기(disabled) 디폴트 -> 인증성공 후 입력란 풀림 -->
         <button type="button" id="btn-verify-code" class="btn btn-success">인증하기</button>
       </div>
     </div>                 
   </form>

<script>
const fnGetContextPath = ()=>{
	  const host = location.host;  /* localhost:8080 */
	  const url = location.href;   /* http://localhost:8080/mvc/getDate.do */
	  const begin = url.indexOf(host) + host.length;
	  const end = url.indexOf('/', begin + 1);
	  return url.substring(begin, end);
	}
// 인증코드 받아서 이메일 중복 체크 함수	
const fnCheckEmail = ()=>{
	

	  /*
	    new Promise((resolve, reject) => {
	      $.ajax({
	        url: '이메일중복체크요청'
	      })
	      .done(resData => {
	        if(resData.enableEmail){  ->  resolve 가 then 호출 , reject 가 catch 호출
	          resolve();
	        } else {
	          reject();
	        }
	      })
	    })
	    .then(() => {
	      $.ajax({
	        url: '인증코드전송요청'
	      })
	      .done(resData => {
	        if(resData.code === 인증코드입력값)
	      })
	    })
	    .catch(() => {
	      
	    })
	    
	   - 요청에 대한 응답이 오지도 않았는데 응답 처리를 해버리기 때문에
        promise 처리가 필요함, promise 객체를 만들어서 넣기
     - ajax 처리 방법이 더 복잡하므로 fetch - then으로 처리하는 것을 추천함!
	  
	  */
	  
	  /*
	    fetch('이메일중복체크요청', {})
	    .then(response=>response.json())
	    .then(resData=>{
	      if(resData.enableEmail){
	        fetch('인증코드전송요청', {})
	        .then(response=>response.json())
	        .then(resData=>{  // {"code": "123asb"}    -> 이메일로 인증코드 보낸 후 어떤 인증코드를 보냈는지 받아와야함
	          if(resData.code === 인증코드입력값)      -> 인증코드 일치 확인
	        })
	      }
	    })
	  */
	
	
  let email = document.getElementById('email');
  let regEmail = /^[A-Za-z0-9-_]{2,}@[A-Za-z0-9]+(\.[A-Za-z]{2,6}){1,2}$/; // ^ : 시작 , $ : 끝
  //이메일 정규식 체크 실패한 경우
  if(!regEmail.test(email.value)) { 
	  alert('이메일 형식이 올바르지 않습니다.');
	  return;  
  }
  // 이메일 중복 체크 요청
  // view (email 보냄, ajax 처리) -> controller -> service -> mapper 에서 select 해서 이메일 중복 체크함 
  // POST 방식(body에 JSON 데이터를 실어서 보냄 => controller에서 @RequestBody 필요, Map으로 받음(jackson 라이브러리가 JSON 데이터를 받아서 Map으로 저장시킴)), email 을 JSON 데이터로 만들어 전송
  // fetch(주소, {옵션});      ***** 09_RESTful 의 member.js 내 fnRegisterMember 함수(ajax)와 비교하기
  fetch(fnGetContextPath() +'/user/checkEmail.do', {
	  method: 'POST',
	  headers: {
		  'Content-Type': 'application/json'
	  },
	  body: JSON.stringify({ // javascript 객체를 넣으면 JSON으로 변환
		  'email': email.value
	  })
  })
  //.then( (response) => { return response.json(); } ) 과 같은 코드( ↓ 생략 ver 코드)
  .then(response=>response.json()) // 받아온 응답 객체 데이터에서 json 만 꺼냄 (response에는 contentType 등 여러 데이터가 들어있음)
  .then(resData=>{ // json이 promise 로 묶여서 넘어옴 => 어떤 응답이 먼저 넘어올지 모름, promise 가 내장된 fetch 사용하여 응답을 순차적으로 처리함(요청/응답 순서를 지켜야 할 경우 반드시 promise가 필요, promise는 ajax가 연속으로 발생될 때) => json 데이터를 받는 then() 메소드
	  // 이메일 중복 체크 통과 O
	  if(resData.enableEmail) {  
		  fetch(fnGetContextPath() + '/user/sendCode.do', {  // 이메일 중복 체크 통과 -> 인증코드 만들어서 이메일로 전송 
			  method: 'POST',
			  headers: {
			    'Content-Type': 'application/json'
			   },
			   body: JSON.stringify({ // javascript 객체를 넣으면 JSON으로 변환
			     'email': email.value
			  })
		  }); 
		// 이메일 중복 체크 통과 X		  
	  } else {  
		  document.getElementById('msg-email').innerHTML = '이미 사용 중인 이메일입니다.'; // <div id="msg-email"></div> 에 메시지 뿌려줌
		  return;
	  }
  }) 
}

document.getElementById('btn-code').addEventListener('click', fnCheckEmail);




</script>
  
</body>
</html>