package com.gdu.prj06.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

/*
 * <bean> , @Bean  -> 이미 만들어진 클래스에 사용 (exist class)
 * @Component      -> 내가 만드는 클래스에 사용   (make class)
 */

@Slf4j
@Aspect
public class MyAroundAspect {

  // PointCut  : 언제 동작하는가?  => 모든 컨트롤러의 모든 메소드에 적용
  @Pointcut("execution (* com.gdu.prj06.controller.*Controller.*(..))")
  //                    * (모든 반환타입).패키지.*Controller(모든컨트롤러).*(..)(모든 메소드,모든 매개변수)
  public void setPointCut() {}
   // 본문은 없음 => 할 일은 Advice 에 작성
  
  // Advice    : 무슨 동작을 하는가?
  @Around("setPointCut()") // Around 에 동작 할 메소드를 지정함
  
  /*
   * Around Advice 메소드 작성 방법
   * 1. 반환타입 : Object
   * 2. 메소드명 : 마음대로
   * 3. 매개변수 : ProceedingJoinPoint 타입 객체
   */
  public Object myAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    
    log.info("-".repeat(80));                     // 동작 이전 (@Before 이전)
    
    Object obj = proceedingJoinPoint.proceed();   // 동작 시점
    
    log.info("{}\n", "-".repeat(80));             // 동작 이후 (@After 이전)
    
    return obj;
    
  }

}
