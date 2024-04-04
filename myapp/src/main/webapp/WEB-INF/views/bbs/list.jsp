<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<style>
  .blind{
    display: none;
  }
</style>

<h1 class="title">BBS</h1>

<a href="${contextPath}/bbs/write.page">작성하러가기</a>

<table border="1">
  <thead>
    <tr>
      <td>순번</td>
      <td>작성자</td>
      <td>내용</td>
      <td>작성일자</td>
    </tr>
  </thead>
  <tbody>
    <!--  bbs (BbsDto) : 원글 -->
    <c:forEach items="${bbsList}" var="bbs" varStatus="vs"> <!-- var="bbs" :  BbsDto-->
      <tr class="bbs"> 
        <td>${beginNo - vs.index}</td> <!-- 인덱스 호출할 때 vs 필요함 -->
        <!-- state 확인하는 if문 => state 1 : 화면 출력, state 0 : 화면 출력 x  -->
        <c:if test="${bbs.state == 1}">
        <td>${bbs.user.email}</td>
        <td>
          <!-- 들여쓰기 -->
          <c:forEach begin="1" end="${bbs.depth}" step="1">&nbsp;&nbsp;</c:forEach>
          <c:if test="${bbs.depth != 0}"><i class="fa-solid fa-share"></i></c:if>
          ${bbs.contents}
          <!-- 내가 작성한 게시글은 "답글" 버튼 안보임 -->
          <c:if test="${bbs.user.userNo != sessionScope.user.userNo}">
            <button type="button" class="btn-reply">답글</button>
          </c:if>
          <!-- 내가 작성한 게시글만 "삭제" 버튼 보임 -->
          <c:if test="${bbs.user.userNo == sessionScope.user.userNo}">
            <button type="button" class="btn-remove">삭제</button>
            <input type="hidden" value="${bbs.bbsNo}">
          </c:if>
        </td>
        <td>
          <fmt:formatDate value="${bbs.createDt}" pattern="yyyy.MM.dd. HH:mm:ss"/>
        </td>
        </c:if>
        <c:if test="${bbs.state == 0}">
          <td colspan="3">삭제된 게시글입니다.</td> 
        </c:if>
      </tr>
      <tr class="write blind">
        <!-- 답글 작성 form -->
        <td colspan="4">
          <form method="POST"
                action="${contextPath}/bbs/registerReply.do">
          <div>
            <span>답글작성자</span> 
            <span>${sessionScope.user.email}</span> <!-- 작성자는 email(아이디) : session 에 정보 저장되어 있어 session 에서 꺼냄 -->
          </div>
        
          <div>
            <!-- submit 할 때 전달될 정보 1  : contents -->
            <textarea class="contents" name="contents" placeholder="답글을 입력하세요"></textarea> 
          </div>
          
          <div>
            <!-- submit 할 때 전달될 정보 2 : userNo --> 
            <input type="hidden" name="userNo" value="${sessionScope.user.userNo}">
            <!-- 원글의 depth / groupNo / groupOrder => 3개의 원글의 정보가 있어야 답글 삽입할 때 넘겨줄 수 있음  -->
            <input type="hidden" name="depth"  value="${bbs.depth}">
            <input type="hidden" name="groupNo"  value="${bbs.groupNo}">
            <input type="hidden" name="groupOrder"  value="${bbs.groupOrder}"> 
            <button type="submit">작성완료</button>
          </div>
          </form>
        </td>
      </tr>
    </c:forEach>  
  </tbody>
</table>

<div>${paging}</div>


<script>
// 게시글 삭제
const fnBtnRemove = () => {
  $('.btn-remove').on('click', (evt) => {
	  if(confirm('게시글을 삭제할까요?')) {
		  location.href ='${contextPath}/bbs/removeBbs.do?bbsNo=' + $(evt.target).next().val(); // 삭제 버튼 다음 요소(hidden)의 value에 bbsNo 들어있음
	  }
  })	
}

// 로그인 체크
const fnCheckSignin = () => {
	  if('${sessionScope.user}' === '') {  // session에 저장된 유저가 없을 때
		  if(confirm('Sign In 이 필요한 기능입니다. Sign In 할까요?')) {
			  location.href = '${contextPath}/user/signin.page';
		  }
	  }
  } 
   
  // 답글  
  const fnBtnReply = () => {
	  $('.btn-reply').on('click', (evt) => { // javaScript 는 for문 돌려야해서 jQuery로 작성 (jQuery는 for문 없어 꺼내서 쓸 수 있음)
	  // Sign In 체크
	  fnCheckSignin();  // 로그인 체크
	  
	  // 답글 작성 화면 조작하기
	  let write = $(evt.target).closest('.bbs').next(); // 답글 버튼의 <tr> (할아버지)을 찾음 => evt.target .parent.parent / evt.target.closest('.bbs') -> <tr>(할아버지) 의 형제를 찾음 => .next()
		  if(write.hasClass('blind')) {    // 할아버지의 형제 <tr> 이 blind 처리 되어 있을 경우
			  $('.write').addClass('blind'); // 모든 답글 작성 화면 닫은 뒤
			  write.removeClass('blind');    // 클릭한 답글 작성 화면만 열기
		  } else {
			  write.addClass('blind');       // 답글 작성 화면이 열려있었다면 닫겠다.
		  }
	  }) 
  }
   
  // insertBbsCount 처리하는 함수
  const fnInsertBbsCount = () => {
	  let insertBbsCount = '${insertBbsCount}';
	  if(insertBbsCount != '') {
		  if(insertBbsCount === '1') {
			  alert('BBS 원글이 등록되었습니다.');
		  } else {
			  alert('BBS 원글이 등록되지 않았습니다.');
		  }
	  }
  }
  
//insertReplyCount 처리하는 함수
  const fnInsertReplyCount = () => {
    let insertReplyCount = '${insertReplyCount}';
    if(insertReplyCount != '') {
      if(insertReplyCount === '1') {
        alert('BBS 답글이 등록되었습니다.');
      } else {
        alert('BBS 답글이 등록되지 않았습니다.');
      }
    }
  }
  
  fnBtnRemove();
  $('.contents').on('click', fnCheckSignin);
  fnBtnReply();
  fnInsertBbsCount();
  fnInsertReplyCount();
  
</script>


 <!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



