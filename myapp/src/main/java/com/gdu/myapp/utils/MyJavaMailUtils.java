package com.gdu.myapp.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/*
 *  이메일 보내기 (모듈화)
 *  1) 인증번호 
 *  2) 임시비번
 *     select 는 불가능 => 암호화 되어 있기 때문
 */

@PropertySource(value = "classpath:email.properties")  // properties 파일 읽어드리는 annotation | src/main/resources 까지가 classpath: 임, 현재는 폴더 내에 들어가 있지 않으므로 파일명만 기입
@Component  // UserServiceImpl 에서 가져다 쓸 수 있도록 bean 에 등록
public class MyJavaMailUtils {
  
  @Autowired       // 필드 직접 주입
  Environment env; // Environment : spring에서 지원하는 application properties 를 읽어드리는 인터페이스
  
  public void sendMail(String to, String subject, String content) {
    
    // 이메일을 보내는 호스트의 정보 : 구글 (메뉴얼 데이터)
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", 587);
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.starttls.enable", true);
    
    // javax.mail.Session 객체 생성 : 이메일을 보내는 사용자의 정보 (개인 정보) 
    Session session = Session.getInstance(props, new Authenticator() { // (properties, 사용자 정보) | Authenticator : javax.mail 
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(env.getProperty("spring.mail.username"), // 이 부분은 개인 정보 부분으로 별도의 파일(email.properties => properties 파일은 공백 x, 공백도 데이터로 인식함)로 빼서 github 에서 ignore 처리 해야함!
                                          env.getProperty("spring.mail.password"));  
      }
    });
    
    try {
      // 메일 만들기 (보내는 사람 + 받는 사람 + 제목 + 내용)
      MimeMessage mimeMessage = new MimeMessage(session);                          // session 받아서 그대로 전달 => session에 보내는 사람, 받는 사람
      mimeMessage.setFrom(new InternetAddress(env.getProperty("spring.mail.username"), "myapp"));                  // From : 보내는 사람, InternetAddress(String address, String personal) : (보내는 사람, 별칭)
      mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Recipient : 받는 사람, InternetAddress(String address) : (받는 사람) => 별칭 필요 x => 받는 사람은 사용자가 입력한 이메일(to) 임, Service로 부터 파라미터로 받아옴
      mimeMessage.setSubject(subject);                                             // 제목 => 파라미터로 받아옴
      mimeMessage.setContent(content, "text/html; charset=UTF-8");                 // setContent(내용, 받아오는 type)
      
      // 메일 보내기
      Transport.send(mimeMessage);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
