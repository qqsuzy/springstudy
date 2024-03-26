package com.gdu.prj09.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gdu.prj09.service.MemberService;

import lombok.RequiredArgsConstructor;

/*
 *  RESTful
 *  1. REpresentation State Transfer
 *  2. 요청 주소를 작성하는 한 방식이다.
 *  3. 요청 파라미터를 ? 뒤에 추가하는 Query String 방식을 사용하지 않는다.
 *  4. 요청 파라미터를 주소에 포함하는 Path Variable 방식을 사용하거나, 요청 본문에 포함하는 방식을 사용한다.
 *  5. 요청의 구분을 "주소 + 메소드" 조합으로 구성한다.
 *  6. CRUD 요청 예시
 *  
 *            |  URL                       | Method
 *     -------|----------------------------|---------         
 *    1) 목록 | /members                   |  GET
 *            | /members/page/1            |
 *            | /members/page/1/display/20 |
 *    2) 상세 | /members/1                 |  GET
 *    3) 삽입 | /members                   |  POST   => 삽입과 수정은 방식으로 구분 가능 (주소 같음, POST or PUT)
 *    4) 수정 | /members                   |  PUT
 *    5) 삭제 | /members/1                 |  DELETE  => 상세와 삭제는 방식으로 구분 가능 (주소 같음, GET or DELETE)
 *            | /members/1,2,3     
 */

@RequiredArgsConstructor
@Controller
public class MemberController {

  private final MemberService memberService;
  
  @GetMapping("/admin/member.do")
  public void adminMember() {   
    // 반환 타입이 void 인 경우 주소를 jsp 경로로 인식함
    // /admin/member.do ======> /WEB-INF/views/admin/member.jsp
  }
  
 @PostMapping(value="/members", produces="application/json")
 // 문자열 형식의 json 데이터를 분리해서 각각의 DTO(MemberDto , AddressDto)로 받을 수 없기 때문에 2가지 방법으로 처리 가능함 => 1. 새로운 DTO 생성(한 번에 받을 수 있는 DTO) 2. MAP 으로 받기
 public ResponseEntity<Map<String, Object>> registerMember(@RequestBody Map<String, Object> map  // @RequestBody : 요청 본문 (받아내기 위해 사용)
                                                           , HttpServletResponse response) { 
   
   return memberService.registerMember(map, response);
 }
  
 // 멤버 명단 조회
 // value 의 경로변수(PathVariable)는 중괄호{} 로 표기함
 @GetMapping(value="/members/page/{p}/display/{dp}", produces="application/json") // produces(controller에서 주는 것) : ajax에서 dataType(ajax에서 받는 것)과 일치해야 함
 public ResponseEntity<Map<String, Object>> getMembers(@PathVariable(value="p", required = false) Optional<String> optPage        // 경로변수를 int page에 저장 => 변수명: value값 (중괄호 내의 변수와 같음) , page 값의 전달이 필수가 아닐 경우 (null값 전달) => Optional 을 감싸서 null일 경우 처리될 수 있도록 함 
                                                     , @PathVariable(value="dp", required = false) Optional<String> optDisplay) { // Optional 타입은 String => 파라미터와 동일함
   int page = Integer.parseInt(optPage.orElse("1"));
   int display = Integer.parseInt(optDisplay.orElse("20"));
   return memberService.getMembers(page, display);      // memberService의 getMembers로 값 전달
 }
 
 // 상세보기
 @GetMapping(value="members/{memberNo}", produces="application/json")
 public ResponseEntity<Map<String, Object>> getMemberByNo(@PathVariable(value="memberNo", required=false) Optional<String> opt) {
   int memberNo = Integer.parseInt(opt.orElse("0"));
   return memberService.getMemberByNo(memberNo);
 }
 
 
}
