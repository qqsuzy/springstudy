package com.gdu.myapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.UserMapper;

// JUnit5
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
class UserMapperTest {

  @Autowired // 직접 주입방식
  private UserMapper userMapper;
 
  @Test
  public void test() {
    assertEquals(1, userMapper.insertUser(new UserDto()));
  }

}
