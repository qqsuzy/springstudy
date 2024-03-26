package com.gdu.prj09.dao;

import java.util.List;
import java.util.Map;

import com.gdu.prj09.dto.AddressDto;
import com.gdu.prj09.dto.MemberDto;

public interface MemberDao {
  int insertMember(MemberDto member);
  int insertAddress(AddressDto address);
  int updateMember(MemberDto member);
  int deleteMember(int memberNo);
  int deleteMembers(List<String> memberNoList);
  int getTotalMemberCount();
  List<AddressDto> getMemberList(Map<String, Object> map);
  MemberDto getMemberByNo(int memberNo);               // 회원정보 1개 가져옴 (1)
  int getTotalAddressCountByNo(int memberNo);         // 주소 Total값 , memberNo로 조회할 수 있도록 함
  List<AddressDto> getAddressListByNo(Map<String, Object> map);  // 주소정보 여러개 가져옴 (M) , map에 begin/end/memberNo 담겨있음
  
}
