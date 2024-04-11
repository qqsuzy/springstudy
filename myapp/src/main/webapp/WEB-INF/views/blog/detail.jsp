<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp">
  <jsp:param value="${blog.blogNo}번 블로그" name="title"/>
</jsp:include>

<link href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<h1 class="title">블로그 상세화면</h1>

<div>
  <span>작성자</span> 
  <span>${blog.user.email}</span> 
</div>

<div>
  <span>조회수</span> 
  <span>${blog.hit}</span> 
</div>

<div>
  <span>제목</span> 
  <span>${blog.title}</span> 
</div>

<div>
  <span>내용</span> 
  <span>${blog.contents}</span> 
</div>

<div>
 <c:if test="${sessionScope.user.userNo == blog.user.userNo}">
   <form id="frm-btn" method="POST">  
      <input type="hidden" name="blogNo" value="${blog.blogNo}">
      <button type="button" id="btn-edit" class="btn btn-warning btn-sm">편집</button>
      <button type="button" id="btn-remove" class="btn btn-danger btn-sm">삭제</button>
    </form>
 </c:if>
</div>
  
 <hr>
<!--  댓글 작성창 -->
<form id="frm-comment">
  <textarea id="contents" name="contents"></textarea>
  <input type="hidden" name="blogNo" value="${blog.blogNo}">
   <!-- 상세보기는 비로그인일 때도 접근 가능 -> session에 로그인 정보가 없으므로 null값 전달됨 -> 오류 메시지 뜸(500), 화면에 전달되어야 하는 값이 넘어오지 않을 경우 => 이러한 null 값이나 값이 넘어오지 않는 예외의 상황에 대해 처리가 반드시 필요함! -->
  <c:if test="${not empty sessionScope.user}">
    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
  </c:if>
  <button type="button" id="btn-comment-register">댓글등록</button>
</form>  

<hr>

<!--  댓글 목록
 -->
<div id="comment-list"></div>
<div id="paging"></div>

<script>

//로그인 체크
const fnCheckSignin = () => {
    if('${sessionScope.user}' === '') {  // session에 저장된 유저가 없을 때
      if(confirm('Sign In 이 필요한 기능입니다. Sign In 할까요?')) {
        location.href = '${contextPath}/user/signin.page';
      }
    }
  }

// 댓글 작성
const fnRegisterComment = () => {
	$('#btn-comment-register').on('click', (evt) => {
		fnCheckSignin();  // 댓글을 클릭했을 때, 로그인 여부 체크
		 $.ajax({         // 로그인 됨 (DB로 데이터 저장 -> ajax 처리 필요)
			 // 요청
			 type: 'POST',
			 url: '${contextPath}/blog/registerComment.do', 
			 data: $('#frm-comment').serialize(),  // <form> 내부의 모든 입력을 파라마터 형식으로 보낼 때 사용, 입력 요소들은 name 속성을 가지고 있어야 함(본래 파라미터는 name 속성 => 같은 것!) -> 받을 때는 request, @requestparam , command 객체로 받을 수 있음
			 // 응답
			 dataType: 'json',
			 success: (resData) => {  // resData = {"insertCount": 1}
			   if(resData.insertCount === 1) {
				   alert('댓글이 등록되었습니다.');
				   $('#contents').val('');   // 댓글 등록 완료 후 댓글창(textarea) 초기화 시킴 
				   // 댓글 목록 보여주는 함수 호출 (댓글 등록 후 바로 댓글목록 보여줌)
				   fnCommentList(); 
			   } else {
				   alert('댓글 등록이 실패했습니다.');
			   }
			 },
			 error: (jqXHR) => {
				 alert(jqXHR.statusText + '(' + jqXHR.status + ')');
			 }
		 })
	})
}

// 전역 변수
var page = 1;  // 전역 변수로 잡은 이유는 getAsyncPaging 메소드 때문

