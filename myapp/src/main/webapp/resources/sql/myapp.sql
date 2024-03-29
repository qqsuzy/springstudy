DROP SEQUENCE USER_SEQ;
DROP SEQUENCE ACCESS_HISTORY_SEQ;
DROP SEQUENCE LEAVE_USER_SEQ;
CREATE SEQUENCE USER_SEQ NOCACHE;
CREATE SEQUENCE ACCESS_HISTORY_SEQ NOCACHE;
CREATE SEQUENCE LEAVE_USER_SEQ NOCACHE;

DROP TABLE LEAVE_USER_T;
DROP TABLE ACCESS_HISTORY_T; /* 생성과 삭제는 역순으로 ACCESS_HISTORY_T를 먼저 삭제 */
DROP TABLE USER_T;

CREATE TABLE USER_T (
  /* 회원번호(PK) */               USER_NO      NUMBER             NOT NULL,
  /* 이메일(인증수단) */           EMAIL        VARCHAR2(100 BYTE) NOT NULL UNIQUE,
  /* 암호화(SHA-256) */            PW           VARCHAR2(64 BYTE), /* 64 BYTE는 고정값으로 1글자를 입력해도 64 BYTE */
  /* 이름 */                       NAME         VARCHAR2(100 BYTE),
  /* 성별 */                       GENDER       VARCHAR2(5 BYTE),
  /* 휴대전화 */                   MOBILE       VARCHAR2(20 BYTE),
  /* 이벤트동의여부(0,1) */        EVENT_AGREE  NUMBER,
  /* 가입형태(0:직접, 1:네이버) */ SINGNUP_KIND NUMBER, /* 자사 사이트에서 가입했는지, 타사 사이트에서 가입했는지 */
  /* 비밀번호수정일 */             PW_MODIFY_DT DATE,
  /* 가입일 */                     SINGUP_DT    DATE,
  CONSTRAINT PK_USER PRIMARY KEY(USER_NO)
);

CREATE TABLE ACCESS_HISTORY_T (
  ACCESS_HISTORY_NO NUMBER             NOT NULL,
  EMAIL             VARCHAR2(100 BYTE),
  IP                VARCHAR2(50 BYTE),
  SIGNIN_DT         DATE,
  SIGNOUT_DT        DATE,   /* 기록이 잘 남지 않음, 사용자가 직접 로그아웃을 눌러야 기록이 남음 -> NULL 값이 대부분임 */
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
  

