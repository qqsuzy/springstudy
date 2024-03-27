package com.gdu.prj10.service;

import java.io.File;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.prj10.utils.MyFileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service                     // @Service를 명시하면 BoardServiceImple 클래스가 Spring 컨테이너에 자동으로 등록됨 =>  (@Component의 하위 객체)
public class BoardServiceImple implements BoardService {

  private final MyFileUtils myFileUtils;
  
  @Override
  public ResponseEntity<Map<String, Object>> summernoteImageUpload(MultipartHttpServletRequest multipartRequest) {
   
    // 저장 경로
    String uploadPath = myFileUtils.getUploadPath();
    File dir = new File(uploadPath);
    if(!dir.exists()) {
      dir.mkdirs();
    }
   
    // 저장 파일
    MultipartFile multipartFile = multipartRequest.getFile("image");
    String filesystemName = myFileUtils.getFilesystemName(multipartFile.getOriginalFilename());
    File file = new File(dir, filesystemName);
    
    // 실제 저장
    try {
      multipartFile.transferTo(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // 반환할 Map
    // view 단으로 보낼 src = "http://localhost:8080/prj10/upload/2024/03/27/1234567890.jpg"  => http://localhost:8080/prj10/upload 는 /upload/ , 2024/03/27/1234567890.jpg 는 ** 부분에 해당함 
    // servlet-context.xml 에서 <resources> 태그를 추가한다.
    // <resources mapping="/upload/**" location="file:///upload/">  =>  프로젝트 외부의 일반경로는 file: 을 붙여줌 -> 구분자 // -> root 밑에 경로 /upload/
    Map<String, Object> map = Map.of("src", multipartRequest.getContextPath() + uploadPath +"/" + filesystemName); // view는 주소를 기반으로 삽입한 이미지를 보여줌 => contextPath로 시작, 주소는 맘대로 정하기 가능 => 어떤 파일로 저장되어 있는지 파일명은 주소에 포함시켜야 함(filesystemName)
    
    // 반환
    return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    
  }

}
