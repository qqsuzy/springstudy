/**
 * 
 */
 
// 전역 변수 (vXXX)
var vPage = 1;
var vDisplay = 20; 
 
// jQuery 객체 선언 (jqXXX)
var jqMembers = $('#members');
var jqTotal = $('#total');
var jqPaging = $('#paging');
var jqDisplay = $('#display');
var jqEmail = $('#email');
var jqName = $('#name');
var jqZonecode = $('#zonecode');
var jqAddress = $('#address');
var jqDetailAddress = $('#jqDetailAddress');
var jqExtraAddress = $('#jqExtraAddress');
var jqBtnInit = $('#btn-init');
var jqBtnRegister = $('#btn-register');
var jqBtnModify = $('#btn-modify');
var jqBtnRemove = $('#btn-remove');
var jqBtnSelectRemove = $('#btn-select-remove');
   
// 함수 표현식 (함수 만들기) (fnXXX)
const fnInit = ()=>{
// 초기화     
jqEmail.val('');
jqName.val('');
$('#none').prop('checked', true); // 선택안함 radio 버튼을 체크를 기본으로 해줌 => 자매품 : $('#none').attr('checked', 'checked');
jqZonecode.val('');
jqAddress.val('');
jqDetailAddress.val('');
jqExtraAddress.val('');
 }
 
const getContextPath = ()=>{
  const host = location.host; /* localhost:8080 */
  const url = location.href   /* http://localhost:8080/mvc/getDate.do */
  const begin = url.indexOf(host) + host.length;
  const end = url.indexOf('/', begin + 1);
  return url.substring(begin, end);
}

// 멤버 등록      
const fnRegisterMember = ()=>{
  $.ajax({
   // 요청 (요청 본문(body)에 데이터를 실어서 보냄)
      type: 'POST',
      url:  getContextPath() + '/jqMembers',    // jsp에서는 인식되나 js에서는 인식 안됨
      contentType: 'application/json',        // (java로) 보내는 데이터의 타입
      data: JSON.stringify({                  // 보내는 데이터(문자열 형식의 JSON 데이터) => json 데이터를 보낼 때 (js -> server) 문자열로 보내야 함 (js표준 객체화 함수)
        'jqEmail': jqEmail.val(),
        'name': jqName.val(),
        'gender': $(':radio:checked').val(),  // radio에서 체크된 요소의 값(value)
        'jqZonecode': jqZonecode.val(),
        'jqAddress': jqAddress.val(),
        'jqDetailAddress': jqDetailAddress.val(),
        'jqExtraAddress': jqExtraAddress.val()
      }),
      // 응답
      dataType: 'json'  // 받는 데이터 타입
    }).done(resData=>{  // resData = {"insertCount": 2}
      if(resData.insertCount === 2){
        alert('정상적으로 등록되었습니다.');
        fnInit();
        fnGetMemberList();
      }
    }).fail(jqXHR=>{    // UNIQUE 관련 오류(eamil 중복)는 fail 로 넘어옴
      alert(jqXHR.responseText);
    })

 }


// 멤버 명단 조회 - 함수 표현식 (함수 만들기)
const fnMemberList = ()=>{
  $.ajax({
    type: 'GET',
    url: getContextPath() + '/jqMembers/vPage/' + vPage + '/vDisplay/' + vDisplay,
    dataType: 'json',       // 응답 데이터 타입
    success: (resData)=>{   /*   
                               resData = {
                                "jqMembers":[
                                  {"jqAddressNo": 1,
                                   "jqZonecode": '12345',
                                   "jqAddress": '서울시 구로구',
                                   "jqDetailAddress": '디지털로',
                                   "jqExtraAddress": '(가산동)',
                                   "member": {
                                     "memberNo": 1,
                                     "jqEmail": 'aaa@bbb',
                                     "name": 'gildong',
                                     "gender": 'none'
                                   }
                                  }, ...
                                ], 
                                 "jqTotal": 30,
                                 "jqPaging": '<1 2 3 4 5 6 7 8 9 10>'
                              }
                            */
    
                            
      jqTotal.html('총 회원 ' + resData.jqTotal + '명');
        jqMembers.empty(); 
        $.each(resData.jqMembers, (i, member)=>{  // ajax 내부 처리 방법 (success, error) , ajax 외부 처리 방법 (done, fail)
          let str = '<tr>';
          str += '<td><input type="checkbox" class="chk-member" value="' + member.member.memberNo + '"></td>'; // 다중 삭제에 필요한 checkbox => memberNo가 필요
          str += '<td>' + member.member.jqEmail + '</td>';
          str += '<td>' + member.member.name + '</td>';
          str += '<td>' + member.member.gender + '</td>';
          str += '<td><button type="button" class="btn-detail" data-member-no="' + member.member.memberNo + '">조회</button></td>';
          str += '</tr>';
          jqMembers.append(str);
        })
        jqPaging.html(resData.jqPaging); // append도 사용 가능
      },
      error: (jqXHR)=>{
        alert(jqXHR.statusText + '(' + jqXHR.status + ')');
      }                           
  })  
}  
 
 const fnPaging = (p)=>{  // 사용자가 10을 누르면 -> p로 10이 넘어옴 -> 전역변수인 vPage 를 넘어온 값인 10으로 바꾸어 줌 -> 명단 보기 함수 호출 -> 바뀐 vPage 번호의 명단을 보여줌
   vPage = p;
   fnMemberList();
 }
 
 const fnChangeDisplay = ()=>{
    vDisplay = jqDisplay.val();
    fnMemberList();
  }

 
 
// 함수 호출 및 이벤트 
fnInit();                                  // 일반 실행 (Init은 2가지 필요)
jqBtnInit.on('click', fnInit);               // 초기화 버튼 클릭 (Init은 2가지 필요)
jqBtnRegister.on('click', fnRegisterMember); // 등록 버튼 클릭
fnMemberList();
jqDisplay.on('change', fnChangeDisplay);