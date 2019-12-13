package com.hexing.uap.service.cache;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hexing.uap.bean.jpa.UapCalendarManagement;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.repository.CalendarRepository;
import com.hexing.uap.repository.custom.CalendarCustomRepository;
import com.hexing.uap.repository.param.CalendarQuery;
import com.hexing.uap.service.CalendarService;
import com.hexing.uap.service.RedisService;
import com.hexing.uap.util.JsonUtil;
import com.hexing.uap.util.MathUtil;

/**
 * <p>
 * 
 * @author hua.zhiwei<br>
 */
@Repository
public class CalendarCache {

	/** 租户日历缓存，缓存的KEY为租户ID-年-月-日 */
	private static String CACHE_KEY = "UAP:calendarCache";
	/** 租户对应时区缓存，租户ID-时区 */
	private static String CACHE_TIME_ZONE = "UAP:timeZone";
	// 租户和租户内日历的缓存，缓存的key为特定前缀+租户ID，value为对应的日历年份.
	private static final String TENANT_CALENDAR_SET = "UAP:tenant_calendar:";

	/** 缓存key分隔符 */
	public static final String SPLIT_SIGN = "-";

	@Autowired
	protected RedisService redisUtil;

	@Autowired
	private CalendarCustomRepository calendarCustomRepository;

	@Autowired
	private CalendarRepository calendarRepository;
	
	@Autowired
	TenancyCache tenancyCache;

	private static Logger LOG = LoggerFactory.getLogger(CalendarCache.class);

	public void clear(Long tenantId) {
		if (null == tenantId) {
			redisUtil.delete(CACHE_KEY);
			redisUtil.delete(CACHE_TIME_ZONE);
			Set<String> tenantIds = tenancyCache.getAllTenantId();
			if (CollectionUtils.isEmpty(tenantIds)) {
				return;
			} else {
				for (String tenant : tenantIds) {
					redisUtil.delete(TENANT_CALENDAR_SET + tenant);
				}
			}
		} else {
			Set<String> years = getTenantCalendarYear(tenantId);
			String timeZone = findTimeZone(tenantId);
			if (!CollectionUtils.isEmpty(years) && !StringUtils.isEmpty(timeZone)) {
				for (String year : years) {
					List<String> keys = CalendarService.getHashKeys(tenantId, Long.valueOf(year), null, timeZone);
					for (String key : keys) {
						redisUtil.deleteHash(CACHE_KEY, key);
					}
				}
			}
			redisUtil.deleteHash(CACHE_TIME_ZONE, Objects.toString(tenantId));
			redisUtil.delete(TENANT_CALENDAR_SET + tenantId);
		}
	}

	public void init(Long tenantId) {
		clear(tenantId);
		/** 默认初始化本年日历 */
//		LocalDate date = LocalDate.now(DateTimeUtil.clock);
//		Long year = (long) date.getYear();
		load(tenantId, null);
	}

	public void load(Long tenantId, Long year) {
		int pageCount = 1000;
		CalendarQuery param = new CalendarQuery();
		param.setLimitMax(false);
		param.setStart(0);
		param.setLimit(pageCount);
		param.setTenantId(tenantId);
		param.setYear(year);
		PageData<UapCalendarManagement> pageData = calendarCustomRepository.findAll(param);
		long total = pageData.getTotal();
		LOG.info("{} total is {} ", CACHE_KEY, pageData.getTotal());
		List<UapCalendarManagement> datas = pageData.getData();
		if (CollectionUtils.isEmpty(datas)) {
			return;
		}
		Map<String, String> map = Maps.newHashMap();
		Map<String, String> timeZoneMap = Maps.newHashMap();
		Map<Long, Set<String>> yearsMap = Maps.newHashMap();
		Set<Long> tenantIdSet = Sets.newHashSet();
		for (UapCalendarManagement data : datas) {
			String hashKey = data.gainHashKey();
			map.put(hashKey, JsonUtil.write(data));
			timeZoneMap.putIfAbsent(String.valueOf(data.getTenantId()), data.getTimeZone());
			if (tenantIdSet.contains(data.getTenantId())) {
				if (yearsMap.get(data.getTenantId()).contains(String.valueOf(data.getYear()))) {
					continue;
				}
				yearsMap.get(data.getTenantId()).add(String.valueOf(data.getYear()));
			} else {
				tenantIdSet.add(data.getTenantId());
				yearsMap.put(data.getTenantId(), Sets.newHashSet(String.valueOf(data.getYear())));
			}
		}
		putCalendarMap(map, yearsMap);
		if (total > pageCount) {
			long loops = total % pageCount == 0 ? total / pageCount : total / pageCount + 1;
			for (int i = 1; i < loops; i++) {
				param.setStart(pageCount * i);
				pageData = calendarCustomRepository.findAll(param);
				datas = pageData.getData();
				map.clear();
				for (UapCalendarManagement data : datas) {
					String hashKey = data.gainHashKey();
					map.put(hashKey, JsonUtil.write(data));
					timeZoneMap.putIfAbsent(String.valueOf(data.getTenantId()), data.getTimeZone());
					if (tenantIdSet.contains(data.getTenantId())) {
						if (yearsMap.get(data.getTenantId()).contains(String.valueOf(data.getYear()))) {
							continue;
						}
						yearsMap.get(data.getTenantId()).add(String.valueOf(data.getYear()));
					} else {
						tenantIdSet.add(data.getTenantId());
						yearsMap.put(data.getTenantId(), Sets.newHashSet(String.valueOf(data.getYear())));
					}
				}
				putCalendarMap(map, yearsMap);
			}
		}
		putTimeZoneMap(timeZoneMap);
	}

