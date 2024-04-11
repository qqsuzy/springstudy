package com.gdu.myapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.myapp.service.UploadService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/upload")
@RequiredArgsConstructor
@Controller
public class UploadController {
  
  private final UploadService uploadService;
  
  @GetMapping("/list.do")
  public String list(HttpServletRequest request, Model model) {
    model.addAttribute("request", request); // model 에 request 저장
    uploadService.loadUploadList(model);    // model 만 전달 (model 에 request 저장되어 있음) 
    return "upload/list";
  }
  
  
  // <a>업로드작성</a>
  @GetMapping("/write.page")
  public String write() {
    return "upload/write";
  }
  
  @PostMapping("/register.do")
  public String register(MultipartHttpServletRequest multipartRequest
                       , RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("inserted", uploadService.registerUpload(multipartRequest));
    return "redirect:/upload/list.do";
  }
  
  @GetMapping("/detail.do")
  public String detail(@RequestParam(value="uploadNo", required=false, defaultValue="0") int uploadNo
                     , Model model) {
    uploadService.loadUploadByNo(uploadNo, model);
    return "upload/detail";
  }
  
  
}
