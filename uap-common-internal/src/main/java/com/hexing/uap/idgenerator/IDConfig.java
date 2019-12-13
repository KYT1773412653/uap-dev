package com.hexing.uap.idgenerator;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.hexing.uap.common.BeanDef;
import com.hexing.uap.common.exception.UAPException;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.service.CacheLock;
import com.hexing.uap.service.RedisService;

/**
 * Des:
 * 
 * @author hua.zhiwei<br>
 * @CreateDate 2019年10月30日
 */
@Component(BeanDef.COMMON_IDCONFIG)
public class IDConfig {

	// 实例注册缓存，存放已注册的实例信息，并据此分发实例ID
	private static final String CACHE_KEY = "UAP:IDCache";

	// 实例编码，在服务启动时确定，全局唯一。在服务运行期间，ID管理模块据此识别每个服务实例
	protected static String machine_code = createMachineCode();
	// 本实例在系统中的注册码(即IDUtil中的instanceId)
	protected static long register_code = 1L;

	// 距离上次刷新注册信息多久才会被认定为无效(毫秒)
	public static final long INVALID_INTERVAL = 60 * 60 * 1000;
	// 注册码最大值
	public static final long MAX_REGISTER_CODE = IDUtil.MAX_INSTANCE_NUM - 1;
	private static final String REGISTER_CODE_JSON = "register_code";
	private static final String REGISTER_TIME_JSON = "register_time";
	// 对缓存加锁
	private static final String CACHE_LOCK_KEY = CacheLock.getLockName(CACHE_KEY);

	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	RedisService redisService;
	@Autowired
	CacheLock cacheLock;

	// 如果模块数和分布式服务过多，为避免instanceId用完，ID算法可为每个模块单独维护一套ID库，目前UAP项目没有太多实例，暂时不需要。
	// protected static String app_code = "uap";
	// protected abstract void setAppCode();

	void init() {
		cacheLock.getLock(CACHE_LOCK_KEY);
		try{
			createRegisterCode();
		} finally {
			cacheLock.release(CACHE_LOCK_KEY);
		}
		
	}

	// 生成本实例的唯一注册码
	private void createRegisterCode() {
		register_code = registerInstance();
		if (register_code > MAX_REGISTER_CODE) {
			doClear();
			register_code = registerInstance();
			if (register_code > MAX_REGISTER_CODE) {
				throw new UAPException(ResponseCode.INSTANCE_NUMBER_EXCEEDS);
			}
		}
		saveInstanceId(machine_code);
		IDUtil.setInstanceId(register_code);
	}

	private long registerInstance() {
		Map<Object, Object> result = redisTemplate.opsForHash().entries(CACHE_KEY);
		if (CollectionUtils.isEmpty(result)) {
			register_code = 1L;
		} else {
			long values[] = result.values().stream()
					.mapToLong(v -> JSONObject.parseObject(String.valueOf(v)).getLong(REGISTER_CODE_JSON)).sorted()
					.toArray();
			long before = 0L;
			if (values.length >= IDUtil.MAX_INSTANCE_NUM) {
				before = values[values.length - 1];
			} else if (values[values.length - 1] < MAX_REGISTER_CODE) {
				before = values[values.length - 1];
			} else if (values[0] > 1) {
				before = 0L;
			} else if (values.length > 1) {
				for (int i = 0; i < values.length - 1; i++) {
					if (values[i + 1] - values[i] > 1) {
						before = values[i];
						break;
					}
				}
			}
			register_code = before + 1;
		}
		return register_code;
		// return RandomUtils.nextLong(0, IDUtil.MAX_INSTANCE_NUM);
	}

	/**
	 * Des: 定时刷新注册时间
	 * 
	 * @author hua.zhiwei<br>
	 *         <br>
	 */
	public void refreshRegisterTime() {
		if (redisService.hasHashKey(CACHE_KEY, machine_code)) {
			redisTemplate.opsForHash().put(CACHE_KEY, machine_code, getRegisterJson());
		} else {
			machine_code = IDConfig.createMachineCode();
			init();
		}
	}

	public void clear() {
		if (redisService.lock(CACHE_LOCK_KEY)) {
			try {
				doClear();
			} finally {
				redisService.release(CACHE_LOCK_KEY);
			}
		}
	}

	private void delete(String instanceCode) {
		redisService.deleteHash(CACHE_KEY, instanceCode);
	}

	private void saveInstanceId(String instanceCode) {
		redisTemplate.opsForHash().put(CACHE_KEY, instanceCode, getRegisterJson());
	}

	private static String getRegisterJson() {
		JSONObject json = new JSONObject();
		json.put(REGISTER_CODE_JSON, register_code);
		json.put(REGISTER_TIME_JSON, System.currentTimeMillis());
		return json.toString();
	}

	private static String createMachineCode() {
		return UUID.randomUUID().toString();
	}

	private void doClear() {
		Map<Object, Object> result = redisTemplate.opsForHash().entries(CACHE_KEY);
		if (CollectionUtils.isEmpty(result)) {
			return;
		} else {
			long now = System.currentTimeMillis();
			result.forEach((k, v) -> {
				JSONObject json = JSONObject.parseObject(String.valueOf(v));
				long lastTime = json.getLongValue(REGISTER_TIME_JSON);
				if (now - lastTime > INVALID_INTERVAL) {
					delete(String.valueOf(k));
				}
			});
		}
	}

}
