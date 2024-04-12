package com.gdu.myapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.myapp.dto.UploadDto;
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
  
  // 개별 다운로드 <a ${attach.originalFilename}></a>  
  @GetMapping("/download.do") // 다운로드에서는 produces 가 없는 이유? serviceImpl 에서 처리했기 때문임 ->  responseHeader.add("Content-Type", "application/octet-stream") 로 처리함 => service 쪽에서 코드를 빼고 controller 에 value="/download.do", produces="application/octet-stream") 추가하여 작업하여도 상관 없음 ** 04_ajax 에서 설명함 참고!
  public ResponseEntity<Resource> download(HttpServletRequest request) {
    return uploadService.download(request);
  }
  
  // 전체 다운로드 <a id="download-all" href="${contextPath}/upload/downloadAll.do?uploadNo=${upload.uploadNo}">모두 다운로드</a> 
  @GetMapping("/downloadAll.do")
  public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {
    return uploadService.downloadAll(request);
  }
  
  @PostMapping("/edit.do")
  public String edit(@RequestParam int uploadNo, Model model) {
    model.addAttribute("upload", uploadService.getUploadByNo(uploadNo));
    return "upload/edit";
  }
  
  @PostMapping("/modify.do")
  public String modify(UploadDto upload, RedirectAttributes redirectAttributes) { // tilte, contents, userNo 가 uploadDto에 저장됨 => 커맨드 객체로 받음
    redirectAttributes.addFlashAttribute("updateCount", uploadService.modifyUpload(upload));
    // return "redirect:/upload/list.do";  => 목록으로 가기
    return "redirect:/upload/detail.do?uploadNo=" + upload.getUploadNo(); // => 상세보기로 가기 
                                                                          // 목록가기와 다르게 주의해야하는 점은 redirect 되는 주소값에 uploadNo 가 포함되어야 함
                                                                          // edit.jsp 의 <form> 이 submit 되었을 때 어떠한 값이 넘어오는지 확인이 항상 필요함 -> uploadNo가 넘어오지 않는 다면 <form> 내에 hidden 값을 넣어 값을 넘겨 받아야 함
  }
  
  @GetMapping(value="/attachList.do", produces="application/json")
  public ResponseEntity<Map<String, Object>> attachList(@RequestParam int uploadNo) {
    return uploadService.getAttachList(uploadNo);
  }
  
}
