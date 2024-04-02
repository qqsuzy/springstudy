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

<div class="m-3">

  <h1>Sign Up</h1>
  <!-- BootStrap 으로 스타일 적용 (class) --> 
  <form method="POST"
        action="${contextPath}/user/signup.do"
        id="frm-signup">
  
    <div class="row mb-3"> <!-- margin bottom 3 => 숫자값으로 크기 변경 가능 -->
      <label for="inp-email" class="col-sm-3 col-form-label">아이디</label> <!-- 입력란은 id가 inp로 시작 -->
      <div class="col-sm-6"><input type="text" id="inp-email" name="email" class="form-control" placeholder="example@example.com"></div>
      <div class="col-sm-3"><button type="button" id="btn-code" class="btn btn-primary">인증코드받기</button></div>
      <div class="col-sm-3"id="msg-email"></div>
    </div>
    <div class="row mb-3">
      <label for="inp-code" class="col-sm-3 col-form-label">인증코드</label>
      <!-- 이메일로 보낸 인증코드를 입력하는 텍스트창 (controller로 보내서 service 처리할 필요 x, front 단에서 javascript로 처리) -->
      <div class="col-sm-6"><input type="text" id="inp-code" class="form-control" placeholder="인증코드입력" disabled></div> <!-- 입력란 막아두기(disabled) 디폴트 -> 인증성공 후 입력란 풀림 -->
      <div class="col-sm-3"><button type="button" id="btn-verify-code" class="btn btn-primary" disabled>인증하기</button></div>
    </div>
    
    <hr class="my-3">
  
    <div class="row mb-3">
      <label for="inp-pw" class="col-sm-3 col-form-label">비밀번호</label>
      <div class="col-sm-6"><input type="password" id="inp-pw" name="pw" class="form-control"></div>
      <div class="col-sm-3"></div>
      <div class="col-sm-9" id="msg-pw"></div>
    </div>
    <div class="row mb-3">
      <label for="inp-pw2" class="col-sm-3 col-form-label">비밀번호 확인</label>
      <div class="col-sm-6"><input type="password" id="inp-pw2" class="form-control"></div>
      <div class="col-sm-3"></div>
      <div class="col-sm-9" id="msg-pw2"></div>
    </div>
    
    <hr class="my-3">
    
    <div class="row mb-3">
      <label for="inp-name" class="col-sm-3 col-form-label">이름</label>
      <div class="col-sm-9"><input type="text" name="name" id="inp-name" class="form-control"></div>
      <div class="col-sm-3"></div>
      <div class="col-sm-9" id="msg-name"></div>
    </div>

    <div class="row mb-3">
      <label for="inp-mobile" class="col-sm-3 col-form-label">휴대전화번호</label>
      <div class="col-sm-9"><input type="text" name="mobile" id="inp-mobile" class="form-control"></div>
      <div class="col-sm-3"></div>
      <div class="col-sm-9" id="msg-mobile"></div>
    </div>

    <div class="row mb-3">
      <label class="col-sm-3 form-label">성별</label>
      <div class="col-sm-3">
        <input type="radio" name="gender" value="none" id="rdo-none" class="form-check-input" checked>
        <label class="form-check-label" for="rdo-none">선택안함</label>
      </div>
      <div class="col-sm-3">
        <input type="radio" name="gender" value="man" id="rdo-man" class="form-check-input">
        <label class="form-check-label" for="rdo-man">남자</label>
      </div>
      <div class="col-sm-3">
        <input type="radio" name="gender" value="woman" id="rdo-woman" class="form-check-input">
        <label class="form-check-label" for="rdo-woman">여자</label>
      </div>
    </div>
    
    <hr class="my-3">
    
    <div class="form-check mb-3">
      <input type="checkbox" class="form-check-input" id="chk-service">
      <label class="form-check-label" for="chk-service">서비스 이용약관 동의(필수)</label>
    </div>
    <div>
      <textarea rows="5" class="form-control">본 약관은 ...</textarea>
    </div>
    
    <div class="form-check mb-3">
      <input type="checkbox" name="event" class="form-check-input" id="chk-event"> <!-- 서버 단에서 이벤트 알림 동의 여부 확인이 필요하므로 name 값 필요함 -->
      <label class="form-check-label" for="chk-event">
        이벤트 알림 동의(선택)
      </label>
    </div>
    <div>
      <textarea rows="5" class="form-control">본 약관은 ...</textarea>
    </div>
    
    <hr class="my-3">
    
    <div class="m-3">
      <button type="submit" id="btn-signup" class="btn btn-primary">가입하기</button>
    </div>
    
  </form>

</div>

<script>

