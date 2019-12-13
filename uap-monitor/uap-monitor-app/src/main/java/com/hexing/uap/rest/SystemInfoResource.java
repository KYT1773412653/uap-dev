package com.hexing.uap.rest;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.hexing.uap.constant.ResponseCode;
import com.hexing.uap.model.HostInstance;
import com.hexing.uap.rest.systeminfo.AbsSystemInfoResource;

/**
 * Des:
 * 
 * @author hua.zhiwei<br>
 * @CreateDate 2019年9月2日
 */
@RestController
public class SystemInfoResource extends AbsSystemInfoResource {
	
	@Override
	@GetMapping(value = "/system/host", produces = "application/json")
	public ListDataResponse<HostInstance> getAllHost() {
		ListDataResponse<HostInstance> response = new ListDataResponse<>();
		List<HostInstance> result = Lists.newArrayList();
//		List<ServiceInstance> res = systemInfoDynamicService.getAllServiceInstance();
//		for (ServiceInstance instance : res) {
////			JSONObject paramJson = new JSONObject();
////			paramJson.put("host_name", instance.getHost());
////			paramJson.put("url", instance.getUri().toString());
//			// MutablePair<String, String> pair = MutablePair.of(instance.getHost(),
//			// instance.getUri().toString());
//			HostInstance ins = new HostInstance();
//			ins.setHostName(instance.getHost());
//			ins.setUrl(instance.getUri().toString());
//			result.add(ins);
//		}
		response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
		response.setData(result);
		response.setTotal(result.size());
		return response;
	}
}
