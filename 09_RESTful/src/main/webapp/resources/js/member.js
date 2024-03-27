/*************************************************
 * 파일명 : member.js
 * 설  명 : 회원 관리 JavaScript
 *
 * 수정일      수정자  Version   Function 명
 * -------------------------------------------
 * 2024.03.25  민경태  1.0       fnInit
 * 2024.03.25  민경태  1.1       fnRegisterMember
 * 2023.03.26  민경태  1.2       fnGetContextPath
 * 2024.03.26  민수지  1.3       fnGetMemberList
 * 2024.03.26  민수지  1.4       fnPaging
 * 2024.03.26  민수지  1.5       fnChangeDisplay
 * 2024.03.26  민수지  1.6       fnGetMemberByNo
 * 2024.03.27  민수지  1.7       fnModifyMember
 * 2024.03.27  민수지  1.8       fnRemoveMember
 * 2024.03.27  민수지  1.9       fnRemoveMembers
 *************************************************/

// 전역변수 (vXXX)
var vPage = 1;
var vDisplay = 20;

// jQuery 객체 선언 (jqXXX)
var jqMembers = $('#members');
var jqTotal = $('#total');
var jqPaging = $('#paging');
var jqDisplay = $('#display');
var jqEmail = $('#email');
var jqName = $('#name');
var jqMemberNo = $('#member-no');
var jqZonecode = $('#zonecode');
var jqAddress = $('#address');
var jqDetailAddress = $('#detailAddress');
var jqExtraAddress = $('#extraAddress');
var jqBtnInit = $('#btn-init');
var jqBtnRegister = $('#btn-register');
var jqBtnModify = $('#btn-modify');
var jqBtnRemove = $('#btn-remove');
var jqBtnSelectRemove = $('#btn-select-remove');

/*************************************************
 * 함수명 : fnInit
 * 설  명 : 입력란에 입력된 데이터를 모두 초기화
 * 인  자 : 없음
 * 사용법 : fnInit()
 * 작성일 : 2024.03.26
 * 작성자 : 이런저런 개발팀 민경태
 *
 * 수정일     수정자  수정내용
 * --------------------------------
 * 2024.03.25 민경태  입력란 초기화
 *************************************************/

const fnInit = ()=>{
  jqEmail.val('').prop('disabled', false);
  jqName.val('');
  $('#none').prop('checked', true); // 선택안함 radio 버튼을 체크를 기본으로 해줌 => 자매품 : $('#none').attr('checked', 'checked');
  jqZonecode.val('');
  jqAddress.val('');
  jqDetailAddress.val('');
  jqExtraAddress.val('');
  jqBtnRegister.prop('disabled', false);
  jqBtnModify.prop('disabled', true);
  jqBtnRemove.prop('disabled', true);
}
 
const fnGetContextPath = ()=>{
  const host = location.host;  /* localhost:8080 */
  const url = location.href;   /* http://localhost:8080/mvc/getDate.do */
  const begin = url.indexOf(host) + host.length;
  const end = url.indexOf('/', begin + 1);
  return url.substring(begin, end);
}

// 멤버 등록
const fnRegisterMember = ()=>{      
$.ajax({
    // 요청
    type: 'POST',
    url: fnGetContextPath() + '/members', // jsp에서는 인식되나 js에서는 인식 안됨
    contentType: 'application/json',      // (java로) 보내는 데이터의 타입
    data: JSON.stringify({                // 보내는 데이터(문자열 형식의 JSON 데이터) => json 데이터를 보낼 때 (js -> server) 문자열로 보내야 함 (js표준 객체화 함수)
      'email': jqEmail.val(),
      'name': jqName.val(),
      'gender': $(':radio:checked').val(), // radio에서 체크된 요소의 값(value)
      'zonecode': jqZonecode.val(),
      'address': jqAddress.val(),
      'detailAddress': jqDetailAddress.val(),
      'extraAddress': jqExtraAddress.val()
    }),
    // 응답
    dataType: 'json'  // 받는 데이터 타입
  }).done(resData=>{  // resData = {"insertCount": 2}
    if(resData.insertCount === 2){
      alert('정상적으로 등록되었습니다.');
      fnInit();
      fnGetMemberList();
    }
  }).fail(jqXHR=>{     // UNIQUE 관련 오류(eamil 중복)는 fail 로 넘어옴
    alert(jqXHR.responseText);
  })
}


