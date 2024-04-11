package com.gdu.myapp.service;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface UploadService {

  boolean registerUpload(MultipartHttpServletRequest multipartRequest); // 게시글 작성     =>  삽입(insert)가 upload , attach 두 군데에서 진행되어 insertCount 로 삽입여부를 확인하기 어려워서 타입을 int 가 아닌 boolean으로 잡음
  void loadUploadList(Model model);                                     // 게시글 목록보기
  void loadUploadByNo(int uploadNo, Model model);                        // 게시글 상세보기
  
}
