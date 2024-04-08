package com.gdu.myapp.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.gdu.myapp.dto.BlogDto;

public interface BlogService {
  
  // ajax 로 처리해주어야 함 (이미지 첨부를 하여도 페이지는 바뀌지 않는 single 페이지로 작동) 
  ResponseEntity<Map<String, Object>> summernoteImageUpload(MultipartFile multipartFile); // 이미지 첨부할 때 마다 동작함
  int registerBlog(HttpServletRequest request);                                           // 파라미터는 리퀘스트, @RequestParam , 커맨드객체(객체로 받음) 로 전달 할 수 있음
  ResponseEntity<Map<String, Object>> getBlogList(HttpServletRequest request);            // 목록 + 페이지 2개를 가져가야 하므로 Map, 만약 목록 1개만 가져가야 한다면 List 로 적용하면 됨 (ajax 처리를 위해 반환타입 : ResponseEntity )  
                                                                                          // Map 의 타입을 Object 로 적용하는 이유? 언제든지 다양한 타입의 데이터들이 넘어올 수 있기에 타입을 열어두기 위함
  BlogDto getBlogByNo(int blogNo);
}
