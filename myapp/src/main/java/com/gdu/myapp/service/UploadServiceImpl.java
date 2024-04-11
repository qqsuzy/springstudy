package com.gdu.myapp.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.gdu.myapp.dto.AttachDto;
import com.gdu.myapp.dto.UploadDto;
import com.gdu.myapp.dto.UserDto;
import com.gdu.myapp.mapper.UploadMapper;
import com.gdu.myapp.utils.MyFileUtils;
import com.gdu.myapp.utils.MyPageUtils;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class UploadServiceImpl implements UploadService {
  
  // 생성자 주입
  private final UploadMapper uploadMapper;
  private final MyPageUtils  myPageUtils;
  private final MyFileUtils  myFileUtils;
  
  @Override
  public boolean registerUpload(MultipartHttpServletRequest multipartRequest) {

    /*
     *  업로드 게시판 테이블 저장 -> 첨부 파일 테이블 저장 순으로 진행되어야 함
     *  => UPLOAD_NO 를 알아야 첨부 파일 테이블에 INSERT 가능하기 때문!
     */
    
    // UPLOAD_T 테이블에 추가하기
    String title = multipartRequest.getParameter("title");
    String contents = multipartRequest.getParameter("contents");
    int userNo = Integer.parseInt(multipartRequest.getParameter("userNo"));
    
    UserDto user = new UserDto();
    user.setUserNo(userNo);
    
    UploadDto upload = UploadDto.builder()
                               .title(title)
                               .contents(contents)
                               .user(user)
                              .build();
    
    System.out.println("INSERT 이전 : " + upload.getUploadNo());   // uploadNo 없음
    int insertUploadCount = uploadMapper.insertUpload(upload);     // 이 부분에서 upload에 uploadNo 를 저장함 =>  
    System.out.println("INSERT 이후 : " + upload.getUploadNo());   // uploadNo 있음 (<selectKey> 동작에 의해서)
    
    // 첨부 파일 처리하기
    List<MultipartFile> files = multipartRequest.getFiles("files");  // multiple(다중첨부) 이므로 getFiles 메소드로 받음 (단일첨부일 경우는 getFile 로 받아야 함) => <input type="file" name="files" id="files" multiple>
    
    // 첨부 파일이 없는 경우 : [MultipartFile[field="files", filename=, contentType=application/octet-stream, size=0]]      -> size 로 파일 첨부 여부 확인 가능( 파일 사이즈를 가져오는 메소드 : getSize() )
    // 첨부 파일이 있는 경우 : [MultipartFile[field="files", filename=animal2.jpg, contentType=image/jpeg, size=311017]]
    // System.out.println(files);
    int insertAttachCount;
    if(files.get(0).getSize() == 0) { // files를 get으로 가져왔을 때 List에 파일 첨부가 안되어 있어도 MultipartFile가 무조건 들어있어 size가 1임 -> 메소드 체이닝으로 확인 가능
      insertAttachCount = 1;          // 첨부가 없어도 files.size() 는 1 이다.
    } else {
      insertAttachCount = 0;
    }
    
    for(MultipartFile multipartFile : files) {
      
      // 파일 첨부 여부 체크
      if(multipartFile != null && !multipartFile.isEmpty()) {
        String uploadPath = myFileUtils.getUploadPath();
        File dir = new File(uploadPath);
        if(!dir.exists()) {
          dir.mkdirs();
        }
        
        String originalFilename = multipartFile.getOriginalFilename();
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);
        File file = new File(dir, filesystemName);
        
        // 실제 저장
        try {

          multipartFile.transferTo(file);
          
          // 썸네일 작성 (이미지가 아니면 썸네일 생성 x)
          String contentType = Files.probeContentType(file.toPath());                        // Files : java.nio , probeContentType : content타입 확인(path 요구함 -> File을 Path로 변환하여 전달(toPath 메소드 활용)) -> 반환값이 String임으로 String 타입에 저장시킴
          int hasThumbnail = contentType != null && contentType.startsWith("image") ? 1 : 0; // contentType 이 null 이 아니고 image로 시작하는 조건 -> 맞으면 1, 아니면 0 저장함
          if(hasThumbnail == 1) {  // 썸네일이 있으면 썸네일 생성
            
            // Thumbnaillator 를 이용한 이미지 썸네일 만들기
            File thumbnail = new File(dir, "s_" + filesystemName);   // 본래 이름() 앞에 s_ 붙이기 => s_ 는 smallSize 를 의미함
            Thumbnails.of(file)                                      // 원본 이미지 파일
                      .size(96, 64)                                  // 가로 96px, 세로 64px (20분의 1 사이즈)
                      .toFile(thumbnail);                            // 썸네일 이미지 파일  
            
          } 
          
          // ATTACH_T 테이블에 추가하기
          AttachDto attach = AttachDto.builder()
                                     .uploadPath(uploadPath)
                                     .filesystemName(filesystemName)
                                     .originalFilename(originalFilename)
                                     .hasThumbnail(hasThumbnail)
                                     .uploadNo(upload.getUploadNo())  // UploadDto에 들어 있음
                                    .build();
          
          insertAttachCount += uploadMapper.insertAttach(attach);   // for문 안에 들어와 있음 -> 다중 첨부하였을 경우 파일의 수 만큼 insertAttachCount를 누적시켜야 하기 때문에 등호(=) 가 아닌 += 로 누적시켜야 함
          
          
        } catch (Exception e) {
          e.printStackTrace();
        }
       
      }  // if
      
    }  // for
    
    return (insertUploadCount == 1) && (insertAttachCount == files.size());  // 파일 첨부가 안되어도 List에 무조건 MultipartFile가 들어있어 files.size 가 1 임 
                                                                             // -> 이에 insertAttachCount 를 초기값으로 1로 잡아둠 -> 첨부된 파일 개수만큼 insertAttachCount 와 files.size 증가 -> 두개의 값이 동일한지 비교하여 맞음녀 return
  }

  /*
   * 파라미터 타입을 통일시켜 구현할 경우에는 대부분 Model을 씀 , 현재 선생님 코드 스타일은 파라미터가 각각 다름 => model에 모두 저장할 경우 model 에서 꺼내서 다시 캐스팅하고 다소 복잡하지만 개발 현장에 따라서 이부분 달라질 수 있으니 다양하게 공부하기를 권장함! 
   * 
   * interface UploadService {
   *   void execute(Model model);
   * }  
   *   
   * class UploadRegisterService implements UploadService {
   *   @Override
   *   void execute(Model model) {
   *   
   *   }
   * }
   * 
   * class UploadListService implements UploadService {
   *  @Override
   *   void execute(Model model) {
   *   
   *   }
   * }
   *  
   */
  
  
  @Override
  public void loadUploadList(Model model) {
   Map<String, Object> modelMap  = model.asMap(); // asMap : model 에서 값 꺼내는 메소드 => model의 값을 Map에 저장하여 꺼냄 -> Map 에 저장 필요 (modelMap) *getAttribute 도 사용가능 (비교적 최신ver)  
   HttpServletRequest request = (HttpServletRequest)modelMap.get("request");  // Map에서 데이터 꺼내면 저장된 타입으로 반환 -> Object 타입으로 꺼내짐 -> HttpServletRequest 로 캐스팅하여 request 에 저장시킴  
   
   int total = uploadMapper.getUploadCount();
   
   // display Null 처리
   Optional<String> optDisplay = Optional.ofNullable(request.getParameter("display"));
   int display = Integer.parseInt(optDisplay.orElse("20"));
   
   // page Null 처리
   Optional<String> optPage = Optional.ofNullable(request.getParameter("page"));
   int page = Integer.parseInt(optPage.orElse("1"));
   
   myPageUtils.setPaging(total, display, page);
   
   // sort Null 처리
   Optional<String> optSort = Optional.ofNullable(request.getParameter("sort"));
   String sort = optSort.orElse("DESC");
   
   Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd()
                                   , "sort", sort);
   
   /*
    * total - 100, display = 20
    * 
    * page  beginNo
    * 1     100
    * 2     80
    * 3     60
    * 4     40
    * 5     20
    */
   
   model.addAttribute("beginNo", total - (page - 1) * display);
   model.addAttribute("uploadList", uploadMapper.getUploadList(map));
   model.addAttribute("paging", myPageUtils.getPaging(request.getContextPath() + "/upload/list.do", sort, display)); // service 를 호출한 경로를 그대로 넘겨줌
   model.addAttribute("display", display);
   model.addAttribute("sort", sort);
   model.addAttribute("page", page);
   
  }

  @Override
  public void loadUploadByNo(int uploadNo, Model model) {
    model.addAttribute("upload", uploadMapper.getUploadByNo(uploadNo));
  }
  
  
}