	public void put(UapCalendarManagement calendarManagement) {
		String hashKey = calendarManagement.gainHashKey();
		redisUtil.putHash(CACHE_KEY, hashKey, JsonUtil.write(calendarManagement));
//		redisUtil.putHash(CACHE_TIME_ZONE, String.valueOf(calendarManagement.getTenantId()),
//				calendarManagement.getTimeZone());
		redisUtil.redisTemplate.opsForHash().putIfAbsent(CACHE_TIME_ZONE,
				String.valueOf(calendarManagement.getTenantId()), calendarManagement.getTimeZone());
		Set<String> yearSet = Sets.newHashSet(String.valueOf(calendarManagement.getYear()));
		Map<Long, Set<String>> yearsMap = Maps.newHashMap();
		yearsMap.put(calendarManagement.getTenantId(), yearSet);
		putTenantCalendarYear(yearsMap);
	}

	public void putTimeZone(Long tenantId, String timeZone) {
		redisUtil.putHash(CACHE_TIME_ZONE, String.valueOf(tenantId), timeZone);
	}

	public void putTimeZoneMap(Map<String, String> map) {
		redisUtil.putHash(CACHE_TIME_ZONE, map);
	}

	public String findTimeZone(Long tenantId) {
		String timeZone = redisUtil.getHash(CACHE_TIME_ZONE, String.valueOf(tenantId));
		if (StringUtils.isEmpty(timeZone)) {
			List<String> list = calendarCustomRepository.queryTimeZone(tenantId);
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}
			redisUtil.putHash(CACHE_TIME_ZONE, String.valueOf(tenantId), list.get(0));
			// load(tenantId);
			timeZone = redisUtil.getHash(CACHE_TIME_ZONE, String.valueOf(tenantId));
		}
		return timeZone;
	}

	public UapCalendarManagement findByTenantIdAndDate(Long tenantId, Long timestamp, Long year) {
		String hashKey = tenantId + SPLIT_SIGN + timestamp;
		String result = redisUtil.getHash(CACHE_KEY, hashKey);
		if (StringUtils.isEmpty(result)) {
			if (checkCalendar(tenantId, year)) {
				return null;
			}
			load(tenantId, year);
			result = redisUtil.getHash(CACHE_KEY, hashKey);
		}
		return JsonUtil.readObject(result, UapCalendarManagement.class);
	}

	public List<UapCalendarManagement> find(List<String> keys, Long tenantId, Long year) {
		List<UapCalendarManagement> result = Lists.newArrayList();
		if (CollectionUtils.isEmpty(keys)) {
			return result;
		}
		boolean hasKey = redisUtil.redisTemplate.opsForHash().hasKey(CACHE_KEY, keys.get(0));
		if (!hasKey) {
			if (checkCalendar(tenantId, year)) {
				return result;
			}
			load(tenantId, year);
			hasKey = redisUtil.redisTemplate.opsForHash().hasKey(CACHE_KEY, keys.get(0));
			if (!hasKey) {
				return result;
			}
		}
		for (String hashKey : keys) {
			String calendar = redisUtil.getHash(CACHE_KEY, hashKey);
			result.add(JsonUtil.readObject(calendar, UapCalendarManagement.class));
		}
		return result;
	}

	public void putAll(List<UapCalendarManagement> calendars) {
		for (UapCalendarManagement calendar : calendars) {
			put(calendar);
		}
	}

	public void putCalendarMap(Map<String, String> map, Map<Long, Set<String>> yearsMap) {
		redisUtil.putHash(CACHE_KEY, map);
		putTenantCalendarYear(yearsMap);
	}
	
	public void putTenantCalendarYear(Map<Long, Set<String>> years) {
		if (CollectionUtils.isEmpty(years)) {
			return;
		}
		for (Map.Entry<Long, Set<String>> entry : years.entrySet()) {
			redisUtil.putSet(TENANT_CALENDAR_SET + entry.getKey(), entry.getValue());
		}
	}
	
	public Set<String> getTenantCalendarYear(Long tenantId) {
		return redisUtil.getSet(TENANT_CALENDAR_SET + tenantId);
	}

	/**
	 * <p>
	 * 检查租户某年的日历是否已配置
	 * 
	 * @author hua.zhiwei<br>
	 * @param tenantId 租户ID
	 * @param year     年份
	 * @return true if null<br>
	 */
	private boolean checkCalendar(Long tenantId, Long year) {
		Long count = calendarRepository.countByTenantIdAndYear(tenantId, year);
		return MathUtil.getNonNull(count).longValue() == 0L;
	}

}
