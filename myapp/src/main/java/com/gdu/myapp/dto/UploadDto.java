package com.gdu.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UploadDto {

  private int uploadNo;
  private String title, contents, createDt, modifyDt;
  private UserDto user;
  private int attachCount;    // SELECT 결과를 옮겨 주는 것은 DTO -> attachCount는 UPLOAD_T 에는 없지만 DTO에는 필드로 존재해야함 -> 그래야 자바로 저장해서 view 로 보내줄 수 있음 (attachCount 변수명은 DB에 별명(alias) 와 동일해야함!) 
  
}
