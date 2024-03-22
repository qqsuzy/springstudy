<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
</head>
<body>

  <div>
    <form action="${contextPath}/upload1.do"
          method="POST"
          enctype="multipart/form-data"> <!-- multipartResolver 객체(bean) 필요 -->
      <div>
       <!-- 파라미터 가져올 때 :                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   -->
        <input type="file" name="input-files" class="files" accept="image/*" multiple>
      </div>
      <div>
        <!-- 파라미터 가져올 때 : getParameter -->
        <input type="text" name="input-writer" placeholder="작성자">
      </div>
      <div>
        <button type="submit">전송</button>
      </div>
    </form>  
  </div>
  
  <h3>첨부 파일 목록</h3>
  <div id="file-list"></div>
  
  <hr>
  
  <div>
  
    <div>
      <input type="file" id="input-files" class="files" multiple>
    </div>
    <div>
      <input type="text" id="writer" placeholder="작성자">
    </div>
    <div>
      <button type="button" id="btn-upload">전송</button>
    </div> 
  </div>
  
  <script type="text/javascript">
   
  const fnFileCheck = ()=>{
      $('.files').on('change', (evt)=>{
        const limitPerSize = 1024 * 1024 * 10;  // 10MB
        const limitTotalSize = 1024 * 1024 * 100;
        let totalSize = 0;
        const files = evt.target.files;
        const fileList = document.getElementById('file-list');
        fileList.innerHTML = '';
        for(let i = 0; i < files.length; i++){
          if(files[i].size > limitPerSize){
            alert('각 첨부 파일의 최대 크기는 10MB입니다.');
            evt.target.value = '';
            fileList.innerHTML = '';
            return;                   // 함수종료
          }
          totalSize += files[i].size;
          if(totalSize > limitTotalSize){
            alert('전체 첨부 파일의 최대 크기는 100MB입니다.');
            evt.target.value = '';
            fileList.innerHTML = '';
            return;
          }
          fileList.innerHTML += '<div>' + files[i].name + '</div>';
        }
      })
    }
    
    const fnAfterInsertCheck = ()=>{
      const insertCount = '${insertCount}';
      if(insertCount !== ''){
        if(insertCount === '1'){
          alert('저장되었습니다.');
        } else {
          alert('저장실패했습니다.');
        }
      }
    }
     
     // 비동기 처리
       const fnAsyncUpload = ()=>{
      const inputFiles = document.getElementById('input-files');
      const inputWriter = document.getElementById('input-writer');
      let formData = new FormData();
      for(let i = 0; i < inputFiles.files.length; i++){
        formData.append('files', inputFiles.files[i]);
      }
      formData.append('writer', inputWriter);
      fetch('${contextPath}/upload2.do', {
        method: 'POST',
        body: formData         // 요청 본문
      }).then(response=>response.json())
        .then(resData=>{  /* resData = {"success": 1} 또는 {"success": 0} */
          if(resData.success === 1){
            alert('저장되었습니다.');
          } else {
            alert('저장실패했습니다.');
          }
        })
    }
     
      const fnAsyncUpload2 = ()=>{
          const inputFiles = document.getElementById('input-files');
          const inputWriter = document.getElementById('input-writer');
          let formData = new FormData();
          for(let i = 0; i < inputFiles.files.length; i++){
            formData.append('files', inputFiles.files[i]);
          }
          formData.append('writer', inputWriter);
          $.ajax({
            type: 'POST',
            url: '${contextPath}/upload2.do',
            contentType: false,
            processData: false,
            data: formData,
            dataType: 'json'
          }).done(resData=>{
            if(resData.success === 1){
              alert('저장되었습니다.');
            } else {
              alert('저장실패했습니다.');
            }
          })
        }
      
      
     fnFileCheck();
     fnAfterInsertCheck();
     // 이벤트 처리를 함수 밖으로 뺀 case
     document.getElementById('btn-upload').addEventListener('click', fnAsyncUpload2);
     
  </script>
  
</body>
</html>