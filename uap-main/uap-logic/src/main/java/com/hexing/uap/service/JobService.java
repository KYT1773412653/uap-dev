package com.hexing.uap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * Des:集中管理uap平台需要定时执行的各个方法
 * @author hua.zhiwei<br>
 * @CreateDate 2019年5月16日
 */
@Service
public class JobService {
	@Autowired
	private UserService userService;
	@Autowired
	LogService  logService;
	
	@Autowired
	TokenHistoryService tokenHistoryService;
	
	public void updateEffectiveState() {
		userService.updateEffectiveState();
	}
	
	public void clearLog(Long accessTime) {
		logService.deleteByAccessTimeLessThan(accessTime);
	}
	
	public void clearTokenHis(Long accessTime) {
		tokenHistoryService.deleteByInsertTimeLessThan(accessTime);
	}
	
	public void saveLog() {
		logService.save();
	}
}