// 멤버 명단 조회 - 함수 표현식 (함수 만들기)
const fnGetMemberList = ()=>{
  $.ajax({
    type: 'GET',
    url: fnGetContextPath() + '/members/page/' + vPage + '/display/' + vDisplay,
    dataType: 'json',      // 응답 데이터 타입
    success: (resData)=>{  /*
                              resData = {
                                "members": [
                                  {
                                    "addressNo": 1,
                                    "zonecode": '12345',
                                    "address": '서울시 구로구'
                                    "detailAddress": '디지털로',
                                    "extraAddress": '(가산동)',
                                    "member": {
                                      "memberNo": 1,
                                      "email": 'aaa@bbb',
                                      "name": 'gildong',
                                      "gender": 'none'
                                    }
                                  }, ...
                                ],
                                "total": 30,
                                "paging": '< 1 2 3 4 5 6 7 8 9 10 >'
                              }
                           */
      jqTotal.html('총 회원 ' + resData.total + '명');
      jqMembers.empty();
      $.each(resData.members, (i, member)=>{    // ajax 내부 처리 방법 (success, error) , ajax 외부 처리 방법 (done, fail)
        let str = '<tr>';
        str += '<td><input type="checkbox" class="chk-member" value="' + member.member.memberNo + '"></td>'; // 다중 삭제에 필요한 checkbox => memberNo가 필요
        str += '<td>' + member.member.email + '</td>';
        str += '<td>' + member.member.name + '</td>';
        str += '<td>' + member.member.gender + '</td>';
        str += '<td><button type="button" class="btn-detail" data-member-no="' + member.member.memberNo + '">조회</button></td>';
        str += '</tr>';
        jqMembers.append(str);
      })
      jqPaging.html(resData.paging);   // append도 사용 가능
    },
    error: (jqXHR)=>{
      alert(jqXHR.statusText + '(' + jqXHR.status + ')');
    }
  })
}

// MyPageUtils 클래스의 getAsyncPaging() 메소드에서 만든 <a href="javascript:fnPaging()"> 에 의해서 실행되는 함수
const fnPaging = (p)=>{  // 사용자가 10을 누르면 -> p로 10이 넘어옴 -> 전역변수인 vPage 를 넘어온 값인 10으로 바꾸어 줌 -> 명단 보기 함수 호출 -> 바뀐 vPage 번호의 명단을 보여줌
  vPage = p;
  fnGetMemberList();
}
 
const fnChangeDisplay = ()=>{
  vDisplay = jqDisplay.val();
  fnGetMemberList();
}

// 상세보기 -- 함수 표현식 (함수 만들기)
const fnGetMemberByNo = (evt)=>{
  $.ajax({
    type: 'GET',
    url: fnGetContextPath() + '/members/' + evt.target.dataset.memberNo, // evt.target : 클릭한 상세버튼 , $(evt.target).data('memberNo') => jQuery 버전(자매품) | evt.target.dataset.memberNo => js 버전
    dataType: 'json'
  }).done(resData=>{  /* resData = {
                           "addressList": [
                             {
                               "addressNo": 1,
                               "zonecode": "12345",
                               "address": "서울시 구로구 디지털로",
                               "detailAddress": "카카오",
                               "extraAddress": "(가산동)"
                             },
                             ...
                           ],
                           "member": {
                             "memberNo": 1,
                             "email": "email@email.com",
                             "name": "gildong",
                             "gender": "man"
                           }
                         }
                      */
    fnInit();
    if(resData.member !== null){
      jqMemberNo.val(resData.member.memberNo);
      jqEmail.val(resData.member.email).prop('disabled', true);               // 상세보기 할 때 이메일은 수정되지 않도록 막음
      jqName.val(resData.member.name);
      $(':radio[value=' + resData.member.gender + ']').prop('checked', true); // gender 값에 맞게 radio 버튼을 체크됨
      jqBtnRegister.prop('disabled', true);
      jqBtnModify.prop('disabled', false);
      jqBtnRemove.prop('disabled', false);
    }
    if(resData.addressList.length !== 0){
      jqZonecode.val(resData.addressList[0].zonecode);
      jqAddress.val(resData.addressList[0].address);
      jqDetailAddress.val(resData.addressList[0].detailAddress);
      jqExtraAddress.val(resData.addressList[0].extraAddress);
    }
  }).fail(jqXHR=>{
    alert(jqXHR.statusText + '(' + jqXHR.status + ')');
  })
}

