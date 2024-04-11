/******************************** 시퀀스 ********************************/
DROP SEQUENCE USER_SEQ;
DROP SEQUENCE ACCESS_HISTORY_SEQ;
DROP SEQUENCE LEAVE_USER_SEQ;
DROP SEQUENCE BBS_SEQ;
DROP SEQUENCE BLOG_SEQ;
DROP SEQUENCE COMMENT_SEQ;
DROP SEQUENCE UPLOAD_SEQ;
DROP SEQUENCE ATTACH_SEQ;

CREATE SEQUENCE USER_SEQ NOCACHE;
CREATE SEQUENCE ACCESS_HISTORY_SEQ NOCACHE;
CREATE SEQUENCE LEAVE_USER_SEQ NOCACHE;
CREATE SEQUENCE BBS_SEQ NOCACHE;
CREATE SEQUENCE BLOG_SEQ NOCACHE;
CREATE SEQUENCE COMMENT_SEQ NOCACHE;
CREATE SEQUENCE UPLOAD_SEQ NOCACHE;
CREATE SEQUENCE ATTACH_SEQ NOCACHE;


/******************************** 테이블 ********************************/
DROP TABLE ATTACH_T;
DROP TABLE UPLOAD_T;
DROP TABLE COMMENT_T;   /* 생성과 삭제는 역순 */
DROP TABLE BLOG_T;
DROP TABLE BBS_T;
DROP TABLE LEAVE_USER_T;
DROP TABLE ACCESS_HISTORY_T; 
DROP TABLE USER_T;

CREATE TABLE USER_T (
  /* 회원번호(PK) */               USER_NO      NUMBER             NOT NULL,
  /* 이메일(인증수단) */           EMAIL        VARCHAR2(100 BYTE) NOT NULL UNIQUE,
  /* 암호화(SHA-256) */            PW           VARCHAR2(64 BYTE), /* 64 BYTE는 고정값으로 1글자를 입력해도 64 BYTE */
  /* 이름 */                       NAME         VARCHAR2(100 BYTE),
  /* 성별 */                       GENDER       VARCHAR2(5 BYTE),
  /* 휴대전화 */                   MOBILE       VARCHAR2(20 BYTE),
  /* 이벤트동의여부(0,1) */        EVENT_AGREE  NUMBER,
  /* 가입형태(0:직접, 1:네이버) */ SIGNUP_KIND NUMBER, /* 자사 사이트에서 가입했는지, 타사 사이트에서 가입했는지 */
  /* 비밀번호수정일 */             PW_MODIFY_DT DATE,
  /* 가입일 */                     SIGNUP_DT    DATE,
  CONSTRAINT PK_USER PRIMARY KEY(USER_NO)
);

CREATE TABLE ACCESS_HISTORY_T (
  ACCESS_HISTORY_NO NUMBER             NOT NULL,
  EMAIL             VARCHAR2(100 BYTE),
  IP                VARCHAR2(50 BYTE),
  USER_AGENT        VARCHAR2(150 BYTE),  -- 접속 경로
  SESSION_ID        VARCHAR2(32 BYTE),   
  SIGNIN_DT         DATE,
  SIGNOUT_DT        DATE,                -- 기록이 잘 남지 않음, 사용자가 직접 로그아웃을 눌러야 기록이 남음 -> NULL 값이 대부분임 
  CONSTRAINT PK_ACCESS_HISTORY PRIMARY KEY(ACCESS_HISTORY_NO),
  CONSTRAINT FK_ACCESS_HISTORY_USER
    FOREIGN KEY(EMAIL) 
      REFERENCES USER_T(EMAIL)
        ON DELETE CASCADE
);

/* USER_T 와는 관계가 없음 (1:M 관계가 성립되지 않기 때문, 사용자가 여러 EMAIL을 가질 수 없기 때문) */
CREATE TABLE LEAVE_USER_T (
  LEAVE_USER_NO NUMBER             NOT NULL,
  EMAIL         VARCHAR2(100 BYTE) NOT NULL UNIQUE,
  LEAVE_DT      DATE,
  CONSTRAINT PK_LEAVE_USER PRIMARY KEY(LEAVE_USER_NO) 
);

