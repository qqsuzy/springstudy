package com.gdu.prj01.xml03;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor           // 디폴트 생성자를 만들어서 setter로 myService를 가져옴
@Data
public class MyController {

  private MyService myService;
  
  public void add() {
    myService.add();
    System.out.println("MyController add() 호출");
  }
  
}
