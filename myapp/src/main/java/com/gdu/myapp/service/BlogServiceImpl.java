package com.gdu.myapp.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gdu.myapp.dto.BlogDto;
import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.BlogMapper;
import com.gdu.myapp.utils.MyFileUtils;
import com.gdu.myapp.utils.MyPageUtils;
import com.gdu.myapp.utils.MySecurityUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlogServiceImpl implements BlogService {

  private final BlogMapper blogMapper;
  private final MyPageUtils myPageUtils;
  private final MyFileUtils myFileUtils;
  
  @Override
  public ResponseEntity<Map<String, Object>> summernoteImageUpload(MultipartFile multipartFile) {
    
    // 이미지 저장 경로 생성
    String uploadPath = myFileUtils.getUploadPath();
    File dir = new File(uploadPath);
    if(dir.exists()) {
      dir.mkdirs();
    }
   
    // 이미지 저장할 이름 생성
    String filesystemName = myFileUtils.getFilesystemName(multipartFile.getOriginalFilename()); // 오리지널 이름을 전달해주어야 함
    
    // 이미지 저장
    File file = new File(dir, filesystemName);
    try {
      multipartFile.transferTo(file); // 실제 저장 코드 
    } catch (Exception e) {
      e.printStackTrace();
    }    
    
    // 이미지 저장된 경로를 Map으로 반환
    
    return new ResponseEntity<>(Map.of("src", uploadPath +"/" + filesystemName) // uploadPath 에는 이미 앞에 슬래시(/) 포함되어 있음, 이에 filesystemName 앞에만 슬래시 넣어주면 됨
                              , HttpStatus.OK); 
   
  }

  @Override
  public int registerBlog(HttpServletRequest request) {
    
    // 요청 파라미터
    String title = request.getParameter("title");
    String contents = request.getParameter("contents"); 
    int userNo = Integer.parseInt(request.getParameter("userNo"));
        
    // UserNo + BlogDto 객체 생성
    UserDto user = new UserDto();
    user.setUserNo(userNo);
    BlogDto blog = BlogDto.builder()
                         .title(MySecurityUtils.getPreventXss(title))       // parser 를 먼저 실행시키고 mySecurityUtils 나중에 한 후 DB에 저장해야 parser 가 동작함 => 이에 DB로 보낼 때만 getPreventXss 메소드 동작 
                         .contents(MySecurityUtils.getPreventXss(contents))
                         .user(user)
                        .build();
   
    /* summernote 편집기에서 사용한 이미지 확인하는 방법 (Jsoup 라이브러리) */
    Document document = Jsoup.parse(contents);      // parse : 반환 타입은 document (javaScript 의 document)
    Elements elements = document.getElementsByTag("img");  // document 의 하위 요소인 elements 에 이미지 태그들을 저장
    if(elements != null) {
      for(Element element : elements) {  // element : 이미지 (1개)
        String src = element.attr("src");  // <img> 의 attribute 인 src (이미지 소스) 를 반복문으로 빼서 저장시킴
        /* src 정보를 DB에 저장하는 코드 등이 이 곳에 있으면 된다. */
        System.out.println(src);
      }
    }
    
    // DB에 blog 저장
    return blogMapper.insertBlog(blog);
    
  }
  
  @Override
  public ResponseEntity<Map<String, Object>> getBlogList(HttpServletRequest request) {
    
    // 전체 블로그 개수
    int total = blogMapper.getBlogCount();
    
    // 스크롤 이벤트마다 가져갈 목록의 개수
    int display = 10;
    
    // 현재 페이지 번호
    int page = Integer.parseInt(request.getParameter("page")); // 페이지 번호가 넘어오지 않을 경우에 대한 Optional 처리하여도 됨 (이번엔 js 에서 처리 예정 => 전역변수로 page 값을 선언해두어 값이 넘어오지 않는 경우는 없음)

    // 페이징 처리
    myPageUtils.setPaging(total, display, page);

    // 목록 가져올 때 전달할 Map 생성
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                    , "end", myPageUtils.getEnd());
   
    
    // 목록 화면으로 반환할 값 (목록 + 전체 페이지 수) => 반환이 ajax
    return new ResponseEntity<>(Map.of("blogList", blogMapper.getBlogList(map)
                                     , "totalPage", myPageUtils.getTotalPage())
                                     , HttpStatus.OK) ;
  }
  
  @Override
  public BlogDto getBlogByNo(int blogNo) {
    return blogMapper.getBlogByNo(blogNo);
  }
  

}
