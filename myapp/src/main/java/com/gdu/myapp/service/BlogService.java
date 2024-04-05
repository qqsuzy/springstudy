package com.gdu.myapp.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {
  
  // ajax 로 처리해주어야 함 (이미지 첨부를 하여도 페이지는 바뀌지 않는 single 페이지로 작동) 
  ResponseEntity<Map<String, Object>> summernoteImageUpload(MultipartFile multipartFile); // 이미지 첨부할 때 마다 동작함
   int registerBlog(HttpServletRequest request); // 파라미터는 리퀘스트, @RequestParam , 커맨드객체(객체로 받음) 로 전달 할 수 있음  
}
