package com.gdu.prj09.utils;

import lombok.Data;

// 프로젝트할 때는 jspstudy_04_dbcp_MyPageUtils 와 같이 사용되어야 함

@Data
public class MyPageUtils {
  
  // 한 페이지
  private int total;
  private int display;
  private int page;          
  private int begin;
  private int end;
  
  // 전체 페이지
  private int pagePerBlock = 10;
  private int totalPage;
  private int beginPage;
  private int endPage;
  
  public void setPaging(int total, int display, int page) {
    
    // begin 과 end 를 구하는데 필요한 요소 (Service 로 부터 받아와서 Map 으로 구성함 -> List 목록화)
    this.total = total;       // DB 에서 받아옴
    this.display = display;
    this.page = page;

    /*
     * 1 - 1  20
     * 2 - 21 40
     * 3 - 41 60
     */
    
    begin = (page - 1) * display + 1;
    end = begin + display - 1;

    /*
     * total display totalPage
     * 1000  20      1000 / 20 = 50.0 = 50
     * 1001  20      1001.0 / 20.0 = 50.x = 51  => 정수 올림 처리
     */
    
    totalPage = (int) Math.ceil((double)total / display); 
    beginPage = ((page - 1) / pagePerBlock) * pagePerBlock + 1;
    endPage = Math.min(totalPage, beginPage + pagePerBlock - 1);
    
  }
  
  /*
     MyPageUils : getAsyncPaging() 에서 제작
    -------------------------------------------
    <a href="javascropt:fnPaging(10)"> < </a>
    <a href="javascropt:fnPaging(11)"> 11 </a>
    <a href="javascropt:fnPaging(12)"> 12 </a>
    .. 
    <a href="javascropt:fnPaging(20)"> 20 </a>
   
   
     var page = 1; --> 전역변수
     const fnPaging = (p)=>{
      page = p;           -- 1. 파라미터로 넘어온 page값 으로 적용
      fnMemberList();     -- 2 .명단 보여주기 함수 호출
    }
   */
  
   public String getAsyncPaging() {
     
     StringBuilder builder = new StringBuilder();
     
     // <
     if(beginPage == 1) {                     // 시작페이지가 1일 때, href 적용 x
       builder.append("<a>&lt;</a>");
     } else {
       builder.append("<a href=\"javascript:fnPaging(" + (beginPage - 1) + ")\">&lt;</a>");
     }
     
     // 1 2 3 4 5 6 7 8 9 10
     for(int p = beginPage; p <= endPage; p++) {
       if(p == page) {                        // 현재 페이지와 페이지가 같을 때 href 적용 x
         builder.append("<a>" + p + "</a>");
       } else {
         builder.append("<a href=\"javascript:fnPaging(" + p + ")\">" + p + "</a>");
       }
     }
     
     // >
     if(endPage == totalPage) {               // 마지막페이지 일 때, href 적용 x
       builder.append("<a>&gt;</a>");
     } else {
       builder.append("<a href=\"javascript:fnPaging(" + (endPage + 1) + ")\">&gt;</a>");
     }

     return builder.toString();
   }
  
}
