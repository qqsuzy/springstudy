package com.gdu.prj08.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.prj08.service.UploadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UploadController {
  
  private final UploadService uploadService;
  
  @PostMapping("/upload1.do")
  public String upload1(MultipartHttpServletRequest multipartRequest
                      , RedirectAttributes redirectAttributes) {
    int insertCount = uploadService.upload1(multipartRequest);
    redirectAttributes.addFlashAttribute("insertCount", insertCount);
    return "redirect:/main.do";
  }
  
  @ResponseBody // 반환값이 응답 데이터 자체임을 명시해주는 annotation => JSP 경로가 아님을 알려줌
  @PostMapping(value="/upload2.do", produces="application/json") // produces로 해당 Map이 json 데이터 임을 명시해줌
  public Map<String, Object> upload2(MultipartHttpServletRequest multipartRequest) {
    return uploadService.upload2(multipartRequest);
  }
  
  /*
  // @ResponseBody 를 생략할 수 있는 ResponseEntity 객체 
  @PostMapping(value="/upload2.do", produces="application/json") 
  public ResponseEntity<Map<String, Object>> upload2(MultipartHttpServletRequest multipartRequest) {
    return new ResponseEntity(Map.of("success", 1), HttpStatus.OK);
  */
  
  }
  
  