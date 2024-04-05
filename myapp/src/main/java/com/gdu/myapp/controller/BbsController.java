package com.gdu.myapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.myapp.service.BbsService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/bbs")
@RequiredArgsConstructor
@Controller
public class BbsController {

  private final BbsService bbsService;
  
  @GetMapping("/list.do")
  public String list(HttpServletRequest request, Model model) {
    bbsService.loadBbsList(request, model);
    return "bbs/list";   // list.jsp 로 이동
  }
  
  @GetMapping("/write.page")
  public String writePage() {
    return "bbs/write";
  }

  @PostMapping("/register.do")
  public String register(HttpServletRequest request, RedirectAttributes redirectAttributes) { // insert 이후의 이동방식은 redirect -> redirect의 전달방식은 RedirectAttributes (forward의 전달방식은 model)
    redirectAttributes.addFlashAttribute("insertBbsCount", bbsService.registerBbs(request));     // redirectAttributes 로 insertCount 저장
    // 목록으로 삽입 결과 전달
    return "redirect:/bbs/list.do";  
  }
  
  @PostMapping("/registerReply.do")
  public String registerReply(HttpServletRequest request, RedirectAttributes redirectAttributes) { 
    redirectAttributes.addFlashAttribute("insertReplyCount", bbsService.registerReply(request));   
    // 목록으로 삽입 결과 전달
    return "redirect:/bbs/list.do";  
  }  
  
  @GetMapping("/removeBbs.do")
  public String removeBbs(@RequestParam int bbsNo, RedirectAttributes redirectAttributes) { // 삭제 이후에도 redirect 필요
   redirectAttributes.addFlashAttribute("removeBbsCount", bbsService.removeBbs(bbsNo));
    return "redirect:/bbs/list.do"; 
  }
  
  @GetMapping("/search.do")
  public String serach(HttpServletRequest request, Model model) {
    bbsService.loadBbsSearchList(request, model);
    return "bbs/list";
  }
  
}
