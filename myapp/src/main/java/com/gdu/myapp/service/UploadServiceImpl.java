package com.gdu.myapp.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    model.addAttribute("attachList", uploadMapper.getAttachList(uploadNo));
  }

  @Override
  public ResponseEntity<Resource> download(HttpServletRequest request) {
    
    // 첨부 파일 정보를 DB 에서 가져오기 => 정보는 getAttachByNo 에 있음 (SELECT 함)
    int attachNo = Integer.parseInt(request.getParameter("attachNo"));
    AttachDto attach = uploadMapper.getAttachByNo(attachNo);
    
    // 첨부 파일 정보를 File 객체로 만든 뒤 Resource 객체로 만들기
    File file = new File(attach.getUploadPath(), attach.getFilesystemName());  // (경로, 파일명)
    Resource resource = new FileSystemResource(file);
    
    // 첨부 파일이 없으면 다운로드 취소
    if(!resource.exists()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }
    
    // DOWNLOAD_COUNT 증가
    uploadMapper.updateDownloadCount(attachNo);
    
    // 사용자가 다운로드 받을 파일명 결정 (originalFilename 을 브라우저에 따라 다르게 인코딩 처리)
    String originalFilename = attach.getOriginalFilename();
    String userAgent = request.getHeader("user-Agent");     // 유저 접속 경로는 Header에 들어 있음
    
    try {
      
      // IE (Internet Explore)
      if(userAgent.contains("Trident")) {
        originalFilename = URLEncoder.encode(originalFilename, "UTF-8").replace("+", "");  // Trident 의 문제점은 공백을 + 로 출력함 -> 역으로 + 가 나오면 공백으로 replace 함 
      }
      // Edge
      else if(userAgent.contains("Edg")) {
        originalFilename = URLEncoder.encode(originalFilename, "UTF-8");
      }
      // Other 
      else {
        originalFilename = new String(originalFilename.getBytes("UTF-8"), "ISO-8859-1");  // String charset 함께 지정
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드용 응답 헤더 설정 (HTTP 참조)
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.add("Content-Type", "application/octet-stream");                         // contentType 지정
    responseHeader.add("Content-Disposition", "attachment; filename=" + originalFilename);  // Content-Disposition : 다운로드 받은 파일이름 지정
    responseHeader.add("Content-Length", file.length() + "");                               // Content-Length      : 전송되는 콘텐츠의 길이 => add 메소드의 반환타입은 무조건 string 이어야 함, file.length의 반환 타입은 long 으로 문자열 반환을 위해서 빈문자열 더함 
    
    // 다운로드 진행
    return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);
    
  }
  
  @Override
  public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {
    
    /*
     * 임시 파일이름은 currentTimeMillis 씀 -> 숫자형태로 파일이름이 저장됨 -> 인코딩 필요 x
     */
    
    // 다운로드 할 모든 첨부 파일들의 정보를 DB 에서 가져오기
    int uploadNo = Integer.parseInt(request.getParameter("uploadNo"));
    List<AttachDto> attachList = uploadMapper.getAttachList(uploadNo);
    
    // 첨부 파일이 없으면 종료
    if(attachList.isEmpty()) {
      return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }
    
    // 임시 zip 파일 저장할 경로 
    File tempDir = new File(myFileUtils.getTempPath());
    if(!tempDir.exists()) {
      tempDir.mkdirs();
    }
    
    // 임시 zip 파일 이름
    String tempFilename = myFileUtils.getTempFilename() + ".zip";
    
    // 임시 zip 파일 File 객체
    File tempFile = new File(tempDir, tempFilename);
    
    // 첨부 파일들을 하나씩 zip 파일로 모으기
    try {
      
      // ZipOutPutStream 객체 생성
      ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tempFile));

      for(AttachDto attach : attachList) {   // 첨부된 파일의 개수 만큼 for문 수행
        
        // zip 파일에 포함할 ZipEntry 생성
        ZipEntry zipEntry = new ZipEntry(attach.getOriginalFilename());  // zip 파일에 들어갈 개별 이름 (보통 파일이 업로드된 이름 그대로 다운로드 되기 때문에 originalFilename 으로 저장
        
        // zip 파일에 ZipEntry 객체 명단 추가 (파일의 이름만 등록한 상황)
        zout.putNextEntry(zipEntry);
        
        // 실제 첨부 파일을 zip 파일에 등록 (첨부 파일을 읽어서 zip 파일로 보냄) => 파일을 읽어들일 수 있도록 FileInputStream 사용
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(attach.getUploadPath(), attach.getFilesystemName()))); 
        zout.write(in.readAllBytes());
        
        // 사용한 자원 정리
        in.close();
        zout.closeEntry();   // zipEntry 정리는 zout에서 함
      
        // DOWNLOAD_COUNT 증가
        uploadMapper.updateDownloadCount(attach.getAttachNo());
        
      } // for 
      
      // zout 자원 반납
      zout.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드 할 zip File 객체 
    Resource resource = new FileSystemResource(tempFile);
    
    // 다운로드용 응답 헤더 설정 (HTTP 참조)
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.add("Content-Type", "application/octet-stream");                     // contentType 지정
    responseHeader.add("Content-Disposition", "attachment; filename=" + tempFilename);  // 다운로드 받은 파일이름 지정
    responseHeader.add("Content-Length", tempFile.length() + ""); // Content-Length : 전송되는 콘텐츠의 길이 => add 메소드의 반환타입은 무조건 string 이어야 함, file.length의 반환 타입은 long 으로 문자열 반환을 위해서 빈문자열 더함 
    
    // 다운로드 진행
    return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);
    
  }

  @Override
  public void removeTempFiles() {
    
 // 임시 zip 파일 저장할 경로 
    File tempDir = new File(myFileUtils.getTempPath());
    File[] tempFiles = tempDir.listFiles();             // listFiles() : 디렉터리 내의 모든 File 객체를 File[] 배열로 반환
    
    // 임시 폴더의 모든 파일 제거
    if(tempFiles != null) {
      for(File tempFile : tempFiles) {
        tempFile.delete();
      }
    }
  }

  @Override
  public UploadDto getUploadByNo(int uploadNo) {
    return uploadMapper.getUploadByNo(uploadNo);
  }
  
  @Override
  public int modifyUpload(UploadDto upload) {
    return uploadMapper.updateUpload(upload);
  }

  @Override
  public ResponseEntity<Map<String, Object>> getAttachList(int uploadNo) {
    return ResponseEntity.ok(Map.of("attachList", uploadMapper.getAttachList(uploadNo)));
  }
  
}
