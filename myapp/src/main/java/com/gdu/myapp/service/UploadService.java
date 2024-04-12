package com.gdu.myapp.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.myapp.dto.UploadDto;

public interface UploadService {

  boolean registerUpload(MultipartHttpServletRequest multipartRequest); // 게시글 작성     =>  삽입(insert)가 upload , attach 두 군데에서 진행되어 insertCount 로 삽입여부를 확인하기 어려워서 타입을 int 가 아닌 boolean으로 잡음
  void loadUploadList(Model model);                                     // 게시글 목록보기
  void loadUploadByNo(int uploadNo, Model model);                       // 게시글 상세보기
  ResponseEntity<Resource> download (HttpServletRequest request);       // 파일 다운로드시 페이지 바뀌지 x -> ajax 처리 , 파일자체 반환은 Resource 를 반환타입으로 잡는 것 권장, 파라미터는 attachNo만 받아와도 되지만 request 권장
  ResponseEntity<Resource> downloadAll (HttpServletRequest request);
  void removeTempFiles();
  UploadDto getUploadByNo(int uploadNo);
  int modifyUpload(UploadDto upload);
  ResponseEntity<Map<String, Object>> getAttachList(int uploadNo);      // 첨부 파일 리스트 확인을 위해서는 uploadNo(게시판 번호) 필요
}
