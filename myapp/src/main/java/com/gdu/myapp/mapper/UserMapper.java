package com.gdu.myapp.mapper;

import java.util.Map;

import com.gdu.myapp.dto.LeaveUserDto;
import com.gdu.myapp.dto.UserDto;

/*
 *   java : mapper            mybatis : mapper
 *  --------------------------------------------
 *  
 *     interface    ------>         XML
 *     method      <------          id
 *                          
 *                          namespace : Interface
 * 
 */


public interface UserMapper {
  UserDto getUserByMap(Map<String, Object> map);
  int insertAccessHistory(Map<String, Object> map);
  LeaveUserDto getLeaveUserByMap(Map<String, Object> map);
  int insertUser(UserDto user);
  int deleteUser(int userNo);
  
}
