package com.gdu.myapp.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

/*
 *  controller의 파라미터를 그냥 HttpServletRequest로 통일하여도 됨 => request는 모든지 다 저장하여 전달 할 수 있기 때문
 *  model 은 map과 같아서 model 로 모두 통일 후 request 와 response를 넣어서 전달 -> service 에서 필요한 파라미터만 꺼내서 쓸 수 있음 
 */

public interface BbsService {                                 
  int registerBbs(HttpServletRequest request);                     // 등록 (HttpServletRequest를 쓰면 IP 주소를 수월하게 뽑아낼 수 있음)
  void loadBbsList(HttpServletRequest request, Model model);       // 목록 (페이징 처리 필요에 필요한 반환값이 많음(넘겨주어야 하는 데이터가 많음) => service로 model에 담아서 넘겨주기만 함 => 반환하지 않음(void)
  int registerReply(HttpServletRequest request);                   // 답글
  int removeBbs(int bbsNo);                                        // 삭제 (실제 DELETE가 아닌 UPDATE)
  void loadBbsSearchList(HttpServletRequest request, Model model); // 검색
  
}
