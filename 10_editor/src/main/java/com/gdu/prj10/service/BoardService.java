package com.gdu.prj10.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/*
 * 
 *    view           |         back
 *                   |
 *                jackson
 *               라이브러리
 *                   |
 *     json          |       bean/map => bean 보다 map이 확장면에서 편리함
 * 
 * 
 */

public interface BoardService {
   ResponseEntity<Map<String, Object>> summernoteImageUpload(MultipartHttpServletRequest multipartRequest);
}
