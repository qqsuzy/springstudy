package com.gdu.prj04.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.prj04.dto.BoardDto;
import com.gdu.prj04.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/ajax1") // /ajax1 로 시작하는 모든 요청을 담당하는 컨트롤러
@Controller
@RequiredArgsConstructor // Not Null
public class BoardController1 {

  private final BoardService boardService;
   
   /*
    * ajax 처리할 때 반드시 필요한 사항
    * 1. @ResponseBody 
    * 2. produces      : 응답 데이터 타입
    */
   
   @ResponseBody // 뷰 리졸버가 관섭하지 않도록 해주는 annotation 
                 // 반환 값은 jsp 의 이름이 아니고 어떤 데이터임 (비동기 작업에서 꼭 필요한 annotation)
   @GetMapping(value="/list.do", produces="application/json") // produces : 응답 데이터 타입(Content-Type) => application/json : JSON의 java에서 쓰이는 응답 데이터 타입   // JSON 라이브러리 말고 jackson 사용
   public List<BoardDto> list() {  // jackson 라이브러리가 List<BoardDto> 를 JSON 데이터로 변환한다.
     return boardService.getBoardList(); // 데이터 반환
   }
  
   @ResponseBody
   @GetMapping(value="/detail.do", produces="application/json")
   public BoardDto detail(int boardNo) {
     return boardService.getBoardByNo(boardNo);
   }
}
