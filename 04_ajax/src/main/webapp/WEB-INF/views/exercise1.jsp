<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<style>
  .board {
    width: 200px;
    height: 100px;
    border: 1px solid gray;
    cursor: pointer;
  }
</style>
</head>
<body>

 <div>
    <button type="button" id="btn-list">목록갱신</button>
  </div>

  <hr>
  
  <div id="board-list"></div>

  <script>
  
    const fnBoardList = ()=>{
      $('#btn-list').on('click', (evt)=>{
        $.ajax({
          /* 요청 */
          type: 'GET',
          url: '${contextPath}/ajax1/list.do',
          /* 응답 */
          dataType: 'json',
    			success: (resData)=>{ // 응답 데이터(jason으로 변환된 응답 데이터)는 success 의 매개변수로 전달됨
    				const boardList = $('#board-list');
    				boardList.empty();
    			  $.each(resData, (i, board)=>{ // 인덱스와 요소를 매개변수로 받아옴
    				  boardList.append('<div class="board"><div class="board-no">' + board.boardNo + '</div><div>' + board.title + '</div><div>' + board.contents + '</div></div>'); // ${board.contents} : ES6 템플릿 리터럴	
    				})
    			}
    		})
    	})      	
    }
  fnBoardList();
  
  </script>
  
  <script>
  
     var detailWindow = null; // 너비/높이/top-left
  
     const fnBoardDetail = ()=>{
    	$(document).on('click', '.board', (evt)=>{ // board 는 이벤트에 의해서 발생된 요소로 이벤트 처리할 때 옆 코드와 같이 함
    		// evt.target        : .board 내부 요소 중 실제로 클릭한 요소 
    		// evt.currentTarget : .board 자체
    		const boardNo = $(evt.currentTarget).find('.board-no').text(); // 상세보기를 위한 번호 구하기
    		$.ajax({
    			/* 요청 */
    			type: 'GET',
    			// **** ajax 는 주소와 파라미터와 분리가 가능함 ****
    			url: '${contextPath}/ajax1/detail.do',  // 요청 주소
    		  data :'boardNo=' + boardNo,	          // 요청 파라미터
    		  /* 응답 */
    		 dataType: 'json',
          success: (resData)=>{
            if(detailWindow === null || detailWindow.closed) {              
              detailWindow = window.open('', '', 'width=300,height=200,top=100,left=100');
              detailWindow.document.write('<div>' + resData.boardNo + '</div>');
              detailWindow.document.write('<div>' + resData.title + '</div>');
              detailWindow.document.write('<div>' + resData.contents + '</div>');
    			  } else {
    				  alert('먼저 기존 창을 닫으세요')
    				  detailWindow.focus();  // 창이 이미 열려있는 상태에서 또 창을 열려고 할 때
    			  }
    		  }
    		})    		
    	}) 
     }
     fnBoardDetail();
  </script>
  
</body>
</html>