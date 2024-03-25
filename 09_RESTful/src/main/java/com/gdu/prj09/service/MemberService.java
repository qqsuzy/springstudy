package com.gdu.prj09.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import com.gdu.prj09.dto.MemberDto;

// Service 는 여러개의 Dao 를 호출 할 수 있음 (1 : M) => 1 : 1 아님
public interface MemberService {

  /*
   *  파라미터를 받아오는 3가지 방법 : HttpServleRequest, @RequestParam , MyPageUtils
   *   
   *  /prj09/member/page/1/display/20  => @PathVariable (경로에 포함된 데이터, 이번 프로젝트에서 활용되는 방법)   
   *  /prj09/members?page=1&display=20 => @RequestParam (jsp 04_dbcp 에서 활용됨)
   *  
   */
  
  // single page 구현을 위해 service 가 반환하는 값은 통일해야함
  ResponseEntity<Map<String, Object>> getMembers(int page, int display);  // ResponseEntity : @ResponseBody 가 품고 있는 클래스 , 타입은 제네릭 타입에 명시 => Map에 List와 int 값을 담음
  ResponseEntity<MemberDto> getMemberByNo(int memberNo);                  // 상세보기
  ResponseEntity<Map<String, Object>> registerMember(Map<String, Object> map, HttpServletResponse response); // response는 예외 발생시 예외메시지 만들어서 응답해주는 용도로 사용
  ResponseEntity<Map<String, Object>> modifyMember(MemberDto member);     // 회원 정보 수정 (Email은 아이디의 역할을 함) 
  ResponseEntity<Map<String, Object>> removeMember(int memberNo);         // 회원 1명 삭제
  ResponseEntity<Map<String, Object>> removeMembers(String memberNoList); // 회원 여러명 삭제
  // @DeleteMapping 주소창에 정보가 전달 되는 방식 (GET 방식) , 정보가 1개일 경우 : int , 정보가 여러개 일 경우 : String 으로 받아옴 
}
