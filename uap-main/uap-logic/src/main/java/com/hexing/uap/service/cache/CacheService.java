
package com.hexing.uap.service.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.common.properties.GlobalProperties;

/**
 * 系统数据缓存服务
 * 
 * @author Tony
 *
 */
@Service
public class CacheService {

	private static final String CACHE_INIT_LOCK_KEY = "UAP:cacheInitFlag";
	// 用户锁定
	private static Logger LOG = LoggerFactory.getLogger(CacheService.class);

	@Autowired
	AccountLockCache AccountLockCache;
	@Autowired
	ParentCodeCache codeCache;

	@Autowired
	GlobalProperties globalProperties;
	@Autowired
	MenuCache menuCache;
	@Autowired
	private MenuRoleCache menuRoleCache;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RestApiCache restApiCache;
	@Autowired
	private TokenHistoryCache tokenCache;
	@Autowired
	private UserCache userCache;
	@Autowired
	UserRoleCache userRoleCache;
	@Autowired
	TenancyCache tenancyCache;
	@Autowired
	CalendarCache calendarCache;
	@Autowired
	AppCache appCache;
	@Autowired
	OrganizationCache organizationCache;

	public Boolean isLock() {
		return redisTemplate.hasKey(CACHE_INIT_LOCK_KEY);
	}

	public void initCache() {
		LOG.info("Init cache begin");
		redisTemplate.opsForValue().set(CACHE_INIT_LOCK_KEY, "1");
		redisTemplate.expire(CACHE_INIT_LOCK_KEY, 5, TimeUnit.MINUTES);
		tenancyCache.init(null);
		appCache.init(null);
		userCache.init(null);
		userRoleCache.init(null);
		restApiCache.init(null);
		menuCache.init(null);
		menuRoleCache.init(null);
		calendarCache.init(null);
		organizationCache.init(null);
		tokenCache.init();
		codeCache.init(null);
		LOG.info("Init cache end");
	}
	
	public String initTenantCache(Long tenantId, TenantCacheField field) {
		switch (field) {
		case app:
			appCache.init(tenantId);
			break;
		case user:
			userCache.init(tenantId);
			break;
		case userRole:
			userRoleCache.init(tenantId);
			break;
		case organization:
			organizationCache.init(tenantId);
			break;
		case calendar:
			calendarCache.init(tenantId);
			break;
		case tenancy:
			tenancyCache.init(tenantId);
			break;
		default:
			initTenantAll(tenantId);
			break;
		}
		return ResponseCode.OPERATE_SUCCESS;
	}
	
	public String initAppCache(Long appId, AppCacheField field) {
		switch (field) {
		case restApi:
			restApiCache.init(appId);
			break;
		case menu:
			menuCache.init(appId);
			break;
		case menuRole:
			menuRoleCache.init(appId);
			break;
		case code:
			codeCache.init(appId);
			break;
		default:
			initAppAll(appId);
			break;
		}
		return ResponseCode.OPERATE_SUCCESS;
	}
	
	/**
	 * Des:可独立刷新的应用缓存域
	 * @author hua.zhiwei<br>
	 * @CreateDate 2019年7月25日
	 */
	public enum AppCacheField {
		restApi,// 接口
		menu,// 菜单
		menuRole,// 菜单角色关联
		code,// 编码
		all;// 所有
	}
	
	/**
	 * Des:可独立刷新的租户缓存域
	 * @author hua.zhiwei<br>
	 * @CreateDate 2019年7月25日
	 */
	public enum TenantCacheField {
		app, // 应用
		user, // 用户
		userRole,// 用户角色关联
		organization,// 组织
		calendar,// 日历
		tenancy,// 租户
		all;// 所有
	}
	
	private void initTenantAll(Long tenantId) {
		tenancyCache.init(tenantId);
		appCache.init(tenantId);
		userCache.init(tenantId);
		userRoleCache.init(tenantId);
		calendarCache.init(tenantId);
		organizationCache.init(tenantId);
		Set<Long> appIds = appCache.getAppIdSet(tenantId);
		if (!CollectionUtils.isEmpty(appIds)) {
			for (Long appId : appIds) {
				initAppAll(appId);
			}
		}
	}
	
	private void initAppAll(Long appId) {
		restApiCache.init(appId);
		menuCache.init(appId);
		menuRoleCache.init(appId);
		codeCache.init(appId);
	}
}
