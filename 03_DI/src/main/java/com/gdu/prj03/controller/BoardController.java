package com.gdu.prj03.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gdu.prj03.service.BoardService;

import lombok.RequiredArgsConstructor;

//        @Controller  @Service  @Repository =>  bean 총 3개 등록됨
// view - controller - service - dao

@RequiredArgsConstructor // final 필드용 생성자(Non Null)

@Controller  // Controller 에서 사용하는 @Component
public class BoardController {

  /******************************************* DI *******************************************/
  /*
   * Dependency Injection
   * 1. 의존 주입
   * 2. Spring Container 에 저장된 bean 을 특정 객체에 넣어 주는 것을 의미한다.
   * 3. 방법
   *    1) 필드 주입
   *    2) 생성자 주입
   *    3) setter 주입
   * 4. 사용 가능한 annotation
   *    1) @Inject (java 꺼)
   *    2) @Resource, @Qulifier
   *    3) @Autowired (대부분 이걸 사용함)
   */
  
  /* 
   * 1. 필드 주입
   * @Autowired
   * private BoardService boardService;
   * 타입(BoardService)을 비교하여 같으면 가져옴
   */
  
  /*
   * 2. 생성자 주입
   *   1) 생성자의 매개변수로 자동 주입된다. 
   *   2) @Autowired 를 생략할 수 있다. (생성자 주입만 생략 가능)
   *   3) 매개변수 타입을 비교하여 같으면 가져옴 -> 필드로 전달됨
   *       
   * private BoardService boardService;
   * 
   * public BoardController(BoardService boardService) { super();
   * this.boardService = boardService; }
   */
  
  /*
   *  3. setter 주입
   *    1) 메소드의 매개변수로 자동 주입된다.
   *    2) @Autowired 를 생략할 수 없다.
   *    3) 사실 메소드명은 상관이 없다.
   *    4) 매개변수 타입을 비교하여 같으면 가져옴 -> 필드로 전달됨
   *
   * private BoardService boardService;
   * 
   * @Autowired public void setBoardService(BoardService boardService) {
   * this.boardService = boardService; }
   */
  
  // **** 앞으로 사용할 한 가지 방식
  // final 필드 + 생성자 주입 (lombok의 @RequiredArgsConstructor  => 매개변수의 null 체크를 담당하는 annotation)
  // final은 반드시 바로 초기화가 필요하지만 생성자를 만들면 final 필드에 값을 채워 넣어줄 수 있음
  // 생성자 주입은 @Autowired 가 생략가능 (코드 내에 없는 이유) 
  private final BoardService boardService;  

  @GetMapping("/board/list.do")
  public String list(Model model) { // 목록보기 forward를 위해 Model을 파라미터로 적용
    model.addAttribute("boardList", boardService.getBoardList());
    return "board/list";
  }
  
  @GetMapping("/board/detail.do")
  public String detail(int boardNo, Model model) {
    model.addAttribute("board", boardService.getBoardByNo(boardNo));
    return "board/detail";
  }
  
}
