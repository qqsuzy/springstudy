package com.gdu.myapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.myapp.service.BlogService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/blog")
@RequiredArgsConstructor
@Controller
public class BlogController {

  private final BlogService blogService;

  //보통적으로 구현해온 방식이와 다르게 스크롤형 목록보기는 페이지를 가져가지 않음 -> 우선 빈 페이지를 가지고 가서 최초 목록을 뿌려주는 방식
  @GetMapping("/list.page")
  public String List() {
    return "blog/list"; // blog 폴더 밑 list.jsp
  } 
  
  @GetMapping("/write.page")
  public String writePage() {
    return "blog/write";
  }
  
  @PostMapping(value="/summernote/imageUpload.do", produces="application/json")
  public ResponseEntity<Map<String, Object>> summernoteImageUpload(@RequestParam("image") MultipartFile multipartFile) {
    return blogService.summernoteImageUpload(multipartFile);
  }
  
  // 작성하기
  @PostMapping("/register.do")
  public String register(HttpServletRequest request, RedirectAttributes redirectAttributes) { // 삽입 성공,실패 여부를 redirectAttributes 로 redirect 함
    redirectAttributes.addFlashAttribute("insertCount", blogService.registerBlog(request));
    return "redirect:/blog/list.page"; // redirect 는 타입이 아닌 주소를 반환해야함 (목록 보기로 이동)
  }
  
  // 블로그 리스트
  @GetMapping(value="/getBlogList.do", produces = "application/json")  // SELECT 요청은 GET
  public ResponseEntity<Map<String, Object>> getBlogList(HttpServletRequest request) {
    return blogService.getBlogList(request);
  }
  
  // 상세보기
  @GetMapping("/detail.do") // location 이동
  public String detail(@RequestParam int blogNo, Model model) {
    model.addAttribute("blog", blogService.getBlogByNo(blogNo));
    return "blog/detail";
  }
  
  @PostMapping(value="/registerComment.do", produces="application/json")
  public ResponseEntity<Map<String, Object>> registerComment(HttpServletRequest request) {
    System.out.println(request.getParameter("contents"));
    System.out.println(request.getParameter("blogNo"));
    System.out.println(request.getParameter("userNo"));
    return new ResponseEntity<>(null);
  }
}