-- 계층형 게시판 (N차 댓글 : 상세보기 X -> 제목/조회수 없음)
CREATE TABLE BBS_T (
  BBS_NO      NUMBER              NOT NULL,
  CONTENTS    VARCHAR2(4000 BYTE) NOT NULL,
  USER_NO     NUMBER              NOT NULL,
  CREATE_DT   DATE                NULL,
  STATE       NUMBER            NULL,  -- 0:삭제, 1:정상
  /* 정렬용 */
  DEPTH       NUMBER            NULL,  -- 0:원글, 1:답글, 2:답답글, ...
  GROUP_NO    NUMBER            NULL,  -- 원글과 원글에 달린 모든 댓글들은 동일한 GROUP_NO를 가짐
  GROUP_ORDER NUMBER            NULL,  -- 같은 GROUP_NO 내부에서 표시할 순서
  CONSTRAINT PK_BBS PRIMARY KEY(BBS_NO),
  CONSTRAINT FK_BBS_USER FOREIGN KEY(USER_NO)
    REFERENCES USER_T(USER_NO) ON DELETE CASCADE
);

-- 블로그 (댓글형 게시판)
CREATE TABLE BLOG_T (
  BLOG_NO   NUMBER               NOT NULL,
  TITLE     VARCHAR2(1000 BYTE)  NOT NULL,
  CONTENTS  CLOB,
  HIT       NUMBER               DEFAULT 0,  -- INSERT 할 때 데이터 넣지 않아도 자동으로 디폴트 값 들
  USER_NO   NUMBER               NOT NULL,
  CREATE_DT TIMESTAMP,
  MODIFY_DT TIMESTAMP,
  CONSTRAINT PK_BLOG PRIMARY KEY(BLOG_NO),
  CONSTRAINT FK_BLOG_USER FOREIGN KEY(USER_NO)
      REFERENCES USER_T(USER_NO) ON DELETE CASCADE  -- 사용자 탈퇴 시 작성된 정보들 모두 지움

);

-- 블로그 댓글 (댓글 게시판 (1차 답글만 가능))
CREATE TABLE COMMENT_T (
  COMMENT_NO NUMBER              NOT NULL,
  CONTENTS   VARCHAR2(4000 BYTE) NOT NULL,
  CREATE_DT  TIMESTAMP,
  STATE      NUMBER,  -- 0:삭제, 1:정상 (삽입기능수정 / 관련Dto및resultMap 수정)
  DEPTH      NUMBER,  -- 원글0, 답글1
  GROUP_NO   NUMBER,  -- 원글에 달린 모든 답글은 동일한 GROUP_NO 를 가짐
  USER_NO    NUMBER,  -- 외래키의 제약조건은 NULL 인 것이 편함, 나중에 추가하면 되는 사항이기 때문
  BLOG_NO    NUMBER,
  CONSTRAINT PK_COMMENT PRIMARY KEY(COMMENT_NO),
  CONSTRAINT FK_COMMENT_USER FOREIGN KEY(USER_NO)
    REFERENCES USER_T(USER_NO) ON DELETE CASCADE, -- 만약에 회원 탈퇴 시 사용자가 남긴 댓글을 남기고 싶을 때는 ON DELETE SET NULL 로 적용하면 됨 (작성자의 정보만 삭제 -> 댓글은 유지)
  CONSTRAINT FK_COMMENT_BLOG FOREIGN KEY(BLOG_NO)
    REFERENCES BLOG_T(BLOG_NO) ON DELETE CASCADE
);

