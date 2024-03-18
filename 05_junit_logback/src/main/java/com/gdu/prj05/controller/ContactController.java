package com.gdu.prj05.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.prj05.service.ContactService;

import lombok.RequiredArgsConstructor;

@RequestMapping(value="/contact")
@RequiredArgsConstructor
@Controller
public class ContactController {

  private final ContactService contactService;
  
  @GetMapping(value="/list.do")
  public String list(Model model) {
    model.addAttribute("contactList", contactService.getContactList());
    return "contact/list";
  }
  
  @GetMapping(value="/detail.do")
  public String detail(@RequestParam(value="", required=false, defaultValue="0" ) int contactNo) {
    
  }
  
}
