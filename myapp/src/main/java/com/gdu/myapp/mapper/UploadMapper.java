package com.gdu.myapp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.myapp.dto.AttachDto;
import com.gdu.myapp.dto.UploadDto;

@Mapper
public interface UploadMapper {
  int insertUpload(UploadDto upload);
  int insertAttach(AttachDto attach);
  int getUploadCount();
  List<UploadDto> getUploadList(Map<String, Object> map);  // begin, end 값 전달을 위해 Map 필요함
  UploadDto getUploadByNo(int uploadNo);
  
}
