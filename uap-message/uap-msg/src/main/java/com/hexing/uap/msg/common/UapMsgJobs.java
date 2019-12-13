package com.hexing.uap.msg.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hexing.uap.msg.model.jpa.UapMsgInfo;
import com.hexing.uap.msg.model.jpa.UapMsgReceiver;
import com.hexing.uap.msg.model.sqlJpa.UapMsgTopicReceiver;
import com.hexing.uap.msg.service.MsgService;
import com.hexing.uap.msg.service.TopicService;
import com.hexing.uap.service.RedisService;
import com.hexing.uap.util.JsonUtil;

@Component
@Configurable
@EnableScheduling
public class UapMsgJobs {

	private static final Logger LOG = LoggerFactory.getLogger(UapMsgJobs.class);

	@Autowired
	private MsgService msgService;
	@Autowired
	private TopicService topicService;

	private static String INSERT_MSG_LOCK = "UAP:msgSendLock";

	@Autowired
	protected RedisTemplate redisTemplate;
	@Autowired
	protected RedisService redisUtil;
	
	// 用户缓存KEY,缓存为map类型，map的key为用户编号，map值为对应的用户数据
	private static final String CACHE_KEY = "UAP:userCache";

	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0/5 * * * * ? ")
	public void insertMessage() {
		if (redisTemplate.hasKey(INSERT_MSG_LOCK) && "1".equals(redisTemplate.opsForValue().get(INSERT_MSG_LOCK))) {
			return;
		}
		LOG.debug("start message jobs...");
		redisTemplate.opsForValue().set(INSERT_MSG_LOCK, "1");
		redisTemplate.expire(INSERT_MSG_LOCK, 1, TimeUnit.MINUTES);
		Map<String, List<UapMsgTopicReceiver>> receiverMap = topicService.getReceiverMap();
		try {
			if (receiverMap != null && !receiverMap.isEmpty()) {
				Map<String, List<UapMsgInfo>> msgInfoMap = new HashMap<>();
				List<UapMsgInfo> msgAllList = msgService.getMsgInfo("0");
				if (msgAllList != null && !msgAllList.isEmpty()) {
	
					for (UapMsgInfo info : msgAllList) {
						String code = info.getMsgCode();
						if (msgInfoMap.containsKey(code)) {
							msgInfoMap.get(code).add(info);
						} else {
							List<UapMsgInfo> msgList = new ArrayList<>();
							msgList.add(info);
							msgInfoMap.put(code, msgList);
						}
					}
					for (Map.Entry<String, List<UapMsgInfo>> msgCodeInfo : msgInfoMap.entrySet()) {
						String code = msgCodeInfo.getKey();
						if (receiverMap.containsKey(code)) {
							List<UapMsgTopicReceiver> recList = receiverMap.get(code);
							Set<Long> userIds = msgService.getUserIds(recList);
							if (userIds != null && !userIds.isEmpty()) {
								for (UapMsgInfo msg : msgCodeInfo.getValue()) {
									List<UapMsgReceiver> rList = new ArrayList<>();
									for (Long userid : userIds) {
										String obj= redisUtil.getHash(CACHE_KEY, userid.toString());
										Map<String, Object> map = JsonUtil.readObject(obj, Map.class);
										if(msg.getTenancyId() ==null) {
											rList.add(new UapMsgReceiver(userid, "0", System.currentTimeMillis(), msg.getId()));
										}else if (msg.getTenancyId() !=null && map.get("tenant_id") != null && msg.getTenancyId().toString().equals(map.get("tenant_id").toString())) {
											rList.add(new UapMsgReceiver(userid, "0", System.currentTimeMillis(), msg.getId()));
										}
									}
									msgService.saveUapMsgReceive(rList);
									msg.setState("1");
									msgService.saveUapMsgInfo(msg);
								}
	
							}
						}
					}
				}
			}
		}catch(Exception e){
			LOG.error(e.getMessage());
		}finally{
			redisTemplate.opsForValue().set(INSERT_MSG_LOCK, "0");
		}
		
	}

}
