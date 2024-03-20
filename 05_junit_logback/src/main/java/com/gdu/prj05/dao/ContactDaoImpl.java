package com.gdu.prj05.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.gdu.prj05.dto.ContactDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 필드 처리를 위한 생성자 주입 annotation
// @Repository              // Dao를 bean(@Component 로 등록)에 등록 => @Componet 는 컴포넌트를 찾을 위치(scan) 등록이 필요함 => servlet-context.xml 에 component-scan 에 등록되어 있음  
public class ContactDaoImpl implements ContactDao {

  //Dao에서 사용할 bean
  private final SqlSessionTemplate sqlSessionTemplate;

  // mapper 의 namespace(NS) final 필드 등록 (namespace 작성실수 ↓)
  public final static String NS = "com.gdu.prj05.mybatis.mapper.contact_t."; 
  
  @Override
  public int registerContact(ContactDto contact) {
    int insertCount = sqlSessionTemplate.insert(NS + "registerContact", contact); // 무슨 mapper(com.gdu.prj05.mabatis.mapper.contact_t)의 무슨 id(registerContact) 인지 작성해야함
    return insertCount;
  }

  @Override
  public int modifyContact(ContactDto contact) {
    int updateCount = sqlSessionTemplate.update(NS + "modifyContact", contact);
    return updateCount;
  }

  @Override
  public int removeContact(int contactNo) {
    int deleteCount = sqlSessionTemplate.delete(NS + "removeContact", contactNo);
    return deleteCount;
  }

  @Override
  public List<ContactDto> getContactList() {
    List<ContactDto> contactList = sqlSessionTemplate.selectList(NS + "getContactList");
    return contactList;
  }

  @Override
  public ContactDto getContactByNo(int contactNo) {
    ContactDto contact = sqlSessionTemplate.selectOne(NS + "getContactByNo", contactNo);
    return contact;
  }

}
