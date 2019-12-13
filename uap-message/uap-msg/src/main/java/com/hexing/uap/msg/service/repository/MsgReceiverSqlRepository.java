package com.hexing.uap.msg.service.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hexing.uap.msg.model.sqlJpa.UapMsgSqlReceiver;

@Repository
public interface MsgReceiverSqlRepository extends JpaRepository<UapMsgSqlReceiver, Long> {
	
	public List<UapMsgSqlReceiver> findByIdIn(Set<Long> ids);//根据id获取PO
	void deleteByUserIdAndMsgIdIn(Long userId,List<Long> ids);
	public int countByUserIdAndState(Long userId, String state);// 根据id获取PO

	@Modifying
	@Transactional 
	@Query("update UapMsgSqlReceiver u set u.state = ?3 where u.msgId in (?1) and u.userId = ?2")
	Integer updateState(List<Long> msgId,Long userId, String state);
}
