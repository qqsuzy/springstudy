package com.gdu.prj10.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.prj10.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {
 
  private final BoardService boardService;
  
  @PostMapping(value="/summernote/imageUpload.do", produces="application/json")
  public ResponseEntity<Map<String, Object>> imgeUpload(MultipartHttpServletRequest multipartRequest) {
    return boardService.summernoteImageUpload(multipartRequest);
  }
  
  // 전송 버튼 => 누르면 server 로 태그로 넘어옴 => 태그를 그대로 DB에 저장 (DB에는 텍스트만 저장하고 실제 파일은 하드디스크에 보관할 수 있도록 함)
  @PostMapping(value="/board/register.do")
  public String register(HttpServletRequest request) {
    System.out.println(request.getParameter("contents"));
    return "redirect:/main.do";      // 출력 후 main으로 돌아감
  }
}
