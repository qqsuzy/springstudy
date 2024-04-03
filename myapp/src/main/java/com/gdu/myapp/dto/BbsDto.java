package com.gdu.myapp.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BbsDto {
  private int bbsNo, state, depth, groupNo, groupOrder;
  private String contents;
  private Date createDt;
  private UserDto user; // userNo 를 받아오기 위해 UserDto 필드에 추가
  
}
