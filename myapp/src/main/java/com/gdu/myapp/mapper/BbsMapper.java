package com.gdu.myapp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.myapp.dto.BbsDto;

@Mapper // 나는 mybatis Mapper 임을 명시함 : 구현클래스가 없는 인터페이스 (Marker Interface)
public interface BbsMapper {
  int insertBbs(BbsDto bbs);
  int getBbsCount();
  List<BbsDto> getBbsList(Map<String, Object> map);
  int updateGroupOrder(BbsDto bbs);                      // 원글이 넘어감
  int insertReplay(BbsDto reply);                        // 답글이 넘어감
  int removeBbs(int bbsNo);
  int getSearchCount(Map<String, Object> map);           // 검색 결과 개수 반환
  List<BbsDto> getSearchList(Map<String, Object> map);   // 검색 결과 목록 반환
}