// 체크 여부 전역변수
var emailCheck = false; 
var passwordCheck = false;
var passwordConfirm = false;
var nameCheck = false;
var mobileCheck = false;
var agreeCheck = false;

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
	    .then(resData=>{  // {"enableEmail": true}
	      if(resData.enableEmail){
	        fetch('인증코드전송요청', {})
	        .then(response=>response.json())
	        .then(resData=>{  // {"code": "123asb"}    -> 이메일로 인증코드 보낸 후 어떤 인증코드를 보냈는지 받아와야함
	          if(resData.code === 인증코드입력값)      -> 인증코드 일치 확인
	        })
	      }
	    })
	  */
	
	
	  let inpEmail = document.getElementById('inp-email');
	  let regEmail = /^[A-Za-z0-9-_]{2,}@[A-Za-z0-9]+(\.[A-Za-z]{2,6}){1,2}$/; // ^ : 시작 , $ : 끝
	  //이메일 정규식 체크 실패한 경우
	  if(!regEmail.test(inpEmail.value)){
	    alert('이메일 형식이 올바르지 않습니다.');
	    emailCheck = false;
	    return;
	  }
	  // 이메일 중복 체크 요청
	  // view (email 보냄, ajax 처리) -> controller -> service -> mapper 에서 select 해서 이메일 중복 체크함 
	  // POST 방식(body에 JSON 데이터를 실어서 보냄 => controller에서 @RequestBody 필요, Map으로 받음(jackson 라이브러리가 JSON 데이터를 받아서 Map으로 저장시킴)), email 을 JSON 데이터로 만들어 전송
	  // fetch(주소, {옵션});      ***** 09_RESTful 의 member.js 내 fnRegisterMember 함수(ajax)와 비교하기
	  
	  fetch(fnGetContextPath() + '/user/checkEmail.do', {
	    method: 'POST',
	    headers: {
	      'Content-Type': 'application/json'
	    },
	    body: JSON.stringify({    // javascript 객체를 넣으면 JSON으로 변환
	      'email': inpEmail.value
	    })
	  })
	  //.then( (response) => { return response.json(); } ) 과 같은 코드( ↓ 생략 ver 코드)
	  .then(response => response.json())  // 받아온 응답 객체 데이터에서 json 만 꺼냄 (response에는 contentType 등 여러 데이터가 들어있음)
	  .then(resData => {                   // json이 promise 로 묶여서 넘어옴 => 어떤 응답이 먼저 넘어올지 모름, promise 가 내장된 fetch 사용하여 응답을 순차적으로 처리함(요청/응답 순서를 지켜야 할 경우 반드시 promise가 필요, promise는 ajax가 연속으로 발생될 때) => json 데이터를 받는 then() 메소드
		  // 이메일 중복 체크 통과 O
	    if(resData.enableEmail){
	      document.getElementById('msg-email').innerHTML = ''; // 이메일 중복 통과 후 메시지는 빈문자열로 지워주기 ('이미 사용 중인 이메일입니다' 뜨지 않도록 함)
	      fetch(fnGetContextPath() + '/user/sendCode.do', {    // 이메일 중복 체크 통과 -> 인증코드 만들어서 이메일로 전송
	        method: 'POST',
	        headers: {
	          'Content-Type': 'application/json'
	        },
	        body: JSON.stringify({     // javascript 객체를 넣으면 JSON으로 변환
	          'email': inpEmail.value  // email : 받는 사람(map에 들어있음)
	        })
	      })
	      .then(response => response.json())
	      .then(resData => {  // resData = {"code": "123qaz"}
	        alert(inpEmail.value + '로 인증코드를 전송했습니다.'); // 1) 인증코드 전송
	        let inpCode = document.getElementById('inp-code');     
	        let btnVerifyCode = document.getElementById('btn-verify-code');
	        inpCode.disabled = false;                              // 2) disabled 풀어주기 (인증코드 입력 가능해짐)
	        btnVerifyCode.disabled = false;
	        btnVerifyCode.addEventListener('click', (evt) => {
	          if(resData.code === inpCode.value) {     // 인증번호 코드와 사용자가 입력한 코드가 일치할 경우
	            alert('인증되었습니다.');       
	            emailCheck = true;
	          } else {
	            alert('인증되지 않았습니다.');
	            emailCheck = false;
	          }
	        })
	      })
	    // 이메일 중복 체크 통과 X  
	    } else {
	      document.getElementById('msg-email').innerHTML = '이미 사용 중인 이메일입니다.';  // <div id="msg-email"></div> 에 메시지 뿌려줌
	      emailCheck = false;
	      return;
	    }
	  })
	}
	  
// 비밀번호 체크
const fnCheckPassword = () => {
	// 비밀번호 4 ~ 12자, 영문/숫자/특수문자 중 2개 이상 포함 : validCount
  let inpPw = document.getElementById('inp-pw');
  // validCount : 몇가지 규칙을 지켰는지 판단하는 변수 , test : 정규식 검사 메소드
  let validCount = /[A-Za-z]/.test(inpPw.value)     // 영문 포함되어 있으면 true (JavaScript 에서 true 는 숫자 1 같다.)
                 + /[0-9]/.test(inpPw.value)        // 숫자 포함되어 있으면 true
                 + /[^A-Za-z0-9]/.test(inpPw.value) // 영문/숫자가 아니면 true | ^ 사용해서 영문과 숫자를 제외한 문자들(특수문자) 로 표현함 => 캐럿은 [] 대괄호 안에 들어오면 제외, 밖으로 나가면 ~로 시작
  // passwordLength : 비밀번호 길이 변수
  let passwordLength = inpPw.value.length;                
  passwordCheck = passwordLength >= 4      
                && passwordLength <= 12
                && validCount >= 2;
  let msgPw = document.getElementById('msg-pw');              
  if(passwordCheck) {
	 msgPw.innerHTML = '사용 가능한 비밀번호입니다.'; 
  } else {
   msgPw.innerHTML = '비밀번호 4 ~ 12자, 영문/숫자/특수문자 중 2개 이상 포함';	  
  }
}