// 댓글 목록
const fnCommentList = () => {
	$.ajax({
		type: 'GET',
		url: '${contextPath}/blog/comment/list.do',
		data: 'blogNo=${blog.blogNo}&page=' + page,
		dataType: 'json',
		success: (resData) => { // resData = {"commentList": [], "paging": "< 1 2 3 4 5 >"}
		  // 목록, 페이징 초기화 => 목록 가져오기 전에 가장 먼저 해야함
		  let commentList = $('#comment-list');
		  let paging = $('#paging');
		  commentList.empty();  // empty() 는 jquery 객체 -> commentList , paging 은 jQuery 객체를 담고 있음
		  paging.empty();
		  if(resData.commentList.length === 0) {  // 목록의 길이가 0 -> 댓글이 없다는 의미
			  commentList.append('<div>첫 번 째 댓글 주인공이 되어 보세요</div>');
		    paging.empty();
		    return;  // else 를 대체하는 기능을 함 => if문에 걸리고 return 을 만날 경우 바로 함수를 빠져나기 때문
		  }
		  $.each(resData.commentList, (i, comment) => {
			  let str = '';
			  // 댓글은 들여쓰기
			  if(comment.depth === 0) {  // 원글과 댓글은 depth 로 구분지음 -> depth = 0 : 원글
				  // 댓글 여는 <div>
				  str += '<div>';
			  } else {  // depth = 0 : 댓글
				  str += '<div style="padding-left: 32px;">'; // 16px : 한글자로 약 2글자 정도 들여쓰기 함
			  }
			  // 댓글 내용 표시
			  str += '<span>'; 
			  str += comment.user.email;                                           // 작성자
			  str += '(' + moment(comment.createDt).format('YYYY.MM.DD.') + ')';   // 작성일자
			  str += '</span>';
			  str += '<div>' + comment.contents + '</div>';                        // 댓글내용
			  // 답글 버튼 (원글(0)에만 답글 버튼 활성화됨)
			  if(comment.depth === 0) { 
				  str += '<button type="button" class="btn btn-success btn-reply">답글</button>';
			  }
			  // 삭제 버튼 (내가 작성한 댓글에만 삭제 버튼이 생성됨)
			  if(Number('${sessionScope.user.userNo}') === comment.user.userNo) { // 로그인 정보는 session에 들어가 있음 -> 세션의 로그인 정보와 댓글 작성자와 동일한지 비교
				                                                                    // 값은 같은데 타입이 다름 -> session에 저장된 userNo는 문자열, comment에 저장된 userNo는 숫자로 Number 로 캐스팅 처리함 (=== 는 타입과 값 비교로 == 로 변경하여 값만 비교하도록 수정하여도 됨)
				  str += '<button type="button" class="btn btn-danger btn-remove" data-comment-no="'+ comment.commentNo +'">삭제</button>';
			  }
			  /*********************** 답글 입력 화면 ***********************/
			  if(comment.depth === 0) {          
          str += '<div>';
          str += '  <form class="frm-reply">';
          str += '    <input type="hidden" name="groupNo" value="' + comment.groupNo + '">';
          str += '    <input type="hidden" name="blogNo" value="${blog.blogNo}">';                 // blogNo는 comment 와 상세보기 블로그 에서 둘 다 가지고 있음 => ${comment.blogNo} 도 가능하다는 의미
          str += '    <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">';
          str += '    <textarea name="contents" placeholder="답글 입력"></textarea>';
          str += '    <button type="button" class="btn btn-warning btn-register-reply">작성완료</button>';
          str += '  </form>';
          str += '</div>';
        }
			  /**************************************************************/
			  // 댓글 닫는 <div>
			  str += '</div>';
			  // 목록에 댓글 추가
			  commentList.append(str);
		  })
		  // 페이징 표시
		  paging.append(resData.paging);
		},
		error: (jqXHR) => {
			alert(jqXHR.statusText + '(' + jqXHR.status + ')');
		}
	})
}

// 페이징 처리
const fnPaging = (p) => {
	page = p;             // 받아온 페이지 번호로 page 설정
	fnCommentList();      // 페이지 번호 가지고 댓글목록 함수 호출
}

const fnRegisterReply = () => {
  $(document).on('click', '.btn-register-reply', (evt) => {
  	// 로그인 체크
  	fnCheckSignin();
  	// 요청
  	$.ajax({
  		type: 'POST',
  		url: '${contextPath}/blog/comment/registerReply.do',
  		data: $(evt.target).closest('.frm-reply').serialize(),   // serialize() : <form> 내부의 모든 입력을 파라마터 형식으로 보낼 때 사용
    // 응답
     dataType: 'json',
      success: (resData) => {
        if(resData.insertReplyCount === 1) {
      		alert('답글이 등록되었습니다.');
      		$(evt.target).prev().val('');       // btn-register-reply 의 이전 형제인 contents
      		fnCommentList();
        } else {
          alert('답글 등록이 실패했습니다.');
        }
      },
      error: (jqXHR) => {
        alert(jqXHR.statusText + '(' + jqXHR.status + ')');
      }
  	})
  })
}

//전역 객체
var frmBtn = $('#frm-btn');

const fnEditBlog = () => {
  $('#btn-edit').on('click', (evt) => {
    frmBtn.attr('action', '${contextPath}/blog/edit.do');
    frmBtn.submit();
  })
}

const fnRemoveBlog = () => {
  $('#btn-remove').on('click', (evt) => {
    if(confirm('블로그를 삭제하면 모든 댓글이 함께 삭제됩니다. 삭제할까요?')){
      frmBtn.attr('action', '${contextPath}/blog/remove.do');
      frmBtn.submit();
    }
  })
}

$('#contents').on('click', fnCheckSignin); // 로그인이 안되어 있으면 입력(textarea)을 막음
fnRegisterComment();
fnCommentList();
fnRegisterReply();
fnEditBlog();
fnRemoveBlog();
	
</script>

<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



