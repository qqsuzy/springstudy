package com.gdu.myapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gdu.myapp.dto.BbsDto;
import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.BbsMapper;
import com.gdu.myapp.utils.MyPageUtils;
import com.gdu.myapp.utils.MySecurityUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BbsServiceImpl implements BbsService {

  private final BbsMapper bbsMapper;  
  private final MyPageUtils myPageUtils;
  
  // 원글 등록
  @Override
  public int registerBbs(HttpServletRequest request) {
    
    // 사용자가 입력한 contents
    String contents = MySecurityUtils.getPreventXss(request.getParameter("contents"));
    
    // 뷰에서 전달된 userNo
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    // UserDto 객체 생성 (userNo 저장)
    UserDto user = new UserDto();
    user.setUserNo(userNo);
    
    // DB 에 저장할 BbsDto 객체
    BbsDto bbs = BbsDto.builder()
                      .contents(contents)
                      .user(user)
                     .build();
    
    return bbsMapper.insertBbs(bbs);
  }
  
  // 게시글 목록
  @Override
  public void loadBbsList(HttpServletRequest request, Model model) {
    
    // 전체 BBS 게시글 수
    int total = bbsMapper.getBbsCount();
    
    // 한 화면에 표시할 BBS 게시글 수
    int display = 20;
    
    // 표시할 페이지 번호
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1")); // 페이지 번호가 넘어오지 않으면 (null) 1로 표시
    
    // 페이지 처리에 필요한 정보 처리
    myPageUtils.setPaging(total, display, page);
    
    // DB 로 보낼 Map 생성
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    // DB 에서 목록 가져오기
    List<BbsDto> bbsList = bbsMapper.getBbsList(map);
    
    // total = 1000
    //         beginNo (total - (page - 1) * display)
    // page = 1, 1000
    // page = 2, 980
    // page = 3, 960
    
    // 뷰로 전달할 데이터를 모델이 저장
    model.addAttribute("beginNo", total - (page - 1) * display);
    model.addAttribute("bbsList", bbsList);
    model.addAttribute("paging", myPageUtils.getPaging(request.getContextPath() + "/bbs/list.do", null, display));
    
    
  }
  
  // 답글 등록
  @Override
  public int registerReply(HttpServletRequest request) {
    
    // 요청 파라미터 
    // 답글 정보 : userNo, contents
    // 원글 정보 : depth, groupNo, groupOrder
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    String contents = request.getParameter("contents");
    int depth = Integer.parseInt(request.getParameter("depth"));
    int groupNo = Integer.parseInt(request.getParameter("groupNo"));
    int groupOrder = Integer.parseInt(request.getParameter("groupOrder"));
    
    // 원글 BbsDto 객체 생성
    BbsDto bbs = BbsDto.builder()
                      .depth(depth)
                      .groupNo(groupNo)
                      .groupOrder(groupOrder)
                     .build();
    
    // 기존 답글들의 groupOrder 업데이트
    bbsMapper.updateGroupOrder(bbs);  // updateCount : 업데이트한 행의 개수 (insertCount 와 같음) -> 첫 답글의 경우는 O으로 떨어짐 -> 즉, count값 넣지 말고 구현 (반환 값이 있다고 꼭 받아야하는 것은 아님)
    
    // 답글 BbsDto 객체 생성
    UserDto user = new UserDto();
    user.setUserNo(userNo);
    BbsDto reply = BbsDto.builder()
                        .user(user)
                        .contents(contents)
                        .depth(depth + 1)            // 원글 DEPTH + 1
                        .groupNo(groupNo)
                        .groupOrder(groupOrder + 1)  // 원글 GROUP_ORDER + 1
                       .build();
    
    // 새 답글의 추가
    return bbsMapper.insertReplay(reply);  // 새 답글 삽입 성공 : 1 , 실패 : 0
    
  }
  
  // 삭제
  @Override
  public int removeBbs(int bbsNo) {
    return bbsMapper.removeBbs(bbsNo);
  }

  // 검색
  @Override
  public void loadBbsSearchList(HttpServletRequest request, Model model) {
    
    // 요청 파라미터
    String column = request.getParameter("column");
    String query = request.getParameter("query");

    // 검색 데이터 개수를 구할 때 사용할 Map 생성
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("column", column);
    map.put("query", query);
    
    // 검색 데이터 개수 구하기
    int total = bbsMapper.getSearchCount(map);
    
    // 한 페이지에 표시할 검색 데이터 개수
    int display = 20;
    
    // 현재 페이지 번호
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    
    // 페이지 처리에 필요한 처리
    myPageUtils.setPaging(total, display, page);
    
    // 검색 목록을 가져오기 위해서 기존 Map 에 begin 과 end 를 추가
    map.put("begin", myPageUtils.getBegin());
    map.put("end", myPageUtils.getEnd());          // 여기까지 map에 총 4개의 데이터 저장되어 있음 (column, query, begin, end)
  
    // 검색 목록 가져오기                               
    List<BbsDto> bbsList = bbsMapper.getSearchList(map);                     

    // 뷰로 전달할 데이터
    model.addAttribute("beginNo", total - (page - 1) * display);
    model.addAttribute("bbsList", bbsList);
    // 메소드 오버로딩(파라미터는 달라야함, 같으면 x)으로 상황에 맞게 여러개의 메소드를 생성(같은 이름의 메소드)해서 사용하여도 됨 => getPaging을 메소드 오버로딩으로 구현 (MyPageUtils 내 구현됨)
    model.addAttribute("paging", myPageUtils.getPaging(request.getContextPath() + "/bbs/search.do"
                                                     , "" 
                                                     , 20
                                                     , "column=" + column + "&query" + query)); // 검색 페이지를 전달해야함 
    
  }

}
