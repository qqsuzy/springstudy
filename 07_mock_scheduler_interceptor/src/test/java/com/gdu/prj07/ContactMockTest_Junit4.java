package com.gdu.prj07;

// import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.extern.slf4j.Slf4j;

/* 1. JUnit4 를 이용한다. */
//@RunWith(SpringJUnit4ClassRunner.class)

/*
 * 2. ContactDaoImpl 클래스의 bean 생성 방법을 작성한다.
 *  1) <bean>      locations="file:src/main/webapp/WEB-INF/spring/root-context.xml"
 *  2) @Bean       classes=AppConfig.class
 *  3) @Component  locations="file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"
 */

// 테스트 수행 순서 (메소드 이름 순)
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)

//로그
@Slf4j

// @ContextConfiguration + @WebApplicationContext
@SpringJUnitWebConfig(locations={"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class ContactMockTest_Junit4 {
  
  @Autowired
  private WebApplicationContext webApplicationContext;
  
  // 테스트 수행을 위한 MockMvc 객체 선언
  private MockMvc mockMvc;
  
  // MockMvc 객체 생성 (테스트 수행 이전에 생성한다.)
  //@Before
  public void setup() {
    mockMvc = MockMvcBuilders
                 .webAppContextSetup(webApplicationContext)
                .build();
  }
  
  // @Test
  public void 테스트01_MockMvc생성확인() {
    //assertNotNull(mockMvc);
  }
  
  // @Test
  public void 테스트02_삽입() throws Exception {
    MvcResult mvcResult = mockMvc
                             .perform(MockMvcRequestBuilders             // 요청을 만든다.
                                         .post("/contact/register.do")   // 요청 방식
                                         .param("name", "테스트이름" )   // 파라미터  
                                         .param("mobile", "테스트모바일")       
                                         .param("email", "테스트이메일")       
                                         .param("address", "테스트주소"))       
                             .andReturn();
    
    // RedirectAttributes 에 flash attribute 를 등록하고 redirect 한 경우
    log.info(mvcResult.getFlashMap().toString());
    
    // HttpServletResponse 를 이용해 직접 redirect 코드를 작성한 경우
    log.info(mvcResult.getResponse().getContentAsString());
  }
  
  // @Test
  public void 테스트03_상세조회() throws Exception {
   
    MvcResult mvcResult = mockMvc
                             .perform(MockMvcRequestBuilders
                                       .get("/contact/detail.do")
                                       .param("contact-no", "1"))
                             .andReturn();
    
    log.info(mvcResult.getModelAndView().getModelMap().toString()); // model 에 저장된 값을 가져옴 => Map 형태로 변환
  }
  
 
}
