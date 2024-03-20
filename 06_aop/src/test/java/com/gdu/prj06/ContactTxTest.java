package com.gdu.prj06;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gdu.prj06.service.ContactService;

/* 1. JUnit4 를 이용한다. */
@RunWith(SpringJUnit4ClassRunner.class)

/*
 * 2. ContactDaoImpl 클래스의 bean 생성 방법을 작성한다.
 *  1) <bean>      locations="file:src/main/webapp/WEB-INF/spring/root-context.xml"
 *  2) @Bean       classes=AppConfig.class
 *  3) @Component  locations="file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"
 */

// ContactService 타입의 ContactServiceImple bean 이 등록된 파일
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/root-context.xml"})

public class ContactTxTest {
  
  @Autowired
  private ContactService contactService;
  
  @Test
  public void 트랜잭션_테스트() {
    contactService.txTest();
  }
  
  
}
