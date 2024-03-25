package com.gdu.prj09.utils;

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
  
   public String getAsyncPaging() {
     
     StringBuilder builder = new StringBuilder();
     
     // <
     
     // 1 2 3 4 5 6 7 8 9 10
     
     // >
     
     
     return builder.toString();
   }
  
}
