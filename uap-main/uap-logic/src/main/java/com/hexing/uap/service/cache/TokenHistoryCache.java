package com.hexing.uap.service.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hexing.uap.bean.jpa.UapTokenHistory;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.common.bean.PagingParam;
import com.hexing.uap.common.properties.GlobalProperties;
import com.hexing.uap.repository.custom.TokenHistoryCustomRepository;
import com.hexing.uap.service.RedisService;
import com.hexing.uap.util.JsonUtil;
import com.hexing.uap.util.MD5Util;

/**
 * <p>
 * token缓存服务
 * 
 * @author hua.zhiwei<br>
 */
@Repository
public class TokenHistoryCache {
	private static String CACHE_KEY = "UAP:tokenHistory";
	private static Logger LOG = LoggerFactory.getLogger(TokenHistoryCache.class);
	@Autowired
	TokenHistoryCustomRepository customRepository;
	@Autowired
	GlobalProperties globalProperties;

	@Autowired
	public RedisTemplate<String, String> redisTemplate;

	@Autowired
	protected RedisService redisUtil;

	public UapTokenHistory get(String token) {

		String newToken = redisUtil.getObject(token);
		if (!StringUtils.isEmpty(newToken)) {
			token = newToken;
		}
		String his = redisUtil.getHash(CACHE_KEY, token);

		UapTokenHistory uapTokenHistory = null;
		if (!StringUtils.isEmpty(his)) {
			uapTokenHistory = JsonUtil.readObject(his, UapTokenHistory.class);
		}
		return uapTokenHistory;
	}

	public String getTokenPaired(String refreshToken, Long owerId) {
		String key = owerId+"--"+MD5Util.md5(refreshToken);
		return (String) redisUtil.getObject(key);
	}

	public void init() {
		LOG.info("Init {}  ", CACHE_KEY);
		redisUtil.delete(CACHE_KEY);
		load();
	}

	private void load() {
		int pageCount = 1000;
		PagingParam param = new PagingParam();
		param.setLimitMax(false);
		param.setStart(0);
		param.setLimit(pageCount);
		PageData<UapTokenHistory> pageData = customRepository.findAll(param);
		long total = pageData.getTotal();
		LOG.info("{} total is {} ", CACHE_KEY, pageData.getTotal());
		List<UapTokenHistory> datas = pageData.getData();
		for (UapTokenHistory data : datas) {
			put(data);
		}
		if (total > pageCount) {
			long loops = total % pageCount == 0 ? total / pageCount : total / pageCount + 1;
			for (int i = 1; i < loops; i++) {
				param.setStart(pageCount * i);
				pageData = customRepository.findAll(param);
				datas = pageData.getData();
				for (UapTokenHistory data : datas) {
					put(data);
				}
			}
		}
	}

	public void put(UapTokenHistory v) {
		redisUtil.putHash(CACHE_KEY, v.getToken(), JsonUtil.write(v));
	}

	public void putTokenPaired(String refreshToken, Long owerId, String newToken) {
		String key = owerId+"--"+MD5Util.md5(refreshToken);
		redisUtil.putObject(key, newToken);
		Long tokenEpx = globalProperties.getTokenRefreshExpiration();
		if (null == tokenEpx) {
			return;
		}
		redisUtil.expire(key, tokenEpx);
	}
}
