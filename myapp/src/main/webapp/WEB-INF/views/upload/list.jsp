<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="<%=request.getContextPath()%>"/> 
<c:set var="dt" value="<%=System.currentTimeMillis()%>"/>

<!-- 파라미터 전달 가능한 jsp 액션 -->
<jsp:include page="../layout/header.jsp"/>


<h1 class="title">업로드 목록</h1>

<a href="${contextPath}/upload/write.page">업로드작성</a>

<div>
  <div>
    <input type="radio" name="sort" value="DESC" id="descending" checked>
    <label for="descending">내림차순</label>    
    <input type="radio" name="sort" value="ASC" id="ascending">
    <label for="ascending">오름차순</label>    
  </div>
  <div>
    <select id="display" name="display">
      <option>20</option>
      <option>40</option>
      <option>60</option>
    </select>
  </div>
  <!-- bootstrap -->
  <table class="table align-middle">
    <thead>
      <tr>
       <td>순번</td>
       <td>제목</td>
       <td>작성자</td>
       <td>첨부개수</td>
      </tr>
    </thead>
    <tbody>
      <!-- List를 for문으로 돌려서 하나씩 데이터를 뺌 -> upload에 저장 -->
      <c:forEach items="${uploadList}" var="upload" varStatus="vs"> 
        <tr>
          <!--  1page : 100 ~ 81   100 - index 0      -> index 빼기  
                                    99 - index 1
                                    98 - index 2
           -->
          <td>${beginNo - vs.index}</td>
          <td><a href= "${contextPath}/upload/detail.do?uploadNo=${upload.uploadNo}">${upload.title}</a></td>
          <td>${upload.user.email}</td>
          <td>${upload.attachCount}</td>
        </tr>
      </c:forEach>
    </tbody>
    <!-- pagin 처리 -->
    <tfoot>
      <tr>
        <td colspan="4">${paging}</td>
      </tr>
    </tfoot>
  </table>
</div>

<script>

const fnDisplay = () => {
	  document.getElementById('display').value = '${display}';                   // 파마미터에 저장된 값은 EL로 꺼낼 수 있음
	  document.getElementById('display').addEventListener('change', (evt) => {
	    location.href = '${contextPath}/upload/list.do?page=1&sort=${sort}&display=' + evt.target.value; // display 변경하면 page는 1로 돌아옴(만약 20개-> 40개로 바꾸었을 때, 페이징 숫자가 증가하거나 감소함 -> 원래 4페이지였는데 2페이지만 보이는 것과 같음 -> 이에 display 변경하면 우선 첫 페이지(1page)로 가도록 설정 됨) , 현재 display의 값은 이벤트 타겟이 가지고 있음 -> 이에 display 주소 뒤에 evt.target.value 추가
	  })
	}

const fnSort = () => {
	$(':radio[value=${sort}]').prop('checked', true);
	$(':radio').on('click', (evt) => {
	 location.href = '${contextPath}/upload/list.do?page=${page}&sort=' + evt.target.value + '&display=${display}';  // sort만 바꾸고 page와 display는 유지시킴  
 }) 
} 

const fnUploadInserted = () => {
	  const inserted = '${inserted}';
	  if(inserted !== '') {
	    if(inserted === 'true') {
	      alert('업로드 되었습니다.');
	    } else {
	      alert('업로드가 실패했습니다.');
	    }
	  }
	}

/*
const fnUploadDetail = () => {
	document.getElementById('title').on('click', (evt) => {
		location.href = '${contextPath}/upload/detail.do?uploadNo=' + ${upload.uploadNo};
	})
} 
 */
 
 fnDisplay();
 fnSort();
 fnUploadInserted();
  
</script>

<!-- 파라미터 전달 불가능한 지시어, views 폴더 내에 있는 경로는 상대경로로 작성 --> 
<%@ include file="../layout/footer.jsp" %>



