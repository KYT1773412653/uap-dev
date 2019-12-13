package com.hexing.uap.cfg;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hexing.uap.job.SystemInfoJob;
import com.hexing.uap.service.IConfigService;

/** 
 * Des:
 * @author hua.zhiwei<br>
 * @CreateDate 2019年9月23日
 */
@Component
@Order(value = 1)
public class ConfigInit implements CommandLineRunner {
	
	@Value("${uap.system.sampleInterval}")
	private String sampleInterval;

	@Autowired
	IConfigService configService;

	@Override
	public void run(String... args) throws Exception {
		SystemInfoJob.sample_interval = NumberUtils.toInt(sampleInterval, 10);
		configService.init();
	}

}
