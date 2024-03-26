package com.gdu.prj09.service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gdu.prj09.dao.MemberDao;
import com.gdu.prj09.dto.AddressDto;
import com.gdu.prj09.dto.MemberDto;
import com.gdu.prj09.utils.MyPageUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  // MemberDao 와 MyPageUtils 받아옴
  private final MemberDao memberDao;
  private final MyPageUtils myPageUtils;
  
  @Override
  public ResponseEntity<Map<String, Object>> getMembers(int page, int display) { // 반환타입이 Map 으로 잡혀있는 이유 => total 과 members 저장하기 위함
    
    // 전체 개수
    int total = memberDao.getTotalMemberCount();
    
    myPageUtils.setPaging(total, display, page);
    
    Map<String, Object> params = Map.of("begin", myPageUtils.getBegin()
                                       , "end", myPageUtils.getEnd());
    
    // 목록
    List<AddressDto> members = memberDao.getMemberList(params);
   
    // Map으로 members, total 값과 상태값 저장 후 반환 -> ajax 에 .done()이 결과값을 받음
    return new ResponseEntity<Map<String,Object>>(Map.of("members", members
                                                       , "total", total
                                                       , "paging", myPageUtils.getAsyncPaging())
                                                  , HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Map<String, Object>> getMemberByNo(int memberNo) {
    
    // Address 목록 가져올 때 필요한 작업
    int total = memberDao.getTotalAddressCountByNo(memberNo);
    int page = 1;
    int display = 20;
    
    myPageUtils.setPaging(total, display, page);
    
    // Address 목록 가져오기
    Map<String, Object> params = Map.of("memberNo", memberNo, "begin"
                                       , myPageUtils.getBegin(), "end"
                                       , myPageUtils.getEnd());
    
    
    List<AddressDto> addressList = memberDao.getAddressListByNo(params);
    
    // 회원정보 가져오기
    MemberDto member = memberDao.getMemberByNo(memberNo);
    
    return new ResponseEntity<Map<String,Object>>(Map.of("addressList", addressList
                                                        , "member", member)
                                                 , HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Map<String, Object>> registerMember(Map<String, Object> map
                                                          , HttpServletResponse response) {
    
    ResponseEntity<Map<String, Object>> result = null;
    
    try {
      
      MemberDto member = MemberDto.builder()
                          .email((String)map.get("email")) // map 에서 데이터 추출해서 캐스팅 (map 에서 추출한 데이터는 요상해서 캐스팅 꼭 필요!)
                          .name((String)map.get("name"))
                          .gender((String)map.get("gender"))
                         .build();
      
      int insertCount = memberDao.insertMember(member);
      
      AddressDto address = AddressDto.builder()
                             .zonecode((String)map.get("zonecode"))
                             .address((String)map.get("address"))
                             .detailAddress((String)map.get("detailAddress"))
                             .extraAddress((String)map.get("extraAddress"))
                             .member(member)
                            .build();
      
      insertCount += memberDao.insertAddress(address);
      
      result = new ResponseEntity<Map<String,Object>>(
               Map.of("insertCount", insertCount),   // Map.of (보내고자 하는 insertCount 데이터, 상태 전달)
               HttpStatus.OK);   
      
    } catch (DuplicateKeyException e) {  // catch(Exception e) { 이름 확인 : e.getClass().getName(); }
      
      try {
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("이미 가입된 이메일입니다.");  // jqXHR 객체의 responseText 속성으로 확인 가능
        out.flush();
        out.close();
        
      } catch (Exception e2) {
        e.printStackTrace();
      }
      
    }
    
    return result;
    
  }

  @Override
  public ResponseEntity<Map<String, Object>> modifyMember(MemberDto member) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<Map<String, Object>> removeMember(int memberNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseEntity<Map<String, Object>> removeMembers(String memberNoList) {
    // TODO Auto-generated method stub
    return null;
  }

}
