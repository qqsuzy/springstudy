package com.gdu.prj04.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gdu.prj04.dto.BoardDto;
import com.gdu.prj04.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/ajax2")
@RequiredArgsConstructor
@RestController           // 모든 메소드는 @ResponseBody annotation 을 가지게 된다. (ajax 전용 컨트롤러)
public class BoardController2 {

  private final BoardService boardService;
  
  @GetMapping(value="/list.do", produces=MediaType.APPLICATION_JSON_VALUE) // MediaType.APPLICATION_JSON_VALUE :  "application/json" 과 동일한 값
  public List<BoardDto> list() {
    return boardService.getBoardList();
  }
  
  @GetMapping(value="/detail.do", produces=MediaType.APPLICATION_JSON_VALUE)
  public BoardDto detail(BoardDto board) {
    return boardService.getBoardByNo(board.getBoardNo());
  }
  
}
