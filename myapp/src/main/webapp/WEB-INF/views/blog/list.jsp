<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>

<style>
  .blog{
    width: 640px;
    height: 180px;
    margin: 10px auto;
    border: 1px solid gray;
    cursor: pointer;
  }
  .blog > span {
    color: tomato;
    display: inline-block;
    box-sizing: border-box;
  }
  .blog > span:nth-of-type(1) { width: 150px; }
  .blog > span:nth-of-type(2) { width: 250px; }
  .blog > span:nth-of-type(3) { width: 50px; }
  .blog > span:nth-of-type(4) { width: 150px; }
</style>


<h1 class="title">블로그 목록</h1>

<a href="${contextPath}/blog/write.page">블로그작성</a>

<div id="blog-list"></div>

<script>

// 전역 변수
var page = 1;     // fnGetBlogList 와 fnScrollHandler 에서 page 값을 모두 사용하기 위해서 전역변수로 선언
var totalPage = 0;

const fnGetBlogList = () => {
  
	// page 에 해당하는 목록 요청
  $.ajax({
	  // 요청 
    type: 'GET',   // SELECT 요청은 GET
    url: '${contextPath}/blog/getBlogList.do',  // '${contextPath}/blog/getBlogList.do?page=' + page, 와 같음 => url 과 파라미터 분리는 url, data(파라미터) 로 각각 분리 가능
    data: 'page=' + page,
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"blogList": [], "totalPage": 10}
    	totalPage = resData.totalPage;	
    	 $.each(resData.blogList, (i, blog) => {
    	  let str = '<div class="blog" data-user-no="'+ blog.user.userNo +'"  data-blog-no="' + blog.blogNo + '">';
          str += '<span>' + blog.title + '</span>';
          str += '<span>' + blog.user.email + '</span>';
          str += '<span>' + blog.hit + '</span>';
          str += '<span>' + moment(blog.createDt).format('YYYY.MM.DD') + '</span>';
          str += '</div>';
          $('#blog-list').append(str);
      })
    },
    error: (jqXHR) => {
    	alert(jqXHR.statusText + '(' + jqXHR.status + ')');
    }
   });  
  }

const fnScrollHandler = () => {
	
	// 스크롤이 바닥에 닿으면 page 증가(최대 totalPage 까지) 후 새로운 목록 요청
	// totalPage 를 받아오는 곳은 fnGetBlogList 임 => totalPage를 해당 함수 내에서도 사용할 수 있도록 전역변수 선언
	
	//   - 문제점   : scroll 이벤트는 스크롤이 움직이는 동안 연속적으로 요청함 -> 너무 많이 요청한다는 단점이 있음
	//   - 해결방법 : 0.5초 이후에 목록 가져오기 -> 한번 목록을 가져왔으면 목록을 가져오지 말 것
	//              : 타이머 함수 안에 넣고 -> 목록을 가져오면 동작을 취소하여 목록을 중복으로 가져오지 않도록 함 (타이머 함수는 동작을 취소하는 기능이 있어 이를 활용함!) 
	
	// 타이머 id (동작한 타이머의 동작 취소를 위한 변수)
	var timerId;  // undefined, booldean 의 의미로는 false
	
	$(window).on('scroll', (evt) => {                          
	
		if(timerId) {                // timerId 가 undefined 이면 false, 아니면 true
			                           // timerId 가 undefined 이면 setTimeout() 함수가 동작한 적 없음
			  clearTimeout(timerId);   // setTimeout() 함수 동작을 취소함 -> 목록을 가져오지 않는다.   
 		}
		
		
		// 500밀리초(0.5초) 후에 () => {}가 동작하는 setTimeout 함수
		timerId = setTimeout(() => {
			
  		let scrollTop = $(window).scrollTop();
  		let windowHeight = $(window).height();
  		let documentHeight = $(document).height();
  
  		if( (scrollTop + windowHeight + 50 ) >= documentHeight ){ // 스크롤과 바닥 사이 크기가 50px 이하인 경우
  			if(page > totalPage) {
  				return;
  			}
  			page++;
  			fnGetBlogList();
  		}
			
		}, 500);
		
	})
	
	/* javaScript : window.addEventListener('scroll', (evt) => {}) */
	
}

const fnBlogDetail = () => {
	$(document).on('click', '.blog', (evt) => {  // 나중에 javaScript 로 생긴 동적 요소들(이벤트 요소) 들은 이벤트 거는 방법이 다름! (옆에 코드와 같이)
		// <div class="blog"> 중 클릭 이벤트가 발생한 <div> : 이벤트 대상 (evt.target)
		// evt.target.dataset.blogNo === $(evt.target).data('blogNo')
		// evt.target.datadet.userNo === $(evt.target).data('userNo')
		
		// 내가 작성한 블로그는 /detail.do 요청 (조회수 증가가 없음)
		// 남이 작성한 블로그는 /updateHit.do 요청 (조회수 증가가 있음)
		if('${sessionScope.user.userNo}' === evt.target.dataset.userNo) {
  		location.href = '${contextPath}/blog/detail.do?blogNo=' +  evt.target.dataset.blogNo;
		} else{
			location.href = '${contextPath}/blog/updateHit.do?blogNo=' +  evt.target.dataset.blogNo;
		}
		
	})
}

  const fnInsertCount = () => {
	  let insertCount = '${insertCount}';
	  if(insertCount !== '') {
		  if(insertCount === 1) {
			  alert('블로그가 등록되었습니다.');
		  } else {
			  alert('블로그 등록이 실패했습니다.');
		  }
	  }
  }
  
  fnGetBlogList();
  fnScrollHandler();
  fnBlogDetail();
  fnInsertCount();
  
</script>





<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



