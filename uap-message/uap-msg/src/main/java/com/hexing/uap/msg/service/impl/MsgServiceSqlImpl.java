package com.hexing.uap.msg.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.msg.common.bean.ReceiveTpEnum;
import com.hexing.uap.msg.model.jpa.UapMsgInfo;
import com.hexing.uap.msg.model.jpa.UapMsgReceiver;
import com.hexing.uap.msg.model.request.MsgReq;
import com.hexing.uap.msg.model.sqlJpa.UapMsgSqlInfo;
import com.hexing.uap.msg.model.sqlJpa.UapMsgSqlReceiver;
import com.hexing.uap.msg.model.sqlJpa.UapMsgTopic;
import com.hexing.uap.msg.model.sqlJpa.UapMsgTopicReceiver;
import com.hexing.uap.msg.service.MsgService;
import com.hexing.uap.msg.service.repository.MsgInfoSqlRepository;
import com.hexing.uap.msg.service.repository.MsgReceiverSqlRepository;
import com.hexing.uap.msg.service.repository.MsgTopicCache;
import com.hexing.uap.msg.service.repository.custom.MsgCustomRepository;
import com.hexing.uap.service.RedisService;
import com.hexing.uap.util.JsonUtil;
@Service
@ConditionalOnProperty(value="uap.nosql.enabled",havingValue="no")
public class MsgServiceSqlImpl implements MsgService{
	
	@Autowired
	private MsgCustomRepository msgCustomRepository;
	@Autowired
	private MsgInfoSqlRepository msgInfoSqlRepository;
	@Autowired
	private MsgReceiverSqlRepository msgReceiverSqlRepository;
	@Autowired
	protected MsgTopicCache msgTopicCache;
	@Autowired
	protected RedisService redisUtil;
	
	// 用户缓存KEY,缓存为map类型，map的key为用户编号，map值为对应的用户数据
	private static final String CACHE_KEY = "UAP:userCache";
	
	// 用户ID-角色ID缓存KEY,缓存为map类型，map的key为用户ID，map值为对应的角色ID集合
	private static final String ROLE_KEY = "UAP:userRole";
	

	public PageData<UapMsgInfo> getMsgList(MsgReq msgReq,Long tenancyId){
		return msgCustomRepository.getMsgList(msgReq,tenancyId);
	}
	
	public PageData<UapMsgInfo> getMsgUserList(MsgReq msgReq,Long userId) {
		return msgCustomRepository.getMsgUserList(msgReq, userId);
	}
	public UapMsgInfo getMsgByid(String id) {
		UapMsgSqlInfo sqlInfo = msgInfoSqlRepository.getOne(Long.parseLong(id));
		return getMsgInfoBySql(sqlInfo);
	}
	
	public void deleteMsgs(List<String> ids, Long userId) {
		List<Long> idList = new ArrayList<>();
		for(String id:ids) {
			idList.add(Long.parseLong(id));
		}
		msgReceiverSqlRepository.deleteByUserIdAndMsgIdIn(userId, idList);
	}
	public Long updateState(List<String> msgId, Long userId, String state){
		List<Long> idList = new ArrayList<>();
		for(String id:msgId) {
			idList.add(Long.parseLong(id));
		}
		return msgReceiverSqlRepository.updateState(idList, userId, state).longValue();
	}
	public int getMsgNum(Long userId,String state) {
		int count = msgReceiverSqlRepository.countByUserIdAndState(userId, state);
		return count>100?100:count;
	}
	
	public void saveUapMsgInfo(UapMsgInfo info) {
		msgInfoSqlRepository.save(getMsgInfoByNoSql(info));
	}
	
	public void saveUapMsgReceive(List<UapMsgReceiver> receiveList) {
		List<UapMsgSqlReceiver> receiveSqlList = new ArrayList<>();
		for(UapMsgReceiver r:receiveList) {
			UapMsgSqlReceiver rSql = new UapMsgSqlReceiver(r.getUserId(), r.getState(), r.getUpdateTime(),Long.parseLong(r.getMsgId()));
			receiveSqlList.add(rSql);
		}
		msgReceiverSqlRepository.saveAll(receiveSqlList);
	}

