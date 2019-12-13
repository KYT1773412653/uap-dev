package com.hexing.uap.job;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hexing.uap.constant.ResponseCode;
import com.hexing.uap.service.SystemInfoDynamicService;

/**
 * Des:
 * 
 * @author hua.zhiwei<br>
 * @CreateDate 2019年9月12日
 */
@Component
public class SystemInfoJob {

	@Autowired
	SystemInfoDynamicService systemInfoSaveService;
	
	private Logger log = LoggerFactory.getLogger(SystemInfoJob.class);

	private static final int SYSTEM_INFO_SAVE_DAYS = 3;

	// 采样间隔
	public static int sample_interval = 10;
	// 采样计数器
	//public static int sample_arithmometer = 1;
	public static AtomicInteger sample_arithmometer = new AtomicInteger(1);

	/**
	 * Des:采样定时任务，任务每秒执行一次
	 * 执行时判断sample_arithmometer是否达到sample_interval配置的值,如果达到则执行采样，并在采样完成后把计数器还原
	 * 反之，不执行采样，并把计数器加1
	 * @author hua.zhiwei<br> <br>
	 */
	@Scheduled(cron = "0/1 * * * * ?")
	public void getSystemInfo() {
		if (sample_arithmometer.get() < sample_interval) {
			sample_arithmometer.getAndIncrement();
			return;
		}
		try {
			systemInfoSaveService.getSystemInfo();
		} catch (InterruptedException e) {
			log.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
		sample_arithmometer.set(1);
	}

	@Scheduled(cron = "2 0/3 * * * ?")
	public void saveSystemInfo() {
		systemInfoSaveService.save();
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void clearSystemInfo() {
		long clearTime = System.currentTimeMillis() - SYSTEM_INFO_SAVE_DAYS * 24 * 3600 * 1000;
		systemInfoSaveService.clear(clearTime);
	}

//	public void sampleTask() {
//		while(true) {
//			systemInfoSaveService.getSystemInfo();
//			try {
//				Thread.sleep(sample_interval);
//			} catch (InterruptedException e) {
//				
//			}
//		}
//	}
}
