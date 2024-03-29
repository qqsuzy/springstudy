package com.gdu.myapp.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

// 업로드한 파일을 날짜별로 디렉터리 생성 + 파일명 가공하는 유틸

@Component
public class MyFileUtils {

  // 현재 날짜
  public static final LocalDate TODAY = LocalDate.now(); 
  
  // 업로드 경로 반환
  public String getUploadPath() {
    return "/upload" + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(TODAY);
  }
  
  // 저장될 파일명 반환
  public String getFilesystemName(String originalFilename) { // 원래 파일명을 받아와서 변환
    String extName = null;
    if(originalFilename.endsWith("tar.gz")) {
      extName = ".tar.gz";
    } else {
      extName = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    return UUID.randomUUID().toString().replace("-", "") + extName;
  }
  
  
}