// 수정
const fnModifyMember = ()=>{
  $.ajax({
    type: 'PUT',
    url: fnGetContextPath() + '/members',
    contentType: 'application/json',         // 보내는 데이터의 타입 (java로 보내기 때문에 java에서 알 수 있게 작성)
    data: JSON.stringify({                   // 중괄호 넣어서 객체로 만들어 줌
       'memberNo': jqMemberNo.val(),         // 입력된 memberNo 값
       'name': jqName.val(),                 // 입력된 name 값
       'gender': $(':radio:checked').val(),  // 체크 된 gender 값  
       'zonecode': jqZonecode.val(),
       'address': jqAddress.val(),
       'detailAddress': jqDetailAddress.val(),
       'extraAddress': jqExtraAddress.val()
    }),
    dataType: 'json',
    success: (resData)=> {   // resData = {"updateCount": 2}
      if(resData.updateCount === 2){
        alert('회원 정보가 수정되었습니다.');
        fnGetMemberList();
      } else {
        alert('회원 정보가 수정되지 않았습니다.');
      }
    },
    error: (jqXHR)=>{
      alert(jqXHR.statusText + '(' + jqXHR.status + ')');
    }
  })  
}

// 단일 삭제
const fnRemoveMember = ()=>{
  if(!confirm('삭제할까요?')){
    return;                     // 함수 종료하여 취소시킴
  }
  $.ajax({
    type: 'DELETE', // GET 방식과 동일하게 동작
    url: fnGetContextPath() + '/member/' + jqMemberNo.val(),
    dataType: 'json'
  }).done(resData=>{  // {"deleteCount": 1}
    if(resData.deleteCount === 1){
      alert('회원 정보가 삭제되었습니다.');
      fnInit();  // 입력란 초기화
      fnGetMemberList();
    } else {
      alert('회원 정보가 삭제되지 않았습니다.');
    }
  }).fail(jqXHR=>{
    alert(jqXHR.statusText + '(' + jqXHR.status + ')');
  })
} 

// 다중 삭제
const fnRemoveMembers= ()=>{
  // 체크된 요소를 배열에 저장하기
  let arr = [];
  $.each($('.chk-member'), (i, chk)=>{ // 배열에 요소를 꺼내면 jQuery가 아닌 js 객체 -> jQuery wrapper로 묶어서 다시 jQuery 객체로 바꾸어야 jQuery 메소드 사용 가능
    if($(chk).is(':checked')){      // jQuery
      arr.push(chk.value);          // js  | push: javaScript 표준 메소드
    }
  })
  // 체크된 요소가 없으면 함수 종료 (배열이 비어있음)
  if(arr.length === 0) {
    alert('선택된 회원 정보가 없습니다.');
    return;
  }
  // 삭제 확인
  if(!confirm('선택된 회원 정보를 모두 삭제할까요?')){
    return;
  }
  // 삭제
  $.ajax({
    type: 'DELETE',
    url: fnGetContextPath() + '/members/' + arr.join(','), // join: 배열 사이사이에 , 를 넣어줌 (front에서 보낼 때는 문자열로 만들어서 전송-> java에서 다시 split으로 쪼개서 List에 다시 넣음) 
    dataType: 'json',                                      // [1, 2, 3, 4 ...]
    success: (resData)=>{                                  // {"deleteCount": 3}
      if(resData.deleteCount === arr.length){              // 체크된 요소가 3이면 배열의 길이도 3
        alert('선택된 회원 정보가 삭제되었습니다.');
        vPage = 1;          // 목록 갱신 -> 1페이지로 돌아감
        fnGetMemberList();  // 목록 보여주기
      } else {
        alert('선택된 회원 정보가 삭제되지 않았습니다.');
      }
    }, 
    error: (jqXHR)=>{
      alert(jqXHR.statusText + '(' + jqXHR.status + ')');
    }
  })
}

 
// 함수 호출 및 이벤트 
fnInit();                                    // 일반 실행 (Init은 2가지 필요)
jqBtnInit.on('click', fnInit);               // 초기화 버튼 클릭 (Init은 2가지 필요)
jqBtnRegister.on('click', fnRegisterMember); // 등록 버튼 클릭
fnGetMemberList();
jqDisplay.on('change', fnChangeDisplay);
$(document).on('click', '.btn-detail', (evt)=>{ fnGetMemberByNo(evt); }); // (이벤트타입, 이벤트대상, 이벤트함수(evt 객체를 함수로 전달함))
jqBtnModify.on('click', fnModifyMember);
jqBtnRemove.on('click', fnRemoveMember);
jqBtnSelectRemove.on('click', fnRemoveMembers);