DROP TABLE ADDRESS_T;
DROP TABLE MEMBER_T;
CREATE TABLE MEMBER_T (
  MEMBER_NO NUMBER             NOT NULL,
  EMAIL     VARCHAR2(100 BYTE) NOT NULL UNIQUE,
  NAME      VARCHAR2(100 BYTE),
  GENDER    VARCHAR2(5 BYTE),
  CONSTRAINT PK_MEMBER PRIMARY KEY(MEMBER_NO)
);
CREATE TABLE ADDRESS_T (
  ADDRESS_NO     NUMBER NOT NULL,
  ZONECODE       CHAR(5 BYTE),
  ADDRESS        VARCHAR2(100 BYTE),
  DETAIL_ADDRESS VARCHAR2(100 BYTE),
  EXTRA_ADDRESS  VARCHAR2(100 BYTE),
  MEMBER_NO      NUMBER,
  CONSTRAINT PK_ADDRESS PRIMARY KEY(ADDRESS_NO),
  CONSTRAINT FK_ADDRESS_MEMBER FOREIGN KEY(MEMBER_NO)
    REFERENCES MEMBER_T(MEMBER_NO)
      ON DELETE CASCADE
);
DROP SEQUENCE MEMBER_SEQ;
DROP SEQUENCE ADDRESS_SEQ;
CREATE SEQUENCE MEMBER_SEQ NOCACHE;
CREATE SEQUENCE ADDRESS_SEQ NOCACHE;


SELECT RN, MEMBER_NO, EMAIL, NAME, GENDER, ADDRESS_NO, ZONECODE, ADDRESS, DETAIL_ADDRESS, EXTRA_ADDRESS -- 메인쿼리
  FROM (SELECT ROW_NUMBER() OVER (ORDER BY M.MEMBER_NO DESC) AS RN  -- 서브쿼리 (인라인뷰/FROM절 위치), 조건절을 추가해야할 경우에는 우선순위로 서브쿼리에 추가(실행순서 고려 필요함) => 혹시 불가능하다면 메인쿼리에 추가
             , M.MEMBER_NO, M.EMAIL, M.NAME, M.GENDER
             , A.ADDRESS_NO, A.ZONECODE, A.ADDRESS, A.DETAIL_ADDRESS, A.EXTRA_ADDRESS
          FROM MEMBER_T M LEFT JOIN ADDRESS_T A
            ON M.MEMBER_NO = A.MEMBER_NO)
  WHERE RN BETWEEN 1 AND 20;
    