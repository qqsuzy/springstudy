package com.gdu.myapp.utils;

import org.springframework.stereotype.Component;

import lombok.Data;

// 프로젝트할 때 활용하기!

@Component
@Data
public class MyPageUtils {
  
  // 한 페이지
  private int total;     // 전체 게시글 개수                      (DB에서 구한다.)
  private int display;   // 한 페이지에 표시할 게시글 개수        (요청 파라미터로 받는다.)
  private int page;      // 현재 페이지 번호                      (요청 파라미터로 받는다.)
  private int begin;     // 한 페이지에 표시할 게시글의 시작 번호 (계산한다.)
  private int end;       // 한 페이지에 표시할 게시글의 종료 번호 (계산한다.)

  // 전체 페이지
  private int pagePerBlock = 10;  // 한 블록에 표시할 페이지 링크의 개수      (임의로 결정한다.)
  private int totalPage;          // 전체 페이지 개수                         (계산한다.)
  private int beginPage;          // 한 블록에 표시할 페이지 링크의 시작 번호 (계산한다.)
  private int endPage;            // 한 블록에 표시할 페이지 링크의 종료 번호 (계산한다.)
  
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
 
  // MVC 패턴용 
 public String getPaging(String requestURI, String sort, int display) {
    
    StringBuilder builder = new StringBuilder();
    
    // <
    if(beginPage == 1) {
      builder.append("<div class=\"dont-click\">&lt;</div>");
    } else {
      builder.append("<div><a href=\"" + requestURI + "?page=" + (beginPage - 1) + "&sort=" + sort + "&display=" + display + "\">&lt;</a></div>");
    }
    
    // 1 2 3 4 5 6 7 8 9 10
    for(int p = beginPage; p <= endPage; p++) {
      if(p == page) {
        builder.append("<div><a class=\"current-page\" href=\"" + requestURI + "?page=" + p + "&sort=" + sort + "&display=" + display + "\">" + p + "</a></div>");
      } else {
        builder.append("<div><a href=\"" + requestURI + "?page=" + p + "&sort=" + sort + "&display=" + display + "\">" + p + "</a></div>");
      }
    }
    
    // >
    if(endPage == totalPage) {
      builder.append("<div class=\"dont-click\">&gt;</div>");
    } else {
      builder.append("<div><a href=\"" + requestURI + "?page=" + (endPage + 1) + "&sort=" + sort + "&display=" + display + "\">&gt;</a></div>");
    }
    
    return builder.toString();
    
  }
  
 // ajax 용 (비동기 작업) -> 페이지 이동 x (위에 getPaging는 페이지를 이동함)
   public String getAsyncPaging() {
     
     StringBuilder builder = new StringBuilder();
     
     // <
     if(beginPage == 1) {                     // 시작페이지가 1일 때, href 적용 x
       builder.append("<a>&lt;</a>");
     } else {
       builder.append("<a href=\"javascript:fnPaging(" + (beginPage - 1) + ")\">&lt;</a>");  // 페이지 이동 x , javaScript 함수 호출
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
   
   // 검색 페이징 
   public String getPaging(String requestURI, String sort, int display, String params) {  // params : 검색 조건 파라미터 (column=xx&query=yy   -> service에서 문자열로 만들어서 params로 넘김) 
     
     /*
      *  /bbs/search.do?page=2&sort=&display=20
      *  
      *  <검색 페이징 유의점>
      *  column 과 query 가 주소 내에 포함되지 않으면 검색 페이징(1페이지-> 2페이지) 넘길 때 검색 결과가 풀려버림
      *  -> 이에 반드시 column 과 query 가 주소 내에 포함될 수 있도록 파라미터에 포함시켜야 검색 풀리지 않음!
      */
     
     StringBuilder builder = new StringBuilder();
     
     // <
     if(beginPage == 1) {
       builder.append("<div class=\"dont-click\">&lt;</div>");
     } else {
       builder.append("<div><a href=\"" + requestURI + "?page=" + (beginPage - 1) + "&sort=" + sort + "&display=" + display + "&"+ params +"\">&lt;</a></div>");
     }
     
     // 1 2 3 4 5 6 7 8 9 10
     for(int p = beginPage; p <= endPage; p++) {
       if(p == page) {
         builder.append("<div><a class=\"current-page\" href=\"" + requestURI + "?page=" + p + "&sort=" + sort + "&display=" + display + "&"+ params +"\">" + p + "</a></div>");
       } else {
         builder.append("<div><a href=\"" + requestURI + "?page=" + p + "&sort=" + sort + "&display=" + display + "&"+ params +"\">" + p + "</a></div>");
       }
     }
     
     // >
     if(endPage == totalPage) {
       builder.append("<div class=\"dont-click\">&gt;</div>");
     } else {
       builder.append("<div><a href=\"" + requestURI + "?page=" + (endPage + 1) + "&sort=" + sort + "&display=" + display + "&"+ params +"\">&gt;</a></div>");
     }
     
     return builder.toString();
     
   }
   
  
}
