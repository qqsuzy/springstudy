package com.gdu.myapp.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MySecurityUtils {

  /*
   * SHA-256
   * 1. 어떤 값을 256비트(32바이트)로 암호화하는 해시 알고리즘이다.
   * 2. 암호화는 가능하고 복호화는 불가능하다. (단방향 알고리즘) => 일반적으로 본래 어떠한 데이터인지 알아낼 수 없기 떄문에 보안에 강함
   * 3. java.security 패키지를 활용한다. (dependency 추가해서도 사용가능)
   * 
   * Spring Security : 스프링에서 지원하는 보안 기능 
   */
  public static String getSha256(String original) { // 파라미터값으로 pw가 넘어옴
    StringBuilder builder = new StringBuilder();
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.update(original.getBytes());  // getBytes() : String 을 byte 배열로 변환
      byte[] bytes = digest.digest();
      for(int i = 0; i < bytes.length; i++) {
        builder.append(String.format("%02X", bytes[i]));  // 0도 표기할 수 있도록 0 붙여줌
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return builder.toString();
  }
  
  /*
   * 크로스 사이트 스크립트 (Cross Site Scripting)
   * 1. 스크립트 코드를 입력하여 시스템을 공격할 수 있다.
   * 2. 스크립트 코드에 반드시 필요한 "<script>" 입력을 무력화하기 위해서
   *   "<" 기호화 ">" 기호를 엔티티 코드로 변환한다.
   */
  
  // 크로스 사이트 스크립팅 (Cross Site Scripting) 방지 
  public static String getPreventXss(String original) {
    return original.replace("<script>", "&lt;script&gt;").replace("</script>", "&lt;/script&gt;"); // < 가 들어오면 &lt로 바꾸어주고, > 가 들어오면 &gt로 바꾸어줌 (replace는 메소드 체이닝 가능)
  }
  
  /*
   * 인증코드
   * 1. 랜덤으로 생성해야 한다.
   * 2. 보안을 위해 SecureRandom 클래스를 활용한다.
   * 
   * 배열로 만들어서 문자 or 숫자를 넣는다. 
   * index를 랜덤으로 빼서 문자+숫자 조합의 인증코드를 만듬 
   */
  public static String getRandomString(int count, boolean letter, boolean number) {
    StringBuilder builder = new StringBuilder();
    List<String> list = new ArrayList<String>();
    if(letter) {
      for(char ch = 'A'; ch <= 'Z'; ch++) {
        list.add(ch + "");
      }
      for(char ch = 'a'; ch <= 'z'; ch++) {
        list.add(ch + "");
      }
    }
    if(number) {
      for(int n = 0; n <= 9; n++) {
        list.add(n + "");
      }
    }
    SecureRandom secureRandom = new SecureRandom();
    if(letter || number) {
      while(count > 0) {
        builder.append(list.get(secureRandom.nextInt(list.size()))); // list size는 0부터 시작
        count--;
      }
    }
    return builder.toString();
  }
  
  public static void main(String[] args) {
    String pw = "1111";
    System.out.println(getSha256(pw));
    
    String title ="<script>alert('hahaha');</script>";
    System.out.println(getPreventXss(title));
   
    System.out.println(getRandomString(6, false, false));
  
  }
  
}
