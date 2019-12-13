package com.hexing.uap.service;

import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hexing.uap.bean.custom.LogResponse;
import com.hexing.uap.bean.jpa.UapLog;
import com.hexing.uap.bean.jpa.UapRestApi;
import com.hexing.uap.bean.jpa.UapUser;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.message.LogResponseCode;
import com.hexing.uap.repository.LogRepository;
import com.hexing.uap.repository.custom.LogCustomRepository;
import com.hexing.uap.repository.param.LogQuery;
import com.hexing.uap.service.cache.LogCacheService;
import com.hexing.uap.util.ConvertUtil;

/**
 * @author Chent
 *
 */
@Service
public class LogService {
	@Autowired
	LogCustomRepository logCustomRepository;
	@Autowired
	LogRepository logRepository;
	@Autowired
	RestApiService restApiService;
	@Autowired
	LogCacheService logCacheService;
	@Autowired
	UserService userService;
	private static Logger LOG = LoggerFactory.getLogger(LogService.class);

	public boolean canDelete(LogQuery query) {
		return true;
	}

	public String delete(LogQuery query) {
		if (canDelete(query)) {
			List<UapLog> lists = logCustomRepository.queryLogs(query);
			for (UapLog u : lists) {
				logRepository.delete(u);
			}
			return LogResponseCode.OPERATE_SUCCESS;
		}
		return LogResponseCode.DATA_IN_USE;
	}

	public void deleteByAccessTimeLessThan(Long accessTime) {
		logCustomRepository.deleteByAccessTimeLessThan(accessTime);
	}

	public PageData<LogResponse> findLogs(LogQuery queryBean) {
		PageData<LogResponse> responseData = new PageData<LogResponse>();
		List<LogResponse> logList = Lists.newArrayList();
		PageData<UapLog> data = logCustomRepository.findLogs(queryBean);
		if (!CollectionUtils.isEmpty(data.getData())) {
			Map<Long, ImmutablePair<String, String>> userMap = Maps.newHashMap();
			for (UapLog log : data.getData()) {
				ImmutablePair<String, String> pair = userMap.get(log.getUserId());
				if (null == pair) {
					UapUser user = userService.findById(log.getUserId());
					if (null != user) {
						pair = ImmutablePair.of(user.getNo(), user.getName());
					} else {
						pair = ImmutablePair.nullPair();
					}
					userMap.put(log.getUserId(), pair);
				}
				LogResponse response = ConvertUtil.convert(log, LogResponse.class);
				response.setUserNo(pair.left);
				response.setUserName(pair.right);
				logList.add(response);
			}
		}
		responseData.setData(logList);
		responseData.setTotal(data.getTotal());
		return responseData;
	}

	public UapLog get(Long id) {
		if (null == id) {
			return null;
		}
		Optional<UapLog> res = logRepository.findById(id);
		if (res.isPresent()) {
			return res.get();
		}
		return null;
	}

	public String save(UapLog entity) {
		String apiId = entity.getApiId();
		UapRestApi api = restApiService.get(apiId);
		if (api == null) {
			return LogResponseCode.OPERATE_FAILURE;
		}
		String logState = api.getLogState();
		if (StringUtils.isEmpty(logState) || UapConstant.UAP_COMM_DISABLED.equals(logState)) {
			return LogResponseCode.API_LOG_STATE_DISABLED;
		}

		String uuid = UUID.randomUUID().toString();
		Long score = System.currentTimeMillis();
		Random r = new Random();
		//获取0-9之间的随机数
		String id = score + uuid + r.nextInt(10000);
		entity.setId(id);

		UapLog res = logRepository.save(entity);
		if (res == null) {
			return LogResponseCode.OPERATE_FAILURE;
		}
		return LogResponseCode.OPERATE_SUCCESS;
	}

	public String save() {
		Long size = logCacheService.getSize();
		LOG.info("Logs size ={}", size);
		if (size > 0) {
			List<UapLog> logs = logCacheService.get();
			for (UapLog log : logs) {
				try{
					UapLog res = logRepository.save(log);
					if (res != null) {
						logCacheService.delete(log);
					}
				}catch (Exception e) {
					LOG.error("Save {} error", log.getId(),e);
					continue;
				}
			}
		}
		return LogResponseCode.OPERATE_SUCCESS;
	}
}