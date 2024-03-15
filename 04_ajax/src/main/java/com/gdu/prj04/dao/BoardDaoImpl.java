package com.gdu.prj04.dao;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.gdu.prj04.dto.BoardDto;

import lombok.AllArgsConstructor;

@Repository // Dao용 @Component => annotation을 넣으면 BoardDaoImpl 이 bean 에 등록됨
@AllArgsConstructor
public class BoardDaoImpl implements BoardDao {

  private BoardDto board1; // Autowired 는 타입을 비교 후 bean 타입이 같으면 bean 이름으로 구분하여 가져옴
  private BoardDto board2;
  private BoardDto board3;


  @Override
  public List<BoardDto> getBoardList() {
    return Arrays.asList(board1, board2, board3);
  }

  @Override
  public BoardDto getBoardByNo(int boardNo) {
    BoardDto board = null;
    switch(boardNo) {
    case 1 : board = board1; break;
    case 2 : board = board2; break;
    case 3 : board = board3; break;
    }
    return board;
  }

}
