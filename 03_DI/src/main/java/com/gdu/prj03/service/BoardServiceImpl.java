package com.gdu.prj03.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gdu.prj03.dao.BoardDao;
import com.gdu.prj03.dto.BoardDto;

import lombok.RequiredArgsConstructor;

// controller 에서 List 를 가져갈 수 있도록 service 도 <bean>에 등록이 필요함 => @Service : service 용 @Component 
//        @Controller  @Service  @Repository =>  bean 총 3개 등록됨
// view - controller - service - dao

@RequiredArgsConstructor

@Service
public class BoardServiceImpl implements BoardService {

  private final BoardDao boardDao;
  
  @Override
  public List<BoardDto> getBoardList() {
    return boardDao.getBoardList(); // Dao 에서 List 가져와서 바로 controller 에 넘김 (중간 다리 역할만 함)
  }

  @Override
  public BoardDto getBoardByNo(int boardNo) {
    return boardDao.getBoardByNo(boardNo);
  }

}