-- 업로드 게시판
CREATE TABLE UPLOAD_T (
  UPLOAD_NO NUMBER              NOT NULL,
  TITLE     VARCHAR2(4000 BYTE) NOT NULL,
  CONTENTS  VARCHAR2(4000 BYTE),
  CREATE_DT VARCHAR2(100 BYTE),
  MODIFY_DT VARCHAR2(100 BYTE),
  USER_NO   NUMBER,
  CONSTRAINT PK_UPLOAD PRIMARY KEY(UPLOAD_NO),
  CONSTRAINT FK_UPLOAD_USER FOREIGN KEY(USER_NO)
    REFERENCES USER_T(USER_NO) ON DELETE CASCADE
);


-- 첨부 파일 테이블
CREATE TABLE ATTACH_T (
  ATTACH_NO         NUMBER NOT NULL,
  UPLOAD_PATH       VARCHAR2(500 BYTE),
  FILESYSTEM_NAME   VARCHAR2(500 BYTE),
  ORIGINAL_FILENAME VARCHAR2(500 BYTE),
  DOWNLOAD_COUNT    NUMBER,
  HAS_THUMBNAIL     NUMBER,  -- 썸네일이 있으면 1, 없으면 0
  UPLOAD_NO         NUMBER,
  CONSTRAINT PK_ATTACH PRIMARY KEY(ATTACH_NO),
  CONSTRAINT FK_ATTACH_UPLOAD FOREIGN KEY(UPLOAD_NO)
    REFERENCES UPLOAD_T(UPLOAD_NO) ON DELETE CASCADE
);


-- 좋아요 (누가 어느 게시글에 좋아요 했음) 


-- 기초 데이터 (사용자)
INSERT INTO USER_T VALUES (USER_SEQ.NEXTVAL, 'admin@example.com', STANDARD_HASH('admin','SHA256'), '관리자', 'man', '010-1111-1111', 1, 0, CURRENT_DATE, CURRENT_DATE);
COMMIT;

/******************************** 트리거 ********************************/
/*
  1. DML (INSERT, UPDATE, DELETE) 작업 이후 자동으로 실행되는 데이터베이스 객체이다.
  2. 행 (ROW) 단위로 동작한다.
  3. 종류
    1) BEFORE : DML 동작 이전에 실행되는 트리거
    2) AFTER  : DML 동작 이후에 실행되는 트리거 
  4. 형식
    CREATE [OR REPLACE] TRIGGER 트리거명   =>  REPLACE : 내용이 수정되면 다시 만들 수 있는 개념
    BEFORE | AFTER
    INSERT OR UPDATE OR DELETE
    ON 테이블명                            => 어떤 테이블에 INSERT, UPDATE, DELETE 가 발생되었을 때 동작
    FOR EACH ROW
    BEGIN
      트리거 본문
    END;
  5. 특이점
    COMMIT, ROLLBACK 은 자동으로 실행된다.
    
*/
  
/* USER_T 테이블에서 삭제된 회원정보를 LEAVE_USER_T 테이블에 자동으로 삽입하는 
   LEAVE_TRIGGER 트리거 생성하기
   => USER_T 테이블이 삭제된 이후(AFTER) 동작 
   => TRIGGER는 삭제된 정보를 임시 테이블(OLD)에 잠시 저장하고 있음 
      (DELETE 되면 USER_T에서 확인할 수 없으므로 임시 테이블에서 데이터를 가져와야함)
*/
CREATE OR REPLACE TRIGGER LEAVE_TRIGGER  -- 없으면 만들고 있으면 수정하는 트리거
  AFTER
  DELETE
  ON USER_T
  FOR EACH ROW
  BEGIN
    INSERT INTO LEAVE_USER_T (
        LEAVE_USER_NO
      , EMAIL
      , LEAVE_DT
    ) VALUES (
        LEAVE_USER_SEQ.NEXTVAL
      , :OLD.EMAIL             -- 임시테이블(OLD) 에서 EMAIL 데이터 가져옴
      , CURRENT_DATE
    );
  -- COMMIT; 트리거 내에서는 오류가 있으면 ROLLBACK, 없으면 COMMIT 자동 처리
  END;
