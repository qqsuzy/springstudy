package com.gdu.prj01.anno03;

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyConnection {

  private String driver;      // <bean> 이용하여 필드에 값을 넣어줌
  private String url;
  private String user;
  private String password;
  
  public Connection getConnection() {
    Connection con = null;
    try {
       Class.forName(driver); //jdbc
       con = DriverManager.getConnection(url, user, password);
       System.out.println(user + "접속되었습니다.");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return con;  // 반환타입이면 return 먼저 생성하기 (까먹지 않도록) , getConnection이 호출되면 Connection 반환됨
  }
  
}