// 비밀번호 확인 체크
const fnConfirmPassword = () => {
	let inpPw = document.getElementById('inp-pw');
	let inpPw2 = document.getElementById('inp-pw2');
	passwordConfirm = (inpPw.value != '')
	               && (inpPw.value === inpPw2.value) // 입력 필수 && 비밀번호와 비밀번호 확인 값이 동일
  let msgPw2 = document.getElementById('msg-pw2');
	if(passwordConfirm) {
		msgPw2.innerHTML = '';
	} else {
		msgPw2.innerHTML = '비밀번호 입력을 확인하세요.';
	}
}

// 이름 체크
const fnCheckName = () => {
	let inpName = document.getElementById('inp-name');
	let name = inpName.value;
	let totalByte = 0;
	for(let i = 0; i < name.length; i++) { // name.length : 이름 입력 값의 길이
		// ASCII Code  : 128 (0~127) => 아스키 코드에 포함되는 모든 문자는 1바이트 , 한글은 코드 내에 포함되지 않기 때문에 2바이트
		if(name.charCodeAt(i) > 127) { // charCodeAt : 코드값 확인하는 메소드 , 코드값이 127 초과이면 한 글자 당 2바이트 처리함
			totalByte += 2;
		} else {
			totalByte++;
		}
	}
	nameCheck = (totalByte <= 100); // DB에 이름 100 바이트로 잡아둠
	let msgName = document.getElementById('msg-name');
	if(!nameCheck) {
		msgName.innerHTML = '이름은 100 바이트를 초과할 수 없습니다.';
	} else {
		msgName.innerHTML = '';	
	}
}

// 휴대전화 체크
const fnCheckMobile = () => {
	let inpMobile = document.getElementById('inp-mobile');
	let mobile = inpMobile.value;
	mobile = mobile.replaceAll(/[^0-9]/g, '');  // 숫자가 아닌 문자들을 빈문자열로 재배치 함(정규식 체크할 때 - 하이픈과 같은 숫자 외의 문자들 지우고 검사하기 위함) , replaceAll 은 정규식 or 문자열도 가능 (global 처리 필요)
	mobileCheck = /^010[0-9]{8}$/.test(mobile); // 입력한 mobile 값을 정규식 검사 진행 (정규식 검사 통과 : true 아니면 false 저장)
	let msgMobile = document.getElementById('msg-mobile');
	if(mobileCheck) {
		msgMobile.innerHTML = '';
	} else {
		msgMobile.innerHTML = '휴대전화를 확인하세요.';
	}
}

// 약관동의 체크 (회원가입 버튼 클릭시 함수 동작 : submit)
const fnCheckAgree = () => {
	let chkService = document.getElementById('chk-service');
	agreeCheck = chkService.checked;  // 체크 되어 있으면 true, 아니면 false 값 저장
}



// 회원가입
const fnSignup = () => {
    document.getElementById('frm-signup').addEventListener('submit', (evt) => {
    	fnCheckAgree();          // 약관 동의 여부 확인
      if(!emailCheck) {
        alert('이메일을 확인하세요.');
        evt.preventDefault();  // submit 방지 
        return;                // 함수 종료     => 이벤트 방지와 함수 종료 코드를 각각 넣어줘야함 (하나의 set!)
      } else if (!passwordCheck || !passwordConfirm) { // 비밀번호 체크 또는 비밀번호 확인을 하지 않았을 때
    	  alert('비밀번호를 확인하세요.');
        evt.preventDefault();  
        return;
      } else if(!nameCheck) {
    	  alert('이름을 확인하세요.');
        evt.preventDefault();  
        return;
      } else if(!mobileCheck) {
    	  alert('휴대전화를 확인하세요.');
        evt.preventDefault();  
        return;
      } else if(!agreeCheck) {
    	  alert('서비스 약관에 동의해야 서비스를 이용할 수 있습니다.');
    	  evt.preventDefault();
    	  return;
    	}
    })
  }

document.getElementById('btn-code').addEventListener('click', fnCheckEmail);
document.getElementById('inp-pw').addEventListener('keyup', fnCheckPassword);  // 입력 중 : keyup , 입력 완료 후 : blur => 둘 중 하나 선택 가능
document.getElementById('inp-pw2').addEventListener('blur', fnConfirmPassword);
document.getElementById('inp-name').addEventListener('blur', fnCheckName);
document.getElementById('inp-mobile').addEventListener('blur', fnCheckMobile);
fnSignup();



</script>
  
</body>
</html>