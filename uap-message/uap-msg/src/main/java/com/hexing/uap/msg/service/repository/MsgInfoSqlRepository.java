package com.hexing.uap.msg.service.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.msg.model.sqlJpa.UapMsgSqlInfo;

@Repository
public interface MsgInfoSqlRepository extends JpaRepository<UapMsgSqlInfo, Long> {
	
	public List<UapMsgSqlInfo> findByIdIn(Set<Long> ids);	//根据id获取PO
	
	public List<UapMsgSqlInfo> findByState(String state);
}
