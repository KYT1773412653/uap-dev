package com.hexing.uap.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hexing.uap.UapApplication;
import com.hexing.uap.service.cache.MenuRoleCache;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UapApplication.class })
public class MenuRoleCacheTest {
	@Autowired
	MenuRoleCache cache;
	@Test
	public void load(){
		cache.init(null);
		
	}
}
