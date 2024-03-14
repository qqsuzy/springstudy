<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>faq 목록</title>
<script>

/*
 * '${addResult}' 
 *  java attribute 속성으로 넘어오기 때문에 EL로 확인 가능
 *  EL 로 묶을 때 반드시 ''(작은따옴표)로 묶어줘야 값을 인식할 수 있음 
     => 값이 넘어오지 않으면 빈문자열('') 을 전달
 */
  function fnAddResult() {
	 let addResult = '${addResult}'; 
	 if(addResult !== '' && addResult === '1') {
		 alert('정상적으로 등록되었습니다.');
	 }
  }
  fnAddResult();
  
</script>
</head>
<body>

</body>
</html>