	/**
	 * 根据租户Id获取所有用户ID
	 * 
	 * @param p
	 */
	public List<Long> getUserIdByTenant(String tenantId) {
		List<Long> userIdList = new ArrayList<>();
		Map<Object, Object> userMap = redisUtil.getHash(CACHE_KEY);
		if (userMap != null && !userMap.isEmpty()) {
			for (Entry<Object, Object> e : userMap.entrySet()) {
				Map<String, Object> map = JsonUtil.readObject(e.getValue().toString(), Map.class);
				if (map.get("tenant_id") != null && tenantId.equals(map.get("tenant_id").toString())) {
					userIdList.add(Long.parseLong(e.getKey().toString()));
				}
			}
		}
		return userIdList;
	}
	
	/**根据角色获取所有用户ID
	 * @param p
	 */
	public List<Long> getUserIdByRole(Long roleId){
		List<Long> userIdList = new ArrayList<>();
		Map<Object, Object> roleMap = redisUtil.getHash(ROLE_KEY);
		if(roleMap!=null&&!roleMap.isEmpty()) {
			for(Entry<Object, Object> e:roleMap.entrySet()) {
				if (!StringUtils.isEmpty(e.getValue())) {
					Set<Long> set = JsonUtil.readLongSet(e.getValue().toString());
					if(set.contains(roleId)) {
						userIdList.add(Long.parseLong(e.getKey().toString()));
					}
				}
			}
		}
		return userIdList;
	}
	
	public UapMsgTopic getTopicByCode(String code) {
		return msgTopicCache.get(code);
	}	
	
	public Set<Long> getUserIds(List<UapMsgTopicReceiver> recList){
		Set<Long> userIds = new HashSet<>();
		if (recList!=null && !recList.isEmpty()) {
			for (UapMsgTopicReceiver topicTen : recList) {
				if (ReceiveTpEnum.TENANT.getCode().equals(topicTen.getReceiveTp())) {
					userIds.addAll(getUserIdByTenant(topicTen.getTenancyId().toString()));
				} else if (ReceiveTpEnum.ROLE.getCode().equals(topicTen.getReceiveTp())) {
					userIds.addAll(getUserIdByRole(topicTen.getReceiveId()));
				} else {
					userIds.add(topicTen.getReceiveId());
				}
			}
		}
		return userIds;
	}
	
	public List<UapMsgInfo> getMsgInfo(String state){
		List<UapMsgSqlInfo> sqlInfoList = msgInfoSqlRepository.findByState(state);
		List<UapMsgInfo> info = new ArrayList<>();
		if(sqlInfoList!=null&&!sqlInfoList.isEmpty()) {
			for(UapMsgSqlInfo sqlInfo: sqlInfoList) {
				info.add(getMsgInfoBySql(sqlInfo));
			}
		}
		
		return info;
	}
	
	public UapMsgInfo getMsgInfoBySql(UapMsgSqlInfo sqlInfo) {
		UapMsgInfo info = new UapMsgInfo();
		info.setId(sqlInfo.getId().toString());
		info.setMsgCode(sqlInfo.getMsgCode());
		info.setMsgContent(sqlInfo.getMsgContent());
		info.setMsgLevel(sqlInfo.getMsgLevel());
		info.setMsgTitle(sqlInfo.getMsgTitle());
		info.setSenderNm(sqlInfo.getSenderNm());
		info.setState(sqlInfo.getState());
		info.setCreateTime(sqlInfo.getCreateTime());
		info.setTenancyId(sqlInfo.getTenancyId());
		return info;
		
	}
	
	public UapMsgSqlInfo getMsgInfoByNoSql(UapMsgInfo info) {
		UapMsgSqlInfo sqlInfo = new UapMsgSqlInfo();
		if(info.getId()!=null) {
			sqlInfo.setId(Long.parseLong(info.getId()));
		}
		sqlInfo.setMsgCode(info.getMsgCode());
		sqlInfo.setMsgContent(info.getMsgContent());
		sqlInfo.setMsgLevel(info.getMsgLevel());
		sqlInfo.setMsgTitle(info.getMsgTitle());
		sqlInfo.setSenderNm(info.getSenderNm());
		sqlInfo.setState(info.getState());
		sqlInfo.setCreateTime(info.getCreateTime());
		sqlInfo.setTenancyId(info.getTenancyId());
		return sqlInfo;
		
	}
}
