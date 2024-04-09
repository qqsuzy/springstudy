package com.gdu.myapp.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentDto {
 private int commentNo, state, depth, groupNo, blogNo; // blogNo 는 blogDto로 가져올 필요는 없음 -> 블로그 댓글 내에는 상세보기가 필요 없기 때문에 blogNo만 가져옴
 private String contents;
 private Timestamp createDt;
 private UserDto user;

 // BlogDto 자체를 필드에 넣을 경우 UserDto의 와 BlogDto에 모두 blogNo 가 들어가 있기 때문에 SELECT 할 때 누구의 blogNo 인지 확인 어려워 복잡해짐  
 
}
